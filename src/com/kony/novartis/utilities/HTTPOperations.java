package com.kony.novartis.utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

/**
 * Utility methods used to perform http operations. Uses middleware httpclient for executing the http requests.
 * 
 * @author Aditya Mankal, Venkateswara Rao Alla
 *
 */
public class HTTPOperations {

    private static final Logger LOG = Logger.getLogger(String.valueOf(HTTPOperations.class));
    
    public static final String X_KONY_AUTHORIZATION_HEADER = "X-Kony-Authorization";

    private HTTPOperations() {}

    public static String hitPOSTServiceAndGetResponse(String URL, HashMap<String, String> postParams,
        String konyFabricAuthToken, HashMap<String, String> requestHeaders, ResponseHandler<String> handler) {

    	handler = handler == null ? new StringResponseHandler() : handler;
        LOG.log(Level.INFO, "postParams {0}", postParams);

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());

        if (postParams != null && !postParams.isEmpty()) {
            List<NameValuePair> postParametersNameValuePairList = new ArrayList<NameValuePair>();


            for (Map.Entry<String, String> entry : postParams.entrySet()) {
                postParametersNameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            LOG.log(Level.INFO, "postParams {0}", postParametersNameValuePairList);

            httpPost.setEntity(new UrlEncodedFormEntity(postParametersNameValuePairList, StandardCharsets.UTF_8));
        }

        return executeRequest(httpPost, konyFabricAuthToken, requestHeaders, handler);
    }

    public static String hitPOSTServiceAndGetResponse(String URL, JSONObject jsonPostParameter,
            String konyFabricAuthToken, HashMap<String, String> requestHeaders, ResponseHandler<String> handler) {

    	handler = handler == null ? new StringResponseHandler() : handler;
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (jsonPostParameter != null) {
            String jsonString = jsonPostParameter.toString();
            StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);
        }

        return executeRequest(httpPost, konyFabricAuthToken, requestHeaders, handler);
    }

    public static String executeRequest(HttpUriRequest request, String konyFabricAuthToken,
            HashMap<String, String> requestHeaders, ResponseHandler<String> handler) {
        try {
            CloseableHttpClient httpClient = getHttpClient();

            if (StringUtils.isNotBlank(konyFabricAuthToken)) {
                request.setHeader(X_KONY_AUTHORIZATION_HEADER, konyFabricAuthToken);
            }

            if (requestHeaders != null && !requestHeaders.isEmpty()) {
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }

            return httpClient.execute(request, handler);
        } catch (Exception e) {
            LOG.warning("Error occured while executing backend request");
        }
        return null;
    }

    /**
     * <p>
     * Returns {@link CloseableHttpClient} instance from middleware. Lifecycle is managed by middleware.
     * <p>
     * <b>NOTE: Never call close() on this client and never participate this client in try-with-resources statement
     *
     * @return {@link CloseableHttpClient} instance
     */
    private static CloseableHttpClient getHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        return builder.build(); // lifecycle is managed by middleware. Never call close() on this client
    }

}
