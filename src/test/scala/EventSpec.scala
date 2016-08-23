import org.scalatest._
import java.time._
import net.fortuna.ical4j.model.property.Summary
import com.google.api.client.util.DateTime

class EventSpec extends FunSpec with Matchers {
  describe("IcsEvent") {
    val date = LocalDate.now()
    val sample = IcsEvent(date, 500)

    it("has a date and associated wordcount") {
      sample.date should be (date)
      sample.wordCount should be (500)
    }

    it("can be converted into a series of GcalEvents") {
      sample.toGcal should be (Seq())
    }
  }
  describe("GcalEvent") {
    it("has a start, end, and summary") {
      val date = new java.util.Date()
      val d = new DateTime(date)
      val event = GcalEvent(d, d, "500 words")

      event.start should be (d)
      event.end should be (d)
      event.summary should be ("500 words")
    }
  }

  describe("Events") {
    it("parses a summary into an int") {
      val summary = new Summary("500 words")
      Events.summaryToWordCount(summary) should be (500)
    }
    it("parses an input iCal into IcsEvents") {
      val stream = getClass.getResourceAsStream("/PacemakerWritingSchedule.ics")
      val events = Events.fromInputStream(stream)

      val dates: Seq[LocalDate] = Seq(
        LocalDate.of(2016, 8, 22),
        LocalDate.of(2016, 8, 23),
        LocalDate.of(2016, 8, 24),
        LocalDate.of(2016, 8, 25)
      )
      val expectedEvents = dates.map { d => IcsEvent(d, 2000) }

      println(events)
      events should be (expectedEvents)
    }
  }
}
