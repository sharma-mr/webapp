package com.csye6225.neu.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.csye6225.neu.dto.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("amazonSQSService")
@Profile("aws")
public class AmazonSQSClient {

    private AmazonSQS amazonSQSClient;

    @Autowired
    private AmazonSNSClient amazonSNSClient;

    @Value("${app.domainName}")
    private String appDomainName;

    private final static String QUEUE = "due-bill-queue";

    private Logger logger = LoggerFactory.getLogger(AmazonSQSClient.class);

    @PostConstruct
    private void init() {
        this.amazonSQSClient = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }

    public void sendMessage(List<Bill> bills, String email) {
        try {
            CreateQueueResult create_result = amazonSQSClient.createQueue(QUEUE);
            String queueUrl = amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl();
            StringBuilder messageString = new StringBuilder();
            messageString.append(email + ",");
            for (Bill bill : bills) {
                messageString.append("http://" + appDomainName + "v1/bill"+ bill.getId());
                messageString.append(",");
                logger.info("Pushed message to queue with bill id :- " + bill.getId());
            }
            SendMessageRequest messageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl).withMessageBody(messageString.toString());
            amazonSQSClient.sendMessage(messageRequest);
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("The queue already exists" )) {
                logger.error(exception.getMessage());
                throw exception;
            }
        }
    }

    //@Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void receiveMessageAndDelete() {
        String queueUrl = amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl();
        List<Message> receivedMessageList = amazonSQSClient.receiveMessage(amazonSQSClient.getQueueUrl(QUEUE).getQueueUrl()).getMessages();
        for(Message message : receivedMessageList) {
            if (message.getBody() !=null && !message.getBody().isEmpty()) {
                logger.info("Recceving message" + message.getBody());
                amazonSNSClient.publish(message.getBody());
                amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }
        }
    }
}
