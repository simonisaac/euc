/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201110:32:11
 * Workfile::  EucNodeDefProvider.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sri.nodeservice.core.nodedef.service.model.LanguageDefTO;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDef;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDefMgr;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeSetDef;
import org.sri.nodeservice.core.nodemgr.util.transform.LanguageLoader;
import org.sri.nodeservice.core.nodemgr.util.transform.NodeDefTOTransformer;
import org.sri.nodeservice.core.nodemgr.util.transform.XmlToLanguageDefTO;

/**
 * @author jkochhar
 * 
 */
public class EucNodeDefProvider implements IEucNodeDefProvider {

	Logger log = Logger.getLogger(EucNodeDefProvider.class);

	private NodeDefMgr nodeDefMgr;
	private NodeDefTOTransformer trans;

	/**
	 * This method should be called when initialising this service
	 */
	public void init() {
		nodeDefMgr = NodeDefMgr.createDefaultInstance();
		trans = new NodeDefTOTransformer(nodeDefMgr.getTypeDefMgr());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msilm.euc.service.IEucNodeDefProvider#loadNodeDefinition(java.io.
	 * File)
	 */
	@Override
	public void loadNodeDefinition(FileInputStream xmlLanguage) {
		try {
			LanguageDefTO languageDefTO = XmlToLanguageDefTO
					.unmarshall(xmlLanguage);
			loadNodeDefinition(languageDefTO);
		} catch (Exception e) {
			log.error("Unable to load the language definition ", e);
			try {
				xmlLanguage.close();
			} catch (IOException e1) {
				e = e1;
			}
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msilm.euc.service.IEucNodeDefProvider#loadNodeDefinition(org.sri.
	 * nodeservice.core.nodedef.service.model.LanguageDefTO)
	 */
	@Override
	public void loadNodeDefinition(LanguageDefTO languageDefTO) {
		LanguageLoader loader = new LanguageLoader(trans, nodeDefMgr);
		loader.loadLanguage(languageDefTO);
		log.info("Language succefully loaded with Node Type ["+ languageDefTO.getDescription() + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msilm.euc.service.IEucNodeDefProvider#getNodeDefinition(java.lang
	 * .String)
	 */
	@Override
	public NodeDef getNodeDefinition(String nodeType) {
		return nodeDefMgr.getNodeDefinition(nodeType);
	}
	
	/* (non-Javadoc)
	 * @see com.msilm.euc.service.IEucNodeDefProvider#getAllNodeDefinitions()
	 * 
	 * Add Node defs having only one child set.
	 * This version only supports single child set per Node.
	 * 
	 */
	@Override
	public List<NodeDef> getNodeDefinitions() {
		List<NodeDef> nodeDefs = new ArrayList<NodeDef>();
		Map<String, NodeDef> nodeDefinitionMap = nodeDefMgr.getNodeDefinitionMap();
		Collection<NodeDef> values = nodeDefinitionMap.values();
		for (NodeDef nodeDef : values) {
			Collection<NodeSetDef> childSetCollection = nodeDef.getChildSetCollection();
			if(childSetCollection != null && childSetCollection.size() == 1){
				nodeDefs.add(nodeDef);
			}
		}
		return nodeDefs;
	}
}
