package com.kmazur.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Bean
    public AmazonS3 s3Client(AWSCredentials credentials) {
        return new AmazonS3Client(credentials);
    }


    @Bean
    public AWSCredentials credentials(
        @Value("${aws.accessKey}") String accessKey,
        @Value("${aws.secretKey}") String secretKey
    ) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
}
