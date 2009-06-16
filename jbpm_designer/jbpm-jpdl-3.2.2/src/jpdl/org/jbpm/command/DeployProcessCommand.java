package org.jbpm.command;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.zip.ZipInputStream;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.JbpmSchema;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Deploys a process, given as XML-String (be patient with Umlauts or something
 * like that) or par archive (byte-array). if both is given, the byte-array will
 * be preferred
 * 
 * The deplpoyed process definition is returned
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 * 
 */
public class DeployProcessCommand extends AbstractGetObjectBaseCommand {

  private static final long serialVersionUID = -5861811926680981061L;

  private String xml;

  private byte[] par;

  private static final Log log = LogFactory.getLog(DeployProcessCommand.class);

  public DeployProcessCommand() {
    }

  public DeployProcessCommand(byte[] par) {
    this.par = par;
  }

  public DeployProcessCommand(String xml) {
    this.xml = xml;
  }

  /**
   * @return deployed ProcessDefinition
   */
  public Object execute(JbpmContext jbpmContext) throws Exception {
    ProcessDefinition processDefinition = null;
    if (par != null && par.length > 0) {
      log.info("start parsing process from par");

      // Thanks to George Mournos who helped to improve this:
      ZipInputStream zipInputStream = new ZipInputStream(
          new ByteArrayInputStream(par));
      processDefinition = ProcessDefinition
          .parseParZipInputStream(zipInputStream);

      jbpmContext.deployProcessDefinition(processDefinition);
      log.info("deployment sucessfull");
    }
    else if (xml != null && xml.length() > 0) {
      log.info("parse process from String");
      log.debug("deploy process:\n" + xml);

      processDefinition = ProcessDefinition
          .parseXmlString(xml);

      jbpmContext.deployProcessDefinition(processDefinition);
      log.info("deployment sucessfull");
    }
    else
      throw new JbpmException("either xml string or par archive must be given.");
    return retrieveProcessDefinition(processDefinition);
  }

public byte[] getPar() {
    return par;
}

public void setPar(byte[] par) {
    this.par = par;
}

public String getXml() {
    return xml;
}

public void setXml(String xml) {
    this.xml = xml;
}

}
