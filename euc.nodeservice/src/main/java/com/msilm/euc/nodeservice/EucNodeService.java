/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   20 Jul 201111:07:10
 * Workfile::  EucNodeService.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sri.nodeservice.core.nodeaccess.service.NodeAccessProvided;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.model.NodeSet;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.CreateNodeRq;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.CreateNodeRs;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.GetNodeRq;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.GetNodeRs;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ListNodesRq;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ListNodesRs;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.SearchFilter;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.UpdateNodeRq;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.UpdateNodeRs;
import org.sri.nodeservice.core.nodedef.service.NodeDefProvided;
import org.sri.nodeservice.core.nodedef.service.model.NodeDefTO;
import org.sri.nodeservice.core.nodedef.service.rsrq.GetNodeDefRq;
import org.sri.nodeservice.core.nodedef.service.rsrq.GetNodeDefRs;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDef;
import org.sri.nodeservice.core.nodemgr.nodedef.problem.ReportBuilder;
import org.sri.nodeservice.core.nodemgr.util.transform.NodeDefTransformer;
import org.sri.nodeservice.core.nodemgr.validation.IValidationCallback;
import org.sri.nodeservice.transform.excel.Sheet;

import com.msilm.euc.nodeservice.util.EucUtils;
import com.msilm.euc.nodeservice.util.Pageable;
import com.msilm.euc.service.IEucExcelInstructionProvider;
import com.msilm.euc.service.IEucNodeDefProvider;
import com.msilm.euc.service.persistence.IEucNodePersistenceHandler;
import com.msilm.euc.service.persistence.NodeWrapper;

/**
 * @author jkochhar
 * 
 */
public class EucNodeService implements NodeAccessProvided, NodeDefProvided {

	private IEucNodeDefProvider eucNodeDefProvider;
	private IEucExcelInstructionProvider eucExcelInstructionProvider;
	private IEucNodePersistenceHandler eucNodePersistenceHandler;
	
	private static final int reValidateBatchSize = 100;

	@Override
	public GetNodeRs getNode(GetNodeRq rq) {
		GetNodeRs rs = new GetNodeRs();
		Node node = eucNodePersistenceHandler.getNode(rq.getId());
		validateNode(node, new ReportBuilder());
		if((node.getErrors() != null && node.getErrors().getErrorDetails().size() > 0)){
			rs.setErrors(node.getErrors());
		}
		rs.setNode(node);
		rs.setSuccess(true);
		return rs;
	}

	@Override
	public ListNodesRs retrieveNodes(ListNodesRq rq) {

		int limit = rq.getLimit();
		int start = rq.getStart();
		ListNodesRs rs = new ListNodesRs();
		List<Node> nodes = new ArrayList<Node>();

		if ("nodeDefs".equals(rq.getNodeType())) {
			getNodeDefs(limit, start, rs, nodes);
		} 
		else if("excelInstructions".equals(rq.getNodeType())){
			getExcelInstructions(rq, limit, start, rs, nodes);
		}
		else if("uploadedNodes".equals(rq.getNodeType())){
			eucNodePersistenceHandler.retrieveUploadedNodes(start, limit, rs);
		}
		else {
			getExcelNodes(rq, limit, start, rs);
		}
		
		rs.setSuccess(true);
		return rs;
	}
	
