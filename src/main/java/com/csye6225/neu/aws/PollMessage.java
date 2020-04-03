package com.csye6225.neu.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

//@Configuration
//@EnableScheduling
//@Profile("aws")
//public class PollMessage {
//
//    @Autowired(required = false)
//    public AmazonSQSClient amazonSQSClient;
//
//    private Logger logger = LoggerFactory.getLogger(PollMessage.class);
//
//    @Scheduled(fixedRate=1000)
//    public void pollQueue(){
//        amazonSQSClient.receiveMessageAndDelete();
//    }
//
//}
