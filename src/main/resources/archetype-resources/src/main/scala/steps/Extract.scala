package ${package}.steps

import ${package}.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait Extract {
  def name: String
  def extract()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame
}
