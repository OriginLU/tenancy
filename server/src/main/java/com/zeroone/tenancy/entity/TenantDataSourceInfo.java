package com.zeroone.tenancy.entity;

import com.zeroone.tenancy.enums.DataBaseTypeEnum;
import com.zeroone.tenancy.enums.DataSourceConfigStatusEnum;
import com.zeroone.tenancy.enums.DatasourceStatusEnum;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
@Data
@Accessors(chain = true)
@ToString
@Entity
@Table(name = "tenant_data_source_info")
public class TenantDataSourceInfo implements Serializable {


    private static final long serialVersionUID = 823067067135243374L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_code")
    private String tenantCode;
    /**
     * 数据库链接 url
     */
    @Column(name = "url")
    private String url;

    /**
     * 数据库
     */
    @Column(name = "database_name")
    private String database;
    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String username;
    /**
     * 密码
     */
    @Column(name = "password")
    private String password;
    /**
     * 服务名
     */
    @Column(name = "server_name")
    private String serverName;
    /**
     * 数据库类型 mongo/mysql
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DataBaseTypeEnum type;


    @Column(name = "enable_override")
    private Boolean enableOverride;

    /**
     * 数据源状态
     */
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private DataSourceConfigStatusEnum state;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Date modifyTime;


    @Column(name = "delete_status")
    private Integer deleteStatus;



    @Override
    public int hashCode() {
        return Objects.hash(id, tenantCode, url, database, username, password, serverName, type, enableOverride, state, createTime, modifyTime, deleteStatus);
    }
}
