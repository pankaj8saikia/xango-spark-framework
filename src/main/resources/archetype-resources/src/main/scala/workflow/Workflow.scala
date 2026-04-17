package ${package}.workflow

import ${package}.context.PipelineContext
import ${package}.steps.{Extract, Load, Transform}
import org.apache.spark.sql.SparkSession

trait Workflow {

  def name: String

  def extracts: List[Extract]

  def transform: Transform

  def load: Load

  def run()(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit = {
    extracts.foreach(_.extract())
    val transformedData = transform.transform()
    pipelineContext.putDataFrame("final", transformedData)
    load.load(transformedData)
  }
}
