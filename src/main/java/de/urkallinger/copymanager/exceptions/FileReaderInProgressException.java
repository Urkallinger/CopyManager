package de.urkallinger.copymanager.exceptions;

public class FileReaderInProgressException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FileReaderInProgressException(String message) {
		super(message);
	}
}
