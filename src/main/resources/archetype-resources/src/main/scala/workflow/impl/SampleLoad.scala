package ${package}.workflow.impl

import ${package}.context.PipelineContext
import ${package}.steps.Load
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleLoad extends Load {
  override def load(data: DataFrame)(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit = {
    val parameters = pipelineContext.pipelineParameters
    println(
      s"Loading workflow '$${parameters.workflowName}' for env '$${parameters.env}' and runDate '$${parameters.runDate}'"
    )
    pipelineContext.getDataFrame("transformed").show(truncate = false)
  }
}
