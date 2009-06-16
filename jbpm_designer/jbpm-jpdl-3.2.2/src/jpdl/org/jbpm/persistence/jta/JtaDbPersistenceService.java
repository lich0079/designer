package org.jbpm.persistence.jta;

import javax.naming.InitialContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.util.JTAHelper;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;

public class JtaDbPersistenceService extends DbPersistenceService {

  private static final long serialVersionUID = 1L;
  
  private static Log log = LogFactory.getLog(JbpmContext.class);  
  
  boolean isJtaTxCreated = false;

  public JtaDbPersistenceService(DbPersistenceServiceFactory persistenceServiceFactory) {
    super(persistenceServiceFactory);
    
    if (! isCurrentJtaTransactionAvailable()) {
      beginJtaTransaction();
      isJtaTxCreated = true;
    }
  }

  public void close() {
    super.close();

    if (isJtaTxCreated) {
      endJtaTransaction();
    }
  }

  boolean isCurrentJtaTransactionAvailable() {
    SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) persistenceServiceFactory.getSessionFactory();
    return JTAHelper.isTransactionInProgress(sessionFactoryImplementor);
  }

  void beginJtaTransaction() {
    try {
    	log.debug("start user JTA transaction");
      getUserTransaction().begin();
    } catch (Exception e) {
      throw new JbpmException("couldn't start JTA transaction", e);
    }
  }

  void endJtaTransaction() {
    int status = -1;
	log.debug("end user JTA transaction");
    UserTransaction userTransaction = getUserTransaction();
    try {
      status = userTransaction.getStatus();
    } catch (SystemException e) {
      throw new JbpmException("couldn't get status for user transaction", e); 
    }
    
    boolean isRollback = JTAHelper.isRollback(status);
    if (isRollback) {
      log.debug("end jta transation with ROLLBACK");
      try {
        userTransaction.rollback();
      } catch (Exception e) {
        throw new JbpmException("couldn't rollback JTA transaction", e);
      }
    } else {
      log.debug("end jta transation with COMMIT");
      try {
        userTransaction.commit();
      } catch (Exception e) {
        throw new JbpmException("couldn't commit JTA transaction", e);
      }
    }
  }
  
  UserTransaction getUserTransaction() {
    UserTransaction userTransaction = null;
    if (userTransaction == null) {
      String jndiName = "UserTransaction";
      try {
        userTransaction = (UserTransaction) new InitialContext().lookup(jndiName);
      } catch (Exception e) {
        throw new JbpmException("couldn't lookup UserTransaction in JNDI with name "+jndiName, e);
      }
    }
    return userTransaction;
  }


  public boolean isJtaTxCreated() {
    return isJtaTxCreated;
  }
  public void setJtaTxCreated(boolean isJtaTxCreated) {
    this.isJtaTxCreated = isJtaTxCreated;
  }
}
