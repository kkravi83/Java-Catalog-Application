/***************************************************************************
 *   Copyright (C) 2006 by krishnakumar.kr                                 *
 *   krishnakumar.kr@gmail.com                                             *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package com.kcatalog.gui;

import javax.swing.tree.TreeModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;
import com.kcatalog.xmlschema.KCatalogXMLSchemaLookup;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUpManager;
 

public class KCatalogGuiTreeModel implements TreeModel {
	public static String label = "Label-";
	HashMap parentChildMap = new HashMap();
	public static final String ROOT_STRING = "Mp3 Database";
	
	
	public Object getRoot() {		
		return ROOT_STRING;
	}

	public void resetMap(){
		parentChildMap = new HashMap();
	}
	
	public Object getChild(Object parent, int index) {		
	
		ArrayList list = getChildrenList(parent);		
		return list.get(index);
		
	} 
	
	
	public boolean containsComment(String dirPath){
		boolean stat = false;
		if( dirPath.indexOf(label) > -1){
			stat = true;
		}
		return stat;
	}
	
	public static boolean isComment(String dirPath){		
		int index = dirPath.lastIndexOf(KCatalogConfigOptions.getSeparator());
		String comment = dirPath.substring(index+1);		
		/*if(comment.trim().equalsIgnoreCase(label)){
			return true;
		}*/			
		comment = KCatalogCommonUtility.adjustForSeparator(comment,label);
		String tempLabel =
		    KCatalogCommonUtility.adjustForSeparator(label,comment);		
		if(comment.startsWith(tempLabel)){
			return true;
		}
		return false;
	}
	
	public boolean isPermanentLocation(String dirPath){
		//System.out.println("inside permanent loc " + dirPath);
		/*ArrayList pLocList = (ArrayList)KCatalogConfigOptions.
									getObjectOptionValue(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
		for(int i=0;i<pLocList.size();i++){
			String pLocPath = ((String)pLocList.get(i)).trim();
			dirPath = dirPath.trim();
			if(pLocPath.endsWith(KCatalogConfigOptions.getSeparator())){
				pLocPath = pLocPath.substring(0,pLocPath.length()-1);				
			}
			if(dirPath.endsWith(KCatalogConfigOptions.getSeparator())){
				dirPath = dirPath.substring(0,dirPath.length()-1);				
			}
			if(dirPath.trim().equalsIgnoreCase(pLocPath.trim())){
				//System.out.println("inside permanent loc true");
				return true;
				
			}
		}
		//System.out.println("inside permanent loc false");
		return false;*/
		return KCatalogCommonUtility.isPermanentLocation(dirPath);
	}
	
	private ArrayList getChildrenForComment(String comment){
		
		KCatalogXMLSchemaSearch ss = KCatalogXMLSchemaSearch.newInstance();		
		int pos = comment.lastIndexOf(
					KCatalogConfigOptions.getSeparator());
		String parent = comment.substring(0,pos);
		int commentIndex = pos + 1 + label.length();
		comment = comment.substring(commentIndex);
	//	System.out.println("parent " + parent + " comment " + comment);
	/*	ArrayList list = ss.searchForMp3FileRecursive(getMp3FileDtoForSearch(parent,comment),
							KCatalogConstants.SEARCH_OPERATION_TYPE_AND,true);*/
		ArrayList list = getChildrenForComment(
				getMp3FileDtoForSearch(parent,comment));
		ArrayList locList = new ArrayList();		
		for(int i=0;i<list.size();i++){
			String loc = (String)((Mp3FileDto)list.get(i)).getFileLocation().trim();			
			loc = getRootCommentDir(parent,loc);
			if(!locList.contains(loc) &&
							!loc.equalsIgnoreCase(parent)){				
				
				locList.add(loc.trim());
			}			
		}
		ss.close();
		locList = removeInvalidEntries(locList);
		return locList;	
	}
	
	private ArrayList getChildrenForComment(Mp3FileDto dto){
		KCatalogXMLSchemaSearch.close();
		KCatalogXMLSchemaSearch ksearch  = 
						KCatalogXMLSchemaSearch.newInstance(dto,
										KCatalogConstants.SEARCH_OPERATION_TYPE_RECURSIVE_LOC,
										true,
										true);
		int i =1;
		ArrayList result = new ArrayList();
		while(ksearch.isPageNoValid(i)){
			result.addAll(ksearch.getSearchResult(i));
			i++;
		}
		ksearch.close();
		return result;
	}
	
	private String getRootCommentDir(String parent,String fileLoc){
		int parentLen = parent.length();		
		if(parent.equalsIgnoreCase(fileLoc)){
			return parent;
		}
		String child = fileLoc.substring(parentLen+1);
		int posSep = child.indexOf(
				KCatalogConfigOptions.getSeparator());
		return (posSep == -1)?fileLoc: fileLoc.substring(0,parentLen+1+posSep);
	}
	
	private ArrayList removeInvalidEntries(ArrayList list){
		if(list.size() <= 0){
			return list;
		}
		Collections.sort(list);	
		String parent = (String)list.get(0);
		int i = 1;
		while( i+1 <= list.size()){
			String next = (String)list.get(i);			
			next = KCatalogCommonUtility.adjustForSeparator(next,parent);
			parent = KCatalogCommonUtility.adjustForSeparator(parent,next);
			if(next.startsWith(parent.trim())){
				list.remove(next);
				
			}else{
				parent = (String)list.get(i);
				i++;
			}
		}
		return list;
	}
	
	private ArrayList getCommentListForLocContainingComment(String parentStr){
		String comment = KCatalogCommonUtility.getCommentIfPresent(parentStr);
		String parent =
			   KCatalogCommonUtility.removeCommentIfPresent(parentStr);
		KCatalogXMLSchemaSearch ss = KCatalogXMLSchemaSearch.newInstance();	
		ArrayList list = getChildrenForComment(
				getMp3FileDtoForSearch(parent,comment));
		ArrayList locList = new ArrayList();		
		for(int i=0;i<list.size();i++){
			String loc = (String)((Mp3FileDto)list.get(i)).getFileLocation().trim();			
			loc = getRootCommentDir(parent,loc);
			if(!locList.contains(loc) &&
							!loc.equalsIgnoreCase(parent)){				
				
				locList.add(loc.trim());
			}			
		}
		ss.close();
		locList = removeInvalidEntries(locList);
		return locList;	
	}
	
	private ArrayList getCommentListForPermanentLocation(String dirPath){
		
		ArrayList removStoragelist = KCatalogXMLSchemaLookup.getFileLocationList(dirPath,
						 				KCatalogXMLSchemaLookup.SEARCH_OP_TYPE_CHILDREN);
	
	/*	KCatalogXMLSchemaSearch ss = KCatalogXMLSchemaSearch.newInstance();
		ArrayList list = ss.searchCommentForPermanentLocation(getMp3FileDtoForSearch(dirPath,"*.*"),
							KCatalogConstants.SEARCH_OPERATION_TYPE_AND,true);		
		
		ss.close();	*/
		KCatalogXMLSchemaMp3AttributeLookUpManager lm = 
				KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
		ArrayList list = lm.getCommentsForAllPermanentLocation();
		ArrayList commentList = new ArrayList();
		for(int i=0;i<list.size();i++){
			//String comment = (String)((Mp3FileDto)list.get(i)).getComment();
			String comment = (String)list.get(i);
			if(!"".equals(comment.trim())){
				//String fname = (String)((Mp3FileDto)list.get(i)).getFileLocation();
				comment = dirPath+ 
							KCatalogConfigOptions.getSeparator()+ label+comment.trim();
			
				if(!commentList.contains(comment.trim()) &&
						!"".equals(comment.trim())){				
					commentList.add(comment);
				}			
			}
		}
		return commentList;		
	}
	
	private Mp3FileDto getMp3FileDtoForSearch(String loc,String comment){		
		Mp3FileDto dto = new Mp3FileDto();		
		dto.setComment(comment);
		dto.setFileLocation(loc);
		return dto;
	}

	public int getChildCount(Object parent) {
		ArrayList list = getChildrenList(parent);			
		return list.size();
	}

	public boolean isLeaf(Object node) {
		
		ArrayList list = getChildrenList(node);		
	    if(list.size()==0){
	    	return true;
	    }
		return false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO: Add your code here
	}

	public int getIndexOfChild(Object parent, Object child) {
		ArrayList list = getChildrenList(parent);		
		return list.indexOf((String)child);
	}

	public void addTreeModelListener(TreeModelListener l) {
		// TODO: Add your code here
	}

	public void removeTreeModelListener(TreeModelListener l) {
		// TODO: Add your code here
	}
	
	private ArrayList getChildrenList(Object parent){
		ArrayList list = null;
		if(parentChildMap.get(parent) != null){
			return (ArrayList)parentChildMap.get(parent);
		}
		if("Mp3 Database".endsWith((String)parent)){
			if(!KCatalogXMLSchemaLookup.isLocationLookUpFileExists()){
				return new ArrayList();
			}
			list = KCatalogXMLSchemaLookup.getRootFileLocationList();
			Collections.sort(list);
		}else{
			String parentStr = (String)parent;
			if(isComment(parentStr)){			
				list =  getChildrenForComment(parentStr);
				
			}else{
				if(containsComment(parentStr)){					
					list = getCommentListForLocContainingComment(parentStr);   
				}else{
					if(isPermanentLocation(parentStr)){
					
						list = getCommentListForPermanentLocation(parentStr);
					}else{		
						String fileLoc = 
							KCatalogCommonUtility.removeCommentIfPresent(parentStr);	
					//System.out.println(fileLoc);
						list =
						 KCatalogXMLSchemaLookup.getFileLocationList(fileLoc,
						 		KCatalogXMLSchemaLookup.SEARCH_OP_TYPE_CHILDREN);
					}
					
				}
			}
		}
		parentChildMap.put(parent,list);
		return list;
	}
	
	
}

