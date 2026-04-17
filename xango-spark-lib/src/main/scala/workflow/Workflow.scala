package org.xango.spark.workflow

import org.xango.spark.context.{BatchPlanner, PipelineContext, ProcessedDateLog}
import org.xango.spark.steps.{ExtractTask, LoadTask, TransformTask}
import org.apache.spark.sql.SparkSession

trait WorkflowOrchestrator {

  def workflowName: String

  def extractTasks: List[ExtractTask]

  def transformTask: TransformTask

  def loadTask: LoadTask

  def run()(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit = {
    val parameters = pipelineContext.pipelineParameters

    if (parameters.batchingEnabled) {
      val pendingBatches = BatchPlanner.pendingBatches(parameters)

      if (pendingBatches.isEmpty) {
        val requestedStart = parameters.processStartDate.map(_.toString).getOrElse("N/A")
        val requestedEnd = parameters.processEndDate.map(_.toString).getOrElse("N/A")
        println(
          s"No pending dates for workflow '$$workflowName'. " +
            s"Requested range: $$requestedStart to $$requestedEnd. " +
            s"Processed log: $${parameters.processedLogPath}"
        )
      } else {
        pendingBatches.foreach { batch =>
          pipelineContext.clearDataFrames()
          pipelineContext.setCurrentBatch(batch)

          println(
            s"Starting batch $${batch.batchNumber} for workflow '$$workflowName' " +
              s"covering $${batch.startDate} to $${batch.endDate} ($${batch.size} day(s))"
          )

          runTasks()
          ProcessedDateLog.append(parameters.processedLogPath, batch.dates)

          println(
            s"Completed batch $${batch.batchNumber} for workflow '$$workflowName'. " +
              s"Logged $${batch.size} processed day(s) to $${parameters.processedLogPath}"
          )
        }

        pipelineContext.clearCurrentBatch()
        pipelineContext.clearDataFrames()
      }
    } else {
      println(s"Running workflow '$$workflowName' without date batching")
      pipelineContext.clearCurrentBatch()
      pipelineContext.clearDataFrames()
      runTasks()
      pipelineContext.clearDataFrames()
    }
  }

  private def runTasks()(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit = {
    extractTasks.foreach(_.extract())
    val transformedData = transformTask.transform()
    pipelineContext.putDataFrame("final", transformedData)
    loadTask.load(transformedData)
  }
}
