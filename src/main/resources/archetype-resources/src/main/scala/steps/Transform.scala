package ${package}.steps

import ${package}.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait Transform {
  def transform()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame
}
