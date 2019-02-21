package com.kony.novartis.utilities;

import java.io.IOException;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class Base64ResponseHandler implements ResponseHandler<String> {

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        HttpEntity entity = response.getEntity();
        return entity == null ? null : new String(Base64.getEncoder().encode(EntityUtils.toByteArray(entity)));
    }

}
