package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

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

  // online banking app
  case class User(name: String)

  case class Transaction(
                          sender: String,
                          receiver: String,
                          amount: Double,
                          status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(
                           user: User,
                           merchantName: String,
                           amount: Double
                         ): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(
                  username: String,
                  item: String,
                  merchantName: String,
                  cost: Double): String = {
      // fetch the user from the DB
      // create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      // block the transaction status future
      Await.result(transactionStatusFuture, 2.seconds)
      // implicit conversions -> pimp my library
      // it will block call until all the waiting futures are completed
      // in case duration is timeout, it will throw an exception
    }
  }

  println(
    BankingApp
      .purchase(
        "Daniel",
        "iPhone 12",
        "rock the jvm store",
        3000
      )
  )

  // promises
  // futures are the functional way of composing
  // non-blocking computation which will return at some point
  // we can only read or manipulate the results from Futures
  // either by calling onComplete or better by using functional composition
  // futures are read-only when they are done
  // sometimes we need to specifically set or complete
  // a future at a point of our choosing
  private val promise = Promise[Int]() // "controller" over a future
  private val future = promise.future

  // Promise Pattern
  // one thread knows how to handle the future
  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // other thread inserts values/failures into the future
  // thread 2 - "producer"
  private val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    // promise.failure(...)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)
  // Producer-consumer doesn't have concurrency issues
  // The future promise paradigm is more powerful
  // It separates the concern of
  //  - reading/handling futures
  //  - writing a promise
  //  - provides when and how to set a value for future when you see fit
  //  - eliminates concurrency issues
}
