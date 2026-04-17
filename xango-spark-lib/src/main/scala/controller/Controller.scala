package org.xango.spark.controller

import org.xango.spark.context.{PipelineContext, PipelineParameters}
import org.xango.spark.workflow.WorkflowOrchestrator
import org.apache.spark.sql.SparkSession

trait Controller extends App {

  implicit lazy val spark: SparkSession =
    configureSparkSession(
      SparkSession.builder()
        .appName(applicationName)
        .master(master)
    ).getOrCreate()

  def applicationName: String = "xango-spark-job"

  def master: String = "local[*]"

  def sparkConfigurations: Map[String, String] = Map.empty

  def workflows: Map[String, WorkflowOrchestrator]

  implicit lazy val pipelineContext: PipelineContext = new PipelineContext

  private lazy val availableWorkflowNames: String =
    workflows.keys.toList.sorted.mkString(", ")

  private lazy val pipelineParameters: PipelineParameters =
    pipelineContext.setParameters(parseArguments(args.toList))

  private lazy val selectedWorkflowName: String = pipelineParameters.workflowName

  private lazy val selectedWorkflow: WorkflowOrchestrator =
    workflows.getOrElse(
      selectedWorkflowName,
      throw new IllegalArgumentException(
        s"Unknown workflow '$$selectedWorkflowName'. Available workflows: $$availableWorkflowNames"
      )
    )

  try {
    selectedWorkflow.run()
  } finally {
    spark.stop()
  }

  private def configureSparkSession(builder: SparkSession.Builder): SparkSession.Builder =
    sparkConfigurations.foldLeft(builder) { case (configuredBuilder, (key, value)) =>
      configuredBuilder.config(key, value)
    }

  private def parseArguments(arguments: List[String]): PipelineParameters = {
    val parsedArguments = arguments.map { argument =>
      argument.split("=", 2).toList match {
        case key :: value :: Nil if key.nonEmpty && value.nonEmpty => key -> value
        case _ =>
          throw new IllegalArgumentException(
            "Arguments must be passed as key=value pairs, for example: " +
              "workflowName=sample-workflow env=dev runDate=2026-04-17"
          )
      }
    }.toMap

    val workflowName = parsedArguments.getOrElse(
      "workflowName",
      throw new IllegalArgumentException(
        s"workflowName is required. Available workflows: $$availableWorkflowNames"
      )
    )

    val env = parsedArguments.getOrElse(
      "env",
      throw new IllegalArgumentException("env is required")
    )

    PipelineParameters(
      workflowName = workflowName,
      env = env,
      runDate = parsedArguments.get("runDate"),
      extra = parsedArguments -- Set("workflowName", "env", "runDate")
    )
  }
}
