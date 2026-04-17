package org.xango.spark.context

import java.time.LocalDate

case class ProcessingBatch(
  batchNumber: Int,
  dates: List[LocalDate]
) {
  require(dates.nonEmpty, "ProcessingBatch requires at least one processing date")

  lazy val startDate: LocalDate = dates.head

  lazy val endDate: LocalDate = dates.last

  lazy val size: Int = dates.size
}
