/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201112:27:43
 * Workfile::  IEucNodeDefProvider.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service;

import java.io.FileInputStream;
import java.util.List;

import org.sri.nodeservice.core.nodedef.service.model.LanguageDefTO;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDef;

/**
 * @author jkochhar
 *
 */
public interface IEucNodeDefProvider {

	public abstract void loadNodeDefinition(FileInputStream xmlLanguage);

	public abstract void loadNodeDefinition(LanguageDefTO languageDefTO);

	public abstract NodeDef getNodeDefinition(String nodeType);
	
	public abstract List<NodeDef> getNodeDefinitions();

}