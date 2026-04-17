package org.xango.spark.context

import java.time.LocalDate

object BatchPlanner {

  def pendingBatches(parameters: PipelineParameters): List[ProcessingBatch] = {
    parameters.validateBatchConfiguration()

    val requestedDates = dateRange(
      parameters.processStartDate.get,
      parameters.processEndDate.get
    )
    val processedDates = ProcessedDateLog.read(parameters.processedLogPath)
    val pendingDates = requestedDates.filterNot(processedDates.contains)

    pendingDates.grouped(parameters.batchSizeDays).zipWithIndex.map { case (dates, index) =>
      ProcessingBatch(
        batchNumber = index + 1,
        dates = dates
      )
    }.toList
  }

  private def dateRange(startDate: LocalDate, endDate: LocalDate): List[LocalDate] =
    Iterator.iterate(startDate)(_.plusDays(1))
      .takeWhile(!_.isAfter(endDate))
      .toList
}
