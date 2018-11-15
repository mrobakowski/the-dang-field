package dang.field

import org.scalatest.{FlatSpec, Matchers}

class TheDangFieldTest extends FlatSpec with Matchers {
  case class Foo(private val bar: Bar)
  case class Bar(private val baz: Baz)
  case class Baz(private val i: Int)

  "the dang field" should "be easily accessible, even though it's private" in {
    val x = Foo(Bar(Baz(42)))
    val field = TheDangField.bar.baz.i
    val i: Int = field.get(x)

    assert(i == 42)
  }

  it should "be also accessible using DSL" in {
    import TheDangFieldImplicits._
    val x = Foo(Bar(Baz(420)))
    val i: Int = x getMe TheDangField.bar.baz.i

    assert(i == 420)
  }

  it should "be able to be changed, even though it's private" in {
    val x = Foo(Bar(Baz(42)))
    val field = TheDangField.bar.baz.i
    field.set(x, 69)

    assert(field.get[Int](x) == 69)
  }

  it should "also be able to be changed using DSL" in {
    import TheDangFieldImplicits._
    val x = Foo(Bar(Baz(42)))
    x set TheDangField.bar.baz.i to 69

    assert((x getMe[Int] TheDangField.bar.baz.i) == 69)
  }

  class GenericBaseClass[T](private val t: T)
  class Child(t: Baz) extends GenericBaseClass[Baz](t)

  it should "correctly navigate through class hierarchies" in {
    import TheDangFieldImplicits._
    val x = new Child(Baz(420))
    val i: Int = x getMe TheDangField.t.i

    assert(i == 420)
  }

  it should "correctly navigate through generics" in {
    import TheDangFieldImplicits._
    val x = new GenericBaseClass(Baz(420))

    val baz: Baz = x getMe TheDangField.t
    assert(baz == Baz(420))

    val i: Int = x getMe TheDangField.t.i
    assert(i == 420)
  }
}
