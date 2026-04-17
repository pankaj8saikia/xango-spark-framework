package ${package}.workflow.impl

import org.xango.spark.context.PipelineContext
import org.xango.spark.steps.TransformTask
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleTransform extends TransformTask {
  override def transform()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame = {
    val sourceData = pipelineContext
      .getDataFrame("sampleExtract")
    val referenceData = pipelineContext
      .getDataFrame("sampleReferenceExtract")

    val transformedData = sourceData
      .join(referenceData, Seq("topic"), "left")
      .orderBy("rank")

    pipelineContext.putDataFrame("transformed", transformedData)
  }
}
