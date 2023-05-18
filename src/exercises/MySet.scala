package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
    EXERCISE
      implement a functional set
   */

  def apply(elem: A): Boolean =
    contains(elem)

  def contains(elem: A): Boolean

  // add
  def +(elem: A): MySet[A]

  // union
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  /*
    EXERCISE #2
      - removing an element
      - intersection with another set
      - difference with another set
   */

  def -(elem: A): MySet[A]

  // intersection
  def &(anotherSet: MySet[A]): MySet[A]

  // difference
  def --(anotherSet: MySet[A]): MySet[A]

  // EXERCISE #3 - implement a unary_! = NEGATION of a set
  // set[1,2,3] =>
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  // part 2
  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

   override def unary_! : MySet[A] = new AllInclusiveSet[A]
}

/*
class AllInclusiveSet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = true

  override def +(elem: A): MySet[A] = this

  override def ++(anotherSet: MySet[A]): MySet[A] = this

  // naturals = AllInclusiveSet[Int] // all the natural numbers
  // naturals.map(x => x % 3) => ???
  // [0 1 2]
  override def map[B](f: A => B): MySet[B] = ???

  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

  override def filter(predicate: A => Boolean): MySet[A] = ??? // property-based set

  override def foreach(f: A => Unit): Unit = ???

  override def -(elem: A): MySet[A] = ???

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def unary_! : MySet[A] = new EmptySet[A]
}
*/
// ??? over few of the properties

// denotes all the elements of Type A that satisfy property
// class PropertyBasedSet[A](property: A => Boolean) extends MySet[A]

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
    [1 2 3] ++ [4 5]
      = [2 3] ++ [4 5] + 1
      = [3] ++ [4 5] + 1 + 2
      = [] ++ [4 5] + 1 + 2 + 3
      = [4 5] + 1 + 2 + 3
      = [4 5 1 2 3]
   */
  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] =
    (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  // part 2
  override def -(elem: A): MySet[A] =
  // this filter (_ == elem)
    if (head == elem) tail
    else tail - elem + head

  override def &(anotherSet: MySet[A]): MySet[A] =
  // filter(x => anotherSet.contains(x))
  // filter(x => anotherSet(x))
    filter(anotherSet)
  // intersection = filtering!

  override def --(anotherSet: MySet[A]): MySet[A] =
  // filter(x => !anotherSet.contains(x))
  // filter(x => !anotherSet(x))
    filter(!anotherSet)

  def unary_! : MySet[A] = ???
}

object MySet {
  /*
    val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
      = buildSet(seq(2,3), [] + 1)
      = buildSet(seq(3), [1] + 2)
      = buildSet(seq(), [1, 2] + 3)
      = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x =>
    MySet(x, x * 10)) filter (_ % 2 == 0) foreach println
}