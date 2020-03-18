package com.csye6225.neu.controller;

import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.FileStorageException;
import com.csye6225.neu.service.BillService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private StatsDClient statsd;

    private Logger logger = LoggerFactory.getLogger(BillController.class);

    @PostMapping(path = "/v1/bill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> createBill(@RequestHeader("authorization") String auth, @Valid @RequestBody Bill bill) {
        if (!auth.isEmpty()) {
            logger.info("Calling create bill API");
            statsd.incrementCounter("createBillApi");
            long start = System.currentTimeMillis();
            ResponseEntity<Bill> billCreated =  billService.createBill(auth, bill);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by create bill API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("createBillApiTime",timeElapsed);
            return billCreated;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> getBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id) {
        if (!auth.isEmpty()) {
            logger.info("Calling get bill by Id API");
            statsd.incrementCounter("getBillByIdApi");
            long start = System.currentTimeMillis();
            ResponseEntity<Bill> foundBill = billService.getBillById(auth, id);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by getBillById API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("getBillByIdApiTime", timeElapsed);
            return foundBill;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/v1/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Object> getAllBills(@RequestHeader("authorization") String auth) {
        if (!auth.isEmpty()) {
            logger.info("Calling get all bills API");
            statsd.incrementCounter("getBillsApi");
            long start = System.currentTimeMillis();
            ResponseEntity<Object> bills = billService.getAllBills(auth);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by get all bills API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("getAllBillsApiTime", timeElapsed);
            return bills;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> updateBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id, @Valid @RequestBody Bill bill) {
        if (!auth.isEmpty()) {
            logger.info("Calling update bill API");
            statsd.incrementCounter("updateBillApi");
            long start = System.currentTimeMillis();
            ResponseEntity<Bill> updateBill = billService.updateBill(auth, id, bill);
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by update bills API is " + timeElapsed + "ms");
            statsd.recordExecutionTime("updateBillApiTime", timeElapsed);
            return updateBill;
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(path = "/v1/bill/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Bill> deleteBillById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id) throws FileStorageException {
        if (!auth.isEmpty()) {
            return billService.deleteBill(auth, id);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
