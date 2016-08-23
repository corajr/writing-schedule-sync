import org.scalatest._
import java.time._
import net.fortuna.ical4j.model.property.Summary
import com.google.api.client.util.DateTime

class EventSpec extends FunSpec with Matchers with Inspectors {
  describe("IcsEvent") {
    val date = LocalDate.now()
    val sample = IcsEvent(date, 500)

    it("has a date and associated wordcount") {
      sample.date should be (date)
      sample.wordCount should be (500)
    }

    it("can be converted into a series of GcalEvents") {
      implicit val pomoOpts = PomodoroOptions()

      val dateTimes = IndexedSeq(
        LocalDateTime.of(date, LocalTime.of(13, 0)),
        LocalDateTime.of(date, LocalTime.of(14, 30)),
        LocalDateTime.of(date, LocalTime.of(15, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 30))
      ).map(Events.localDateTimeToDateTime)

      val expectedEvents = Seq(
        GcalEvent(dateTimes(0), dateTimes(1), "375 words"),
        GcalEvent(dateTimes(2), dateTimes(3), "125 words")
      )

      forAll(sample.toGcal zip expectedEvents) { case (x, y) =>
        x should equal (y)
      }
    }
  }
  describe("GcalEvent") {
    it("has a start, end, and summary") {
      val date = new java.util.Date()
      val d = new DateTime(date)
      val event = GcalEvent(d, d, "500 Words")

      event.start should be (d)
      event.end should be (d)
      event.summary should be ("500 Words")
    }
  }
  describe("PomodoroOptions") {
    it("has a pace and a start time") {
      val pomoOpts = PomodoroOptions()
      pomoOpts.pacePerPomodoro should equal (125)
      pomoOpts.startTime should equal (LocalTime.of(13,0))
    }
  }

  describe("Events") {
    describe("wordCountToTimeBlocks") {
      import Events.wordCountToTimeBlocks
      it("converts a word count to a list of pomodoro blocks with word counts") {
        implicit val pomoOpts = PomodoroOptions(pacePerPomodoro = 100)
        wordCountToTimeBlocks(50) should equal (Seq(TimeBlock(0, 1, 50)))
        wordCountToTimeBlocks(100) should equal (Seq(TimeBlock(0, 1, 100)))
        wordCountToTimeBlocks(200) should equal (Seq(TimeBlock(0, 2, 200)))
        wordCountToTimeBlocks(300) should equal (Seq(TimeBlock(0, 3, 300)))
        wordCountToTimeBlocks(350) should equal (Seq(TimeBlock(0, 3, 300), TimeBlock(4, 5, 50)))
        wordCountToTimeBlocks(400) should equal (Seq(TimeBlock(0, 3, 300), TimeBlock(4, 5, 100)))
        wordCountToTimeBlocks(600) should equal (Seq(TimeBlock(0, 3, 300), TimeBlock(4, 7, 300)))
        wordCountToTimeBlocks(700) should equal (Seq(TimeBlock(0, 3, 300), TimeBlock(4, 7, 300), TimeBlock(8, 9, 100)))
      }
    }

    describe("summaryToWordCount") {
      it("parses a summary into an int") {
        val summary = new Summary("500 Words")
        Events.summaryToWordCount(summary) should be (500)
      }
    }

    describe("fromInputStream") {
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

        forAll (events zip expectedEvents) { case (x, y) =>
          x should equal (y)
        }
      }
    }
  }
}
