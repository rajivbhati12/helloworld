package helloworld

import java.io.FileNotFoundException
import com.fasterxml.jackson.jr.ob._
import collection.mutable.HashMap


object Boot {


  // In scala, an object can be thought of as a singleton object
  // or a class with static methods. By convention, the object
  // containing the main method is often called "Boot".

  // The file path src/resources/db/
  var databasePath = getClass.getResource("/db").getPath

  // See the DatabaseClient class
  var databaseClient = new DatabaseClient(databasePath)

  // Ordinal HashMap to keep static data out from main method
  val ordinal:HashMap[Int, String] = HashMap(
    1 -> "first",
    2 -> "second",
    3 -> "third",
    4 -> "fourth",
    5 -> "fifth",
    6 -> "sixth",
    7 -> "seventh",
    8 -> "eighth"
  )

  //  var source = Source.fromFile(path)

  //  Entry point for Application

  def main(args: Array[String]) = {
    //  Companion instance of boot class
    var boot:Boot = new Boot
    //  Parse command line arguments
    val id = args(0).toInt
    //  Design message based on id
    val myMessage = boot.designMessage(id)
    //  Message printing
    println(myMessage)
  }
}
//Companion Boot class to make it more accessible/testable
class Boot{
  import Boot._

  //  Design message based on planet id

  def designMessage(id: Int):String = {
    //  Find the database record
    val recordJSON = databaseClient.find(id)
    //  Transform record into an instance of our domain class
    val world = parseJson(recordJSON)

    //  Format Message
    getMessageFromTemplate(world.name, ordinal.get(world.id).get)
  }
  //  Transform record to domain world

  def parseJson(record: String):World = {
    //  Record as Json
    val parsedJSON = JSON.std.mapFrom(record)
    //  Converting Json to domain
    new World(
      parsedJSON.get("key").asInstanceOf[Int],
      parsedJSON.get("value").asInstanceOf[String]
    )
  }

  // Considering: We might like to send new messages to a given world
  // it will be easy to write logic in future for message parsing

  def getMessageFromTemplate(name: String, position: String): String = {
    s"Hello, $name, $position planet from the Sun!"
  }
}


// Unlike Java, scala files can contain multiple classes.
class World(_id: Int, _name: String) {
  // World is the primary "domain" type in this application. Yes,
  // it is very contrived, but as this application grows, it will
  // become a very important concept. Note that there is a much
  // terser syntax available in scala; here it is expanded for clarity.

  def id: Int = _id
  def name: String = _name
}

class DatabaseClient(databasePath: String) {
  // Imagine that this database client has been provided by a 3rd party.
  // This very simple database looks up records in flat files and returns JSON.
  import scala.io.Source // Not the preferred io library in scala btw :)

  // find takes an Int and returns a JSON string
  @throws(classOf[FileNotFoundException])
  @throws(classOf[FileNotFoundException])
  def find(id: Int): String = {
    // Example: databaseClient.find(1)
    // Returns: { key: 1, value: Mercury }
    // Feel free to ignore implementation details here :)
    var path = databasePath + "/" + id.toString
    // Open the file with the file name in path.
    var source = Source.fromFile(path)
    // Read a string from the file.
    var string = try source.getLines.next finally source.close()


    s"""{"key": $id, "value": "$string"}"""
  }
}
