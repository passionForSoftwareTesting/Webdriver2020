package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;

public class GoogleSearchEngineTest {
  /*
     1. Open browser Google.
     2. Enter searchQuery.
     3. Assert result of the search.
   */

  private WebDriver driver;
  private String googleURL;

  @DataProvider(name = "searchQueries")
  public Object[] createData() {
    Object[][] queries = {
            {"Mona Lisa Overdrive"}, //thousands of search results, multiple pages;
            {"\"\""}, // Zero search results, one page;
            {"hds725050ka360"} //only 6 search results, posted on one page;
    };
    return queries;
  }

  @BeforeMethod
  public void setUp() throws Exception {
    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/macos/chromedriver");
    driver = new ChromeDriver();
    googleURL = "https://www.google.com/";

    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

  }

  @Test(dataProvider = "searchQueries")

  public void testSearchEngines(String searchQuery) {
    openBrowser(googleURL);
    inputSearchQuery(searchQuery);
    int resultsOnFirstPage = resultsOnFirstPage();
    int resultStats = getResultStats();
    assertTrue(resultsOnFirstPage <= resultStats);
  }

  private int getResultStats() {
    List<WebElement> resultStats = driver.findElements(By.cssSelector("div#result-stats"));
    int stats = 0;
    ArrayList<String> resultStatsList = new ArrayList<String>();
    if (resultStats.size() != 0) {
      String textResultStats = resultStats.get(0).getText(); //If an element (By.cssSelector("div#result-stats")) is visible, it's only one: get(0);
      resultStatsList.addAll(asList(textResultStats.split(" ")));
      for (String st : resultStatsList) {
        String stringWithoutComma = st.replace(",", "");
        if (isItInteger(stringWithoutComma)) {
          stats = parseInt(stringWithoutComma);
        }
      }
    }
    out.println("stats = " + stats);
    return stats;
  }

  private int resultsOnFirstPage() {
    List<WebElement> searchResultsInFirstPage = driver.findElements(By.cssSelector("div div.g"));
    out.println("Search results on the first page = " + searchResultsInFirstPage.size());
    return searchResultsInFirstPage.size();
  }

  private void inputSearchQuery(String searchQuery) {
    WebElement webElement = driver.findElement(By.cssSelector("input.gLFyf.gsfi"));
    webElement.sendKeys(searchQuery);
    webElement.sendKeys(Keys.ENTER);
  }


  private void openBrowser(String googleURL) {
    driver.get(googleURL);
  }

  private boolean isItInteger(String st) throws NumberFormatException {
    try {
      parseInt(st);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  @AfterMethod
  public void tearDown() throws Exception {
    driver.quit();
  }
}
