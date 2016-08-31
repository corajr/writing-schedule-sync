import java.time._
import codes.reactive.scalatime._
import com.google.api.client.util.DateTime
import java.io.InputStream
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import net.fortuna.ical4j.model.property._
import net.fortuna.ical4j.util.CompatibilityHints

case class IcsEvent(date: java.time.LocalDate, wordCount: Int) {
  import scala.language.postfixOps

  def toGcal(implicit pomoOpts: PomodoroOptions): Seq[GcalEvent] = {
    val timeBlocks = Events.wordCountToTimeBlocks(wordCount)

    if (timeBlocks.nonEmpty) {
      val startTime = LocalDateTime.of(date, pomoOpts.startTime)
      val halfHours: Seq[DateTime] =
        Seq.iterate(startTime, timeBlocks.last.endOffset + 1) { x => x.plus(30 minutes) }
          .map(Events.localDateTimeToDateTime)

      timeBlocks.map { case TimeBlock(i, j, count) =>
        GcalEvent(halfHours(i), halfHours(j), s"$count words")
      }
    } else {
      Seq()
    }
  }
}

case class GcalEvent(start: DateTime, end: DateTime, summary: String)

case class PomodoroOptions(
  pacePerPomodoro: Int = 125,
  startTime: LocalTime = LocalTime.of(13, 0)
)

case class TimeBlock(startOffset: Int, endOffset: Int, wordCount: Int)

object Events {
  import scala.language.implicitConversions
  import scala.collection.JavaConversions.asScalaBuffer
  CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

  val summaryRegex = "\\d+(?= Words)".r

  implicit def summaryToWordCount(summary: Summary): Int = {
    summaryRegex.findFirstIn(summary.getValue) match {
      case Some(n) => n.toInt
      case None => 0
    }
  }

  implicit def dateToLocalDate(date: java.util.Date): LocalDate =
    date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate()

  implicit def localDateTimeToDateTime(ldt: LocalDateTime): DateTime =
    new DateTime(java.util.Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()))

  def wordCountToTimeBlocks(wordCount: Int)(implicit pomoOpts: PomodoroOptions): Seq[TimeBlock] = {
    def wordCountToTimeBlocks0(n: Int, i: Int, count: Int): List[TimeBlock] = {
      if (i == 0) {
        List()
      } else if (i <= 3) {
        List(TimeBlock(n, n+i, count))
      } else {
        val wc = pomoOpts.pacePerPomodoro * 3
        TimeBlock(n, n+3, wc) :: wordCountToTimeBlocks0(n + 4, i - 3, count - wc)
      }
    }

    val totalPomos: Int = Math.ceil(wordCount.toDouble / pomoOpts.pacePerPomodoro).toInt
    wordCountToTimeBlocks0(0, totalPomos, wordCount)
  }

  def fromInputStream(stream: InputStream): Seq[IcsEvent] = {
    val builder = new CalendarBuilder()
    val calendar = builder.build(stream)
    for {
      component <- calendar.getComponents;
      vevent = new VEvent(component.getProperties)
      date = vevent.getStartDate.getDate
      wc = vevent.getSummary
    } yield IcsEvent(date, wc)
  }

  def toGcal(events: Seq[IcsEvent])(implicit pomoOpts: PomodoroOptions = PomodoroOptions()): Seq[GcalEvent] =
    events.flatMap(_.toGcal)

  def fromFileToGcal(file: java.io.File)(implicit pomoOpts: PomodoroOptions = PomodoroOptions()): Seq[GcalEvent] = {
    val fis = new java.io.FileInputStream(file)
    try {
      toGcal(fromInputStream(fis))
    } finally {
      fis.close()
    }
  }
}
