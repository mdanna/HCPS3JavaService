package com.kony.novartis.utilities;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.impl.client.CloseableHttpClient;

import com.konylabs.middleware.connectors.ConnectorUtils;
import com.konylabs.middleware.controller.DataControllerRequest;

/**
 * <b>NOTE: Work in progress</b>
 * 
 * <p>
 * A static HTTP client utility used to execute http operations. The underlying http client used is shared by middleware.
 * 
 * @author Venkateswara Rao Alla
 *
 */

public class HTTPClientUtils {

    private HTTPClientUtils() {}

    /**
     * <p>Constructs and returns the base url from the provided {@link DataControllerRequest} argument. Returns null in case if underlying request instance is null.
     * <p> Base URL string contains scheme, host and optional port and is of the form <b>scheme://host:port</b> Eg: http://xyz:8080, http://127.0.0.1:8080, http://xyz, https://xyz
     * @param request
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getBaseURL(DataControllerRequest request) {
        HttpServletRequest httpServletRequest = null;
        if (request != null && (httpServletRequest = (HttpServletRequest) request.getOriginalRequest()) != null) {
            StringBuilder baseUrl = new StringBuilder();
            String serverName = httpServletRequest.getServerName();
            int serverPort = httpServletRequest.getServerPort();
            String serverScheme = httpServletRequest.getScheme();
            baseUrl.append(serverScheme).append("://").append(serverName);
            if (serverPort != 80 && serverPort != 443 && serverPort != -1) {
                baseUrl.append(":" + serverPort);
            }
            return baseUrl.toString();
        }
        return null;
    }

    
    public static <T> T executePOST(Class<T> clazz, String URL, Map<String, String> postParams,
            Map<String, String> requestHeaders, String konyFabricAuthToken) {
        return null;
    }   

    @SuppressWarnings("unused")
	private static CloseableHttpClient getHTTPClient() {
        return ConnectorUtils.getHttpClient(); // lifecycle is managed by middleware
    }

}
