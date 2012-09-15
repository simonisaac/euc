/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   15 Aug 201114:58:11
 * Workfile::  EucNodePersistenceHandler.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service.persistence;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.model.NodeSet;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ListNodesRs;

/**
 * @author jkochhar
 * 
 */
public class EucNodePersistenceHandler implements IEucNodePersistenceHandler {

	private SimpleJdbcTemplate  simpleJdbcTemplate ;
	private SimpleJdbcInsert insertNode;

	/**
	 * Set the datasource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate (dataSource);
		
		this.insertNode = new SimpleJdbcInsert(dataSource)
				.withTableName("NODE").usingGeneratedKeyColumns("ID");
	}

	public String saveNode(NodeWrapper wrapper, String user) {
		String childNodeType = null;
		boolean validRoot = true;
		Number rootNodeId = null;
		if(wrapper.isRoot()){
			rootNodeId = insertNode(wrapper, user);
			
			Map<String, NodeSet> childSets = wrapper.getNode().getChildSets();
			Iterator<NodeSet> it = childSets.values().iterator();
			NodeSet nodeSet = it.next();
			
			Map<String, Node> nodes = nodeSet.getNodes();
			Iterator<Node> iterator = nodes.values().iterator();
			while(iterator.hasNext()){
				Node node = iterator.next();
				NodeWrapper childNodeWrapper = new NodeWrapper();
				childNodeWrapper.setNode(node);
				childNodeWrapper.setParentNodeId(rootNodeId.longValue());
				
				if(!(node.getErrors() != null && node.getErrors().getErrorDetails().size() > 0)){
					childNodeWrapper.setValid(true);
				}else if(validRoot){
					validRoot = false;
				}
				//Create Node
				insertNode(childNodeWrapper, user);
				//Create Fields
				insertFields(node);
				childNodeType = node.getType();
			}
		}
		
		if(!validRoot){
			updateRootNode(rootNodeId.toString(), "N", "N", user);
		}
		
		return childNodeType;
	}

	private Number insertNode(NodeWrapper wrapper, String user) {
		Node node = wrapper.getNode();
		String type = node.getType();
		
		String ISROOT = wrapper.isRoot() ? "Y" : "N";
		String VALID = wrapper.isValid() ? "Y" : "N";
		String WAREHOUSE_SUBMIT = wrapper.isWarehouseSubmitted() ? "Y" : "N";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("TYPE", type);
		parameters.addValue("ISROOT", ISROOT);
		parameters.addValue("VALID", VALID);
		parameters.addValue("WAREHOUSE_SUBMIT", WAREHOUSE_SUBMIT);
		parameters.addValue("USR_CREATED", user);
		parameters.addValue("VERSION", 0);
		
		if(wrapper.isRoot()){
			parameters.addValue("USR", wrapper.getUser());
			parameters.addValue("DESCRIPTION", wrapper.getDescription());
			parameters.addValue("DATE_CREATED", new Date(System.currentTimeMillis()));
		}else{
			parameters.addValue("PARENT_NODE_ID", wrapper.getParentNodeId());
		}

		Number newId = insertNode.executeAndReturnKey(parameters);
		node.setId(String.valueOf(newId.longValue()));
 
		return newId;
	}

	private int[] insertFields(Node node) {
		final Map<String, String> fields = node.getFields();
		Set<String> keySet = fields.keySet();
		
		List<Object[]> batch = new ArrayList<Object[]>();
		for (String key : keySet) {
			Object[] values = new Object[] {key, fields.get(key), new Long(node.getId())};
			batch.add(values);
		}
		int[] updateCounts = simpleJdbcTemplate.batchUpdate(
				"insert into FIELD (NAME, VALUE, NODE_ID) values (?, ?, ?)",
				batch);
		return updateCounts;
	}
	 

	public void retrieveNodes(String rootNodeId, int start, int limit, ListNodesRs rs) {

		String sql = "SELECT TOP "+limit+" * FROM (SELECT ROW_NUMBER() OVER (ORDER BY ID) AS RowNumber," +
				" * FROM NODE WHERE PARENT_NODE_ID = "+ rootNodeId +") _myResults WHERE RowNumber > "+start;
		List<Node> nodes = this.simpleJdbcTemplate.query(sql, nodeMapper);
		for (Node node : nodes) {
			//Hydrate Fields
			loadFields(node);
		}
		
		int rowCount = this.simpleJdbcTemplate.queryForInt("SELECT count(*) FROM NODE" +
				" WHERE PARENT_NODE_ID = "+rootNodeId);
		
		rs.setNodes(nodes);
		rs.setTotalCount(rowCount);
		
		//Get root node properties 
		String sql2 = "SELECT * FROM NODE WHERE ID = ?";
		Node node = this.simpleJdbcTemplate.queryForObject(sql2, rootNodeMapper, rootNodeId);
		rs.getOtherParams().put("warehouseSubmit", node.getFields().get("warehouseSubmit"));
		rs.getOtherParams().put("valid", node.getFields().get("valid"));
		rs.getOtherParams().put("root_node_version", node.getFields().get("node_version"));
	}
	
	public Node getNode(String nodeId) {
		String sql = "SELECT * FROM NODE WHERE ID = ?";
		Node node = this.simpleJdbcTemplate.queryForObject(sql, nodeMapper, nodeId);
		// Hydrate Fields
		loadFields(node);
		
		//Need to get the root node version in case the Node is not a Root Node.
		//This is required to check concurrent update
		if(node.getFields().get("parentNodeId") != null && !"0".equals(node.getFields().get("parentNodeId"))){
			Node rootNode = this.simpleJdbcTemplate.queryForObject(sql, rootNodeMapper, node.getFields().get("parentNodeId"));
			node.setField("root_node_version", rootNode.getFields().get("node_version"));
		}
		
		return node;
	}
	
	private ParameterizedRowMapper<Node> nodeMapper = new ParameterizedRowMapper<Node>() {
        public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
            Node node = new Node(String.valueOf(rs.getLong("ID")), rs.getString("TYPE"));
            node.setField("parentNodeId", String.valueOf(rs.getLong("PARENT_NODE_ID")));
            node.setField("node_version", String.valueOf(rs.getLong("VERSION")));
            return node;
        }
    };
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	private void loadFields(final Node node){
    	String sql = "SELECT * FROM FIELD WHERE NODE_ID = ?";
    	this.simpleJdbcTemplate.query(sql, new ParameterizedRowMapper() {
            public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
                node.setField(rs.getString("NAME"), rs.getString("VALUE") );
                return node;
            }
        }, node.getId());
    }
    
    public void retrieveUploadedNodes(int start, int limit, ListNodesRs rs) {

		String sql = "SELECT TOP "+limit+" * FROM (SELECT ROW_NUMBER() OVER (ORDER BY ID DESC) AS RowNumber," +
				" * FROM NODE WHERE PARENT_NODE_ID IS NULL AND WAREHOUSE_SUBMIT = 'N') _myResults WHERE RowNumber > "+start;
		List<Node> nodes = this.simpleJdbcTemplate.query(sql, rootNodeMapper);
		
		int rowCount = this.simpleJdbcTemplate.queryForInt("SELECT count(*) FROM NODE" +
				" WHERE PARENT_NODE_ID IS NULL AND WAREHOUSE_SUBMIT = 'N'");
		
		for (Node node : nodes) {
			//set child Type
			String childNodeType = this.simpleJdbcTemplate.queryForObject(
			        "select TOP 1 type from NODE where PARENT_NODE_ID = ?", 
			        String.class, new Object[]{new Long(node.getId())});
			node.setField("childNodeType", childNodeType);
		}
		
		rs.setNodes(nodes);
		rs.setTotalCount(rowCount);
	}
    
    private ParameterizedRowMapper<Node> rootNodeMapper = new ParameterizedRowMapper<Node>() {
        public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
            Node node = new Node(String.valueOf(rs.getLong("ID")), rs.getString("TYPE"));
            node.setField("valid", rs.getString("VALID"));
            node.setField("warehouseSubmit", rs.getString("WAREHOUSE_SUBMIT"));
            node.setField("description", rs.getString("DESCRIPTION"));
            node.setField("node_version", String.valueOf(rs.getLong("VERSION")));
            
            Date date = rs.getDate("DATE_CREATED");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            node.setField("dateCreated", sdf.format(date));
            return node;
        }
    };
    
    
    public Node updateNode(Node node, List<String> exclusionFields, String valid, String user){
    	
    	Map<String, String> fields = node.getFields();
    	for (String field : exclusionFields) {
			fields.remove(field);
		}
    	List<Object[]> batch = new ArrayList<Object[]>();
    	Set<String> fieldNames = fields.keySet();
    	for (String fieldName : fieldNames) {
    		Object[] values = new Object[] {fieldName, fields.get(fieldName), fieldName};
            batch.add(values);
		}
    	
    	String sql = new String("Update FIELD set NAME =? , VALUE=? WHERE NAME =? AND NODE_ID ="+node.getId());
    	int[] updateCounts = simpleJdbcTemplate.batchUpdate(sql, batch);
    	
    	int update = simpleJdbcTemplate.update("UPDATE NODE SET VALID =?, USR_UPDATED=? WHERE ID =?", valid, user, new Long(node.getId()));
    	
    	return getNode(node.getId());
    }
    
    public int updateRootNode(String nodeId, String valid, String warehouseSubmit, String user){
		return simpleJdbcTemplate.update(
				"UPDATE NODE SET VALID =? , WAREHOUSE_SUBMIT =? , USR_UPDATED=? WHERE ID =?",
				valid, warehouseSubmit, user, new Long(nodeId));
    }
    
    /* (non-Javadoc)
     * @see com.msilm.euc.service.persistence.IEucNodePersistenceHandler#selectForUpdate(java.lang.String)
     */
    @Override
    public int selectForUpdate(String nodeId) {
    	
    	String sql = "SELECT ID FROM NODE WITH (XLOCK , ROWLOCK, NOWAIT) WHERE ID = ?";
    	
    	int id = this.simpleJdbcTemplate.queryForObject(sql, 
		        Integer.class, new Object[]{new Long(nodeId)});

    	return id;
    }
    
}
