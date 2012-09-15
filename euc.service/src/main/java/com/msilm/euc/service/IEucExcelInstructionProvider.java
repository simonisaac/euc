/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201112:27:23
 * Workfile::  IEucExcelInstructionProvider.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.sri.nodeservice.transform.excel.Sheet;

/**
 * @author jkochhar
 *
 */
public interface IEucExcelInstructionProvider {

	public abstract Sheet loadExcelInstruction(FileInputStream excelInstructionInXml)
			throws IOException, JAXBException;

	public abstract Sheet getSheet(String sheetId);
	
	public abstract List<Sheet> getSheetsByNodeDefType(String nodeDefType);

}