/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   18 Jul 201115:50:21
 * Workfile::  TestComponent.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.mule.startup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.model.NodeSet;
import org.sri.nodeservice.transform.excel.IProcessSpreadsheet;
import org.sri.nodeservice.transform.excel.Sheet;

import com.msilm.euc.service.EucExcelInstructionProvider;

/**
 * @author jkochhar
 *
 */
public class TestComponent implements Callable {
	
	private IProcessSpreadsheet processSpreadsheet;
	
	private EucExcelInstructionProvider eucExcelInstructionProvider;

	/* (non-Javadoc)
	 * @see org.mule.api.lifecycle.Callable#onCall(org.mule.api.MuleEventContext)
	 */
	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		
		MuleMessage muleMessage = eventContext.getMessage();
		//File excelFile = readIntoFile(payload);
		
		@SuppressWarnings("unused")
		Set<String> inboundPropertyNames = muleMessage.getInboundAttachmentNames();
		for (String string : inboundPropertyNames) {
			DataHandler inboundProperty = (DataHandler)muleMessage.getInboundAttachment(string);
			
			System.out.println("Content Name :: "+string);
			System.out.println("Content Type :: "+inboundProperty.getContentType());
			System.out.println("Content Object :: "+inboundProperty.getContent().toString());
			
			if("fileToUpload".equals(string)){
				File f = readIntoFile(inboundProperty.getInputStream());
				
				Sheet sheet = eucExcelInstructionProvider.getSheet("testSheet-1");
				Node node = processSpreadsheet.process(new FileInputStream(f), sheet);
				
				NodeSet nodeSet = node.getChildNodeSet("testSheet");
				Map<String, Node> nodes = nodeSet.getNodes();
				Collection<Node> values = nodes.values();
				
				print(values);
								
				System.out.println("NODE ---> " +node.getId()+" : "+ node.getType());
				System.out.println("FILE NAME : "+f.getName());
				
			}else{
				System.out.println("INBOUND-PROPERTY ["+string+"="+convertStreamToString(inboundProperty.getInputStream())+"]");
			}
		}
		System.out.println("TESTING....");		
		return null;
	}
	
	/**
	 * @param processSpreadsheet the processSpreadsheet to set
	 */
	public void setProcessSpreadsheet(IProcessSpreadsheet processSpreadsheet) {
		this.processSpreadsheet = processSpreadsheet;
	}
	
	/**
	 * @param eucExcelInstructionProvider the eucExcelInstructionProvider to set
	 */
	public void setEucExcelInstructionProvider(
			EucExcelInstructionProvider eucExcelInstructionProvider) {
		this.eucExcelInstructionProvider = eucExcelInstructionProvider;
	}
	
	
	private String convertStreamToString(InputStream is)
	            throws IOException {
	        /*
	         * To convert the InputStream to String we use the
	         * Reader.read(char[] buffer) method. We iterate until the
	         * Reader return -1 which means there's no more data to
	         * read. We use the StringWriter class to produce the string.
	         */
	        if (is != null) {
	            Writer writer = new StringWriter();
	 
	            char[] buffer = new char[1024];
	            try {
	                Reader reader = new BufferedReader(
	                        new InputStreamReader(is, "UTF-8"));
	                int n;
	                while ((n = reader.read(buffer)) != -1) {
	                    writer.write(buffer, 0, n);
	                }
	            } finally {
	                is.close();
	            }
	            return writer.toString();
	        } else {       
	            return "";
	        }
	    }
	
	
	public static File readIntoFile(InputStream inputStream ) throws FileNotFoundException, IOException{
		//write the inputStream to a FileOutputStream
		File a = new File("JKEXCEL.xls");
		OutputStream out = new FileOutputStream(a);
 
		int read=0;
		byte[] bytes = new byte[1024];
 
		while((read = inputStream.read(bytes))!= -1){
			out.write(bytes, 0, read);
		}
 
		inputStream.close();
		out.flush();
		out.close();	
		return a;
	}
	
	// Un-comment to see the processed fields.
	private static void print(Collection<Node> values) {

		System.out.println("----------------");
		int count = 1;
		for (Node node2 : values) {
			System.out.println("NODE --> " + node2.getId() + " :: "
					+ node2.getType());
			Map<String, String> fields = node2.getFields();
			Set<String> keySet = fields.keySet();
			for (String string : keySet) {
				System.out.print(string + "::" + fields.get(string) + "  ");
			}
			System.out.println();
			System.out.println(count++ + "----------------");
			System.out.println();

		}

	}

}
