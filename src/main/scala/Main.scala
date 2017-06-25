
import MessageActor.{CreateMessage, GetMessages}
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn


object Main extends App with DefaultJsonProtocol with SprayJsonSupport {

  implicit val system = ActorSystem("life-tracker")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val messageFormat = jsonFormat1(Message)
  val greetActor = system.actorOf(Props[MessageActor])

  val route =
    path("messages") {
      get {
        implicit val timeout: Timeout = 5.second
        val res = (greetActor ? MessageActor.GetMessages).mapTo[List[Message]]
        complete(res)
      }
    } ~
    path("message"/) {
      get {
        parameter("text") { text =>
          greetActor ! CreateMessage(Message(text))
          complete(StatusCodes.OK)
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}

case class Message(text: String)
case object MessageActor {
  case object GetMessages
  case class CreateMessage(msg: Message)
}

class MessageActor extends Actor {

  var messages = List.empty[Message]

  override def receive = {
    case GetMessages => sender ! messages
    case req:CreateMessage => {
      messages = messages :+ req.msg
    }
  }
}