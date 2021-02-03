package com.zeroone.tenancy.demo.resource;

import com.zeroone.tenancy.demo.entity.OpenAccountRecord;
import com.zeroone.tenancy.demo.repository.OpenAccountRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestResource {


    @Autowired
    private OpenAccountRecordRepository openAccountRecordRepository;



    @GetMapping("/account/{id}")
    public OpenAccountRecord findById(@PathVariable("id") Long id){
        return openAccountRecordRepository.findById(id).orElse(null);
    }
}
