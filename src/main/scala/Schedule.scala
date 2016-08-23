import com.google.api.client.util.DateTime
import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpHeaders

object Scheduler extends App {
  import scala.collection.JavaConversions.asScalaBuffer
  import CalendarQuickstart.{getCalendarService}

  val service: com.google.api.services.calendar.Calendar =
    getCalendarService()

  val deleteCallback = new JsonBatchCallback[Void]() {
    override def onSuccess(content: Void, responseHeaders: HttpHeaders): Unit = {
    }

    override def onFailure(e: GoogleJsonError, responseHeaders: HttpHeaders): Unit = {
      println(s"Error: ${e.getMessage}")
    }
  }

  val now = new DateTime(System.currentTimeMillis())

  val calendarId = sys.env.getOrElse("GCAL_CALENDAR_ID", { throw new IllegalStateException("Must have a calendar ID set")})

  val events = service.events().list(calendarId)
    // .setMaxResults(10)
    .setTimeMin(now)
    .setOrderBy("startTime")
    .setSingleEvents(true)
    .execute()

  val batch = service.batch()

  for (
    event <- events.getItems;
    evtId = event.getId
  ) {
    service.events().delete(calendarId, evtId).queue(batch, deleteCallback)
  }

  batch.execute()
}
