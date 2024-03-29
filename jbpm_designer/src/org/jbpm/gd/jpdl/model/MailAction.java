package org.jbpm.gd.jpdl.model;

import org.jbpm.gd.common.model.AbstractNamedElement;


public class MailAction extends AbstractNamedElement implements ActionElement, AsyncableElement, MailElement {
	
	private String async;
	private String template;
	private String actors;
	private String to;
	private Subject subject;
	private Text text;
	
	public void setAsync(String newAsync) {
		String oldAsync = async;
		async = newAsync;
		firePropertyChange("async", oldAsync, newAsync);
	}
	
	public String getAsync() {
		return async;
	}
	
	public void setTemplate(String newTemplate) {
		String oldTemplate = template;
		template = newTemplate;
		firePropertyChange("template", oldTemplate, newTemplate);
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setActors(String newActors) {
		String oldActors = actors;
		actors = newActors;
		firePropertyChange("actors", oldActors, newActors);
	}
	
	public String getActors() {
		return actors;
	}
	
	public void setTo(String newTo) {
		String oldTo = to;
		to = newTo;
		firePropertyChange("to", oldTo, newTo);
	}
	
	public String getTo() {
		return to;
	}
	
	public void setSubject(Subject newSubject) {
		Subject oldSubject = subject;
		subject = newSubject;
		firePropertyChange("subject", oldSubject, newSubject);
	}
	
	public Subject getSubject() {
		return subject;
	}
	
	public void setText(Text newText) {
		Text oldText = text;
		text = newText;
		firePropertyChange("text", oldText, newText);
	}
	
	public Text getText() {
		return text;
	}

}
