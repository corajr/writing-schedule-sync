import java.time._
import codes.reactive.scalatime._
import com.google.api.client.util.DateTime
import java.io.InputStream
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.component._
import net.fortuna.ical4j.model.property._
import net.fortuna.ical4j.util.CompatibilityHints

sealed trait Event

case class IcsEvent(date: java.time.LocalDate, wordCount: Int) extends Event {
  def toGcal: Seq[GcalEvent] = Seq()
}

case class GcalEvent(start: DateTime, end: DateTime, summary: String) extends Event


case class PomodoroOptions(
  pacePerPomodoro: Int = 125,
  startTime: LocalTime = LocalTime.of(13, 0)
)

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

  def wordCountToTimeBlocks(wordCount: Int)(implicit pomoOpts: PomodoroOptions): Seq[(Int, Int, Int)] = {
    def wordCountToTimeBlocks0(n: Int, i: Int, count: Int): Seq[(Int, Int, Int)] = {
      if (i == 0) {
        Seq()
      } else if (i < 3) {
        Seq((n, n+i, count))
      } else {
        val wc = pomoOpts.pacePerPomodoro * 3
        Seq((n, n+3, pomoOpts.pacePerPomodoro * 3)) ++ wordCountToTimeBlocks0(n + 4, i - 3, count - wc)
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
}
