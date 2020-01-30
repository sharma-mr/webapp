package com.csye6225.neu.controller;

import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.User;
import com.csye6225.neu.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping(path = "/v1/bill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> createBill(@RequestHeader("authorization") String auth, @Valid @RequestBody Bill bill) {
        if (!auth.isEmpty()) {
            return billService.createBill(auth, bill);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> getBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id) {
        if (!auth.isEmpty()) {
            return billService.getBillById(auth, id);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/v1/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Object> getAllBills(@RequestHeader("authorization") String auth) {
        if (!auth.isEmpty()) {
            return billService.getAllBills(auth);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> updateBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id, @Valid @RequestBody Bill bill) {
        if (!auth.isEmpty()) {
            return billService.updateBill(auth, id, bill);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> deleteBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id) {
        if (!auth.isEmpty()) {
            return billService.deleteBill(auth, id);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
