package com.zeroone.tenancy.demo.repository;

import com.zeroone.tenancy.demo.entity.OpenAccountRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OpenAccountRecordRepository extends JpaRepository<OpenAccountRecord,Long>, JpaSpecificationExecutor<OpenAccountRecord> {

}
