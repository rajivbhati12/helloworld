package helloworld


import java.io.FileNotFoundException
import java.util.NoSuchElementException
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.TableDrivenPropertyChecks._

// FlatSpec docs: http://doc.scalatest.org/1.8/org/scalatest/FlatSpec.html
// Matchers docs: http://www.scalatest.org/user_guide/using_matchers

// As you extract classes or methods out of Boot, add unit tests for them using
// this as a template.

class BootSpec extends FlatSpec with Matchers with MockitoSugar{
  var boot:Boot = new Boot()
  // Mock DatabaseClient class for UnitTest
  var mockDatabaseClient = mock[DatabaseClient]
  when(mockDatabaseClient.find(2)).thenReturn("{\"key\": 2, \"value\": \"mocked up Planet\"}")
  when(mockDatabaseClient.find(0)).thenThrow(new FileNotFoundException)
  when(mockDatabaseClient.find(9)).thenThrow(new NoSuchElementException)
  //Update Boot class variable with mock instance
  Boot.databaseClient = mockDatabaseClient

  behavior of "\nBoot.designMessage"
  it should "return Hello world - message for existing planet" in {
    boot.designMessage(2) shouldBe "Hello, mocked up Planet, second planet from the Sun!"
  }
  it should "throw exception for non-existing planet" in {
    an [FileNotFoundException] should be thrownBy boot.designMessage(0)
  }
  it should "throw exception for unknown planet" in {
    an [NoSuchElementException] should be thrownBy boot.designMessage(9)
  }

  behavior of "\nBoot.parseJson"
  it should "return World[domain] for existing planet" in {
    val returnWorld = boot.parseJson(s"""{"key": 2, "value": "Venus"}""")
    returnWorld.name shouldBe "Venus"
    returnWorld.id shouldBe  2
  }
//  Test data for exception validation
  val parserExceptionTestData =
    Table(
      ( "input"                               ,"expectedExceptionType"                            ),
      (  null                                 ,"java.lang.NullPointerException"                   ),
      (  ""                                   ,"com.fasterxml.jackson.jr.ob.JSONObjectException"  ),
      (  s"""{"key": 2.3, "value": "Venus"}""","java.lang.ClassCastException"                     ),
      (  s"""{"key": 2, "value": }"""         ,"com.fasterxml.jackson.core.JsonParseException"    ),
      (  s"""{"key": , "value": "Earth" }"""  ,"com.fasterxml.jackson.core.JsonParseException"    )
    )
  forAll (parserExceptionTestData) { (input: String, expectedExceptionType: String) => {
    it should s"throw exception for invalid Json string: $input" in {
      var thrown = intercept[Exception] {
        boot.parseJson(input)
      }
      thrown.toString.split(":")(0) shouldBe expectedExceptionType
    }
  }}

  behavior of "\nBoot.getMessageFromTemplate"
  it should s"design hello world - message using planet name and position" in {
    boot.getMessageFromTemplate("Earth", "third") shouldBe "Hello, Earth, third planet from the Sun!"
  }

  //  Unit Test for Class:- World
  behavior of "\nWorld.id"
  it should "produce world id per domain instance" in {
    val id = 4
    val world = new World(4,"Name")
    world.id shouldBe id
  }
  it should "produce world name per domain instance" in {
    val name = "this is my fifth Planet name"
    val world = new World(5,"this is my fifth Planet name")
    world.name shouldBe name
  }
}