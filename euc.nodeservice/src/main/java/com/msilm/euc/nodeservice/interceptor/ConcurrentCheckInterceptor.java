/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Oct 201112:16:34
 * Workfile::  EucNodeServiceInterceptor.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice.interceptor;

import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.UpdateNodeRq;

import com.msilm.euc.service.persistence.IEucNodePersistenceHandler;

/**
 * @author jkochhar
 * 
 */
public class ConcurrentCheckInterceptor implements MethodInterceptor {

	private Logger log = Logger.getLogger(ConcurrentCheckInterceptor.class);
	
	private IEucNodePersistenceHandler eucNodePersistenceHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept
	 * .MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		Object[] arguments = invocation.getArguments();
		UpdateNodeRq rq = (UpdateNodeRq) arguments[0];
		Map<String, String> fields = rq.getNode().getFields();
		
		if (fields.get("subMethod") != null) {

			log.info("Performing version check and locking for update...RootNode["+rq.getNode().getId()+"]");
			
			String rootNodeId = rq.getNode().getId();
			String old_Version = rq.getNode().getFields().get("node_version");
			Node node = eucNodePersistenceHandler.getNode(rootNodeId);
			if (old_Version.equals(node.getFields().get("node_version"))) {
				// Acquire the lock to protect from concurrent update
				eucNodePersistenceHandler.selectForUpdate(rootNodeId);
			} else {
				throw new StaleNodeStateException(
						"Record(s) has been updated by another user.");
			}
			
		} else {
			
			log.info("Performing version check and locking for update...Node["+rq.getNode().getId()+"]");
			
			String nodeId = rq.getNode().getId();
			String rootNodeId = rq.getNode().getFields().get("rootNodeId");
			String old_Version = rq.getNode().getFields().get("node_version");
			String old_RootNodeVersion = rq.getNode().getFields()
					.get("root_node_version");

			Node node = eucNodePersistenceHandler.getNode(nodeId);
			Node rootNode = eucNodePersistenceHandler.getNode(rootNodeId);

			if (old_Version.equals(node.getFields().get("node_version"))
					&& old_RootNodeVersion.equals(rootNode.getFields().get(
							"node_version"))) {
				// Acquire the lock to protect from concurrent update
				eucNodePersistenceHandler.selectForUpdate(rootNodeId);
				eucNodePersistenceHandler.selectForUpdate(nodeId);
			} else {
				throw new StaleNodeStateException(
						"Record(s) has been updated by another user.");
			}
			
		}

		Object obj = invocation.proceed();
		return obj;
	}

	/**
	 * @param eucNodePersistenceHandler
	 *            the eucNodePersistenceHandler to set
	 */
	public void setEucNodePersistenceHandler(
			IEucNodePersistenceHandler eucNodePersistenceHandler) {
		this.eucNodePersistenceHandler = eucNodePersistenceHandler;
	}

}
