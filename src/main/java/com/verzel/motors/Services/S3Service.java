package com.verzel.motors.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

@Configuration
public class S3Service {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${cloud.aws.region.static}")
    private Region region;
    @Value("${application.bucket.name}")
    private String bucket;


    S3Presigner presigner = S3Presigner
            .create();

    public String uploadFile(String fileName, InputStream inputStream) throws IOException {
        S3Client s3Client = S3Client
                .builder()
                .region(region).build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(
                        inputStream,
                        inputStream.available()));


        return fileName;
    }

    public URL putPresignedUrl(String key) {
        URL url = null;
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            String myURL = presignedRequest.url().toString();

            url = presignedRequest.url();
        } catch (S3Exception e) {
            e.getStackTrace();
        }

        return url;
    }

    public URL getPresignedUrl(String key) {
        URL url = null;
        try {
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

            GetObjectPresignRequest getObjectPresignRequest =  GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(20))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    presigner.presignGetObject(getObjectPresignRequest);

            url = presignedGetObjectRequest.url();
        } catch (S3Exception e) {
            e.printStackTrace();
        }

        return url;
    }
}
