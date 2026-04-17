package ${package}

import ${package}.controller.Controller
import ${package}.workflow.Workflow
import ${package}.workflow.impl.SampleWorkflow

object Main extends Controller {
  override val workflows: Map[String, Workflow] = Map(
    "sample-workflow" -> new SampleWorkflow
  )
}
