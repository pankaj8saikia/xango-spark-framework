# ${artifactId}

Spark application generated from the `spark-archetype`.

## Pattern

- `Main` is the entry point and extends the `Controller` trait.
- `Controller` initializes Spark, creates a shared `PipelineContext`, and selects a workflow from the CLI argument.
- `Controller` also parses runtime parameters like `workflowName`, `env`, and `runDate`.
- `Workflow` orchestrates `extract -> transform -> load` through an `extracts` list plus `transform` and `load`.
- `Extract`, `Transform`, and `Load` all receive the same implicit `SparkSession` and `PipelineContext`.
- Use `PipelineContext` as the shared DataFrame registry so stages can exchange intermediate and final results.
- The sample workflow includes two extract implementations to demonstrate pulling from two sources before transforming.

## Project structure

```text
src/main/scala
├── Main.scala
├── controller
│   └── Controller.scala
├── context
│   └── PipelineContext.scala
│   └── PipelineParameters.scala
├── steps
│   ├── Extract.scala
│   ├── Load.scala
│   └── Transform.scala
└── workflow
    ├── Workflow.scala
    └── impl
        ├── SampleExtract.scala
        ├── SampleReferenceExtract.scala
        ├── SampleLoad.scala
        ├── SampleTransform.scala
        └── SampleWorkflow.scala
```

## Build

```bash
mvn clean package
```

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

## Sharing DataFrames

- Store a DataFrame in any stage with `pipelineContext.putDataFrame("orders", ordersDf)`.
- Read it later in another stage with `pipelineContext.getDataFrame("orders")`.
- The workflow stores the transformed output under the `final` key before calling `load`.

## Runtime parameters

- Access required parameters with `pipelineContext.pipelineParameters.workflowName`, `env`, and `runDate`.
- Pass extra parameters as additional `key=value` arguments and read them with `pipelineContext.pipelineParameters.get("key")`.
- Arguments are passed to the Spark application after the jar path in `spark-submit`.
