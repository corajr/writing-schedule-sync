import java.time._
import com.google.api.services.calendar.model._
import com.google.api.client.util.DateTime
import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpHeaders

object Scheduler extends App {
  import scala.collection.JavaConversions.asScalaBuffer
  import CalendarQuickstart.{getCalendarService}

  lazy val service: com.google.api.services.calendar.Calendar =
    getCalendarService()

  lazy val deleteCallback = new JsonBatchCallback[Void]() {
    override def onSuccess(content: Void, responseHeaders: HttpHeaders): Unit = {
    }

    override def onFailure(e: GoogleJsonError, responseHeaders: HttpHeaders): Unit = {
      println(s"Error: ${e.getMessage}")
    }
  }

  lazy val eventCallback = new JsonBatchCallback[Event]() {
    override def onSuccess(event: Event, responseHeaders: HttpHeaders): Unit = {
      println(s"Added ${event}")
    }

    override def onFailure(e: GoogleJsonError, responseHeaders: HttpHeaders): Unit = {
      println(s"Error: ${e.getMessage}")
    }
  }

  val calendarId = sys.env.getOrElse("GCAL_CALENDAR_ID", { throw new IllegalStateException("Must have a calendar ID set")})

  val startOfDay = new DateTime(
    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
  )

  def deleteExistingEvents(): Unit = {
    val events = service.events().list(calendarId)
      .setTimeMin(startOfDay)
      .setOrderBy("startTime")
      .setSingleEvents(true)
      .execute()

    val eventItems = events.getItems
    if (eventItems.nonEmpty) {
      val batch = service.batch()

      for (
        event <- eventItems;
        evtId = event.getId
      ) {
        service.events().delete(calendarId, evtId).queue(batch, deleteCallback)
      }

      batch.execute()
    }
  }

  def addEvents(events: Seq[GcalEvent]): Unit = {
    val batch = service.batch()

    for (
      GcalEvent(start, end, summary) <- events
      if start.getValue() >= startOfDay.getValue()
    ) {
      val event = new Event()
      event.setSummary(summary)
      event.setStart(new EventDateTime().setDateTime(start))
      event.setEnd(new EventDateTime().setDateTime(end))
      service.events().insert(calendarId, event).queue(batch, eventCallback)
    }

    batch.execute()
  }

  args match {
    case Array(url) =>
      val events = Web.extractCalendar(url)
      if (events.nonEmpty) {
        deleteExistingEvents()
        addEvents(Events.toGcal(events))
      }
    case _ => println("Needs a pacemaker.press plan URL")
  }

}
