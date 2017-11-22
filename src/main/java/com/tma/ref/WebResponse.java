package com.tma.ref;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WebResponse {
	RequestType requestType;
	ResponseStatus status;
	String error;
	Object result;

	public WebResponse(RequestType requestType, ResponseStatus status, String error, Object result) {
		/*
		 * This class create a response for web service.
		 * Transform this to JSON make a new response.
		 */
		this.requestType = requestType;
		this.status = status;
		this.error = error;
		this.result = result;
	}

	public RequestType getRequestType() {
		//Direct non-http Request Type that get that response;
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "WebResponse [requestType=" + requestType + ", status=" + status + ", error=" + error + ", result="
				+ result + "]";
	}
	
	public String toJsonStr() {
		try {
			return Libraries.jsonObj.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}