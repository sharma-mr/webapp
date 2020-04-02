package com.csye6225.neu.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service("amazonSNSService")
@Profile("aws")
public class AmazonSNSClient {

    private AmazonSNS snsClient;

    @Value("${topic.arn}")
    private String topicArn;

    private Logger logger = LoggerFactory.getLogger(AmazonSNSClient.class);

    @PostConstruct
    private void initializeAmazon() {
        this.snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }

    public void publish(String message) {
        // Publish a message to an Amazon SNS topic.
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        final PublishResult publishResponse = snsClient.publish(publishRequest);
        logger.info("Published message with messageId :- " + publishResponse.getMessageId());
    }
}
