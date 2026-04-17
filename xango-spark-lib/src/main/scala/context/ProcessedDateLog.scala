package org.xango.spark.context

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.time.LocalDate
import java.time.format.DateTimeParseException
import scala.collection.JavaConverters._

object ProcessedDateLog {

  def read(path: String): Set[LocalDate] = {
    val logPath = Paths.get(path)
    if (!Files.exists(logPath)) {
      Set.empty
    } else {
      Files.readAllLines(logPath, StandardCharsets.UTF_8).asScala
        .iterator
        .map(_.trim)
        .filter(_.nonEmpty)
        .map(parseDate(path, _))
        .toSet
    }
  }

  def append(path: String, dates: Seq[LocalDate]): Unit = {
    if (dates.nonEmpty) {
      val logPath = Paths.get(path)
      val parent = Option(logPath.getParent)
      parent.foreach(directory => Files.createDirectories(directory))

      val orderedDates = dates.distinct.sortBy(_.toEpochDay)
      val content = orderedDates.map(_.toString).mkString("", System.lineSeparator(), System.lineSeparator())
      Files.write(
        logPath,
        content.getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      )
    }
  }

  private def parseDate(path: String, value: String): LocalDate =
    try {
      LocalDate.parse(value)
    } catch {
      case _: DateTimeParseException =>
        throw new IllegalArgumentException(
          s"Processed date log '$$path' contains an invalid ISO date: '$$value'"
        )
    }
}
