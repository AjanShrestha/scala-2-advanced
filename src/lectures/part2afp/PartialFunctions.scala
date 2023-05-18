package lectures.part2afp

object PartialFunctions extends App {
  /*
  trait PartialFunction[-A, +B] extends (A => B) {
    def apply(x: A): B
    def isDefinedAt(x: A): Boolean
  }
  */

  private val aFunction = (x: Int) => x + 1 // Function[Int, Int] === Int => Int

  private val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  private class FunctionNotApplicableException extends RuntimeException

  private val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  // {1,2,5} => Int
  // aNicerFussyFunction is a partial Function from Int => Int

  private val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value

  println(aPartialFunction(2))
  // partial function is based on pattern matching
  // println(aPartialFunction(11))

  // PF utilities
  println(aPartialFunction.isDefinedAt(67))

  // lift
  private val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  // chain
  private val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal functions
  private val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  private val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  /*
    Note: PF can only have ONE parameter type
   */

  /*
    Exercises
    1.  construct a PF instance yourself (anonymous class)
    2.  dumb chat-bot as a PF
   */

  // scala.io.Source.stdin.getLines().foreach(line => println("you said: " + line))

  // Boilerplate
  private val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5
  }

  private val chatbot: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is HAL9000"
    case "goodbye" => "Once you start talking to me, there is no return, human!"
    case "call mom" => "Unable to find your phone without your credit card"
  }

  //  scala.io.Source.stdin.getLines().foreach(line =>
  //    println("chatbot says: " + chatbot(line)))
  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
