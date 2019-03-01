package com.dangdang.reader.execption;

import com.dangdang.execption.DDException;

public class PdfException extends DDException {

	private static final long serialVersionUID = 1L;

	public PdfException(int errorCode) {
		super(errorCode);
	}
	
	public PdfException(int errorCode, String detailMessage) {
		super(detailMessage);
		setErrorCode(errorCode);
	}

	public PdfException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public PdfException(String detailMessage) {
		super(detailMessage);
	}

	public PdfException(Throwable throwable) {
		super(throwable);
	}
	
	
	
	

}
