import com.google.api.client.util.DateTime

object Scheduler extends App {
  import scala.collection.JavaConversions.asScalaBuffer
  import CalendarQuickstart.{getCalendarService}

  val service: com.google.api.services.calendar.Calendar =
    getCalendarService()

  val now = new DateTime(System.currentTimeMillis())
  val events = service.events().list("primary")
    .setMaxResults(10)
    .setTimeMin(now)
    .setOrderBy("startTime")
    .setSingleEvents(true)
    .execute()

  val items = events.getItems()

  if (items.size() == 0) {
    println("No upcoming events found.");
  } else {
    println("Upcoming events");
    for (event <- items) {
      val start = Option(event.getStart().getDateTime()).getOrElse(event.getStart().getDate())
      println(s"${event.getSummary} ($start)")
    }
  }

}