	@Override
	public UpdateNodeRs updateNode(UpdateNodeRq rq) {
		
		UpdateNodeRs rs = new UpdateNodeRs();
		
		//Handle other actions..
		Map<String, String> fields = rq.getNode().getFields();
		if(fields.get("subMethod") != null){
			
			if("reValidate".equals(fields.get("subMethod"))){
				boolean isValid = validateAllNodes(rq.getNode().getId(), 0, reValidateBatchSize);
				//if Valid
				String valid = isValid ? "Y" : "N";
				eucNodePersistenceHandler.updateRootNode(rq.getNode().getId(), valid, "N", EucUtils.getLoggedInUser());
			}else if("submitToWarehouse".equals(fields.get("subMethod"))){
				//TODO Implement Serialisation service
				eucNodePersistenceHandler.updateRootNode(rq.getNode().getId(), "Y", "Y", EucUtils.getLoggedInUser());
			}
			rs.setNode(rq.getNode());
			rs.setSuccess(true);
			return rs;
		}
		
		{
			//Update the Excel Row Node
			List<String> fieldsToExclude = new ArrayList<String>();
			fieldsToExclude.add("method");
			fieldsToExclude.add("sessionToken");
			fieldsToExclude.add("node_version");
			fieldsToExclude.add("rootNodeId");
			fieldsToExclude.add("root_node_version"); //root_node_version
			
			Node node = rq.getNode();
			String rootNodeId = node.getFields().get("rootNodeId");
			
			validateNode(node, new ReportBuilder());
			
			if((node.getErrors() != null && node.getErrors().getErrorDetails().size() > 0)){
				rs.setSuccess(false);
				rs.setErrors(node.getErrors());
			}else{
				eucNodePersistenceHandler.updateNode(node, fieldsToExclude, "Y",EucUtils.getLoggedInUser());
				rs.setSuccess(true);
			}
			rs.setNode(node);
		}
		return rs;
	}

	/**
	 * 
	 */
	@Override
	public CreateNodeRs createNode(CreateNodeRq rq) {
		
		Map<String, NodeSet> childSets = rq.getNode().getChildSets();
		
		//This version only supports single child set per Node.
		if(childSets != null && childSets.size() > 1){
			throw new RuntimeException("Single child NodeSet is allowed");
		}

		//Validate the Node
		validateNode(rq.getNode(), new ReportBuilder());
		
		//Persist the validated Node
		NodeWrapper rootNodeWrapper = new NodeWrapper();
		rootNodeWrapper.setNode(rq.getNode());
		rootNodeWrapper.setRoot(true);
		rootNodeWrapper.setDescription(rq.getNode().getFields().get("description"));
		rootNodeWrapper.setUser(rq.getNode().getFields().get("user"));
		String childNodeType = eucNodePersistenceHandler.saveNode(rootNodeWrapper, EucUtils.getLoggedInUser());

		//Create Response
		CreateNodeRs rs = new CreateNodeRs();
		//Dynamic node with rootNodeId and child Node Type to call retrieve in the subsequent request.
		Node dynamicNode = new Node(rootNodeWrapper.getNode().getId(), childNodeType);
		rs.setNode(dynamicNode);
		rs.setSuccess(true);
		return rs;
	}
	
	@Override
	public GetNodeDefRs getNodeDefinition(GetNodeDefRq rq) {
		
		NodeDef nodeDef = eucNodeDefProvider.getNodeDefinition(rq.getNodeType());
		GetNodeDefRs rs = new GetNodeDefRs();
		NodeDefTransformer transformer = new NodeDefTransformer();
		NodeDefTO nodeDefTo = transformer.transformNodeDef(nodeDef);
		rs.setNodeDefinition(nodeDefTo);
		
		return rs;
	}
	
	/**
	 * 
	 * @param node
	 */
	private void validateNode(Node node, IValidationCallback builder){
		
		String nodeType = node.getType();
		NodeDef nodeDef = eucNodeDefProvider.getNodeDefinition(nodeType);
		nodeDef.validate(node, builder);
		
		EucUtils.mapResponse(node, builder.getRoot());
	}
	
	private void getExcelNodes(ListNodesRq rq, int limit, int start,
			ListNodesRs rs) {
		//Handle Excel Node Types
		String rootNodeId = null;
		List<SearchFilter> filters = rq.getFilters();
		for (SearchFilter searchFilter : filters) {
			if("rootNodeIdDyn".equals(searchFilter.getColumnKey())){
				rootNodeId = searchFilter.getColumnValue();
			}
		}
		
		eucNodePersistenceHandler.retrieveNodes(rootNodeId, start, limit, rs);
		//Re-Validate Each Node to associate errors if any
		List<Node> pagedNodes = rs.getNodes();
		for (Node node : pagedNodes) {
			validateNode(node, new ReportBuilder());
		}
	}

