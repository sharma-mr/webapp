package com.csye6225.neu.service.impl;

import com.csye6225.neu.aws.AmazonClient;
import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.FileAttachment;
import com.csye6225.neu.dto.PaymentStatus;
import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.AuthorizationException;
import com.csye6225.neu.exception.FileStorageException;
import com.csye6225.neu.exception.UserExistsException;
import com.csye6225.neu.exception.ValidationException;
import com.csye6225.neu.repository.BillRepository;
import com.csye6225.neu.repository.FileRepository;
import com.csye6225.neu.repository.UserRepository;
import com.csye6225.neu.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service("billService")
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Value("${spring.profiles.active}")
    private String[] profiles;

    @Autowired(required = false)
    private AmazonClient amazonClient;


    @Override
    public ResponseEntity<Bill> createBill(String auth, Bill bill) {
        String[] userInfo = new String(Base64.getDecoder().decode(auth.substring(6).getBytes())).split(":");
        User user = userRepository.findByEmail(userInfo[0]);
        if (user == null) {
            throw new UserExistsException(userInfo[0]);
        } else {
            if (checkForPaymentStatus(bill.getPaymentStatus())) {
                bill.setOwnerId(user.getId());
                billRepository.save(bill);
                return new ResponseEntity<Bill>(bill, HttpStatus.CREATED);
            } else {
                throw new ValidationException("Payment Status can only be paid, due, past_due, no_payment_required");
            }
        }
    }

    private boolean checkForPaymentStatus(PaymentStatus paymentStatus) {
        return Arrays.stream(PaymentStatus.values())
                .map(PaymentStatus::name)
                .collect(Collectors.toSet())
                .contains(paymentStatus.name());
    }

    @Override
    public boolean userExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null)
            return true;
        return false;
    }

    @Override
    public ResponseEntity<Bill> getBillById(String auth, String id) {
        User user = authenticateUser(auth);
        UUID uid = UUID.fromString(id);
        Optional<Bill> bill = billRepository.findById(uid);
        if(bill.isPresent() && bill.get().getOwnerId().equals(user.getId())){
            return new ResponseEntity<Bill>(bill.get(), HttpStatus.OK);
        } else if (bill.isPresent() && !(bill.get().getOwnerId().equals(user.getId()))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> getAllBills(String auth) {
        User user = authenticateUser(auth);
        if (null != user) {
            List<Bill> bills = billRepository.findByOwnerId(user.getId());
            if(!bills.isEmpty()) {
                return new ResponseEntity<Object>(bills, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new UserExistsException("User not found");
        }

    }

    @Override
    public ResponseEntity<Bill> updateBill(String auth, String id, Bill bill) {
        User user = authenticateUser(auth);
        UUID uid = UUID.fromString(id);
        Optional<Bill> billOptional = billRepository.findById(uid);
        if(billOptional.isPresent() && billOptional.get().getOwnerId().equals(user.getId())){
            bill.setId(uid);
            billRepository.save(bill);
            return new ResponseEntity<>(billOptional.get(),HttpStatus.OK);
        } else if (billOptional.isPresent() && !(billOptional.get().getOwnerId().equals(user.getId()))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Bill> deleteBill(String auth, String id) throws FileStorageException {
        User user = authenticateUser(auth);
        UUID uid = UUID.fromString(id);
        Optional<Bill> bill = billRepository.findById(uid);
        List<String> activeProfiles = new ArrayList<>();
        activeProfiles = Arrays.asList(profiles);
        if(bill.isPresent() && bill.get().getOwnerId().equals(user.getId())){
            if(bill.get().getFileAttachment() != null) {
                Optional<FileAttachment> fileOptional = fileRepository.findById(bill.get().getFileAttachment().getId());
                if (fileOptional.isPresent() && activeProfiles.contains("default")) {
                    deleteAfile(fileOptional.get().getUrl());
                    billRepository.deleteById(uid);
                } else if(fileOptional.isPresent() && activeProfiles.contains("aws")) {
                    amazonClient.deleteFileFromS3Bucket(fileOptional.get().getUrl());
                    billRepository.deleteById(uid);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } else if (bill.isPresent() && !(bill.get().getOwnerId().equals(user.getId()))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private boolean validateUser(String auth) {
        String[] userInfo = new String(Base64.getDecoder().decode(auth.substring(6).getBytes())).split(":");
        User user = userRepository.findByEmail(userInfo[0]);
        if (user == null) {
            throw new UserExistsException(userInfo[0]);
        } else if (new BCryptPasswordEncoder().matches(userInfo[1], user.getPassword())) {
            return true;
        } else {
            throw new AuthorizationException("User is unauthorized");
        }

    }

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
}
