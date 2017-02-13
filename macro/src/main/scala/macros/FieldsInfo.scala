package macros

import scala.reflect.runtime.universe._

object FieldsInfo {

  def caseClassFields[T: TypeTag] = weakTypeOf[T].decls.collectFirst {
    case m: MethodSymbol if m.isPrimaryConstructor => m
  }.get.paramLists.head

  def caseClassFieldUnordered[T: TypeTag] = typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

}
