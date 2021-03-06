package com.mastercard.exception;

import org.springframework.boot.ExitCodeGenerator;

public class CityLoadException extends Exception implements ExitCodeGenerator {

	private static final long serialVersionUID = 2552863626296511285L;	
	
	private int _exitCode;

	public CityLoadException(int exitCode, String message) {
		super(message);
		_exitCode = exitCode;
	}

	@Override
	public int getExitCode() {
		return _exitCode;
	}

}
