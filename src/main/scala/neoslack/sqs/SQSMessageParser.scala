package neoslack.sqs

import akka.actor.Actor
import com.amazonaws.services.sqs.model.Message

/**
  * Created by rag on 15/02/2017.
  */
class SQSMessageParser extends Actor {

  override def preStart(): Unit = {
    super.preStart()
  }

  override def receive = {
    case m: Message =>
  }

}

object SQSMessageParser {
  case object NoMessages
}