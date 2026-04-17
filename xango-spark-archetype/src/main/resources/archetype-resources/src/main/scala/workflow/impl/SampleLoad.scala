package ${package}.workflow.impl

import org.xango.spark.context.PipelineContext
import org.xango.spark.steps.LoadTask
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleLoad extends LoadTask {
  override def load(data: DataFrame)(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit = {
    val parameters = pipelineContext.pipelineParameters
    val batchMessage = pipelineContext.currentBatchOption match {
      case Some(batch) =>
        s"for batch $${batch.batchNumber} covering $${batch.startDate} to $${batch.endDate}"
      case None =>
        "without batching"
    }

    println(s"Loading workflow '$${parameters.workflowName}' for env '$${parameters.env}' $$batchMessage")
    pipelineContext.getDataFrame("transformed").show(truncate = false)
  }
}
