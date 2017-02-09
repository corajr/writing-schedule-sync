import org.scalatest._
import java.time.LocalDate

@Ignore
class WebSpec extends FunSpec with Matchers with Inspectors {
  describe("getTitle") {
    ignore("gets the title of a page") {
      Web.getTitle("https://pacemaker.press/users/corajr/plans/test-data") should be ("Pacemaker Practice Writing Schedule : 10,000 words between Aug 22, 2016 and Aug 26, 2016")
    }
  }
  describe("extractCalendar") {
    it("extracts a sequences of events from a pacemaker URL") {
      val plan = Web.extractCalendar("https://pacemaker.press/users/corajr/plans/test-data")

      val dates: Seq[LocalDate] = Seq(
        LocalDate.of(2016, 8, 22),
        LocalDate.of(2016, 8, 23),
        LocalDate.of(2016, 8, 24),
        LocalDate.of(2016, 8, 25),
        LocalDate.of(2016, 8, 26)
      )
      val expectedEvents = dates.map { d => IcsEvent(d, 2000) }

      plan should have size (expectedEvents.size)

      forAll (plan zip expectedEvents) { case (x, y) =>
        x should equal (y)
      }
    }
  }
}
