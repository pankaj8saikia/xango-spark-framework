package ${package}.workflow.impl

import ${package}.context.PipelineContext
import ${package}.steps.Extract
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleReferenceExtract extends Extract {
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
