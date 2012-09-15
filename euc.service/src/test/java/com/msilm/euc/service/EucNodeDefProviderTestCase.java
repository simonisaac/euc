/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201114:05:23
 * Workfile::  EucNodeDefProviderTestCase.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDef;

/**
 * @author jkochhar
 *
 */
public class EucNodeDefProviderTestCase {

	@org.junit.Test
	public void testLoadNodeDefinition() throws IOException{
		
		EucNodeDefProvider provider = new EucNodeDefProvider();
		provider.init();
		
		Resource xmlLanguage1 = new ClassPathResource("xml/languageDef.xml");
		Resource xmlLanguage2 = new ClassPathResource("xml/languageDefHierarchy.xml");
		
		provider.loadNodeDefinition(new FileInputStream(xmlLanguage1.getFile()));
		provider.loadNodeDefinition(new FileInputStream(xmlLanguage2.getFile()));
		
		NodeDef nodeDefSimple = provider.getNodeDefinition("simpleNodeDef");
		NodeDef nodeDefHierarchy = provider.getNodeDefinition("simpleHierachyDef");
		NodeDef nodeDefChildnode = provider.getNodeDefinition("childnode");
		
		Assert.assertEquals("simpleNodeDef", nodeDefSimple.getNodeDefId());
		Assert.assertEquals(5,nodeDefSimple.getAttrDefList().size());
		Assert.assertEquals("A Long",nodeDefSimple.getAttrDefById("long").getLabel());
		
		Assert.assertEquals("simpleHierachyDef", nodeDefHierarchy.getNodeDefId());
		Assert.assertEquals(1,nodeDefHierarchy.getAttrDefList().size());
		Assert.assertEquals("A String",nodeDefHierarchy.getAttrDefById("string").getLabel());
		Assert.assertNotNull(nodeDefHierarchy.getChildSetCollection());
		Assert.assertEquals(1,nodeDefHierarchy.getChildSetCollection().size());
		
		Assert.assertEquals("childnode", nodeDefChildnode.getNodeDefId());
		Assert.assertEquals(1,nodeDefChildnode.getAttrDefList().size());
		Assert.assertEquals("A String",nodeDefChildnode.getAttrDefById("string").getLabel());
	}
}
