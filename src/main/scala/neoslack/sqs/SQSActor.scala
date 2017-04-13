package neoslack.sqs

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import neoslack.sqs.SQSModel.{DeleteMessageFromQueue, RequestForMessage}
import SQSActor._

class SQSActor(amazonSQS: SQSWrapper,
               queueUrl: String,
               waitTime: Int)
  extends Actor with LazyLogging {

  private val receiveActor = context.actorOf(receiveProps(amazonSQS, queueUrl, waitTime))
  private val deleteActor = context.actorOf(deleteProps(amazonSQS, queueUrl))

  override def receive: Receive = {
    case r: RequestForMessage => receiveActor ! r
    case d: DeleteMessageFromQueue => deleteActor ! d
  }

}

object SQSActor {
  def receiveProps(amazonSQS: SQSWrapper, url: String, wait: Int) = Props(classOf[SQSReceiveMessageActor], amazonSQS, url, wait)
  def deleteProps(amazonSQS: SQSWrapper, url: String) = Props(classOf[SQSDeleteMessageActor], amazonSQS, url)
}