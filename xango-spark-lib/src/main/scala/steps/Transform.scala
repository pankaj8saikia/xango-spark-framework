package org.xango.spark.steps

import org.xango.spark.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait TransformTask {
  def transform()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame
}
