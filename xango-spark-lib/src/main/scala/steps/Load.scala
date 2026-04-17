package org.xango.spark.steps

import org.xango.spark.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait LoadTask {
  def load(data: DataFrame)(implicit spark: SparkSession, pipelineContext: PipelineContext): Unit
}
