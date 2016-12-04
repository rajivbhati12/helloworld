package helloworld

import org.scalatest.{FlatSpec, Matchers}
import java.io.FileNotFoundException
import java.util.NoSuchElementException

class BootIntegrationSpec  extends FlatSpec with Matchers{
  var boot:Boot = new Boot()

  behavior of "\nBoot.designMessage"
  it should "produce/return message saying - Hello, Mercury, first planet from the Sun!" in {
    boot.designMessage(1) shouldBe "Hello, Mercury, first planet from the Sun!"
  }
  it should s"produce java.io.FileNotFoundException for non-existing planet" in {
    an [FileNotFoundException] should be thrownBy boot.designMessage(0)
  }

  behavior of "\nDatabaseClient.find"
  var databaseClient = new DatabaseClient(Boot.databasePath)
  it should "produce string - Json{key:value} for valid input planet => 1:Mercury" in {
    databaseClient.find(1) shouldBe "{\"key\": 1, \"value\": \"Mercury\"}"
  }
  it should "throw exception for non existing planet" in {
    an [FileNotFoundException] should be thrownBy databaseClient.find(0)
  }
  it should "throw exception for noname planet" in {
    an [NoSuchElementException] should be thrownBy databaseClient.find(9)
  }
}
