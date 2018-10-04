package ca.concordia.httpClient.lib;

public class PostRequest extends ClientHttpRequest {

	//josn String represents object, set from makePostRequestObject
	private String body;
	
	//set to be true if above is not null
	private boolean hasBody;
	
	private String file;
	
	private boolean hasFile;

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
		this.hasBody = true;
		this.hasFile = false;
		this.file = null;
	}

	/**
	 * @return the hasBody
	 */
	public boolean isHasBody() {
		return hasBody;
	}

	/**
	 * @param hasBody the hasBody to set
	 */
	public void setHasBody(boolean hasBody) {
		this.hasBody = hasBody;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
		this.hasFile = true;
		this.body = null;
		this.hasBody = false;
	}

	/**
	 * @return the hasFile
	 */
	public boolean isHasFile() {
		return hasFile;
	}

	/**
	 * @param hasFile the hasFile to set
	 */
	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}
	
	
	//return well formatted string representing http post request based on HTTP protocals. 
	//check get for detail
	@Override
	public String toString() {
		return "";
	}
}