	private void getExcelInstructions(ListNodesRq rq, int limit, int start,
			ListNodesRs rs, List<Node> nodes) {
		List<SearchFilter> filters = rq.getFilters();
		String nodeDefType = null;
		for (SearchFilter searchFilter : filters) {
			String columnKey = searchFilter.getColumnKey();
			if("nodeTypeDef".equals(columnKey)){
				nodeDefType = searchFilter.getColumnValue();
				break;
			}
		}
		List<Sheet> sheets = eucExcelInstructionProvider.getSheetsByNodeDefType(nodeDefType);
		for (Sheet sheet : sheets) {
			Node node = new Node(sheet.getSheetId(), sheet.getSheetRootNodeType());
			node.setField("description", sheet.getSheetDescription());
			node.setField("childNodeType", sheet.getSheetNodeType());
			nodes.add(node);
		}
		
		Pageable<Node> page = new Pageable<Node>(nodes, limit);
		if (start == 0) {
			page.setPage(1);
		} else {
			page.setPage((start / limit) + 1);
		}
		List<Node> pagedNodes = page.getListForPage();
		
		rs.setNodes(nodes);
		rs.setTotalCount(nodes.size());
	}

	private void getNodeDefs(int limit, int start, ListNodesRs rs,
			List<Node> nodes) {
		List<NodeDef> nodeDefs = eucNodeDefProvider.getNodeDefinitions();
		for (NodeDef nodeDef : nodeDefs) {
			Node node = new Node(nodeDef.getNodeDefId(), nodeDef.getNodeDefId());
			node.setField("description", "Description :"+ nodeDef.getNodeDefId());
			nodes.add(node);
		}
		
		Pageable<Node> page = new Pageable<Node>(nodes, limit);
		if (start == 0) {
			page.setPage(1);
		} else {
			page.setPage((start / limit) + 1);
		}
		List<Node> pagedNodes = page.getListForPage();
		
		rs.setNodes(pagedNodes);
		rs.setTotalCount(nodes.size());
	}
	
	private boolean validateAllNodes(String rootNodeId, int start, int limit){
		boolean isValid = true;
		
		ListNodesRs rs = new ListNodesRs();
		eucNodePersistenceHandler.retrieveNodes(rootNodeId, start, limit, rs);
		
		List<Node> nodes = rs.getNodes();
		for (Node node : nodes) {
			validateNode(node, new ReportBuilder());
			if(node.getErrors() != null && node.getErrors().getErrorDetails().size() > 0){
				isValid = false;
				break;
			}
		}
		int totalCount = rs.getTotalCount();
		if(isValid && (start+limit < totalCount)){
			isValid = validateAllNodes(rootNodeId, start+limit, limit);
		}
		return isValid;
	}

	/**
	 * @param eucNodeDefProvider
	 *            the eucNodeDefProvider to set
	 */
	public void setEucNodeDefProvider(IEucNodeDefProvider eucNodeDefProvider) {
		this.eucNodeDefProvider = eucNodeDefProvider;
	}

	/**
	 * @param eucExcelInstructionProvider the eucExcelInstructionProvider to set
	 */
	public void setEucExcelInstructionProvider(
			IEucExcelInstructionProvider eucExcelInstructionProvider) {
		this.eucExcelInstructionProvider = eucExcelInstructionProvider;
	}
	
	/**
	 * @param eucNodePersistenceHandler the eucNodePersistenceHandler to set
	 */
	public void setEucNodePersistenceHandler(
			IEucNodePersistenceHandler eucNodePersistenceHandler) {
		this.eucNodePersistenceHandler = eucNodePersistenceHandler;
	}
}
