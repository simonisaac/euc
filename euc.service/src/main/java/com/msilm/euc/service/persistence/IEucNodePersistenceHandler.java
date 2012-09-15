/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   15 Aug 201114:52:57
 * Workfile::  IEucNodePersistenceHandler.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service.persistence;

import java.util.List;

import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ListNodesRs;



/**
 * @author jkochhar
 *
 */
public interface IEucNodePersistenceHandler {
	
	/**
	 * 
	 * @param wrapper
	 * @param user
	 * @return
	 */
	public String saveNode(NodeWrapper wrapper, String user);
	
	/**
	 * Updates the ListNodesRs with the child nodes list
	 * 
	 * @param rootNodeType
	 * @param rootNodeId
	 * @param start
	 * @param limit
	 * @param rs
	 */
	public void retrieveNodes(String rootNodeId, int start, int limit, ListNodesRs rs);
	
	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public Node getNode(String nodeId);
	
	/**
	 * 
	 * @param start
	 * @param limit
	 * @param rs
	 */
	public void retrieveUploadedNodes(int start, int limit, ListNodesRs rs);
	
	/**
	 * 
	 * @param node
	 * @param exclusionFields
	 * @param valid
	 * @param user
	 * @return
	 */
	public Node updateNode(Node node, List<String> exclusionFields, String valid, String user);
	
	/**
	 * 
	 * @param nodeId
	 * @param valid
	 * @param warehouseSubmit
	 * @param user
	 * @return
	 */
	public int updateRootNode(String nodeId, String valid, String warehouseSubmit, String user);
	
	
	/**
	 * 
	 * @param rootNodeId
	 * @return
	 */
	public int selectForUpdate(String nodeId);

}
