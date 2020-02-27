package com.csye6225.neu.controller;

import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.exception.FileStorageException;
import com.csye6225.neu.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(path ="/v1/bill/{id}/file")
    protected ResponseEntity<?> uploadFile(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String id, @RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return fileService.uploadFile(auth, id, file);
    }

    @GetMapping(path = "/v1/bill/{billId}/file/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> getFileById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String billId, final @PathVariable(required = true) String fileId) {
        if (!auth.isEmpty()) {
            return fileService.getFileById(auth, billId, fileId);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(path = "/v1/bill/{billId}/file/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> deleteFileById(@RequestHeader("authorization") String auth, final @PathVariable(required = true) String billId, final @PathVariable(required = true) String fileId) throws FileStorageException {
        if (!auth.isEmpty()) {
            return fileService.deleteFileById(auth, billId, fileId);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
