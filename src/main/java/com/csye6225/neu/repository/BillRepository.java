package com.csye6225.neu.repository;

import com.csye6225.neu.dto.Bill;
import com.csye6225.neu.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {

    public List<Bill> findByOwnerId(UUID id);
}
