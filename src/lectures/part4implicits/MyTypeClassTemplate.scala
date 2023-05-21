package lectures.part4implicits

// TYPE CLASS
trait MyTypeClassTemplate[T] {
  def action(value: T): String
}

// Type class instances (often implicit)
// implicit object MyTypeClassInstance extends MyTypeClassTemplate[Int] {...}

// 1- Invoking type class instances

object MyTypeClassTemplate {
  def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] =
    instance
}

// 2- Enriching types with type classes
/*
implicit class ConversionClass[T](value: T) {
  def action(implicit instance: MyTypeClassTemplate[T]): String =
    instance.action(value)
}
*/
// 2.action
