/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Oct 201116:19:20
 * Workfile::  StaleNodeStateException.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice.interceptor;

/**
 * @author jkochhar
 * 
 */
public class StaleNodeStateException extends RuntimeException {

	/**
	 * 
	 */
	public StaleNodeStateException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StaleNodeStateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public StaleNodeStateException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StaleNodeStateException(Throwable cause) {
		super(cause);
	}

}
