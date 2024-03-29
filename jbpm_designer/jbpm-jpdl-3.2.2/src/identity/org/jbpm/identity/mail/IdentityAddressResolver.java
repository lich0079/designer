package org.jbpm.identity.mail;

import org.jbpm.JbpmContext;
import org.jbpm.identity.User;
import org.jbpm.identity.hibernate.IdentitySession;
import org.jbpm.mail.AddressResolver;
import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

/**
 * translates actorIds into email addresses with the jBPM identity module.
 * Only user actorIds are resolved to their email addresses.  Group actorIds return null. 
 */
public class IdentityAddressResolver implements AddressResolver, ServiceFactory, Service {

  private static final long serialVersionUID = 1L;

  public Object resolveAddress(String actorId) {
    String emailAddress = null;
    IdentitySession identitySession = new IdentitySession(JbpmContext.getCurrentJbpmContext().getSession());
    User user = identitySession.getUserByName(actorId);
    if (user!=null) {
      emailAddress = user.getEmail();
    }
    return emailAddress;
  }

  public Service openService() {
    return this;
  }
  public void close() {
  }
}
