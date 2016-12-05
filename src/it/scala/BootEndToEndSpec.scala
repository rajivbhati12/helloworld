package helloworld


import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.TableDrivenPropertyChecks._

// FlatSpec docs: http://doc.scalatest.org/1.8/org/scalatest/FlatSpec.html
// Matchers docs: http://www.scalatest.org/user_guide/using_matchers

class BootEndToEndSpec extends FlatSpec with Matchers {
  var boot:Boot = new Boot()

  behavior of "\nBoot.main"
  //  Basic positive test
  it should "print Hello world - message for valid planet number" in {
    val stream = new java.io.ByteArrayOutputStream()
    scala.Console.withOut(stream) {
      Boot.main(Array("3"))
    }
    stream.toString shouldBe "Hello, Earth, third planet from the Sun!\n"
  }
  val mainExceptionTestData =
    Table(
      ("printInput"     ,"input"                              ,"expectedExceptionType"                            ),
      ("<blank>"        ,Array("")                            ,"java.lang.NumberFormatException"                  ),
      ("<empty>"        ,Array[String]()                      ,"java.lang.ArrayIndexOutOfBoundsException"         ),
      ("no-exist"       ,Array("-1")                          ,"java.io.FileNotFoundException"                    ),
      ("null"           ,null                                 ,"java.lang.NullPointerException"                   )
    )
  forAll (mainExceptionTestData) {(printInput,testArg,expectedExceptionType) => {
    it should s"produce exception for invalid planet number: $printInput" in {
      var thrown = intercept[Exception] {
        Boot.main(testArg)
      }
      thrown.toString.split(":")(0) shouldBe expectedExceptionType
    }
  }}
}
