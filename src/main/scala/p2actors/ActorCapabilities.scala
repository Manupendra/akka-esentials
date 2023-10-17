package p2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello, there!" // replying to a message
      case message: String => println(s"$self I have received $message")
      case number: Int => println(s"[simple actor] I've received a NUMBER: $number")
      case SpecialMessage(contents) => println(s"[simple actor] I have received a something special: $contents")
      case SendMessageToYourself(content) =>
        self ! content
      case SayHiTo(ref) => ref ! "Hi" // (ref ! "Hi")(self)
      case WirelessPhoneMessage(content, ref) => ref forward (content + "s") // i keep the original sender of the WPM
    }
  }

  val system = ActorSystem("actorCapabilities")
  val simpleActor = system.actorOf(Props[SimpleActor],"simpleActor")

  simpleActor ! "hello, actor"

  // 1 - message can be of any type
  // a) message must be IMMUTABLE
  // b) message must be SERIALIZABLE
  // In practice use case classes and case objects

  simpleActor ! 42

  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("some special content")

  // 2 - actors have information about their context and about themselves
  // context.self === `this` in OOPS

  case class SendMessageToYourself(context: String)
  simpleActor ! SendMessageToYourself("I am an actor and I am proud of it!")

  // 3 - actors can REPLY to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)

  // 4 - dead letters
  alice ! "Hi" // reply to "me"

  // 5 - forwarding messages
  // M -> A -> B
  // forward = sending a message with the ORIGINAL sender

  case class WirelessPhoneMessage(content: String, ref: ActorRef)
  alice ! WirelessPhoneMessage("Hi", bob)  // noSender

}
