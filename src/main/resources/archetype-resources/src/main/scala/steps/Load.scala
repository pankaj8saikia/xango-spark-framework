package ${package}.steps

import ${package}.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait Load {
  def load(data: DataFrame)(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit
}
