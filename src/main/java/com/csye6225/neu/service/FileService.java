package com.csye6225.neu.service;

import com.csye6225.neu.dto.User;
import com.csye6225.neu.exception.FileStorageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface FileService {

    public User authenticateUser(String auth);

    public ResponseEntity<?> uploadFile(String auth, String id, MultipartFile file) throws IOException, NoSuchAlgorithmException;

    public ResponseEntity<?> getFileById(String auth, String billId, String fileId);

    public ResponseEntity<?> deleteFileById(String auth, String billId, String fileId) throws FileStorageException;
}
