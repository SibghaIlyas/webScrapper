package webscrapper;

import com.google.api.services.sheets.v4.model.ValueRange;
import helpers.GoogleSpreadsheetHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class WebScrapper {

    private String fetchedPrice = null;
    private String range = "Products!I1:N1000";
    private int totalRows = 0;

    public WebScrapper() throws IOException {

    }

    public void amazonWebScrapper() throws IOException, InterruptedException, GeneralSecurityException {
        int rowNo = 1;
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        ValueRange sheetDataResponse = GoogleSpreadsheetHelper.getData(range);
        List<List<Object>> dataList = sheetDataResponse.getValues();
        totalRows = GoogleSpreadsheetHelper.nonEmptyRowsCount(sheetDataResponse, range);
        System.out.println(totalRows);
        System.out.println(dataList);
        System.setProperty("webdriver.chrome.driver", "chromedriver");

        driver.get("https://www.amazon.com/");
        //click on location to set location
        driver.findElement(By.cssSelector("#glow-ingress-block")).click();
        //enter zip code in input
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".GLUX_Full_Width")));
        driver.findElement(By.cssSelector(".GLUX_Full_Width")).sendKeys("44118");
        //click apply button
        driver.findElement(By.xpath("//input[@aria-labelledby='GLUXZipUpdate-announce']")).click();
        //click confirm button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@aria-labelledby='GLUXConfirmClose-announce'])[2]")));
        driver.findElement(By.xpath("(//input[@aria-labelledby='GLUXConfirmClose-announce'])[2]")).click();

        System.out.println("total rows in excel: " + totalRows);
        while(rowNo < totalRows) {
            Thread.sleep(1000);

             //get price
            try {
                String url = dataList.get(rowNo).get(2).toString();
                System.out.println(url);
                driver.navigate().to(url);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#price_inside_buybox")));
                fetchedPrice = driver.findElement(By.cssSelector("#price_inside_buybox")).getText();

            } catch (Exception e) {
                //may be url is not present
                if (e.equals(new IndexOutOfBoundsException())) {
                    fetchedPrice = "url does not exist";
                } else {
                    try {
                        String seeAllOption = "//a[contains(text(),'See All Buying Options')]";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(seeAllOption)));
                        driver.findElement(By.xpath(seeAllOption)).click();
                        Thread.sleep(10000);

                        JavascriptExecutor js = (JavascriptExecutor)driver;
                        fetchedPrice = (String) js.executeScript("return document.getElementById('aod-price-1').childNodes[1].childNodes[0].innerText");


                    } catch (Exception e1) {
                        fetchedPrice = "price not found";
                        System.out.println(e1);
                    }
                }

            }

            System.out.println(fetchedPrice);
            GoogleSpreadsheetHelper.updatePriceOnSheet(range,fetchedPrice, sheetDataResponse, rowNo, "amazon");
            rowNo++;
        }
    }

    public void walmartWebScrapper() throws IOException, InterruptedException, GeneralSecurityException {
        int rowNo = 1;
        ValueRange sheetDataResponse = GoogleSpreadsheetHelper.getData(range);
        List<List<Object>> dataList = sheetDataResponse.getValues();
        totalRows = GoogleSpreadsheetHelper.nonEmptyRowsCount(sheetDataResponse, range);
        System.out.println(totalRows);
        System.out.println(dataList);
        System.setProperty("webdriver.chrome.driver", "chromedriver");

        System.out.println("total rows in excel: " + totalRows);
        while(rowNo < totalRows) {
            Thread.sleep(1000);
            WebDriver driver = new ChromeDriver();
            WebDriverWait wait = new WebDriverWait(driver, 15);
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies(); //delete all cookies
            //get price
            try {
                String url = dataList.get(rowNo).get(5).toString();
                System.out.println("url: " + url);
                driver.get(url);

                String priceSelector = "//span[@id='price']/div/span[1]/span/span[1]";
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(priceSelector)));
                fetchedPrice = driver.findElement(By.xpath(priceSelector)).getText();

            } catch (Exception e) {
                System.out.println(e);
                Thread.sleep(900000);
                fetchedPrice = "Price not found / url not present";

            }

            System.out.println(fetchedPrice);
            GoogleSpreadsheetHelper.updatePriceOnSheet(range,fetchedPrice, sheetDataResponse, rowNo, "walmart");
            rowNo++;
            driver.quit();
        }

    }

}
