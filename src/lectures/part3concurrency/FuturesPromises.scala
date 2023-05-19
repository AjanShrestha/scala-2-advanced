package lectures.part3concurrency

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

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

  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0.dummy" -> "Dummy",
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )
    private val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to poke bill
  private val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  /*
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(ex) => ex.printStackTrace()
      }
    }
    case Failure(ex) => ex.printStackTrace()
  }
  */

  // functional composition of futures
  // map, flatMap, filter
  private val nameOnTheWall = mark.map(profile => profile.name)
  private val marksBestFriend = mark.flatMap(profile =>
    SocialNetwork.fetchBestFriend(profile))
  private val zucksBestFriendRestricted = marksBestFriend
    .filter(profile => profile.name startsWith ("Z"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  private val aProfileNoMatterWhat =
    SocialNetwork
      .fetchProfile("unknown id")
      .recover {
        case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
      }

  private val aFetchedProfileNoMatterWhat =
    SocialNetwork
      .fetchProfile("unknown id")
      .recoverWith {
        case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
      }

  private val fallbackResult = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.o-dummy"))
}
