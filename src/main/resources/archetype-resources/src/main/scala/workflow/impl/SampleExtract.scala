package ${package}.workflow.impl

import ${package}.context.PipelineContext
import ${package}.steps.Extract
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleExtract extends Extract {
  override val name: String = "sampleExtract"

  override def extract()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame = {
    import spark.implicits._

    val env = pipelineContext.pipelineParameters.env
    val runDate = pipelineContext.pipelineParameters.runDate

    val extractedData = Seq(
      ("spark", 1, env, runDate),
      ("scala", 2, env, runDate),
      ("etl", 3, env, runDate)
    ).toDF("topic", "rank", "env", "runDate")

    pipelineContext.putDataFrame(name, extractedData)
  }
}
