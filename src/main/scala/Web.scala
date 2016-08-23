import org.scalatest.selenium._
import org.openqa.selenium.WebDriver
import com.machinepublishers.jbrowserdriver.JBrowserDriver

object Web extends WebBrowser {
  implicit val webDriver: WebDriver = new JBrowserDriver

  def getTitle(url: String): String = {
    go to url
    pageTitle
  }
  def extractCalendar(url: String): Unit = ???
}
