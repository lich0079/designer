/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.identity.hibernate;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.List;

import org.apache.commons.logging.*;
import org.hibernate.cfg.*;
import org.hibernate.connection.*;
import org.hibernate.dialect.*;
import org.hibernate.engine.*;
import org.hibernate.mapping.*;
import org.hibernate.tool.hbm2ddl.*;
import org.hibernate.util.*;

public class IdentitySchema {

  private static final String IDENTITY_TABLE_PREFIX = "JBPM_ID_";
  
  Configuration configuration = null;
  Properties properties = null;
  Dialect dialect = null;
  Mapping mapping = null;
  String[] createSql = null;
  String[] dropSql = null;
  String[] cleanSql = null;

  ConnectionProvider connectionProvider = null;
  Connection connection = null;
  Statement statement = null;

  public IdentitySchema(Configuration configuration) {
    this.configuration = configuration;
    this.properties = configuration.getProperties();
    this.dialect = Dialect.getDialect(properties);
    try {
      // get the mapping field via reflection :-(
      Field mappingField = Configuration.class.getDeclaredField("mapping");
      mappingField.setAccessible(true);
      this.mapping = (Mapping) mappingField.get(configuration);
    } catch (Exception e) {
      throw new RuntimeException("couldn't get the hibernate mapping", e);
    }
  }

  // scripts lazy initializations /////////////////////////////////////////////
  
  public String[] getCreateSql() {
    if (createSql==null) {
      createSql = configuration.generateSchemaCreationScript(dialect);
    }
    return createSql;
  }
  
  public String[] getDropSql() {
    if (dropSql==null) {
      dropSql = configuration.generateDropSchemaScript(dialect);
    }
    return dropSql;
  }
  
