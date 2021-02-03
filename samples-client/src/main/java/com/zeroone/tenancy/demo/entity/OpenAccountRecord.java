package com.zeroone.tenancy.demo.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.ZonedDateTime;


@Data
@Generated
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "open_account_record")
public class OpenAccountRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "req_serial_number")
    private String reqSerialNumber;

    @Column(name = "account_name")
    private String accountName;


    @Column(name = "account_number")
    private String accountNumber;


    @Column(name = "account_bank_name")
    private String accountBankName;


    @Column(name = "tel_phone")
    private String telPhone;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "credential_code")
    private String credentialCode;


    @Column(name = "open_account_status")
    private Integer openAccountStatus;


    @Column(name = "message")
    private String message;

    @Column(name = "operation_id")
    private Long operationId;


    @Column(name = "remark")
    private String remark;


    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "modify_time")
    private ZonedDateTime modifyTime;


    @Column(name = "delete_status")
    private Integer deleteStatus;



}
