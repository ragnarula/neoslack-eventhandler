package neoslack.sqs

import akka.actor.FSM
import com.amazonaws.services.sqs.model.{Message, ReceiveMessageRequest}
import com.typesafe.scalalogging.LazyLogging
import neoslack.sqs.SQSModel._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class SQSReceiveMessageActor(amazonSQS: SQSWrapper,
                             queueURL: String,
                             waitTime: Int) extends FSM[State, Data] with LazyLogging {

  private val receiveMessageRequest = new ReceiveMessageRequest(queueURL).withWaitTimeSeconds(waitTime)

  startWith(Idle, ReceiveBuffer(Seq(), Set()))

  when(Idle) {
    case Event(RequestForMessage(ref), r @ ReceiveBuffer(Nil, handlers)) =>
      receiveMessage()
      goto (Busy) using r.copy(Seq(), handlers + ref)

    case Event(RequestForMessage(ref), r @ ReceiveBuffer(x :: xs, handlers)) =>
      ref ! x
      stay using r.copy(xs, handlers)

    case Event(m: Message, r @ ReceiveBuffer(messages, handlers)) if handlers.isEmpty =>
      stay using r.copy(messages :+ m, handlers)

    case Event(m: Message, r @ ReceiveBuffer(messages, handlers)) if handlers.nonEmpty =>
      val h = handlers.head
      h ! m
      stay using r.copy(messages, handlers - h)
  }

  when(Busy) {
    case Event(SQSRequestFailed, r @ ReceiveBuffer(_, handlers)) =>
      handlers.foreach(h => context.parent ! RequestForMessage(h))
      throw RequestFailedException("Receive request failed")

    case Event(SQSRequestTimeout, r @ ReceiveBuffer(_, _)) =>
      receiveMessage()
      stay using r

    case Event(SQSRequestComplete, r @ ReceiveBuffer(_, _)) =>
      goto (Idle) using r

    case Event(RequestForMessage(ref), r @ ReceiveBuffer(messages, handlers)) =>
      stay using r.copy(messages, handlers + ref)
  }

  private def receiveMessage() = amazonSQS.receiveMessage(receiveMessageRequest)
    .onComplete {
      case Failure(e) => logger.error("SQS receive message failed", e)
        context.self ! SQSRequestFailed
      case Success(Nil) =>
        context.self ! SQSRequestTimeout
      case Success(xs) =>
        context.self ! SQSRequestComplete
        xs.foreach(x => context.self ! x)
    }
}
