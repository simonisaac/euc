/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   21 Jul 201114:14:05
 * Workfile::  EucRsToNodeRs.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sri.nodeservice.core.nodemgr.nodedef.problem.ValidationReport;
import org.sri.nodeservice.core.nodemgr.util.report.ReportPrinterSimpleText;

/**
 * @author jkochhar
 * 
 */
public class EucRsToNodeRs {

	protected final Log log = LogFactory.getLog(EucRsToNodeRs.class);

	
	public static void traceReport(ValidationReport vp) {
		ReportPrinterSimpleText printer = new ReportPrinterSimpleText();
		printer.fullDetail(vp);
		System.out.println(printer.getString());
	}
}
