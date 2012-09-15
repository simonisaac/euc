/*
 * ########################################################################################
 * Copyright (c) Mitsui Sumitomo Insurance (London Management) Ltd  All rights reserved.
 * ########################################################################################
 *
 * Author::   jkochhar
 * Date::   23 Aug 201109:53:48
 * Workfile::  PageableTestCase.java
 *
 * @version $Id$
 *
 * ########################################################################################
 */
package com.msilm.euc.nodeservice.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jkochhar
 *
 */ 
public class PageableTestCase {

	@org.junit.Test
	public void testPaging(){
		List<String> elements = new ArrayList<String>();
		for(int i = 1; i<50; i++){
			elements.add("ELEMENT-"+i);
		}
		
		Pageable<String> page = new Pageable<String>(elements, 6);
		page.setPage(3);
		
		List<String> lp = page.getListForPage();
		for (String string : lp) {
			System.out.println(string);
		}
	}
}
