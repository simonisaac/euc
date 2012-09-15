/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   16 Aug 201109:56:56
 * Workfile::  NodeWrapper.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service.persistence;

import java.sql.Date;

import org.sri.nodeservice.core.nodeaccess.service.model.Node;

/**
 * @author jkochhar
 * 
 */
public class NodeWrapper {

	private Node node;

	private boolean root = false;
	private boolean valid = false;
	private boolean warehouseSubmitted = false;
	private String user;
	private String description;
	private Date dateCreated;

	private Long parentNodeId;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isWarehouseSubmitted() {
		return warehouseSubmitted;
	}

	public void setWarehouseSubmitted(boolean warehouseSubmitted) {
		this.warehouseSubmitted = warehouseSubmitted;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the parentNodeId
	 */
	public Long getParentNodeId() {
		return parentNodeId;
	}
	
	/**
	 * @param parentNodeId the parentNodeId to set
	 */
	public void setParentNodeId(Long parentNodeId) {
		this.parentNodeId = parentNodeId;
	}
}
