import org.scalatest.selenium._
import org.openqa.selenium.{WebDriver, WebElement, By}
import org.openqa.selenium.support.ui._
import com.machinepublishers.jbrowserdriver.JBrowserDriver

object Web extends WebBrowser {
  implicit val webDriver: WebDriver = new JBrowserDriver

  def waitFor[T](f: (WebDriver) => T, timeOut: Long = 5)(implicit driver: WebDriver): T = {
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

  def extractCalendar(url: String): Boolean = {
    go to url
    waitFor(_.findElement(By.id("schedule")))

    click on id("exportCalLink")

    val calFile = new java.io.File("./download_cache/PacemakerWritingSchedule.ics")

    waitFor({_ => calFile.exists})
  }
}
