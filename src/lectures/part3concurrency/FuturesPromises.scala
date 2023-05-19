package lectures.part3concurrency

import scala.concurrent.Future
import scala.util.{Failure, Success}

// IMPORTANT for futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App {
  /*
   * Futures
   *  - functional way of computing something in parallel or another thread
   */

  private def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  private val aFuture = Future {
    calculateMeaningOfLife // calculates the meaning of life on ANOTHER thread
  } // (global) which is injected by the compiler

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")
  // aFuture.onComplete(t => t match {
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  } // called by SOME thread

  Thread.sleep(3000)
}
