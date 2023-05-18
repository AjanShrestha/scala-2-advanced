package lectures.part2afp

/*
 * Monads are an abstract type
 * - kind of types which have some fundamental ops
 *
 * trait MonadTemplate[A] {
 *  // apply(in Scala) or pure
 *  // constructs a Monad out of a value or many values
 *  def unit(value: A): MonadTemplate[A]
 *  // bind in other language
 *  // transforms a Monad of certain type to Monad of another type
 *  def flatMap[B](f: A => MonadTemplate[B]): MonadTemplate[B]
 * }
 *
 * Monads in Scala
 *  - List
 *  - Option
 *  - Try
 *  - Future
 *  - Steam
 *  - Set, etc
 *
 * Operations must satisfy the monad laws:
 *  1. left-identity    unit(x).flatMap(f) == f(x)
 *  2. right-identity   aMonadInstance.flatMap(unit) == aMonadInstance
 *  3. associativity    m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
 *
 * Example: List
 *
 * Left Identity:
 * List(x).flatMap(f)
 * = f(x) ++ Nil.flatMap(f)
 * = f(x)
 *
 * Right Identity
 * list.flatMap(x => List(x))
 * list
 *
 * Associativity
 * [a b c].flatMap(f).flatMap(g)
 * = (f(a) ++ f(b) ++ f(c)).flatMap(g)
 * = f(a).flatMap(g) ++ f(b).flatMap(g) ++ f(c).flatMap(g)
 * = [a b c].flatMap(f(_).flatMap(g))
 * = [a b c].flatMap(x => f(x).flatMap(g))
 *
 * Another Example: Option
 *
 * Left Identity
 * Option(x).flatMap(f) = f(x)
 * Some(x).flatMap(f) = f(x)
 *
 * Right Identity
 * opt.flatMap(x => Option(x)) = opt
 * Some(v).flatMap(x => Option(x))
 * = Option(v)
 * = Some(v)
 *
 * Associativity
 * o.flatMap(f).flatMap(g) = o.flatMap(x => f(x).flatMap(g))
 * Some(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
 * Some(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
 */

object Monads extends App {

  // our own Try monad

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  private object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
    left-identity

    unit.flatMap(f) = f(x)
    Attempt(x).flatMap(f) = f(x) // Success case!
    Success(x).flatMap(f) = f(x) // proved.

    right-identity

    attempt.flatMap(unit) = attempt
    Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
    Fail(_).flatMap(...) = Fail(e)

    associativity

    attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
    Fail(e).flatMap(f).flatMap(g) = Fail(e)
    Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

    Success(v).flatMap(f).flatMap(g)
     = f(v).flatMap(g) OR Fail(e)

    Success(v).flatMap(x => f(x).flatMap(g))
     = f(v).flatMap(g) OR Fail(e)
   */

  private val attempt = Attempt {
    throw new RuntimeException("My own monad, yes!")
  }
  println(attempt)

  /*
    EXERCISE:
    1. implement a Lazy[T] monad
        = computation which will only be executed when it's needed.
          unit/apply
          flatMap

    2.  Monads = unit + flatMap
        // Alternative definition
        Monads = unit + map + flatten

        Monad[T] {
          def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

          def map[B](f: T => B): Monad[B] = ???
          def flatten(m: Monad[Monad[T]]): Monad[T] = ???
        }

        (have List in mind)
   */

  // 1 - Lazy monad
  private class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value

    def use: A = internalValue

    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
  }

  private object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value) // unit
  }

  private val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    42
  }
  private val flatMappedInstance =
    lazyInstance.flatMap(x => Lazy {
      10 * x
    })

  private val flatMappedInstance2 =
    lazyInstance.flatMap(x => Lazy {
      10 * x
    })
  flatMappedInstance.use
  flatMappedInstance2.use

  /*
    left-identity
    unit.flatMap(v) = f(v)
    Lazy(v).flatMap(f) = f(v)

    right-identity
    l.flatMap(unit) = l
    Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

    associativity
    l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
    Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
    Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */

  // 2: map and flatten in terms of flatMap
  /*
    Monad[T] { // List
      def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

      def map[B](f: T => B): Monad[B] =
        flatMap(x => unit(f(x))) // Monad[B]
      def flatten(m: Monad[Monad[T]]): Monad[T] =
        m.flatMap(x: Monad[T] => x)

      List(1,2,3).map(_ * 2) =
        List(1,2,3).flatMap(x => List(x * 2))
      List(List(1,2), List(3,4)).flatten =
        List(List(1,2), List(3,4)).flatMap(x => x)
        = List(1,2,3,4)
    }
   */
}
