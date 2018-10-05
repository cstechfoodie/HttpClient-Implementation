package ca.concordia.httpClient.lib;

public class ClientHttpResponse {
	private String header;
	private String body;
	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}
	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append(header).append(body);
		return bld.toString();
	}
}
