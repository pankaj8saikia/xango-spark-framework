package ${package}.workflow.impl

import org.xango.spark.context.PipelineContext
import org.xango.spark.steps.ExtractTask
import org.apache.spark.sql.{DataFrame, SparkSession}

class SampleExtract extends ExtractTask {
  override val name: String = "sampleExtract"

  override def extract()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame = {
    import spark.implicits._

    val env = pipelineContext.pipelineParameters.env
    val processingDates = pipelineContext.currentBatchOption
      .map(_.dates)
      .getOrElse(List(pipelineContext.pipelineParameters.runDate.getOrElse("no-date-run")))

    val extractedData = processingDates.flatMap { processingDate =>
      Seq(
        ("spark", 1, env, processingDate.toString),
        ("scala", 2, env, processingDate.toString),
        ("etl", 3, env, processingDate.toString)
      )
    }.toDF("topic", "rank", "env", "runDate")

    pipelineContext.putDataFrame(name, extractedData)
  }
}
