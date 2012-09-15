/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201111:11:42
 * Workfile::  EucXmlInstructionProvider.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.sri.nodeservice.transform.excel.Sheet;
import org.sri.nodeservice.transform.util.CommonUtils;
import org.sri.nodeservice.transform.util.IXmlToSheetTransformer;

/**
 * @author jkochhar
 *
 */
public class EucExcelInstructionProvider implements IEucExcelInstructionProvider {
	
	private Map<String, Sheet> xmlProcessingInstructions = new HashMap<String, Sheet>();
	
	private IXmlToSheetTransformer xmlToSheetTransformer;
	
	public void init(){
		//Add initialisation logic if required.
	}
	
	/* (non-Javadoc)
	 * @see com.msilm.euc.service.IEucExcelInstructionProvider#loadExcelInstruction(java.io.File)
	 */
	@Override
	public Sheet loadExcelInstruction(FileInputStream excelInstructionInXml) throws IOException, JAXBException{
		String xmlAsString = CommonUtils.convertStreamToString(excelInstructionInXml);
		Sheet sheet = xmlToSheetTransformer.parseProcessingInstructionXml(xmlAsString);
		xmlProcessingInstructions.put(sheet.getSheetId(), sheet);
		return sheet;
	}
	
	/* (non-Javadoc)
	 * @see com.msilm.euc.service.IEucExcelInstructionProvider#getSheetsByNodeDefType(java.lang.String)
	 */
	@Override
	public List<Sheet> getSheetsByNodeDefType(String nodeDefType) {
		List<Sheet> sheets = new ArrayList<Sheet>(); 
		Collection<Sheet> values = xmlProcessingInstructions.values();
		for (Sheet sheet : values) {
			if(nodeDefType.equals(sheet.getSheetRootNodeType())){
				sheets.add(sheet);
			}
		}
		return sheets;
	}
	
	/* (non-Javadoc)
	 * @see com.msilm.euc.service.IEucExcelInstructionProvider#getSheet(java.lang.String)
	 */
	@Override
	public Sheet getSheet(String sheetId){
		return xmlProcessingInstructions.get(sheetId);
	}
	
	public void setXmlToSheetTransformer(
			IXmlToSheetTransformer xmlToSheetTransformer) {
		this.xmlToSheetTransformer = xmlToSheetTransformer;
	}
}
