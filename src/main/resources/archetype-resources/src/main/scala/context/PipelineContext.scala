package ${package}.context

import org.apache.spark.sql.DataFrame

import scala.collection.mutable

class PipelineContext {
  private val dataFrames = mutable.LinkedHashMap.empty[String, DataFrame]
  private var parameters: Option[PipelineParameters] = None

  def setParameters(value: PipelineParameters): PipelineParameters = {
    parameters = Some(value)
    value
  }

  def pipelineParameters: PipelineParameters =
    parameters.getOrElse(
      throw new IllegalStateException("Pipeline parameters were not initialized")
    )

  def putDataFrame(key: String, dataFrame: DataFrame): DataFrame = {
    dataFrames.update(key, dataFrame)
    dataFrame
  }

  def getDataFrame(key: String): DataFrame =
    dataFrames.getOrElse(
      key,
      throw new NoSuchElementException(s"DataFrame '$key' was not found in the pipeline context")
    )

  def getDataFrameOption(key: String): Option[DataFrame] = dataFrames.get(key)

  def containsDataFrame(key: String): Boolean = dataFrames.contains(key)

  def allDataFrames: Map[String, DataFrame] = dataFrames.toMap
}
