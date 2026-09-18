package com.greenpulse.api.repository;

import com.greenpulse.api.model.Complaint;
import com.greenpulse.api.model.ComplaintCategory;
import com.greenpulse.api.model.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCategory(ComplaintCategory category);
    List<Complaint> findByStatus(ComplaintStatus status);
}
