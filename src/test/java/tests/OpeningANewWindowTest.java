package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class OpeningANewWindowTest {
    private WebDriver driver;
    String pageURL = "https://the-internet.herokuapp.com/windows";
    String textOfNewWindowsLink = "Click Here";

    @BeforeMethod
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/macos/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }
  /* idea: we don't know any info (title, for example) about new window before open and switch to it;

  1. Open pageURL;
  2. Click to the link "Click here" to open new window;
  3. Verify, that we have 2 open windows (or for one more windows, as it was);
  4. Switch to new opened window;
  5. Verify, that URL of current window is equal to the URL of the link.
  6. Verify, that current windowHandle is the same as for the new window;
  7. Verify, that current window has different pageSource from the first one.
   */

    @Test
    public void testOpeningNewWindow() throws InterruptedException {
        goTo(pageURL);
        String pageSource = driver.getPageSource();
        String linkByTextURL = getLinkURL(this.textOfNewWindowsLink);
        int amountOfWindowsBeforeClick = getAmountOfWindows();
        clickToTheLink(this.textOfNewWindowsLink);
        waitAmountOfWindows(amountOfWindowsBeforeClick + 1); //after that assertion, that we have +1 open windows, is redundunt - we are already verify that, when we have waited for +1 open window;
        String[] arrayOfWindowHandlesAfterClick = getWindowsHandleInfo();
        switchToNewWindow(linkByTextURL); //already assert, that current window has the same URL, as the linkByTextURL;
        Assert.assertEquals(arrayOfWindowHandlesAfterClick[arrayOfWindowHandlesAfterClick.length - 1], driver.getWindowHandle());
        Assert.assertNotEquals(driver.getPageSource(), pageSource);
    }


    private String[] getWindowsHandleInfo() {
        Set<String> windowsHandlesSet = driver.getWindowHandles();
        return windowsHandlesSet.toArray(new String[]{});
    }

    private void switchToNewWindow(String linkByTextURL) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String eachHandle : windowHandles) {
            driver.switchTo().window(eachHandle);
            if (driver.getCurrentUrl().equals(linkByTextURL)) {
                return;
            }
        }
    }

    private void waitAmountOfWindows(int i) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(driver ->
                driver.getWindowHandles().size() == i);
    }

    private int getAmountOfWindows() {
        return driver.getWindowHandles().size();
    }

    private void clickToTheLink(String linkByTextURL) {
        WebElement webElement = driver.findElement(By.linkText(linkByTextURL));
        webElement.click();
    }

    private String getLinkURL(final String linkText) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(driver -> (driver.findElement(By.cssSelector("div.example a")).getText().equals(linkText)));
        return driver.findElement(By.linkText(linkText)).getAttribute("href");
    }

    private void goTo(String pageURL) {
        driver.get(pageURL);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
    }


}
