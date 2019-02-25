package lz.skeleton.common;

import java.io.Serializable;
import java.util.Map;

public class ServiceRequest implements Serializable{
 
	private static final long serialVersionUID = -3472458263523488765L;
	
	private Map parameters;
	
	private String sessionId;

	public ServiceRequest(Map params) {
		parameters=params;
	}

	public Map getParameters() {
		return parameters;
	}

	// set
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	

}
