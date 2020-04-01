package com.csye6225.neu.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.service.impl.BillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("amazonSQSService")
@Profile("aws")
public class AmazonSQSClient {

    private AmazonSQS amazonSQSClient;

    @Value("${domainName}")
    private String domainName;

    private final static String QUEUE = "csye6225";

    private Logger logger = LoggerFactory.getLogger(AmazonSQSClient.class);

    @PostConstruct
    private void initializeAmazon() {
        this.amazonSQSClient = new com.amazonaws.services.sqs.AmazonSQSClient();
    }

    public void sendMessage(List<Bill> bills) {
        try {
            CreateQueueResult create_result = amazonSQSClient.createQueue(QUEUE);
            String queueUrl = amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl();
            for (Bill bill : bills) {
                SendMessageRequest messageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody("http://" + domainName + "v1/bill" + bill.getId());
                amazonSQSClient.sendMessage(messageRequest);
                logger.info("Pushed message to queue with bill id :- " + bill.getId());
            }
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("The queue already exists" )) {
                logger.error(exception.getMessage());
                throw exception;
            }
        }
    }

    private void receiveMessageAndDelete() {
        String queueUrl = amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl();
        List<Message> receivedMessageList = amazonSQSClient.receiveMessage(amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl()).getMessages();
        for (Message message : receivedMessageList) {
            amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
        }
    }
}
