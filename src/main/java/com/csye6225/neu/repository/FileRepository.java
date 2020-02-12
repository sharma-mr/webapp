package com.csye6225.neu.repository;

import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;
@Transactional
@Repository
public interface FileRepository extends JpaRepository<FileAttachment, UUID> {
}
