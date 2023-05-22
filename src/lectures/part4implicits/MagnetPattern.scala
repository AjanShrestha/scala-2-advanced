package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {

  // to resolve problems created by method overloading

  /*
   * Example
   *  Working on an API for a remote peer-to-peer communication protocol.
   *  All the actors in the network are identical,
   *  so they should be able to handle various kinds of messages.
   */
  class P2PRequest

  class P2PResponse

  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int

    def receive(request: P2PRequest): Int

    def receive(response: P2PResponse): Int

    // def receive[T](message: T)(implicit serializer: Serializer[T]): Int
    def receive[T: Serializer](message: T): Int

    def receive[T: Serializer](message: T, statusCode: Int): Int

    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int
    // generic type are erased at compile time

    // lots of overloads
  }

  /*
   * Posses a number of problem
   *  1. type erasure
   *  2. lifting doesn't work for all overloads
   *
   *      val receiveFV = receive _ // ?!
   *
   *  3. code duplication
   *  4. type inference and default args
   *
   *      actor.receive(?!) // what default args?
   */

  // All these problems can be resolved by Magnet Pattern
  // Good news! This API can be rewritten

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  // actor method
  private def receive[R](magnet: MessageMagnet[R]): R = magnet()

  // how can we make sure this receive method
  // can possibly get other types as arguments
  // by implicit conversion
  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling a P2PRequest
      println("Handing P2P request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling a P2PResponse
      println("Handing P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  /*
   * Benefits
   *  1. no more type erasure problems!
   *  2. lifting works for HOFs
   *      catch - can only work for one return Type only
   */

  // 1 - no more type erasure problems!
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2 - lifting works
  trait Mathlib {
    def add1(x: Int): Int = x + 1

    def add1(s: String): Int = s.toInt + 1
    // add1 overloads
  }

  // "magnetize"
  trait AddMagnet {
    def apply(): Int
  }

  private def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  private val addFV = add1 _
  println(addFV(1))
  println(addFV("3"))

  /*
   * Drawbacks
   *  1. verbose
   *  2. harder to read
   *  3. you can't name or place default arguments
   *  4. call by name doesn't work correctly
   *    (exercise: prove it!) (hint: side effects)
   */

  class Handler {
    def handle(s: => String): Unit = {
      println(s)
      println(s)
    }
    // other overloads
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  private def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  private def sideEffectMethod(): String = {
    println("Hello, Scala")
    "hahaha"
  }

  // handle(sideEffectMethod())
  handle {
    println("Hello, Scala")
    "hahaha" //  new StringHandle("hahaha")
    // only this is
  }
  // careful
}
