package webscrapper;

import java.io.IOException;

public class MainClass {

    public static void main(String[] arg) throws IOException, InterruptedException {
        WebScrapperAmazon webScrapperAmazon = new WebScrapperAmazon();
        webScrapperAmazon.amazonWebScrapper();
    }
}
