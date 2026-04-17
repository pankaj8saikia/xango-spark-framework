package org.xango.spark.context

import java.time.LocalDate
import java.time.format.DateTimeParseException

case class PipelineParameters(
  workflowName: String,
  env: String,
  runDate: Option[String],
  extra: Map[String, String]
) {
  lazy val parsedRunDate: Option[LocalDate] = runDate.map(parseDate("runDate", _))

  lazy val batchingEnabled: Boolean =
    extra.get("enableBatching")
      .map(parseBoolean("enableBatching", _))
      .getOrElse(hasBatchingInputs)

  lazy val processStartDate: Option[LocalDate] =
    extra.get("processStartDate")
      .map(parseDate("processStartDate", _))
      .orElse(parsedRunDate)

  lazy val processEndDate: Option[LocalDate] =
    extra.get("processEndDate")
      .map(parseDate("processEndDate", _))
      .orElse(parsedRunDate)

  lazy val batchSizeDays: Int =
    extra.get("batchSizeDays")
      .map(parsePositiveInt("batchSizeDays", _))
      .getOrElse(30)

  lazy val processedLogPath: String =
    extra.getOrElse(
      "processedLogPath",
      s"pipeline-progress/$$workflowName/$$env-processed-dates.log"
    )

  def get(key: String): Option[String] =
    key match {
      case "workflowName" => Some(workflowName)
      case "env" => Some(env)
      case "runDate" => runDate
      case "enableBatching" => Some(batchingEnabled.toString)
      case "processStartDate" => processStartDate.map(_.toString)
      case "processEndDate" => processEndDate.map(_.toString)
      case "batchSizeDays" => Some(batchSizeDays.toString)
      case "processedLogPath" => Some(processedLogPath)
      case other => extra.get(other)
    }

  def require(key: String): String =
    get(key).getOrElse(
      throw new IllegalArgumentException(s"Required pipeline parameter '$key' was not provided")
    )

  def asMap: Map[String, String] =
    Map(
      "workflowName" -> workflowName,
      "env" -> env,
      "enableBatching" -> batchingEnabled.toString,
      "batchSizeDays" -> batchSizeDays.toString,
      "processedLogPath" -> processedLogPath
    ) ++ runDate.map("runDate" -> _).toMap ++
      processStartDate.map("processStartDate" -> _.toString).toMap ++
      processEndDate.map("processEndDate" -> _.toString).toMap ++
      extra

  def validateBatchConfiguration(): Unit =
    if (batchingEnabled) {
      val startDate = processStartDate.getOrElse(
        throw new IllegalArgumentException(
          "Batching is enabled but no processStartDate was provided. " +
            "Provide processStartDate or runDate."
        )
      )
      val endDate = processEndDate.getOrElse(
        throw new IllegalArgumentException(
          "Batching is enabled but no processEndDate was provided. " +
            "Provide processEndDate or runDate."
        )
      )

      if (startDate.isAfter(endDate)) {
        throw new IllegalArgumentException(
          s"processStartDate '$$startDate' must be on or before processEndDate '$$endDate'"
        )
      }
    }

  private def parseDate(name: String, value: String): LocalDate =
    try {
      LocalDate.parse(value)
    } catch {
      case _: DateTimeParseException =>
        throw new IllegalArgumentException(
          s"$$name must be an ISO date in yyyy-MM-dd format, but was '$$value'"
        )
    }

  private def parseBoolean(name: String, value: String): Boolean =
    value.trim.toLowerCase match {
      case "true" => true
      case "false" => false
      case _ =>
        throw new IllegalArgumentException(s"$$name must be true or false, but was '$$value'")
    }

  private def parsePositiveInt(name: String, value: String): Int =
    try {
      val parsed = value.toInt
      if (parsed <= 0) {
        throw new IllegalArgumentException(s"$$name must be greater than 0, but was '$$value'")
      }
      parsed
    } catch {
      case _: NumberFormatException =>
        throw new IllegalArgumentException(s"$$name must be an integer, but was '$$value'")
    }

  private def hasBatchingInputs: Boolean =
    Set("processStartDate", "processEndDate", "batchSizeDays", "processedLogPath")
      .exists(extra.contains)
}
