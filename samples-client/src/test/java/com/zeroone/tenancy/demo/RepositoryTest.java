//package com.zeroone.tenancy.demo;
//
//import com.zeroone.tenancy.demo.entity.BankAccount;
//import com.zeroone.tenancy.demo.repository.BankAccountRepository;
//import com.zeroone.tenancy.utils.TenantIdentifierHelper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringJUnit4ClassRunner.class)
//public class RepositoryTest {
//
//
//    @Autowired
//    private BankAccountRepository bankAccountRepository;
//
//
//    @Test
//    public void test(){
//
//        TenantIdentifierHelper.setTenant("1009");
//        final BankAccount bankAccount = bankAccountRepository.findById(1L).orElse(new BankAccount());
//        log.info(bankAccount.toString());
//    }
//}
