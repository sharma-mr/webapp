package com.csye6225.neu.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.neu.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.UUID;

@Service("amazonService")
@Profile("aws")
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${bucketName}")
    private String bucketName;

    @PostConstruct
    private void initializeAmazon() {
        this.s3client = new AmazonS3Client();
    }

    private String generateFileName(MultipartFile multiPart) {
        return generateUUID() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String uploadFile(MultipartFile multipartFile) throws FileStorageException {
        String fileUrl = "";
        File file = null;
        String fileName = null;
        try {
            ObjectMetadata objectMeatadata = new ObjectMetadata();
            objectMeatadata.setContentType(multipartFile.getContentType());
            fileName = generateFileName(multipartFile);
            fileUrl = "https://" + bucketName + ".s3.amazonaws.com" + "/" + fileName;
            s3client.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMeatadata));
        } catch (Exception e) {
            throw new FileStorageException("File not stored in S3 bucket. File name: " + fileName+""+e);
        } finally {
            if (file != null) {
                file.delete();
            }
        }
        return fileUrl;
    }

    public void deleteFileFromS3Bucket(String fileUrl) throws FileStorageException {
        String fileName = null;
        try {

            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3client.deleteObject(
                    new DeleteObjectRequest(bucketName, fileName));
        } catch (Exception e) {
            throw new FileStorageException("File not stored in S3 bucket. File name: " + fileName);
        }

    }
    }

