/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   18 Jul 201110:30:53
 * Workfile::  ResetLoadConfigs.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.mule.startup;

import java.io.File;
import java.io.IOException;

import org.mule.util.FileUtils;

/**
 * @author jkochhar
 *
 */
public class ResetLoadConfigs {

	private String fromFolder;
	private String toFolder;
	
	public void init(){
		try {
			File fromDir = FileUtils.openDirectory(fromFolder);
			File toDir = FileUtils.openDirectory(toFolder);
			
			org.apache.commons.io.FileUtils.copyDirectory(fromDir, toDir);
			org.apache.commons.io.FileUtils.cleanDirectory(fromDir);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param fromFolder the fromFolder to set
	 */
	public void setFromFolder(String fromFolder) {
		this.fromFolder = fromFolder;
	}
	
	/**
	 * @param toFolder the toFolder to set
	 */
	public void setToFolder(String toFolder) {
		this.toFolder = toFolder;
	}
}
