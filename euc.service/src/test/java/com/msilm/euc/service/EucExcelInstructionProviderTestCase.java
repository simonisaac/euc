/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   14 Jul 201113:41:36
 * Workfile::  EucExcelInstructionProviderTest.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.service;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.sri.nodeservice.transform.excel.Sheet;
import org.sri.nodeservice.transform.util.IXmlToSheetTransformer;
import org.sri.nodeservice.transform.util.XmlToSheetTransformer;
import org.sri.nodeservice.transform.util.XmlValidator;

/**
 * @author jkochhar
 * 
 */
public class EucExcelInstructionProviderTestCase {

	@org.junit.Test
	public void testLoadExcelInstruction() throws JAXBException, IOException {

		EucExcelInstructionProvider provide = new EucExcelInstructionProvider();
		provide.setXmlToSheetTransformer(getTransformer());

		Resource xml1 = new ClassPathResource("xml/test3.xml");
		Resource xml2 = new ClassPathResource("xml/test3-B.xml");
		Resource xml3 = new ClassPathResource("xml/ids.xml");

		provide.loadExcelInstruction(new FileInputStream(xml1.getFile()));
		provide.loadExcelInstruction(new FileInputStream(xml2.getFile()));
		provide.loadExcelInstruction(new FileInputStream(xml3.getFile()));
		
		Sheet sheet1 = provide.getSheet("testSheet-1");
		Sheet sheet2 = provide.getSheet("IDSheet-1");
		Sheet sheet3 = provide.getSheet("testSheet-3");

		Assert.assertEquals("test3SheetType", sheet1.getSheetNodeType());
		Assert.assertEquals("IDSSheetType", sheet2.getSheetNodeType());
		Assert.assertEquals("test3BSheetType", sheet3.getSheetNodeType());
	}

	private IXmlToSheetTransformer getTransformer() {
		XmlValidator xmlValidator = new XmlValidator();
		xmlValidator.setXsdClasspath("xsd/nodeExcelSchema.xsd");
		xmlValidator.init();

		XmlToSheetTransformer transformer = new XmlToSheetTransformer();
		transformer.setValidator(xmlValidator);

		return transformer;
	}

}
