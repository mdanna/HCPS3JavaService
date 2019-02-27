package com.kony.novartis.s3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Base64;
import java.util.HashMap;

import com.amazonaws.services.s3.AmazonS3;
import com.kony.novartis.utilities.Base64ResponseHandler;
import com.kony.novartis.utilities.HTTPOperations;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class S3JavaService implements JavaService2 {

	public Object invoke(String operationName, Object[] objectArray, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		Result result = new Result();
		try {
			
			ServicesManager sm = request.getServicesManager();
			ConfigurableParametersHelper paramHelper = sm.getConfigurableParametersHelper();
		
			String region = paramHelper.getServerProperty("HCP_S3_REGION");
			String accessKey = paramHelper.getServerProperty("HCP_S3_ACCESS_KEY");
			String secretKey = paramHelper.getServerProperty("HCP_S3_SECRET_KEY");
			String bucketName = paramHelper.getServerProperty("HCP_S3_BUCKET");
			String accountId = paramHelper.getServerProperty("HCP_DOCUSIGN_ACCOUNT_ID");
			String authorization = paramHelper.getServerProperty("HCP_DOCUSIGN_TOKEN");
			String baseUrl = paramHelper.getServerProperty("HCP_BASE_URL");
			
			AmazonS3 s3 = S3Utils.getS3Client(region, accessKey, secretKey);

			if(operationName.equals("getDocFromDocusignAndUploadToS3")) {
				String envelopeId = request.getParameter("envelopeId");
				String documentId = request.getParameter("documentId");
			    String filename = envelopeId + "-" + documentId;
				
				String URL = baseUrl + "/services/DocuSignAPIs/getEnvelopeDocument";
				HashMap<String, String> postParams = new HashMap<String, String>();
				postParams.put("accountId", accountId);
				postParams.put("envelopeId", envelopeId);
				postParams.put("documentId", documentId);
				
				String konyFabricAuthToken = request.getHeaderMap().get("x-kony-authorization").toString();
				
				HashMap<String, String> requestHeaders = new HashMap<String, String>();
				requestHeaders.put("Authorization", authorization);
				
				String output = HTTPOperations.hitPOSTServiceAndGetResponse(URL, postParams, 
						konyFabricAuthToken, requestHeaders, new Base64ResponseHandler());
				byte[] content = Base64.getDecoder().decode(output.getBytes());
				String szContent = new String(content);
				if(szContent != null && szContent.contains("errorCode")) {
					result.addParam(new Param("docusignOutput", szContent));
					throw new IllegalStateException("Docusign error");
				}

				String url = S3Utils.uploadFile(s3, filename, "pdf", content, bucketName);
				
				result.addParam(new Param("documentUrl", url, "string"));
			
			} else if(operationName.equals("deleteDocFromS3")) {
				String envelopeId = request.getParameter("envelopeId");
				String documentId = request.getParameter("documentId");
			    String key = envelopeId + "-" + documentId + ".pdf";
	            s3.deleteObject(bucketName, key);
			}
			
			result.addParam(new Param("debug", "success", "string"));
			result.addParam(new Param("opstatus","0","string"));

		} catch(Throwable e) {
			
			Writer writer = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(writer);
		    e.printStackTrace(printWriter);
			result.addParam(new Param("errorMsg", "error message: " + e.getMessage(), "string"));
			result.addParam(new Param("stackTrace", "stack trace: " + writer.toString(), "string"));
			throw e;
		}

		return result;
	}
}
