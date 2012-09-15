/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   17 Oct 201110:31:13
 * Workfile::  EucServiceExceptionTransformer.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.mule.transformer;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.security.access.AccessDeniedException;
import org.sri.nodeservice.transform.common.exception.IServiceExceptionTransformer;
import org.sri.nodeservice.transform.common.exception.ServiceException;
import org.sri.nodeservice.transform.common.util.TransformContext;

import com.msilm.euc.nodeservice.interceptor.StaleNodeStateException;

/**
 * @author jkochhar
 *
 */
public class EucServiceExceptionTransformer implements IServiceExceptionTransformer{

	/* (non-Javadoc)
	 * @see org.sri.nodeservice.mule.transform.util.IServiceExceptionTransformer#transform(java.lang.Throwable)
	 */
	@Override
	public ServiceException transform(TransformContext context, Throwable throwable) {
		
		String description = throwable.getLocalizedMessage();
		String code = "999";

		if(throwable.getCause() != null){
			Object cause = (Object)throwable.getCause();
			if(cause instanceof AccessDeniedException){
				description = "Insufficient permissions.";
				code = "103";
			}else if(cause instanceof CannotAcquireLockException){
				description = "Another user has got the wirte lock.";
				code = "104";
			}else if(cause instanceof StaleNodeStateException){
				description = "Another user has updated the data, please reload the records";
				code = "105";
			}
		}
		
		return new ServiceException(code, description);
	}
	
	
}
