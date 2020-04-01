package com.csye6225.neu.service;


import com.csye6225.neu.dto.Bill;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.FileStorageException;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface BillService {

    public User authenticateUser(String auth);

    public ResponseEntity<Bill> createBill(String auth, Bill bill);

    public boolean userExists(String userName);

    public ResponseEntity<Bill> getBillById(String auth, String id);

    public ResponseEntity<Object> getAllBills(String auth);

    public ResponseEntity<Bill> updateBill(String auth, String id, Bill bill);

    public ResponseEntity<Bill> deleteBill(String auth, String id) throws FileStorageException;

    public ResponseEntity<Object> getDueBills(String auth, String days) throws ParseException;

}
