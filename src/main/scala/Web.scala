import org.scalatest.selenium._
import org.openqa.selenium._
import org.openqa.selenium.support.ui._
import java.time.LocalDate
import com.machinepublishers.jbrowserdriver.JBrowserDriver

object Web extends WebBrowser {
  implicit val webDriver: WebDriver = new JBrowserDriver

  def waitFor[T](f: (WebDriver) => T, timeOut: Long = 10)(implicit driver: WebDriver): T = {
    new WebDriverWait(driver, timeOut).until(
      new ExpectedCondition[T] {
        override def apply(d: WebDriver) = f(d)
      }
    )
  }

  def getTitle(url: String): String = {
    go to url
    waitFor(_.findElement(By.id("schedule")))
    pageTitle
  }

  def extractCalendar(url: String): Seq[IcsEvent] = {
    import scala.collection.JavaConversions._
    go to url
    waitFor(_.findElement(By.id("schedule")))

    executeScript("""$(".el.el-th-list").click();""")

    val schedule = waitFor(_.findElement(By.xpath("""//div[@id="scheduleDisplay"]/table""")))

    val formatter = java.time.format.DateTimeFormatter.ofPattern("M/d/uuuu")

    val events = for {
      element <- schedule.findElements(By.xpath("./tbody/tr[position() > 0]"))
      dateString = element.findElement(By.xpath("./td[3]")).getText
      wordcountString = element.findElement(By.xpath("./td[4]")).getText
      date = LocalDate.parse(dateString, formatter)
      wordCount = wordcountString.filter(_.isDigit).toInt
    } yield IcsEvent(date, wordCount)

    quit()
    events
  }
}
