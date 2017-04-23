package de.urkallinger.copymanager.exceptions;

public class FileCopierInProgressException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileCopierInProgressException(String message) {
		super(message);
	}
}
