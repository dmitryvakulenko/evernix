package com.n0dwis.Evernix.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EvernoteOAuth {

    public static final String AUTH_EVERNOTE_URL = "https://sandbox.evernote.com/OAuth.action";
    public static final String CALLBACK_URL = "www.noooone.local";

    private CookieManager cookieManager;
    private Logger logger;

    public EvernoteOAuth() {
        cookieManager = new CookieManager();
        logger = Logger.getLogger(getClass().getName());
        HttpsURLConnection.setFollowRedirects(false);
    }

    public String authorize(String username, String password) throws RuntimeException {
        try {
            return authorizeImpl(username, password);
        } catch (IOException e) {
            throw new RuntimeException("Can't authorize", e);
        }
    }

    private String authorizeImpl(String username, String password) throws IOException {
        OAuthService service = new ServiceBuilder()
                .provider(EvernoteApi.Sandbox.class)
                .apiKey("zexar")
                .apiSecret("b6f899bf87eda14f")
                .callback("http://" + CALLBACK_URL)
                .build();

        Token requestToken = service.getRequestToken();

        String authorizationUrl = service.getAuthorizationUrl(requestToken);
        HttpsURLConnection connection = get(authorizationUrl);

        InputStream input = connection.getInputStream();
        Document userAuthPage = Jsoup.parse(connection.getInputStream(), "UTF-8", authorizationUrl);
        input.close();

        Map<String, String> formParams = extractFormFields(userAuthPage);
        formParams.put("username", username);
        formParams.put("password", password);
        formParams.remove("cancelLogin");

        connection = post(AUTH_EVERNOTE_URL, formParams);
        if (connection.getResponseCode() != 302) {
            throw new RuntimeException("Didn't get redirect.");
        }
        connection = get(connection.getHeaderField("Location"));

        input = connection.getInputStream();
        Document allowAccessPage = Jsoup.parse(connection.getInputStream(), "UTF-8", connection.toString());
        input.close();

        formParams = extractFormFields(allowAccessPage, "#oauth-authorize-form");
        formParams.put("expireMillis", "31536000000");
        formParams.remove("revoke");
        formParams.remove("cancel");
        connection = post(AUTH_EVERNOTE_URL, formParams);
        if (connection.getResponseCode() != 302) {
            throw new RuntimeException("Didn't get redirect.");
        }

        URL noopRedirect = new URL(connection.getHeaderField("Location"));
        if (!noopRedirect.getHost().equals(CALLBACK_URL)) {
            throw new RuntimeException("Wrong redirect");
        }

        Map<String, String> query = parseUrlQuery(noopRedirect.getQuery());
        Verifier v = new Verifier(query.get("oauth_verifier"));
        return service.getAccessToken(requestToken, v).getToken();
    }

    private static String collectResponse(InputStream input) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(input));
        String line;
        StringBuilder response = new StringBuilder();
        while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\n');
        }
        rd.close();

        return response.toString();
    }

    private static Map<String, String> extractFormFields(Document htmlPage) {
        Elements inputs = htmlPage.select("form input");

        HashMap<String, String> result = new HashMap<String, String>();
        for (Element e : inputs) {
            result.put(e.attr("name"), e.attr("value"));
        }

        return result;
    }

    private static Map<String, String> extractFormFields(Document htmlPage, String formSelector) {
        Elements inputs = htmlPage.select("form" + formSelector + " input");

        HashMap<String, String> result = new HashMap<String, String>();
        for (Element e : inputs) {
            result.put(e.attr("name"), e.attr("value"));
        }

        return result;
    }

    private HttpsURLConnection get(String url) throws IOException {
        logger.info("Preparing GET request to " + url);
        URL evernoteUrl = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) (evernoteUrl).openConnection();
        connection.setRequestMethod("GET");
        cookieManager.setCookies(connection);
        connection.connect();

        if (connection.getResponseCode() >= 400) {
            logger.info("ERROR: " + connection.getResponseCode());
            throw new RuntimeException("Connection error");
        } else {
            logger.info("SUCCESS: " + connection.getResponseCode());
        }

        cookieManager.storeCookies(connection);

        return connection;
    }

    private HttpsURLConnection post(String url, Map<String, String> params) throws IOException {
        logger.info("Preparing POST request to " + url);
        URL formUrl = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) formUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Charset", "utf-8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        cookieManager.setCookies(connection);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

        String strParams = buildPostRequest(params);
        logger.info("With params: " + strParams);
        wr.writeBytes(strParams);
        wr.flush();
        wr.close();

        if (connection.getResponseCode() >= 400) {
            logger.info("ERROR: " + connection.getResponseCode());
            throw new RuntimeException("Connection error");
        } else {
            logger.info("SUCCESS: " + connection.getResponseCode());
        }

        cookieManager.storeCookies(connection);

        return connection;
    }

    private static String buildPostRequest(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for (Map.Entry<String, String> e : params.entrySet()) {
            result += e.getKey() + "=" + URLEncoder.encode(e.getValue(), "UTF-8") + "&";
        }

        return result.substring(0, result.length() - 1);
    }

    private Map<String, String> parseUrlQuery(String query) throws UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String[] nameValue = pairs[i].split("=");
            result.put(nameValue[0], URLDecoder.decode(nameValue[1], "UTF-8"));
        }

        return result;
    }
}
