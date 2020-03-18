package com.csye6225.neu.service.impl;

import com.csye6225.neu.aws.AmazonClient;
import com.csye6225.neu.controller.FileController;
import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.FileAttachment;
import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.AuthorizationException;
import com.csye6225.neu.exception.FileStorageException;
import com.csye6225.neu.exception.UserExistsException;
import com.csye6225.neu.repository.BillRepository;
import com.csye6225.neu.repository.FileRepository;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.FileService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service("fileService")
public class FileServiceImpl implements FileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private FileRepository fileRepository;

    @Value("${path.to.file}")
    private String UPLOADED_FOLDER;

    @Value("${spring.profiles.active}")
    private String[] profiles;

    @Autowired(required = false)
    private AmazonClient amazonClient;

    @Autowired
    private StatsDClient statsd;

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public User authenticateUser(String auth) {
        String[] userInfo = new String(Base64.getDecoder().decode(auth.substring(6).getBytes())).split(":");
        String email = userInfo[0];
        String password = userInfo[1];
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserExistsException(userInfo[0]);
        } else if (BCrypt.checkpw(password, user.getPassword())) {
            return user;
        } else {
            throw new AuthorizationException("User is unauthorized");
        }
    }

    @Override
    public ResponseEntity<?> uploadFile(String auth, String id, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        User user = authenticateUser(auth);
        UUID uid = UUID.fromString(id);
        Optional<Bill> bill = billRepository.findById(uid);
        FileAttachment fileAttachement = new FileAttachment();
        String fileName = "";
        Optional<String> extension = getExtensionByStringHandling(file.getOriginalFilename());
        if (bill.isPresent() && bill.get().getOwnerId().equals(user.getId())) {
            if (file.isEmpty()) {
                return new ResponseEntity("please select a file!", HttpStatus.BAD_REQUEST);
            }

            if(bill.get().getFileAttachment() != null) {
                return new ResponseEntity<>("File already exists", HttpStatus.BAD_REQUEST);
            }

            if(!(extension.get().equals("pdf") || extension.get().equals("jpg") || extension.get().equals("jpeg") || extension.get().equals("png"))) {
                return new ResponseEntity("Only pdf, jpg, jpeg, png allowed", HttpStatus.BAD_REQUEST);
            }

            try {
                List<String> activeProfiles = new ArrayList<>();
                activeProfiles = Arrays.asList(profiles);
                if(activeProfiles.contains("default")) {
                    fileName = generateRandomString() + file.getOriginalFilename();
                    fileAttachement.setFile_name(fileName);
                    fileAttachement.setUrl(UPLOADED_FOLDER + fileAttachement.getFile_name());
                    fileAttachement.setMd5(computeMD5Hash(file.getBytes()));
                    fileAttachement.setSize(Long.toString(file.getSize()));
                    fileAttachement.setBill(bill.get());
                    saveUploadedFiles(file, fileAttachement.getFile_name());
                    bill.get().setFileAttachment(fileAttachement);
                    fileRepository.save(fileAttachement);
                } else if (activeProfiles.contains("aws")){
                    fileAttachement.setFile_name(fileName);
                    logger.info("Uploading file to S3");
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                   String url = amazonClient.uploadFile(file);
                   stopWatch.stop();
                   statsd.recordExecutionTime("uploadFileToS3Time",stopWatch.getLastTaskTimeMillis());
                   fileAttachement.setUrl(url);
                   fileAttachement.setMd5(computeMD5Hash(file.getBytes()));
                   fileAttachement.setSize(Long.toString(file.getSize()));
                   fileAttachement.setBill(bill.get());
                   bill.get().setFileAttachment(fileAttachement);
                   fileRepository.save(fileAttachement);
                }
            } catch (IOException | FileStorageException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(fileAttachement, HttpStatus.CREATED);
        } else if (bill.isPresent() && !(bill.get().getOwnerId().equals(user.getId()))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> getFileById(String auth, String billId, String fileId) {
        User user = authenticateUser(auth);
        UUID uidFile = UUID.fromString(fileId);
        UUID uidBill = UUID.fromString(billId);
        Optional<Bill> billOptional = billRepository.findById(uidBill);
        if (billOptional.isPresent() && billOptional.get().getOwnerId().equals(user.getId()) &&
                billOptional.get().getFileAttachment() != null) {
                if (billOptional.get().getFileAttachment().getId().equals(uidFile)) {
                    Optional<FileAttachment> fileOptional = fileRepository.findById(uidFile);
                    if (fileOptional.isPresent()) {
                        return new ResponseEntity<>(fileOptional.get(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> deleteFileById(String auth, String billId, String fileId) throws FileStorageException {
        User user = authenticateUser(auth);
        UUID uidFile = UUID.fromString(fileId);
        UUID uidBill = UUID.fromString(billId);
        Optional<Bill> billOptional = billRepository.findById(uidBill);
        List<String> activeProfiles = new ArrayList<>();
        activeProfiles = Arrays.asList(profiles);
        if(! billOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (billOptional.isPresent() && billOptional.get().getOwnerId().equals(user.getId())) {
            if (billOptional.get().getFileAttachment()!=null && billOptional.get().getFileAttachment().getId().equals(uidFile)) {
                Optional<FileAttachment> fileOptional = fileRepository.findById(uidFile);
                if (fileOptional.isPresent()) {
                     if (activeProfiles.contains("default")){
                        if (deleteAfile(fileOptional.get().getUrl())) {
                        billOptional.get().setFileAttachment(null);
                        fileRepository.deleteById(uidFile);
                        billRepository.save(billOptional.get());
                    }
                } else if (activeProfiles.contains("aws")) {
                         amazonClient.deleteFileFromS3Bucket(fileOptional.get().getUrl());
                         billOptional.get().setFileAttachment(null);
                         fileRepository.deleteById(uidFile);
                         billRepository.save(billOptional.get());
                    }
                    else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private void saveUploadedFiles(MultipartFile file, String fileName) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + fileName);
        Files.write(path, bytes);
    }


    public String computeMD5Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        byte[] digest = messageDigest.digest(data);

        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        return sb.toString();
    }

    public String generateRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    public boolean deleteAfile(String url) {
        try{

            File file = new File(url);
            if(file.delete()){
                return true;
            }else{
                return false;
            }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
