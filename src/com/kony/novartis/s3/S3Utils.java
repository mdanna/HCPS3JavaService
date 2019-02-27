package com.kony.novartis.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Utils {

	public static AmazonS3 getS3Client(String region, String accessKey, String secretKey) {
		AWSCredentials credentials = new AWSCredentials() {
  		  public String getAWSSecretKey() { return secretKey; }
  		  public String getAWSAccessKeyId() { return accessKey; }
      	};

      	AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build();
        
        return s3;
	}
	
	public static String uploadFile(AmazonS3 s3, String filename, String extension, byte[] content, String bucketName) throws IOException {
		File tempFile = File.createTempFile(filename, extension);
		tempFile.deleteOnExit();
		OutputStream stream = new FileOutputStream(tempFile);
		try {
		    stream.write(content);
		} finally {
			stream.close();
		}
		
		String key = filename + "." + extension;

		s3.putObject(new PutObjectRequest(bucketName, key, tempFile));
		
		AccessControlList acl = s3.getObjectAcl(bucketName, key);
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		s3.setObjectAcl(bucketName, key, acl);
		
		String url = s3.getUrl(bucketName, key).toString();
		return url;
		
	}
	
}
