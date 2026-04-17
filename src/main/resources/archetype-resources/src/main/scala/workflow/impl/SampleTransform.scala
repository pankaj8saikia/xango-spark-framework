package ${package}.workflow.impl

import ${package}.context.PipelineContext
import ${package}.steps.Transform
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleTransform extends Transform {
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