  public String[] getCleanSql() {
    if (cleanSql==null) {
      // loop over all foreign key constraints
      List dropForeignKeysSql = new ArrayList();
      List createForeignKeysSql = new ArrayList();
      Iterator iter = configuration.getTableMappings();
      while ( iter.hasNext() ) {
        Table table = ( Table ) iter.next();
        if ( table.isPhysicalTable() ) {
          Iterator subIter = table.getForeignKeyIterator();
          while ( subIter.hasNext() ) {
            ForeignKey fk = ( ForeignKey ) subIter.next();
            if ( fk.isPhysicalConstraint() ) {
              // collect the drop key constraint
              dropForeignKeysSql.add( fk.sqlDropString( 
                  dialect, 
                  properties.getProperty(Environment.DEFAULT_CATALOG),
                  properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
              createForeignKeysSql.add( fk.sqlCreateString( 
                  dialect,
                  mapping,
                  properties.getProperty(Environment.DEFAULT_CATALOG),
                  properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
            }
          }
        }
      }

      List deleteSql = new ArrayList();
      iter = configuration.getTableMappings();
      while (iter.hasNext()) {
        Table table = (Table) iter.next();
        deleteSql.add("delete from "+table.getName());
      }

      List cleanSqlList = new ArrayList();
      cleanSqlList.addAll(dropForeignKeysSql);
      cleanSqlList.addAll(deleteSql);
      cleanSqlList.addAll(createForeignKeysSql);
      
      cleanSql = (String[]) cleanSqlList.toArray(new String[cleanSqlList.size()]);
    }
    return cleanSql;
  }

  // runtime table detection //////////////////////////////////////////////////
  
  public boolean hasIdentityTables() {
    return (getIdentityTables().size()>0);
  }

  public List getIdentityTables() {
    // delete all the data in the jbpm tables
    List jbpmTableNames = new ArrayList();
    try {
      createConnection();
      ResultSet resultSet = connection.getMetaData().getTables("", "", null, null);
      while(resultSet.next()) {
        String tableName = resultSet.getString("TABLE_NAME");
        if ( (tableName!=null)
             && (tableName.length()>5)
             && (IDENTITY_TABLE_PREFIX.equalsIgnoreCase(tableName.substring(0,5))) ) {
          jbpmTableNames.add(tableName);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("couldn't get the jbpm table names");
    } finally {
      closeConnection();
    }
    return jbpmTableNames;
  }
  
  // script execution methods /////////////////////////////////////////////////
  
  public void dropSchema() {
    execute( getDropSql() );
  }

  public void createSchema() {
    execute( getCreateSql() );
  }

  public void cleanSchema() {
    execute( getCleanSql() );
  }

  public void saveSqlScripts(String dir, String prefix) {
    try {
      new File(dir).mkdirs();
      saveSqlScript(dir+"/"+prefix+".drop.sql", getDropSql());
      saveSqlScript(dir+"/"+prefix+".create.sql", getCreateSql());
      saveSqlScript(dir+"/"+prefix+".clean.sql", getCleanSql());
      new SchemaExport(configuration)
        .setDelimiter(getSqlDelimiter())
        .setOutputFile(dir+"/"+prefix+".drop.create.sql")
        .create(true, false);
    } catch (Exception e) {
      throw new RuntimeException("couldn't generate scripts", e);
    }
  }

  // main /////////////////////////////////////////////////////////////////////
  
  public static void main(String[] args) {
    try {
      if ( (args!=null)
           && (args.length==1)
           && ("create".equalsIgnoreCase(args[0])) ) {
        new IdentitySchema(IdentitySessionFactory.createConfiguration()).createSchema();
      } else if ( (args!=null)
              && (args.length==1)
              && ("drop".equalsIgnoreCase(args[0])) ) {
        new IdentitySchema(IdentitySessionFactory.createConfiguration()).dropSchema();
      } else if ( (args!=null)
              && (args.length==1)
              && ("clean".equalsIgnoreCase(args[0])) ) {
        new IdentitySchema(IdentitySessionFactory.createConfiguration()).cleanSchema();
      } else if ( (args!=null)
              && (args.length==3)
              && ("scripts".equalsIgnoreCase(args[0])) ) {
        new IdentitySchema(IdentitySessionFactory.createConfiguration()).saveSqlScripts(args[1], args[2]);
      } else {
        System.err.println("syntax: JbpmSchema create");
        System.err.println("syntax: JbpmSchema drop");
        System.err.println("syntax: JbpmSchema clean");
        System.err.println("syntax: JbpmSchema scripts <dir> <prefix>");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  private void saveSqlScript(String fileName, String[] sql) throws FileNotFoundException {
    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
    PrintStream printStream = new PrintStream(fileOutputStream);
    for (int i=0; i<sql.length; i++) {
      printStream.println(sql[i]+getSqlDelimiter());
    }
  }
  
  // sql script execution /////////////////////////////////////////////////////

  public void execute(String[] sqls) {
    String sql = null;
    String showSqlText = properties.getProperty("hibernate.show_sql");
    boolean showSql = ("true".equalsIgnoreCase(showSqlText));

    try {
      createConnection();
      statement = connection.createStatement();
      
      for (int i=0; i<sqls.length; i++) {
        sql = sqls[i];
        String delimitedSql = sql+getSqlDelimiter();
        
        if (showSql) log.debug(delimitedSql);
        statement.executeUpdate(delimitedSql);
      }
    
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("couldn't execute sql '"+sql+"'", e);
    } finally {
      closeConnection();
    }
  }

  private void closeConnection() {
    try {
      if (statement!=null) statement.close();
      if (connection!=null) {
        JDBCExceptionReporter.logWarnings( connection.getWarnings() );
        connection.clearWarnings();
        connectionProvider.closeConnection(connection);
        connectionProvider.close();
      }
    }
    catch(Exception e) {
      System.err.println( "Could not close connection" );
      e.printStackTrace();
    }
  }

  private void createConnection() throws SQLException {
    connectionProvider = ConnectionProviderFactory.newConnectionProvider(properties);
    connection = connectionProvider.getConnection();
    if ( !connection.getAutoCommit() ) {
      connection.commit();
      connection.setAutoCommit(true);
    }
  }
  
  public Properties getProperties() {
    return properties;
  }

  // sql delimiter ////////////////////////////////////////////////////////////
  
  private static String sqlDelimiter = null;
  private synchronized String getSqlDelimiter() {
    if (sqlDelimiter==null) {
      sqlDelimiter = properties.getProperty("jbpm.sql.delimiter", ";");
    }
    return sqlDelimiter;
  }

  // logger ///////////////////////////////////////////////////////////////////
  
  private static final Log log = LogFactory.getLog(IdentitySchema.class);
}
