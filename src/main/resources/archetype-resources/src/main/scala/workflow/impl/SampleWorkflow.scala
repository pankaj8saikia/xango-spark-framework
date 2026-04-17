package ${package}.workflow.impl

import ${package}.steps.Extract
import ${package}.workflow.Workflow

class SampleWorkflow extends Workflow {
  override val name: String = "sample-workflow"

  override val extracts: List[Extract] = List(
    new SampleExtract,
    new SampleReferenceExtract
  )

  override val transform: SampleTransform = new SampleTransform

  override val load: SampleLoad = new SampleLoad
}
