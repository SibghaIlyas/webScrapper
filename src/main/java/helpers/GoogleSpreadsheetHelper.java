package helpers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSpreadsheetHelper {
    private static final String APPLICATION_NAME = "webScrapper";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSpreadsheetHelper.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static ValueRange getData(String range) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1BN1i84Rg6Ux1afttmhUuET6znYh0lr_w_hbk-e8ckpM";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();

        List<List<Object>> values = response.getValues();
        response.setValues(values);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            System.out.println(values);
            return response;
        }
    }

    public static void updatePriceOnSheet(String range, String price, ValueRange sheetData, int rowNum, String subject) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1BN1i84Rg6Ux1afttmhUuET6znYh0lr_w_hbk-e8ckpM";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        List<List<Object>> sheetDataValues = sheetData.getValues();
        List<Object> updatedRow = sheetDataValues.get(rowNum);
        if(subject == "amazon")
            updatedRow.set(0, price);
        else if(subject == "walmart")
            updatedRow.set(4, price);
        sheetDataValues.set(rowNum, updatedRow);
        sheetData.setValues(sheetDataValues);

        Sheets.Spreadsheets.Values.Update updateDataRequest = service.spreadsheets().values().update(spreadsheetId, range, sheetData);
        updateDataRequest.setValueInputOption("RAW");

        UpdateValuesResponse u = updateDataRequest.execute();

    }

    public static int nonEmptyRowsCount(ValueRange response, String range) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1BN1i84Rg6Ux1afttmhUuET6znYh0lr_w_hbk-e8ckpM";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        List<List<Object>> valuesToBeKeptAfter = response.getValues();
        response.setValues(new ArrayList<>());
        Sheets.Spreadsheets.Values.Append request = service.spreadsheets().values().append(spreadsheetId, range, response);
        request.setValueInputOption("RAW");
        request.setInsertDataOption("INSERT_ROWS");

        AppendValuesResponse exec = request.execute();
        String responseRange = exec.getTableRange();
        responseRange = responseRange.split(":")[1];
        String[] strCount = responseRange.split("[A-Z]");

        System.out.println("range: " + exec.getTableRange());
        response.setValues(valuesToBeKeptAfter);
        return Integer.parseInt(strCount[1]);
    }

}
