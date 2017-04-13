package neoslack.sqs

import akka.actor.FSM
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.typesafe.scalalogging.LazyLogging
import neoslack.sqs.SQSModel._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by rag on 16/02/2017.
  */
class SQSDeleteMessageActor(amazonSQS: SQSWrapper, queueURL: String) extends FSM[State, Data] with LazyLogging {

  startWith(Idle, DeleteBuffer(Seq()))

  when(Idle) {
    case Event(DeleteMessageFromQueue(id), d @ DeleteBuffer(_)) =>
      deleteMessage(id)
      goto (Busy) using d
  }

  when (Busy) {
    case Event(DeleteMessageFromQueue(id), d @ DeleteBuffer(ids)) =>
      stay using d.copy(ids :+ id)
    case Event(SQSRequestComplete, d @ DeleteBuffer(Nil)) =>
      goto (Idle) using d
    case Event(SQSRequestComplete, d @ DeleteBuffer(x :: xs)) =>
      deleteMessage(x)
      stay using d.copy(xs)
    case Event(SQSRequestFailed, d @ DeleteBuffer(ids)) =>
      ids.foreach(i => context.parent ! DeleteMessageFromQueue(i))
      throw RequestFailedException("SQS Delete Request Failed")
  }

  private def deleteMessage(id: String) = amazonSQS.deleteMessage(new DeleteMessageRequest(queueURL, id))
    .onComplete {
      case Failure(e) => logger.error("SQS delete message failed", e)
        context.self ! SQSRequestFailed
      case Success(_) =>
        context.self ! SQSRequestComplete
    }
}