package ${package}.workflow.impl

import org.xango.spark.steps.ExtractTask
import org.xango.spark.workflow.WorkflowOrchestrator

class SampleWorkflow extends WorkflowOrchestrator {
  override val workflowName: String = "sample-workflow"

  override val extractTasks: List[ExtractTask] = List(
    new SampleExtract,
    new SampleReferenceExtract
  )

  override val transformTask: SampleTransform = new SampleTransform

  override val loadTask: SampleLoad = new SampleLoad
}
