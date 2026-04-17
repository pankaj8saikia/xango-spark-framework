package ${package}.context

case class PipelineParameters(
  workflowName: String,
  env: String,
  runDate: String,
  extra: Map[String, String]
) {
  def get(key: String): Option[String] =
    key match {
      case "workflowName" => Some(workflowName)
      case "env" => Some(env)
      case "runDate" => Some(runDate)
      case other => extra.get(other)
    }

  def require(key: String): String =
    get(key).getOrElse(
      throw new IllegalArgumentException(s"Required pipeline parameter '$key' was not provided")
    )

  def asMap: Map[String, String] =
    Map(
      "workflowName" -> workflowName,
      "env" -> env,
      "runDate" -> runDate
    ) ++ extra
}
