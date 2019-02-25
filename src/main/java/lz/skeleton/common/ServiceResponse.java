package lz.skeleton.common;

import java.io.Serializable;
import java.util.Map;

public class ServiceResponse implements Serializable{
 
	private static final long serialVersionUID = 8252215468375932861L;

	private String result =null;
	
	private String token=null;

    private String responseCode;
    
    private String resonseMsg;

	public ServiceResponse(String result, String responseCode, String resonseMsg) {
		super();
		this.setResult(result);
		this.setResponseCode(responseCode);
		this.setResonseMsg(resonseMsg);
	}

	public ServiceResponse(String responseCode, String resonseMsg) {
		super();
		this.setResponseCode(responseCode);
		this.setResonseMsg(resonseMsg);
	}

	public ServiceResponse(String result) {
		super();
		this.setResult(result);
	}

	public ServiceResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResonseMsg() {
		return resonseMsg;
	}

	public void setResonseMsg(String resonseMsg) {
		this.resonseMsg = resonseMsg;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
