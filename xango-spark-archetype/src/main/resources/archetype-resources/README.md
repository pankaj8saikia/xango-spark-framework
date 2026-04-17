# ${artifactId}

Spark application generated from the `xango-spark-archetype`.

## Pattern

- The generated project contains only your application entry point and implementation classes.
- The shared workflow framework comes from the published `xango-spark-lib` dependency in `pom.xml`.
- `Main` extends `org.xango.spark.controller.Controller` and registers the workflows you want to expose.
- `WorkflowOrchestrator` runs `extract -> transform -> load` through `extractTasks`, `transformTask`, and `loadTask`.
- Batching is optional. Date-based pipelines can enable batch runs with processed-date logging, while non-date pipelines can run once end to end.
- Use `pipelineContext.currentBatchOption` inside your impl classes when logic depends on the current batch window.

## Project structure

```text
.
├── pom.xml
└── src/main/scala
    ├── Main.scala
    └── workflow/impl
```

Users typically edit `src/main/scala/Main.scala` and the classes in `src/main/scala/workflow/impl`. The framework plumbing is supplied by the library dependency instead of being generated into the project.

## Build

```bash
mvn clean package
```

## Spark configuration

Use `Main.scala` when you want to define Spark settings for the whole pipeline:

```scala
override val sparkConfigurations: Map[String, String] = Map(
  "spark.sql.shuffle.partitions" -> "200",
  "spark.sql.session.timeZone" -> "UTC"
)
```

These entries are applied to the `SparkSession.builder()` before the session is created.

## Create a deployable jar

```bash
mvn clean package assembly:single
```

## Run with spark-submit

```bash
spark-submit \
  --class ${mainClass} \
  target/${artifactId}-${version}-jar-with-dependencies.jar \
  workflowName=sample-workflow \
  env=dev \
  runDate=2026-04-17
```

## Run with extra parameters

```bash
spark-submit \
  --class ${mainClass} \
  target/${artifactId}-${version}-jar-with-dependencies.jar \
  workflowName=sample-workflow \
  env=prod \
  runDate=2026-04-17 \
  region=apac
```

## Run in date batches

Use `enableBatching=true` with `processStartDate`, `processEndDate`, and `batchSizeDays` when you want to process a large range in smaller windows:

```bash
spark-submit \
  --class ${mainClass} \
  target/${artifactId}-${version}-jar-with-dependencies.jar \
  workflowName=sample-workflow \
  env=prod \
  enableBatching=true \
  runDate=2026-04-17 \
  processStartDate=2025-04-18 \
  processEndDate=2026-04-17 \
  batchSizeDays=30 \
  processedLogPath=state/sample-workflow-prod.log
```

In that example, the workflow processes the requested year in 30-day batches. After each successful batch, the processed dates are appended to the log file. If the job fails and you rerun it with the same arguments, already logged dates are skipped automatically.

## Run without dates

If a pipeline does not process date-based data, leave batching disabled and run it without any date range arguments:

```bash
spark-submit \
  --class ${mainClass} \
  target/${artifactId}-${version}-jar-with-dependencies.jar \
  workflowName=full-load-workflow \
  env=prod \
  sourceSystem=crm
```

In that mode, the workflow runs once from start to finish and does not create or consult a processed-date log.

## Library dependency

The generated `pom.xml` imports the shared framework from Maven Central:

```xml
<dependency>
  <groupId>${xangoSparkLibraryGroupId}</groupId>
  <artifactId>${xangoSparkLibraryArtifactId}</artifactId>
  <version>${xangoSparkLibraryVersion}</version>
</dependency>
```

Override those coordinates during archetype generation if you want to target a different published version of the library.

## Sharing DataFrames

- Store a DataFrame in any task with `pipelineContext.putDataFrame("orders", ordersDf)`.
- Read it later in another task with `pipelineContext.getDataFrame("orders")`.
- The workflow orchestrator stores the transformed output under the `final` key before calling `loadTask`.

## Runtime parameters

- Access core parameters with `pipelineContext.pipelineParameters.workflowName` and `env`.
- `runDate` is optional and can still be used by date-aware pipelines.
- Pass extra parameters as additional `key=value` arguments and read them with `pipelineContext.pipelineParameters.get("key")`.
- Optional batching parameters:
  - `enableBatching` defaults to `false`, and also turns on automatically if any batching-specific arguments are provided
  - `processStartDate` defaults to `runDate` when batching is enabled
  - `processEndDate` defaults to `runDate` when batching is enabled
  - `batchSizeDays` defaults to `30`
  - `processedLogPath` defaults to `pipeline-progress/<workflowName>/<env>-processed-dates.log`
- Arguments are passed to the Spark application after the jar path in `spark-submit`.
