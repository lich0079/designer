package org.jbpm.persistence.jta;

import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Service;


public class JtaDbPersistenceServiceFactory extends DbPersistenceServiceFactory {

  private static final long serialVersionUID = 1L;

  public JtaDbPersistenceServiceFactory() {
    setCurrentSessionEnabled(true);
    setTransactionEnabled(true);
  }
  
  public Service openService() {
    return new JtaDbPersistenceService(this);
  }

  public void close() {
  }
}
