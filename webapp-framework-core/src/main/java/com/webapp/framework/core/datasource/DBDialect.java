package com.webapp.framework.core.datasource;

import java.util.ArrayList;
import java.util.List;

import com.webapp.framework.utils.tools.StringUtil;

public enum DBDialect
{
  UNKNOWN(0, "unknown", "unknown db", ""), 
  ORACLE(100, "oracle", "oracle any version ", "org.hibernate.dialect.OracleDialect"), 
  ORACLE9I(101, "oracle", "oracle9i", "org.hibernate.dialect.Oracle9iDialect"), 
  ORACLE10G(102, "oracle", "oracle10g", "org.hibernate.dialect.Oracle10gDialect"), 
  MYSQL(200, "mysql", "mysql", "org.hibernate.dialect.MySQLDialect"), 
  MYSQLINNODB(201, "mysqlinnodb", "MySQL with InnoDB", "org.hibernate.dialect.MySQLInnoDBDialect"), 
  MYSQLMYISAMDIALECT(202, "mysqlmyisam", "MySQL with MyISAM", "org.hibernate.dialect.MySQLMyISAMDialect"), 
  SQLSERVER(300, "sqlserver", "Microsoft SQL Server", "org.hibernate.dialect.SQLServerDialect"), 
  SYBASEASE(400, "SYBASE_ASE", "", "org.hibernate.dialect.SybaseDialect"), 
  SYBASEASA(500, "SYBASE_ASA", "", "org.hibernate.dialect.SybaseAnywhereDialect"), 
  DB2(600, "db2", "DB2", "org.hibernate.dialect.DB2Dialect"), 
  DB2400(601, "db2400", "DB2 AS/400", "org.hibernate.dialect.DB2400Dialect"), 
  DB2390(602, "db2390", "DB2 OS390", "org.hibernate.dialect.DB2390Dialect"), 
  POSTGRESQL(700, "postgresql", "PostgreSQL", "org.hibernate.dialect.PostgreSQLDialect");

  private int id;
  private String key;
  private String desc;
  private String dialect;

  private DBDialect(int id, String key, String desc, String dialect) { this.id = id;
    this.key = key;
    this.desc = desc;
    this.dialect = dialect; }

  public static int getId(String dialect)
  {
    DBDialect[] values = values();
    for (DBDialect db : values) {
      if (db.getDialect().equalsIgnoreCase(dialect)) {
        return db.getId();
      }
    }
    return UNKNOWN.getId();
  }

  public static DBDialect getDBDialect(String dialect) {
    DBDialect[] values = values();
    for (DBDialect db : values) {
      if (dialect.equals(db.getDialect())) {
        return db;
      }
    }
    return UNKNOWN;
  }
  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public static List<DBDialect> getDataBase(String key) {
    List list = new ArrayList();
    if (StringUtil.isNull(key)) {
      return list;
    }
    DBDialect[] values = values();
    for (DBDialect dBDialect : values) {
      if (dBDialect.getKey().equals(key)) {
        list.add(dBDialect);
      }
    }
    return list;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getDialect() {
    return this.dialect;
  }

  public void setDialect(String dialect) {
    this.dialect = dialect;
  }
}