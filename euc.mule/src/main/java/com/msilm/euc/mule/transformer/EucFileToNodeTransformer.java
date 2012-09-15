/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   22 Jul 201112:07:33
 * Workfile::  EucFileToNodeTransformer.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.mule.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.mule.transform.nodeaccess.InHttpMultipartTransformer;
import org.sri.nodeservice.mule.transform.util.Helper;
import org.sri.nodeservice.transform.excel.IProcessSpreadsheet;
import org.sri.nodeservice.transform.excel.Sheet;

import com.msilm.euc.service.IEucExcelInstructionProvider;

/**
 * @author jkochhar
 * 
 */
public class EucFileToNodeTransformer implements InHttpMultipartTransformer {

	private IProcessSpreadsheet processSpreadsheet;
	private IEucExcelInstructionProvider eucExcelInstructionProvider;
	
	private static String EXCEL_INST_ID = "excelInstId";
	private static String FILE_TO_UPLOAD = "fileToUpload";

	@Override
	public Node transformMessage(MuleMessage message) {
		Node node = null;
		Set<String> inboundPropertyNames = message.getInboundAttachmentNames();
		if (inboundPropertyNames != null && inboundPropertyNames.size() > 0) {

			try {
				InputStream sheetIdInputStream = message.getInboundAttachment(EXCEL_INST_ID).getInputStream();
				InputStream excelFileInputStream = message.getInboundAttachment(FILE_TO_UPLOAD).getInputStream();
				
				Sheet sheet = eucExcelInstructionProvider.getSheet(Helper.convertStreamToString(sheetIdInputStream));
				node = processSpreadsheet.process(excelFileInputStream, sheet);

				for (String paramName : inboundPropertyNames) {
					DataHandler inboundProperty = (DataHandler) message.getInboundAttachment(paramName);
					if (!(FILE_TO_UPLOAD.equals(paramName) || EXCEL_INST_ID.equals(paramName))) {
						String paramValue = Helper.convertStreamToString(inboundProperty.getInputStream());
						node.setField(paramName, paramValue);
					} 
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return node;
	}

	/**
	 * @param eucExcelInstructionProvider
	 *            the eucExcelInstructionProvider to set
	 */
	public void setEucExcelInstructionProvider(
			IEucExcelInstructionProvider eucExcelInstructionProvider) {
		this.eucExcelInstructionProvider = eucExcelInstructionProvider;
	}

	/**
	 * @param processSpreadsheet
	 *            the processSpreadsheet to set
	 */
	public void setProcessSpreadsheet(IProcessSpreadsheet processSpreadsheet) {
		this.processSpreadsheet = processSpreadsheet;
	}
}
