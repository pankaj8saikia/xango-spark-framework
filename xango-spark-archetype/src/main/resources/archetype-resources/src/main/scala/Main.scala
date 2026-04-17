package ${package}

import org.xango.spark.controller.Controller
import org.xango.spark.workflow.WorkflowOrchestrator
import ${package}.workflow.impl.SampleWorkflow

object Main extends Controller {
  override val sparkConfigurations: Map[String, String] = Map(
    // Define Spark configs for your pipeline here.
    // "spark.sql.shuffle.partitions" -> "200"
  )

  override val workflows: Map[String, WorkflowOrchestrator] = Map(
    "sample-workflow" -> new SampleWorkflow
  )
}
