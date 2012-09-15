/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   15 Aug 201114:39:49
 * Workfile::  EucUtils.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice.util;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.sri.nodeservice.core.nodeaccess.service.model.Node;
import org.sri.nodeservice.core.nodeaccess.service.model.NodeSet;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ErrorDetail;
import org.sri.nodeservice.core.nodeaccess.service.rsrq.ErrorTypeEnum;
import org.sri.nodeservice.core.nodemgr.defaultimpl.node.RichAttr;
import org.sri.nodeservice.core.nodemgr.defaultimpl.node.RichNode;
import org.sri.nodeservice.core.nodemgr.nodedef.NodeDef;
import org.sri.nodeservice.core.nodemgr.nodedef.problem.ValidationReport;
import org.sri.nodeservice.core.nodemgr.nodedef.problem.ValidationReport.ReportCollection;
import org.sri.nodeservice.core.nodemgr.validation.report.Problem;

/**
 * @author jkochhar
 * 
 */
public class EucUtils {

	public static void mapResponse(Node node, ValidationReport report) {

		// Add node level errors
		{
			for (List<Problem> probList : report.getProblemsByType().values()) {
				for (Problem problem : probList) {
					ErrorDetail ed = new ErrorDetail();
					ed.setDescription(problem.getMessage());
					ed.setType(ErrorTypeEnum.BUSINESS);
					node.addErrorDetail(ed);
				}
			}
		}

		// Add attribute level errors
		{
			ReportCollection attributeReports = report
					.getChildReportCollection(NodeDef.ATTR_REPORT);
			if (attributeReports != null) {
				for (ValidationReport attributeReport : attributeReports.getReports()) {
					for (Problem curProblem : attributeReport.getProblems()) {
						RichAttr attr = (RichAttr) attributeReport.getRichObject();
						String attributeId = attr.getAttrDef().getNodeDefId();
						ErrorDetail ed = new ErrorDetail();
						ed.setFieldName(attributeId);
						ed.setType(ErrorTypeEnum.VALIDATION);
						ed.setDescription(curProblem.getMessage());
						node.addErrorDetail(ed);
					}
				}
			}
		}

		// Add Child Node Sets level errors
		{
			ReportCollection childSetReports = report
					.getChildReportCollection(NodeDef.NODESET_REPORT);
			if (childSetReports != null) {
				for (ValidationReport childSetReport : childSetReports.getReports()) {
					String nodeSetEntityId = childSetReport.getEntityId();
					NodeSet childNodeSet = node.getChildNodeSet(nodeSetEntityId);

					// now do the childset problems
					for (Problem curProblem : childSetReport.getProblems()) {
						ErrorDetail ed = new ErrorDetail();
						ed.setDescription(curProblem.getMessage());
						ed.setType(ErrorTypeEnum.BUSINESS);
						node.addErrorDetail(ed);
					}
					ReportCollection childNodeReports = childSetReport.getChildReportCollection(NodeDef.NODE_REPORT);
					for (ValidationReport childNodeReport : childNodeReports.getReports()) {
						RichNode richNode = (RichNode) childNodeReport.getRichObject();
						String id = richNode.getNodeId();
						Node childNode = childNodeSet.getNodes().get(id);
						mapResponse(childNode, childNodeReport);
					}
				}
			}
		}
	}
	
	public static String getLoggedInUser(){
		//Get the user name from security context.
		String username = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
		return username;
	}

}
