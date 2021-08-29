package webscrapper;

import com.google.api.client.util.Value;
import com.google.api.services.sheets.v4.model.ValueRange;
import helpers.GoogleSpreadsheetHelper;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainClass {

    public static void main(String[] arg) throws IOException, InterruptedException, GeneralSecurityException {
        WebScrapper webScrapper = new WebScrapper();
        webScrapper.amazonWebScrapper();
        webScrapper.walmartWebScrapper();

    }
}
