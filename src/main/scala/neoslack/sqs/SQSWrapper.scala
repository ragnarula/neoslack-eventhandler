package neoslack.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.{DeleteMessageBatchRequest, DeleteMessageRequest, Message, ReceiveMessageRequest}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class SQSWrapper(amazonSQS: AmazonSQS) {
  def receiveMessage(receiveMessageRequest: ReceiveMessageRequest): Future[List[Message]] = Future {
    blocking {
      amazonSQS.receiveMessage(receiveMessageRequest).getMessages.asScala.toList
    }
  }

  def deleteMessage(deleteMessageRequest: DeleteMessageRequest): Future[Unit] = Future {
    blocking {
      amazonSQS.deleteMessage(deleteMessageRequest)
    }
  }

  def deleteMessageBatch(deleteMessageBatchRequest: DeleteMessageBatchRequest): Future[Unit] = Future {
    blocking {
      amazonSQS.deleteMessageBatch(deleteMessageBatchRequest)
    }
  }
}
