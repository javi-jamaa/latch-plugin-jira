package JiraMarketplace;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class LatchAuthenticator {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String AUTH_METHOD = "11PATHS";
    private String applicationId;
    private String secret;

    public LatchAuthenticator(String applicationId, String secret) {
        this.applicationId = applicationId;
        this.secret = secret;
    }

    public String generateAuthorizationHeader(String method, String url, String customHeaders, String queryParams, String body) throws Exception {
        String date = getCurrentDate();
        String requestSignature = generateRequestSignature(method, date, customHeaders, url, queryParams, body);
        return String.format("%s %s %s", AUTH_METHOD, applicationId, requestSignature);
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    private String generateRequestSignature(String method, String date, String customHeaders, String url, String queryParams, String body) throws Exception {
        StringBuilder signatureBase = new StringBuilder();
        signatureBase.append(method.toUpperCase()).append("\n");
        signatureBase.append(date).append("\n");
        signatureBase.append(customHeaders).append("\n");
        signatureBase.append(url).append(queryParams);

        if (method.equals("POST") || method.equals("PUT")) {
            signatureBase.append("\n").append(body);
        }

        return signData(signatureBase.toString(), secret);
    }

    private String signData(String data, String key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
