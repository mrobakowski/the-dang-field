package io.github.mrobakowski.thedangfield

import scala.util.Try
import java.lang.reflect.{Field, ParameterizedType}
import scala.language.dynamics

class TheDangField(val fieldList: List[String]) extends Dynamic {
  def selectDynamic(fieldName: String): TheDangField = {
    new TheDangField(fieldName :: fieldList)
  }

  private def getField(x: AnyRef, fieldName: String): Field = {
    var field: Field = null
    var currentClass = x.getClass
    while (field == null && currentClass != null) {
      field = Try(currentClass.getDeclaredField(fieldName)).getOrElse(null)

      currentClass = currentClass.getGenericSuperclass match {
        case clazz: Class[_] => clazz.asInstanceOf[Class[_ <: AnyRef]]
        case parametrized: ParameterizedType => parametrized.getRawType.asInstanceOf[Class[_ <: AnyRef]]
      }
    }

    field
  }

  def getObject(x: AnyRef): AnyRef = {
    fieldList match {
      case Nil =>
        x
      case head :: tail =>
        val obj = new TheDangField(tail).getObject(x)
        val field = getField(obj, head)

        val originalAccessible = field.isAccessible
        field.setAccessible(true)
        val res = field.get(obj)
        field.setAccessible(originalAccessible)

        res
    }
  }

  def set(x: AnyRef, v: Any): Unit = {
    fieldList match {
      case Nil =>
        throw new RuntimeException("cannot set nothing!")
      case head :: tail =>
        val obj = new TheDangField(tail).getObject(x)
        val field = getField(obj, head)

        val originalAccessible = field.isAccessible
        field.setAccessible(true)
        field.set(obj, v)
        field.setAccessible(originalAccessible)
    }
  }

  def get[T](x: AnyRef): T = {
    getObject(x).asInstanceOf[T]
  }
}

object TheDangField extends TheDangField(List())

object TheDangFieldImplicits {
  implicit class FrigginObject[O <: AnyRef](x: O) {
    def getMe[T](dangField: TheDangField): T = dangField.get[T](x)
    def set(theDangField: TheDangField) = DangFieldSetter(x, theDangField)
  }

  case class DangFieldSetter(obj: AnyRef, field: TheDangField) {
    def to(value: Any): Unit = {
      field.set(obj, value)
    }
  }
}