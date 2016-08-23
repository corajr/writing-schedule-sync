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

object Events {
  import scala.language.implicitConversions
  import scala.collection.JavaConversions.asScalaBuffer
  CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

  val summaryRegex = "\\d+(?= words)".r

  implicit def summaryToWordCount(summary: Summary): Int = {
    summary.getValue match {
      case summaryRegex(n) => n.toInt
      case _ => 0
    }
  }

  implicit def dateToLocalDate(date: java.util.Date): LocalDate =
    date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate()

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
