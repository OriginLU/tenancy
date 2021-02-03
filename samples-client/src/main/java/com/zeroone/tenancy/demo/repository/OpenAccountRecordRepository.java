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

    Long countByChannelCodeAndAccountNumberAndCredentialCodeAndProtocolNumberAndOpenAccountStatus(Long repaymentMethod, String bankNum, String idNumber, String protocolNo, Integer openAccountStatus);

    Boolean existsByChannelCodeAndAccountNumberAndCredentialCodeAndOpenAccountStatus(Long channelCode,String accountNumber,String credentialCode,Integer status);

    Boolean existsByChannelCodeAndAccountNumberAndCredentialCodeAndOpenAccountStatusAndTelPhone(Long channelCode,String accountNumber,String credentialCode,Integer status,String telPhone);

    boolean existsByProtocolNumberAndOpenAccountStatus(String protocolNumber, int code);

    OpenAccountRecord findFirstByChannelCodeAndCredentialCodeAndAccountNumberAndOpenAccountStatus(long channelCode, String idNumber, String bankNumber, Integer code);

    Optional<OpenAccountRecord> findByUniqueCodeAndChannelCode(Long valueOf, Long channelCode);

    Optional<OpenAccountRecord> findByUniqueCodeAndChannelCodeAndOpenAccountStatus(Long valueOf, Long channelCode,Integer state);

    List<OpenAccountRecord> findByUniqueCodeAndOpenAccountStatus(Long uniqueCode, Integer state);

    Optional<OpenAccountRecord> findByUniqueCode(Long uniqueCode);

    List<OpenAccountRecord> findByProtocolNumber(String protocolNo);

    List<OpenAccountRecord> findByCredentialCodeAndChannelCodeAndOpenAccountStatusOrderByCreateTimeDesc(String idCard,Long channelCode,int openAccountStatus);

    Optional<OpenAccountRecord> findByCredentialCodeAndAccountNumberAndChannelCode(String credentialCode,String accountNumber,Long channelCode);

    List<OpenAccountRecord> findAllByCredentialCode(String credentialId);

    Optional<OpenAccountRecord> findFirstByCredentialCodeAndOpenAccountStatusOrderByCreateTimeDesc(String customerId, int code);

    Boolean existsByIdInAndOpenAccountStatusAndChannelCodeIn(Set<Long> serialNum, int code, Set<Long> shareSet);

    List<OpenAccountRecord> findByCredentialCodeAndOpenAccountStatusAndPayInstitutionType(String credentialCode, Integer openAccountStatus, Long payInstitutionType);

    boolean existsByCredentialCodeAndAccountNumberAndOpenAccountStatusAndChannelCodeIn(String credentialId, String accountNo, int status, Set<Long> shareChannels);

    Optional<OpenAccountRecord> findByCredentialCodeAndAccountNumberAndChannelCodeAndOpenAccountStatus(String customerId, String newBankCard, Long channelCode, int code);

    List<OpenAccountRecord> findByCredentialCodeAndAccountNumber(String credentialId, String accountNo);

    List<OpenAccountRecord> findByChannelCodeAndCredentialCodeAndOpenAccountStatus(Long channelCode, String idCardNum, int status);

    List<OpenAccountRecord> findByCredentialCodeAndAccountNumberAndOpenAccountStatus(String idNumber, String bankNum, int status);

    Optional<OpenAccountRecord> findFirstByCredentialCodeAndAccountNumberAndOpenAccountStatus(String idNumber, String userAccountNum, int code);

    List<OpenAccountRecord> findAllByCredentialCodeAndOpenAccountStatus(String credentialId, int status);

    Optional<OpenAccountRecord> findFirstByCredentialCodeAndOpenAccountStatus(String idNumber, int code);

    List<OpenAccountRecord> findByCredentialCodeInAndOpenAccountStatus(List<String> idCard, int state);

    Optional<OpenAccountRecord> findByAccountNumberAndChannelCodeAndOpenAccountStatus(String accountNumber, Long channelCode, int code);


    Optional<OpenAccountRecord> findFirstByUniqueCode(Long uniqueCode);

    Boolean existsByCredentialCodeAndAccountNumberAndOpenAccountStatus(String idCard, String accountNumber,Integer status);

    @Query("select distinct accountName from OpenAccountRecord  where customerType = ?1")
    List<String> findByCustomerType(int value);

    @Transactional
    void deleteByUniqueCode(Long uniqueCode);

    boolean existsByUniqueCodeAndChannelCode(Long uniqueCode, Long channelCode);

    Optional<OpenAccountRecord> findFirstByCredentialCodeAndOpenAccountStatusAndPayInstitutionTypeOrderByCreateTimeDesc(String idCard, int code,Long payIns);

    boolean existsByCredentialCodeAndAccountNumberAndOpenAccountStatusAndTelPhoneAndChannelCodeIn(String credentialId, String accountNo, int code, String telPhone,Set<Long> shareSet);

    Optional<OpenAccountRecord> findByChannelCodeAndAccountNumberAndCredentialCodeAndOpenAccountStatus(Long channelCode, String accountNumber, String credentialCode, int code);

    @Transactional
    void deleteByUniqueCodeAndChannelCode(Long uniqueCode, Long channelCode);

    List<OpenAccountRecord> findByAccountNumberAndOpenAccountStatus(String oldBankCard, int code);

    boolean existsByAccountNumberAndCredentialCodeAndOpenAccountStatusAndTelPhone(String bankNumber, String idNumber, int code, String telPhone);

    List<OpenAccountRecord> findByAccountNumberAndCredentialCodeAndOpenAccountStatusAndTelPhone(String bankNumber, String idNumber, int code, String telPhone);
}
