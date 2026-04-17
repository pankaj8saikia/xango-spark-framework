# Spark Scala Maven Archetype

This project is a Maven archetype that generates a Spark + Scala starter project.

## Architecture

The generated project follows a controller-driven workflow pattern where the controller selects a workflow, and each workflow runs its own extract, transform, and load stages.

![Spark archetype workflow diagram](docs/images/spark-archetype-flow.png)

In the generated archetype, `Controller` acts as the entry point, dispatches execution to a workflow, and each workflow owns the ETL-style sequence that processes the data end to end.

## Install the archetype locally

```bash
mvn clean install
```

## Generate a project from the archetype

```bash
mvn archetype:generate \
  -DarchetypeGroupId=org.example \
  -DarchetypeArtifactId=spark-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.acme.data \
  -DartifactId=sample-spark-job \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.acme.data \
  -DinteractiveMode=false
```

## Optional generation properties

- `scalaBinaryVersion` defaults to `2.12`
- `scalaVersion` defaults to `2.12.18`
- `sparkVersion` defaults to `3.5.1`
- `mainClass` defaults to `${package}.Main`
