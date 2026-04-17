package org.xango.spark.steps

import org.xango.spark.context.PipelineContext
import org.apache.spark.sql.{DataFrame, SparkSession}

trait ExtractTask {
  def name: String
  def extract()(implicit spark: SparkSession, pipelineContext: PipelineContext): DataFrame
}
