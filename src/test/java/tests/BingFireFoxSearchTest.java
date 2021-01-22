package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BingFireFoxSearchTest {
    WebDriver driver;
    String query = "testing";
    Properties properties = new Properties();

    @BeforeMethod
    public void setUp() throws Exception {
        System.setProperty("webdriver.gecko.driver", "/Users/irinagavrilova/Desktop/Devel/trainings/seleniumMaven/Webdriver2020/src/test/resources/drivers/macos/geckodriver");
        String target = System.getProperty("target", "local");
        properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    /*
    1. Open Bing search in FireFox in maximum size window;
    2. Enter search query in search field, press Enter;
    3. Verify, that amount of finding results more, than quantity results in the first page;
     */
    @Test
    public void testBingSearch() {
        goTo(properties.getProperty("web.baseBingURL"));
        search(query);
        Assert.assertTrue(getAmountResultsInTheFirstPage() <= getAmountSearchResults(), "Inconsistency amount of results: we found less search results than appeared on the search page.");
    }

    private int getAmountResultsInTheFirstPage() {
        return driver.findElements(By.cssSelector("ol#b_results li.b_algo")).size();
    }

    private int getAmountSearchResults() {
        int result = 0;
        List<WebElement> webElement = driver.findElements(By.cssSelector("span.sb_count"));
        if (webElement.size() != 0) {
            String text = driver.findElement(By.cssSelector("span.sb_count")).getText();
            List<String> words = Arrays.asList(text.
                    replace(",", "").
                    split(" "));
            for (String w : words) {
                if (isItInteger(w)) {
                    return Integer.parseInt(w);
                }
            }
        }
        return result;
    }

    private boolean isItInteger(String w) throws NumberFormatException {
        try {
            Integer.parseInt(w);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }

    }


    public void goTo(String url) {
        driver.get(url);
    }

    public void search(String query) {
        WebElement webElement = driver.findElement(By.cssSelector("input.sb_form_q"));
        webElement.sendKeys(query + Keys.ENTER);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
    }


}
