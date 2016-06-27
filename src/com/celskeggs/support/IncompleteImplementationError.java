package com.celskeggs.support;

import java.lang.String;
import java.lang.VirtualMachineError;

public class IncompleteImplementationError extends VirtualMachineError {

	public IncompleteImplementationError() {
		super();
	}

	public IncompleteImplementationError(String message) {
		super(message);
	}
}
