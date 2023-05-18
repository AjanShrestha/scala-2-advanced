package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  private val superAdder: Int => Int => Int =
    x => y => x + y

  private val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  // METHOD!
  private def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  // METHOD to Function value => lifting
  private val add4: Int => Int = curriedAdder(4)

  // lifting = ETA-EXPANSION
  // functions != methods (JVM limitation)
  private def inc(x: Int): Int = x + 1

  List(1, 2, 3).map(inc) // ETA-expansion
  // List(1, 2, 3).map(x => inc(x))

  // Manually trigger ETA-expansions
  // Partial function applications
  private val add5 = curriedAdder(5) _ // Int => Int ; compiler do the ETA-expansions

  // EXERCISE
  private val simpleAddFunction = (x: Int, y: Int) => x + y

  private def simpleAddMethod(x: Int, y: Int): Int = x + y

  private def curriedAddMethod(x: Int)(y: Int): Int = x + y

  // define add7: Int => Int = y => 7 + y
  // as many different implementations of add7 using the above
  // be creative!
  private val add7Function = (y: Int) => simpleAddFunction(7, y) // simplest
  // private val add7Method: Int => Int = (y: Int) => simpleAddMethod(7, y)
  // private val add7Curried: Int => Int = curriedAddMethod(7)

  private val add7_2 = simpleAddFunction.curried(7)

  private val add7_3 = curriedAddMethod(7) _ // PAF
  private val add7_4 = curriedAddMethod(7)(_) // PAF = alternative syntax

  private val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax to turning methods into function values
  // y => simpleAddMethod(7, y)
  private val add7_6 = simpleAddFunction(7, _: Int) // works as well

  // underscore are powerful
  private def concatenator(a: String, b: String, c: String): String = a + b + c

  private val insertName = concatenator("Hello, I'm ", _: String, ", how are you?")
  // x: String => concatenator(hello, x, howareyou)
  println(insertName("Daniel"))

  private val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
  // (x, y) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Daniel", " Scala is awesome!"))

  // EXERCISES
  /*
    1.  Process a list of numbers and return their string representations with different formats
        Use the %4.2f, %8.6f and %14.12f with a curried formatter function.
   */
  // println("%8.6f".format(Math.PI))
  private def curriedFormatter(s: String)(number: Double): String = s.format(number)

  private val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  private val simpleFormat = curriedFormatter("%4.2f") _
  private val seriousFormat = curriedFormatter("%8.6f") _
  private val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%14.2f"))) // compiler does sweet eta-expansion

  /*
    2.  difference between
        - functions vs methods
        - parameters: by-name vs 0-lambda
   */
  private def byName(n: => Int): Int = n + 1

  private def byFunction(f: () => Int): Int = f() + 1

  private def method: Int = 42

  private def parenMethod(): Int = 42

  /*
    calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
   */

  byName(23) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware ==> byName(parenMethod())
  // parenMethod() is called
  // confusion: byName param as HOF and expect func to use func inside
  // rather it is called and returns value
  // byName(() => 42) // not ok; byName arg value type is not same as func type
  byName((() => 42)()) // ok => provides value
  // byName(parenMethod _) // not ok - provides func value

  // byFunction(45) // not ok
  // byFunction(method) // not ok!!!!!! does not do ETA-Expansion!
  // parameterless methods - accessors without any paren
  // proper methods with paren are different
  byFunction(parenMethod) // compiler does ETA-expansion
  byFunction(() => 45)
  byFunction(parenMethod _) // also works
}
