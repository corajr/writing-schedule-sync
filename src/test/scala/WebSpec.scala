import org.scalatest._

// @Ignore
class WebSpec extends FunSpec with Matchers {
  describe("getTitle") {
    it("gets the title of a page") {
      Web.getTitle("https://pacemaker.press/users/corajr/plans/example") should be ("Pacemaker Practice Writing Schedule : 10,000 words between Aug 22, 2016 and Aug 26, 2016")
    }
  }
  describe("extractCalendar") {
    it("extracts a sequences of events from a pacemaker URL") {
      val plan = Web.extractCalendar("https://pacemaker.press/users/corajr/plans/example")
      plan should be (true)
    }
  }
}
