import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpecLike, Matchers}
import scala.pickling.Defaults._, scala.pickling.json._
/**
  * Created by rag on 17/02/2017.
  */
class PicklingSpec extends FlatSpecLike with MockFactory with BeforeAndAfter with Matchers{
  case class demo(field1: String, field2: Int)
  "unpicklig" should "pickle json to case class" in {
    val str  = """ {"field1": "str-value", "field2": 3} """
    val c = str.unpickle[demo]

    c shouldBe demo("str-value", 3)
  }

  "unpickling" should "succeed when there are extra fields" in {
    val str  = """ {"field1": "str-value", "field2": 3, "field3" : 7} """
    val c = str.unpickle[demo]

    c shouldBe demo("str-value", 3)
  }

  "unpickling" should "fail when fields are missing" in {
    val str  = """ {"field1": "str-value", "field4": 3, "field5" : 7} """
    val c = str.unpickle[demo]

    c shouldBe demo("str-value", 3)
  }
}
