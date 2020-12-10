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
package com.kcatalog.xmlschema;

import com.kcatalog.xmlschema.KCatalogXMLSchemaBase;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.Transformer;
import com.kcatalog.common.KCatalogCommonUtility;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.FileOutputStream;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class KCatalogXMLSchemaLookup extends
							KCatalogXMLSchemaBase{
		
		public static final String SEARCH_OP_TYPE_EQUALS = "equals";
		public static final String SEARCH_OP_TYPE_ALL= "all";
		public static final String SEARCH_OP_TYPE_CHILDREN= "children";
		public static final String SEARCH_OP_TYPE_ROOT_CHILDREN= "rootchildren";
		public static final String OP_TYPE_DELETE = "deleteLoc";
		public static final String OP_TYPE_DELETE_RECURSIVE = "deleteLocRecursive";
		
		
		
		public static ArrayList getFileLocationList(String locPattern,
														String opType){
															
			return new KCatalogXMLSchemaLookup().
							getFileLocations(locPattern,opType);
		}
		
		public static ArrayList getRootFileLocationList(){															
																											
			return new KCatalogXMLSchemaLookup().
							getFileLocations(null,
											SEARCH_OP_TYPE_ROOT_CHILDREN);
		}
			
		
		public static ArrayList getAllFileLocationList(){
															
			return new KCatalogXMLSchemaLookup().
									getFileLocations(null,SEARCH_OP_TYPE_ALL);
		}
		
		public static void deleteLocation(String locPattern){
						
			new KCatalogXMLSchemaLookup().updateLookUpFile(locPattern,
				OP_TYPE_DELETE);
		}
		
		public static void deleteLocationsRecursively(String locPattern){
						
			new KCatalogXMLSchemaLookup().updateLookUpFile(locPattern,
				OP_TYPE_DELETE_RECURSIVE);
		}
		
		private void updateLookUpFile(String locPattern,String opType){
			
			Document doc = getDocumentFromFactory(getLookUpFilePath());
			Element header = doc.getDocumentElement();
			NodeList childNodes = header.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node = childNodes.item(i);
				String nodeName = node.getNodeName();
				nodeName = ( nodeName == null)?"":nodeName.trim();
				if( KCatalogConstants.MP3_XML_FILELOCATION.equals(
											nodeName)){
					NamedNodeMap attrMap = node.getAttributes();
					String fileLocation = 
								attrMap.getNamedItem("value").getNodeValue();
					if(checkMatch(fileLocation,locPattern,opType)){
						performOperation(doc,header,node,
								fileLocation.trim(),opType);
					}
				}
			}
			writeDocToFile(doc,getLookUpFilePath());	
		}
				
		private void performOperation(Document doc,
									Node header,
									  Node locNode,
										String fileLocation,
											String opType){
			if(OP_TYPE_DELETE.equals(opType)){
				header.removeChild(locNode);
			}
			if(OP_TYPE_DELETE_RECURSIVE.equals(opType)){
				header.removeChild(locNode);
			}
		}
		
		private boolean checkMatch(String fileLocation,
								String locPattern, String opType){
			fileLocation =
			        KCatalogCommonUtility.adjustForSeparator(fileLocation,locPattern);
			locPattern =
			        KCatalogCommonUtility.adjustForSeparator(locPattern,fileLocation);
			if(OP_TYPE_DELETE.equals(opType)){
				return fileLocation.trim().equalsIgnoreCase(locPattern.trim());
			}
			if(OP_TYPE_DELETE_RECURSIVE.equals(opType)){
				return fileLocation.trim().startsWith(locPattern.trim());				
			}
			return false;
		}
					
		private ArrayList getFileLocations(String locPattern,
												String opType){
			ArrayList locList = new ArrayList();			
			if( !isLocationLookUpFileExists()){
				throw new KCatalogException(
					KCatalogConstants.ERROR_READING_LOCATION_LOOKUP_FILE);
			}
			try{
				
				Document doc = getDocumentFromFactory(getLookUpFilePath());
				Element header = doc.getDocumentElement();
				NodeList childNodes = header.getChildNodes();
				for(int i = 0;i < childNodes.getLength();i++){
					Node node = childNodes.item(i);
					String nodeName = node.getNodeName();
					nodeName = ( nodeName == null)?"":nodeName.trim();
					if( KCatalogConstants.MP3_XML_FILELOCATION.equals(
										nodeName)){
						NamedNodeMap attrMap = node.getAttributes();
						String fileLocation = 
									attrMap.getNamedItem("value").getNodeValue();
						//System.out.println("fileLocation " + fileLocation);
						addToListIfMatching(fileLocation.trim(),locPattern,opType
												,locList);													
					}
				}
				 
				return locList;
			}catch(Exception e){
				throw new KCatalogException(
					      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
			}
			
			
		}
		
		private void addToListIfMatching(String fileLocFromDB,
										String fileLocFromDto,String matchType,
										ArrayList locList){
			if(SEARCH_OP_TYPE_ALL.equals(matchType)){
				addElementToUniqueList(locList,fileLocFromDB);								
			}
			fileLocFromDto = (fileLocFromDto == null)?"":
									fileLocFromDto.trim();
			if(SEARCH_OP_TYPE_EQUALS.equals(matchType)){
				if(fileLocFromDB.equalsIgnoreCase(fileLocFromDto)){
					addElementToUniqueList(locList,fileLocFromDB);
					
				}
			}			
			if(SEARCH_OP_TYPE_CHILDREN.equals(matchType)){				
				addToListIfImmediateChild(fileLocFromDto,
											fileLocFromDB,locList);					
			}
			if(SEARCH_OP_TYPE_ROOT_CHILDREN.equals(matchType)){	
				String separator = KCatalogConfigOptions.getSeparator();			
				StringTokenizer st = new StringTokenizer(fileLocFromDB,separator);
				if(st.hasMoreTokens()){
					String rootDir = st.nextToken();
					addElementToUniqueList(locList,rootDir);					
				}else{
					String rootDir = fileLocFromDB;
					addElementToUniqueList(locList,rootDir);	
				}
			}		
		}
		
		private void addToListIfImmediateChild(String parent,String child,								ArrayList locList){
			parent = KCatalogCommonUtility.adjustForSeparator(parent,child);
			child = KCatalogCommonUtility.adjustForSeparator(child,parent);
			if(!child.startsWith(parent)){
				return;
			}
			if(child.equalsIgnoreCase(parent)){
				//addElementToUniqueList(locList,child);					
				return;
			}
			int index = parent.length()+1;
			String separator = KCatalogConfigOptions.getSeparator();			
			StringTokenizer st = new StringTokenizer(child.substring(index),separator);
			if(st.hasMoreTokens()){
				String dir = st.nextToken();
				addElementToUniqueList(locList,
							parent+separator+dir);					
			}else{
				String dir = child;
				addElementToUniqueList(locList,dir);	
			}			
		}
		
		private void addElementToUniqueList(ArrayList list,String elem){
			if(!list.contains(elem)){
				list.add(elem);
			}
		}
		
		public static ArrayList getLocationFileId(String mp3FileLocation){
			return new KCatalogXMLSchemaLookup().lookupLocationFile(mp3FileLocation);
		}
		
		private ArrayList lookupLocationFile(String mp3FileLocation){
			ArrayList locationIdList = new ArrayList();
			if( !isLocationLookUpFileExists()){
				throw new KCatalogException(
					KCatalogConstants.ERROR_READING_LOCATION_LOOKUP_FILE);
			}
			try{
				String containedField = null;
				Document doc = getDocumentFromFactory(getLookUpFilePath());
				Element header = doc.getDocumentElement();
				NodeList childNodes = header.getChildNodes();
				for(int i = 0;i < childNodes.getLength();i++){
					Node node = childNodes.item(i);
					String nodeName = node.getNodeName();
					nodeName = ( nodeName == null)?"":nodeName.trim();
					if( KCatalogConstants.MP3_XML_FILELOCATION.equals(
												nodeName)){
						NamedNodeMap attrMap = node.getAttributes();
						String fileLocation = 
									attrMap.getNamedItem("value").getNodeValue();
						if(fileLocation.trim().equals(mp3FileLocation.trim())){
							 containedField = 
								this.getTextNodeWithName(node,
											KCatalogConstants.LOOKUP_LOCATION_ID);
							 locationIdList.add(containedField);
						}
					}
				}
				 
				return locationIdList;
			}catch(Exception e){
				throw new KCatalogException(
					      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
			}
			
		}
		
		public static boolean isLocationLookUpFileExists(){
			
			String lookupFilePath = getLookUpFilePath();
			File lookupFile = new File(lookupFilePath);
			if(!lookupFile.isFile()){
			
				return false;
			}
			return true;
		}
		
		public static boolean write(String fileLocation,String locationId){
			if(!isLocationLookUpFileExists()){
				createLookUpFile();
			}
			
			String lookupFilePath = getLookUpFilePath();
			return new KCatalogXMLSchemaLookup().write(lookupFilePath,
															fileLocation,
																locationId);	
			
		}
		
		public static String getLastAddedLocationId(){
			return new KCatalogXMLSchemaLookup().lookupLastAddedLocationID();
		}
		
		private String lookupLastAddedLocationID(){
			Document doc = this.getDocumentFromFactory(getLookUpFilePath());
			Element header = doc.getDocumentElement();
			return header.getAttribute(KCatalogConstants.LAST_ADDED_LOCATION_ID);
		}
		
		private boolean write(String lookupFilePath,
								String fileLocation,
									String locationId){
			try{
				Document doc = this.getDocumentFromFactory(lookupFilePath);
				Element header = doc.getDocumentElement();
				
				if(locationLookupAlreadyExist(header,fileLocation,locationId)){
					return true;
				}
				
				Element newElement = createNewElement(doc,
											fileLocation,locationId);
				header.appendChild(newElement);
				header.setAttribute(
					KCatalogConstants.LAST_ADDED_LOCATION_ID,locationId.trim());
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer tfm = tf.newTransformer();
				tfm.setOutputProperty(OutputKeys.INDENT,"yes");
				DOMSource ds = new DOMSource(doc);							
				File lookupFile = new File(lookupFilePath);
				FileOutputStream fos = new FileOutputStream(lookupFile);			
				StreamResult sr = new StreamResult(fos);
				tfm.transform(ds,sr);
				fos.close();
				return true;
			}catch(Exception e){
				throw new KCatalogException(
					      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
			}
		}
		
		private boolean locationLookupAlreadyExist(Element header,
													String fileLocation,
													String locationId){																	
			ArrayList locationIdlist
					= KCatalogXMLSchemaLookup.getLocationFileId(fileLocation);				
			for(int i =0;i<locationIdlist.size();i++){
				String locationIdTemp = (String)locationIdlist.get(i);
				if(isExactMatch(locationId,locationIdTemp)){
					return true;
				}
			}	
			return false;
													
		}
		
		private Element createNewElement(Document doc,
								String fileLocation,String locationId){
			Element fileLocationElement = doc.createElement(
											KCatalogConstants.MP3_XML_FILELOCATION);						
			fileLocationElement.setAttribute("value",fileLocation.trim());
			
			Element locationIdNode = doc.createElement(
											KCatalogConstants.LOOKUP_LOCATION_ID);
			fileLocationElement.appendChild(locationIdNode);
//			CDATASection textNode = doc.createCDATASection(	KCatalogConstants.LOOKUP_LOCATION_ID);
		/*	Node textNode = doc.createTextNode(KCatalogConstants.LOOKUP_LOCATION_ID);
			textNode.setNodeValue(locationId.trim());*/
			CDATASection textNode = doc.createCDATASection(KCatalogConstants.LOOKUP_LOCATION_ID);			
			textNode.setData(locationId.trim());
			locationIdNode.appendChild(textNode);
			return fileLocationElement;
		}
		
		public static boolean createLookUpFile(){
			
			String lookupFilePath = getLookUpFilePath();
			return new KCatalogXMLSchemaLookup().createLookUpFile(lookupFilePath);
		}
		
		private boolean createLookUpFile(String lookupFilePath){
			try{
				if(isLocationLookUpFileExists()){
					throw new 
					    KCatalogException(KCatalogConstants.LOCATION_LOOK_UP_FILE);
				}
				Document doc = this.getNewDocumentFromFactory();
				
				Element header = createHeader(doc);			
				doc.appendChild(header);
				
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer tfm = tf.newTransformer();
				DOMSource ds = new DOMSource(doc);
				
			
				File lookupFile = new File(lookupFilePath);
				FileOutputStream fos = new FileOutputStream(lookupFile);			
				StreamResult sr = new StreamResult(fos);
				tfm.transform(ds,sr);
				fos.close();
				return true;
			}catch(Exception e){
				throw new KCatalogException(
					KCatalogConstants.ERROR_CREATING_LOCATION_LOOKUP_FILE,e);
			}
		}
		
		private static Element createHeader(Document doc){
			Element header = doc.createElement(KCatalogConstants.CONFIG_ROOT);
			header.setAttribute(
				KCatalogConstants.LAST_ADDED_LOCATION_ID,"0");
			return header;
		}
		
}
