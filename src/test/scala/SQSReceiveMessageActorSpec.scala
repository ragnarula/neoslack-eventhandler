import akka.actor.ActorSystem
import akka.testkit.{TestFSMRef, TestKit}
import com.amazonaws.services.sqs.model.{Message, ReceiveMessageRequest}
import neoslack.sqs.SQSModel.RequestForMessage
import neoslack.sqs.{SQSReceiveMessageActor, SQSWrapper}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpecLike, Matchers}

import scala.concurrent.Future

/**
  * Created by rag on 15/02/2017.
  */
class SQSReceiveMessageActorSpec extends TestKit(ActorSystem("SQSTest")) with FlatSpecLike with MockFactory with BeforeAndAfter with Matchers {

  class MockableSQSWrapper extends SQSWrapper(null)

  "The actor" should "respond with the message when one is available" in {
    val mockSQS = mock[MockableSQSWrapper]
    val fsm = TestFSMRef(new SQSReceiveMessageActor(mockSQS, "", 10))
    val testMessage = new Message()

    fsm ! testMessage
    fsm ! RequestForMessage(testActor)
    expectMsg(testMessage)
  }

  "The actor" should "request new messages and respond with them when they are not buffered" in {
    val message1 = new Message()
    val message2 = new Message()
    message1.setMessageId("1")
    message2.setMessageId("2")

    val testMessageList = List(message1, message2)
    val mockSQS2 = mock[MockableSQSWrapper]
    val fsm = TestFSMRef(new SQSReceiveMessageActor(mockSQS2, "url", 10))

    (mockSQS2.receiveMessage _)
      .expects(new ReceiveMessageRequest("url").withWaitTimeSeconds(10))
      .returning(Future.successful(testMessageList))

    fsm ! RequestForMessage(testActor)
    Thread.sleep(100)
    expectMsg(message1)
    fsm ! RequestForMessage(testActor)
    Thread.sleep(100)
    expectMsg(message2)
  }

  "The actor" should "retry after timeout" in {
    val message1 = new Message()
    val message2 = new Message()
    message1.setMessageId("1")
    message2.setMessageId("2")
    val testMessageList = List(message1, message2)

    val mockSQS3 = mock[MockableSQSWrapper]
    val fsm = TestFSMRef(new SQSReceiveMessageActor(mockSQS3, "url", 10))

    inSequence {
      (mockSQS3.receiveMessage _)
        .expects(new ReceiveMessageRequest("url").withWaitTimeSeconds(10))
        .returning(Future.successful(List())).noMoreThanOnce()

      (mockSQS3.receiveMessage _)
        .expects(new ReceiveMessageRequest("url").withWaitTimeSeconds(10))
        .returning(Future.successful(testMessageList)).noMoreThanOnce()
    }

    fsm ! RequestForMessage(testActor)
    Thread.sleep(100)
    expectMsg(message1)
    fsm ! RequestForMessage(testActor)
    Thread.sleep(100)
    expectMsg(message2)
  }


//  "The actor" should "throw when request fails" in {
//    val mockSQS = mock[MockableSQSWrapper]
//    val fsm = TestFSMRef(new SQSReceiveMessageActor(mockSQS, "url", 10))
//
//    (mockSQS.receiveMessage _)
//      .expects(new ReceiveMessageRequest("url").withWaitTimeSeconds(10))
//      .returning(Future.failed(new Exception("failed"))).noMoreThanOnce()
//
//    val actorRef: TestActorRef[SQSReceiveMessageActor] = fsm
//    assertThrows[RequestFailedException] {actorRef.underlyingActor.receive(RequestForMessage(testActor)); Thread.sleep(100);}
//
//  }
}
