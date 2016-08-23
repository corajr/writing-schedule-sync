import org.scalatest._
import scala.io.Source

import com.google.api.client.util.DateTime

class EventSpec extends FunSpec with Matchers {
  describe("An Event") {
    it("has a start, end, and summary") {
      val date = new java.util.Date()
      val d = new DateTime(date)
      val event = Event(d, d, "500 words")

      event.start should be (d)
      event.end should be (d)
      event.summary should be ("500 words")
    }
  }

  describe("Events") {
    it("parses an input iCal into events") {
      val stream = getClass.getResourceAsStream("/PacemakerWritingSchedule.ics")
      val events = Events.fromInputStream(stream)
    }
  }
}
