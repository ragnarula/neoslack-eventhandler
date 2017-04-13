import akka.actor.ActorSystem
import akka.testkit.{TestFSMRef, TestKit}
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import neoslack.sqs.SQSModel.DeleteMessageFromQueue
import neoslack.sqs.{SQSDeleteMessageActor, SQSWrapper}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpecLike, Matchers}

import scala.concurrent.Future

/**
  * Created by rag on 17/02/2017.
  */
class SQSDeleteMessageActorSpec extends TestKit(ActorSystem("SQSTest")) with FlatSpecLike with MockFactory with BeforeAndAfter with Matchers{
  class MockableSQSWrapper extends SQSWrapper(null)

  "the actor" should "create a delete request with the correct ID" in {
    val mockSQS = mock[MockableSQSWrapper]
    val fsm = TestFSMRef(new SQSDeleteMessageActor(mockSQS, "url"))
    (mockSQS.deleteMessage _).expects(new DeleteMessageRequest("url","id")).returning(Future.successful(Unit))
    fsm ! DeleteMessageFromQueue("id")
    Thread.sleep(100)
  }
}
