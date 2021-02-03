package com.zeroone.tenancy.constants;

public interface MysqlConstants {


    String QUERY_SCHEMA_SQL = "select count(sc.SCHEMA_NAME) from information_schema.SCHEMATA sc where sc.SCHEMA_NAME = ? ";

    String CREATE_DATABASE_SQL = "create database `{}` default character set ?";

    String USE_DATABASE_SQL = "use `{}`";

    String TEST_QUERY = "select 1";

    String DEFAULT_CHARSET = "utf8mb4";
}
