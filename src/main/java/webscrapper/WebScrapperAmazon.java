package webscrapper;

import helpers.ExcelHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebScrapperAmazon {
    public void amazonWebScrapper() throws IOException, InterruptedException {
        int rowNo = 2;
        String fetchedPrice = null;
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        ExcelHelper excelHelper = new ExcelHelper();
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

        while(rowNo <= 5) {
            Thread.sleep(1000);
            String url = excelHelper.getValueFromCell("Products", rowNo, 10);
            System.out.println(url);
            driver.navigate().to(url);
//

             //get price
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#price_inside_buybox")));
                fetchedPrice = driver.findElement(By.cssSelector("#price_inside_buybox")).getText();

            } catch (Exception e) {
                //may be value is not on main pAge
                try {
                    String seeAllOption = "//a[contains(text(),'See All Buying Options')]";
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(seeAllOption)));
                    driver.findElement(By.xpath(seeAllOption)).click();
                    Thread.sleep(2000);

                    JavascriptExecutor js = (JavascriptExecutor)driver;
                    fetchedPrice = (String) js.executeScript("return document.getElementById('aod-price-1').childNodes[1].childNodes[0].innerText");


                } catch (NoSuchElementException e1) {
                    fetchedPrice = "price not found";
                    System.out.println(e1);
                }
            }


            System.out.println(fetchedPrice);
            excelHelper.setValueOfCell("Products", rowNo, 8, fetchedPrice);
            rowNo++;
        }
        excelHelper.saveFile();
        driver.quit();



    }
}
