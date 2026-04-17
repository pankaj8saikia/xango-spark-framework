package ${package}.controller

import ${package}.context.{PipelineContext, PipelineParameters}
import ${package}.workflow.Workflow
import org.apache.spark.sql.SparkSession

trait Controller extends App {

  implicit lazy val spark: SparkSession = SparkSession.builder()
    .appName(applicationName)
    .master(master)
    .getOrCreate()

  def applicationName: String = "${artifactId}"

  def master: String = "local[*]"

  def workflows: Map[String, Workflow]

  implicit lazy val pipelineContext: PipelineContext = new PipelineContext

  private lazy val availableWorkflowNames: String =
    workflows.keys.toList.sorted.mkString(", ")

  private lazy val pipelineParameters: PipelineParameters =
    pipelineContext.setParameters(parseArguments(args.toList))

  private lazy val selectedWorkflowName: String = pipelineParameters.workflowName

  private lazy val selectedWorkflow: Workflow =
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

    val runDate = parsedArguments.getOrElse(
      "runDate",
      throw new IllegalArgumentException("runDate is required")
    )

    PipelineParameters(
      workflowName = workflowName,
      env = env,
      runDate = runDate,
      extra = parsedArguments -- Set("workflowName", "env", "runDate")
    )
  }
}
