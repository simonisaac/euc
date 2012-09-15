/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   15 Jul 201111:22:56
 * Workfile::  NodeDefinitionEmbeddedTest.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.mule;

import org.mule.tck.FunctionalTestCase;

/**
 * @author jkochhar
 *
 */
public class NodeDefinitionEmbeddedTest extends FunctionalTestCase {
	
	@Override
	protected String getConfigResources() {
		return "mule-config.xml";
	}

	@SuppressWarnings("unchecked")
	public void testLoad() throws Exception {
		//Thread.sleep(2000000);
	}

}
