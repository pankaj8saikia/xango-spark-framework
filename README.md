# Spark Scala Maven Archetype

This project is a Maven archetype that generates a Spark + Scala starter project.

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
