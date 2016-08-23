import com.google.api.client.util.DateTime
import java.io.{InputStream, FileInputStream}
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.util.CompatibilityHints

case class Event(start: DateTime, end: DateTime, summary: String)

object Events {
  CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

  def fromInputStream(stream: InputStream): Seq[Event] = {
    val builder = new CalendarBuilder()
    val calendar = builder.build(stream)
    println(calendar)
    Seq()
  }
}
