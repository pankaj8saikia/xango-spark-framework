package ${package}.workflow.impl

import org.xango.spark.context.PipelineContext
import org.xango.spark.steps.ExtractTask
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleReferenceExtract extends ExtractTask {
  override val name: String = "sampleReferenceExtract"

  override def extract()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame = {
    import spark.implicits._

    val referenceData = Seq(
      ("spark", "framework"),
      ("scala", "language"),
      ("etl", "pattern")
    ).toDF("topic", "category")

    pipelineContext.putDataFrame(name, referenceData)
  }
}
