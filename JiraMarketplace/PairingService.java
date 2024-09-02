package JiraMarketplace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PairingService {

    private static final String PAIRING_ENDPOINT = "https://latch.tu.com/api/2.0/pair/";
    private LatchAuthenticator latchAuthenticator;

    public PairingService(String applicationId, String secret) {
        this.latchAuthenticator = new LatchAuthenticator(applicationId, secret);
    }

    public String pairAccount(String token) throws Exception {
        String url = PAIRING_ENDPOINT + token;
        String method = "GET";
        String customHeaders = ""; // Add any custom headers if needed
        String queryParams = ""; // Add any query parameters if needed
        String body = ""; // No body for GET request

        String authorizationHeader = latchAuthenticator.generateAuthorizationHeader(method, url, customHeaders, queryParams, body);
        String dateHeader = latchAuthenticator.getCurrentDate();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", authorizationHeader);
        connection.setRequestProperty("X-11Paths-Date", dateHeader);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("Failed to pair account: HTTP error code " + responseCode);
        }
    }
}
