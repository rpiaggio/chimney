package io.scalaland.chimney.internal

sealed trait ChimneyDerivationError {
  def sourceTypeName: String
  def targetTypeName: String
}

case class MissingField(fieldName: String, fieldTypeName: String, sourceTypeName: String, targetTypeName: String)
    extends ChimneyDerivationError

case class MissingTransformer(fieldName: String,
                              sourceFieldTypeName: String,
                              targetFieldTypeName: String,
                              sourceTypeName: String,
                              targetTypeName: String)
    extends ChimneyDerivationError

case class NotSupportedDerivation(sourceTypeName: String, targetTypeName: String) extends ChimneyDerivationError

object ChimneyDerivationError {

  def printErrors(errors: Seq[ChimneyDerivationError]): String = {

    errors
      .groupBy(_.targetTypeName)
      .map {
        case (targetTypeName, errs) =>
          val errStrings = errs.map {
            case MissingField(fieldName, fieldTypeName, sourceTypeName, _) =>
              s"  $fieldName: $fieldTypeName - no field named $fieldName in source type $sourceTypeName"
            case MissingTransformer(fieldName, sourceFieldTypeName, targetFieldTypeName, sourceTypeName, _) =>
              s"  $fieldName: $targetFieldTypeName - can't derive transformation from $fieldName: $sourceFieldTypeName in source type $sourceTypeName"
            case NotSupportedDerivation(sourceTypeName, _) =>
              s"  derivation from $sourceTypeName is not supported in Chimney!"
          }

          s"""$targetTypeName
           |${errStrings.mkString("\n")}
           |""".stripMargin
      }
      .mkString("\n")
  }
}
