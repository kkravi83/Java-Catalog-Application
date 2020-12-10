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
package com.kcatalog.common;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.xmlschema.KCatalogXMLSchemaWriter;

import java.util.StringTokenizer;

import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import com.kcatalog.xmlschema.KCatalogXMLSchemaBase;

public class KCatalogConfigOptions extends KCatalogXMLSchemaBase{
	
	private static HashMap configMap = null;
	
	
	public static String getOptionValue(String key){	
		if(configMap == null){
			populateConfigMap();
		}
		Object value = configMap.get(key.trim());
		return (value == null)?"":(String)value;
	}
	
	public static Object getObjectOptionValue(String key){
		if(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS.equals(key.trim())){
			return getPermanentLocList(getOptionValue(key));
		}else{
			return (Object)getOptionValue(key);
		}
	}
	
	public static void setOptionValue(String key,Object value){
		String valueStr = getOptionValueForUpdate(key,value);
		setValueInDocument(key,valueStr);
	}
	
	private static void setValueInDocument(String key, String value){
		DocumentBuilder db = null;	
		try{
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
			dbf.setIgnoringComments(true);
			db = dbf.newDocumentBuilder();
			String separator = File.separator;
			if(!"\\".equals(separator.trim()) &&
			    !"/".equals(separator.trim())){
			    separator = "\\";			    		    
			}						
			Document doc = db.parse(
				
			          new File(KCatalogCommonUtility.getConfigPath() + separator +
			         "config.xml").getAbsolutePath());		    	
		    setValueInDoc(doc,key,value);
		    writeConfigFile(doc);
		}catch(Exception e){			
			KCatalogException kce = 
			   new KCatalogException(KCatalogConstants.ERROR_MESSAGE_OPEN_CONFIG,
			                                e);
			throw kce;
		}	
	}
	
	
	
	private static void writeConfigFile(Document doc){
		
		writeDocToFile(doc,KCatalogCommonUtility.getConfigPath()+ File.separator  
								+"config.xml");
		
	}
	
	private static void setValueInDoc(Document doc,String key,String value){
		Node root = doc.getChildNodes().item(0);		
		if(root.getNodeType() == Node.ELEMENT_NODE){
			 String rootName = root.getNodeName();
			 rootName = (rootName == null)?"":rootName;
			 if(!KCatalogConstants.CONFIG_ROOT.equals(rootName.trim())){
			 	 KCatalogException kce = 
			   		new KCatalogException(KCatalogConstants.ERROR_CONFIG_FILE_HEADER);
			   	 throw kce;
			 }else{
			 	NodeList list = root.getChildNodes();
			 	for(int i = 0; i < list.getLength();i++){
			 	
			 		Node node = list.item(i);
			 		
			 		NamedNodeMap attributeMap = node.getAttributes();
			 		if( attributeMap != null ){
			 			if( attributeMap.getLength() < 0 ||
				 		 	attributeMap.getLength() > 1 ){
			 				KCatalogException kce = 
				   		        new KCatalogException(
			   		        			KCatalogConstants.ERROR_CONFIG_FILE_HEADER);	 		
			 			}		
			 			Node attribute = attributeMap.item(0);	 			
			 			String nodeName = node.getNodeName();
			 			nodeName = ( nodeName == null)? "": nodeName.trim();			 						 			
						if(nodeName.equals(key)){							
							attribute.setNodeValue(value);							
							return;
						}			 			
			 		}
			 	}
			 }
		}		
	}
	
	
	private static String getOptionValueForUpdate(String key,
														Object value){
		if(!KCatalogConstants.CONFIG_PERMANENT_LOCATIONS.equals(key)){			
			return (String)value;	
		}
		ArrayList locationList = (ArrayList)value;
		String locations = "";
		for(int i=0;i<locationList.size();i++){
			String location = (String)locationList.get(i);
			
			if("".equals(locations)){
				locations += location.trim();	
			}else{
				locations += "|" + location.trim();	
			}
		}		
		return locations.trim();
	}
	
	private static ArrayList getPermanentLocList(String locationList){
		ArrayList list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(locationList.trim(),
																	"|");
		while(tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			if(token != null && !"".equals(token)){
				list.add(token.trim());
			}
		}
		return list;
	}
	
	public static String getSeparator(){
		String separator = "";
		separator = File.separator;
		if(!"\\".equals(separator.trim()) &&
		    !"/".equals(separator.trim())){
		    separator = "\\";			    		    
		}
		return separator;
	}
	
	private static void populateConfigMap(){
		String separator = "";
		DocumentBuilder db = null;	
		try{
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
			dbf.setIgnoringComments(true);
			db = dbf.newDocumentBuilder();
			separator = File.separator;
			if(!"\\".equals(separator.trim()) &&
			    !"/".equals(separator.trim())){
			    separator = "\\";			    		    
			}
			Document doc = db.parse(
		
			  new File(KCatalogCommonUtility.getConfigPath() + separator + "config.xml").getAbsolutePath());		    	
		    configMap = getHashMapFromDoc(doc);
		}catch(Exception e){			
			KCatalogException kce = 
			   new KCatalogException(KCatalogConstants.ERROR_MESSAGE_OPEN_CONFIG,
			                                e);
			throw kce;
		}	
		
	}
	
	private static HashMap getHashMapFromDoc(Document doc){
		HashMap map = new HashMap();
		Node root = doc.getChildNodes().item(0);		
		if(root.getNodeType() == Node.ELEMENT_NODE){
			 String rootName = root.getNodeName();
			 rootName = (rootName == null)?"":rootName;
			 if(!KCatalogConstants.CONFIG_ROOT.equals(rootName.trim())){
			 	 KCatalogException kce = 
			   		new KCatalogException(KCatalogConstants.ERROR_CONFIG_FILE_HEADER);
			   	 throw kce;
			 }else{
			 	NodeList list = root.getChildNodes();
			 	for(int i = 0; i < list.getLength();i++){
			 	
			 		Node node = list.item(i);
			 		
			 		NamedNodeMap attributeMap = node.getAttributes();
			 		if( attributeMap != null ){
			 			if( attributeMap.getLength() < 0 ||
				 		 	attributeMap.getLength() > 1 ){
			 				KCatalogException kce = 
				   		        new KCatalogException(
			   		        			KCatalogConstants.ERROR_CONFIG_FILE_HEADER);	 		
			 			}
			 			Node value = attributeMap.item(0);
			 			String nodeName = node.getNodeName();
			 			nodeName = ( nodeName == null)? "": nodeName.trim();
			 			String attrValue = value.getNodeValue();
			 			attrValue = (attrValue == null)?"":attrValue.trim();
			 			map.put(nodeName,attrValue);			 						 			
			 		}
			 	}
			 }
		}
		return map;
	}
	
	
}
