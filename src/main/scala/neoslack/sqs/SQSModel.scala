package neoslack.sqs

import akka.actor.ActorRef
import com.amazonaws.services.sqs.model.Message

/**
  * Created by rag on 16/02/2017.
  */
object SQSModel {

  final case class RequestFailedException(msg: String) extends Exception(msg)
  sealed trait State
  case object Idle extends State
  case object Busy extends State

  sealed trait SQSEvent
  final case class RequestForMessage(ref: ActorRef) extends SQSEvent
  final case class DeleteMessageFromQueue(id: String) extends SQSEvent
  case object SQSRequestComplete extends SQSEvent
  case object SQSRequestFailed extends SQSEvent
  case object SQSRequestTimeout extends SQSEvent

  sealed trait Data
  final case class ReceiveBuffer(messages: Seq[Message], handlers: Set[ActorRef]) extends Data
  final case class DeleteBuffer(deletes: Seq[String]) extends Data

}
