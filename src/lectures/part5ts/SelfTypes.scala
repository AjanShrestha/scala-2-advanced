package lectures.part5ts

object SelfTypes extends App {

  // requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist =>
    // SELF TYPE
    // whoever implements Singer must
    // implement Instrumentalist

    // rest of the implementation or API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  // illegal
  /*
  class Vocalist extends Singer {
    override def sing(): Unit = ???
  }
  */

  private val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  private class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }

  private val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // SELF TYPE compared with inheritance
  class A

  class B extends A // B IS AN A

  trait T

  trait S {
    self: T =>} // S REQUIRES A T

  // CAKE PATTERN => "dependency injection"

  // Classical DI
  class Component {
    // API
  }

  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent {
    self: ScalaComponent =>

    def dependentAction(x: Int): String = action(x) + "this rocks!"
  }

  // Example
  // layer 1 - small components
  trait Picture extends ScalaComponent

  trait Stats extends ScalaComponent

  trait ScalaApplication {
    self: ScalaDependentComponent =>}

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture

  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  /*
   * Difference between DI and Cake Pattern
   *  - DI framework or other piece of code takes care
   *    to verify or inject our values at runtime
   *  - Cake Pattern these dependencies are checked at
   *    COMPILE time
   */

  // cyclical dependencies
  // private class X extends Y
  // private class Y extends X

  trait X {
    self: Y => }

  trait Y {
    self: X => }
  // whoever implements X must implement Y and vice versa
}
