package ca.concordia.httpClient.lib;

import java.io.Serializable;

public abstract class ClientHttpRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Enum<HttpMethod> method;
	
	private String URI;
	
	private String version = "HTTP/1.0";
	
	private String host;

}
