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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.xmlschema.KCatalogSchemaMp3FileDto;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class KCatalogXMLSchemaMetaInfo extends KCatalogXMLSchemaBase {
	
	public static final String META_INFO_FILE_NAME = "Meta-Info.xml";
	public static final String MODIFIED_LOCS_HEADER_NAME = "modified-locations";
	public static final String MODIFIED_LOC_HEADER_NAME = "modified-location";
	public static final String MODIFIED_LOC_OLD_LOC = "old-location";
	public static final String MODIFIED_LOC_NEW_LOC = "new-location";
	public static final String MODIFIED_LOC_ATTRIBUTE_NAME = "unsavedDataPresent";
	
	private String filePath = "";
	private static KCatalogXMLSchemaMetaInfo currentInstance = null;
	Document metaInfoDoc = null;
	
	
	private KCatalogXMLSchemaMetaInfo() {	
		filePath = KCatalogConfigOptions.getOptionValue(
						KCatalogConstants.CONFIG_DATABASE_LOCATION);
		filePath += KCatalogConfigOptions.getSeparator() +
													META_INFO_FILE_NAME;			
		System.out.println("Meta Info File Path " + 
											filePath);				
		metaInfoDoc = getInstanceOfMetaInfo();
	}	
	
	private Document getInstanceOfMetaInfo(){
		Document doc = null;
		if(new File(filePath).exists()){
			doc = this.getDocumentFromFactory(filePath);
		}else{
			doc = this.getNewDocumentFromFactory();
			initializeMetaInfoDoc(doc);
			this.writeDocToFile(doc,filePath);
		}		
		return doc;
	}
	
	public boolean removeModifedLocs(){
		setValueForUnsavedLocData(metaInfoDoc,false);
		Element root = metaInfoDoc.getDocumentElement();
		Node modHeader = root.getElementsByTagName(MODIFIED_LOCS_HEADER_NAME).item(0);
		NodeList list = modHeader.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node child = list.item(i);
			modHeader.removeChild(child);
		}
		return true;		
	}
	
	public Map getModifiedLocationList(){
		HashMap map = new HashMap();
		Element root = metaInfoDoc.getDocumentElement();
		Node modHeader = root.getElementsByTagName(MODIFIED_LOCS_HEADER_NAME).item(0);
		NodeList modLocs = ((Element)modHeader).getElementsByTagName(MODIFIED_LOC_HEADER_NAME);
		for(int i=0;i<modLocs.getLength();i++){
			Node modLoc = modLocs.item(i);
			System.out.println("Inside get " + modLoc.getNodeName());
			
			
			System.out.println("Inside get " + this.getTextNodeWithName(modLoc,this.MODIFIED_LOC_OLD_LOC));
						
			/*System.out.println("Inside get " + oldLoc.getNodeName());
			System.out.println(this.getTextNodeWithName(oldLoc,MODIFIED_LOC_OLD_LOC) + " " +this.getTextNodeWithName(newLoc,MODIFIED_LOC_NEW_LOC));
			map.put(this.getTextNodeWithName(oldLoc,MODIFIED_LOC_OLD_LOC),this.getTextNodeWithName(newLoc,MODIFIED_LOC_NEW_LOC));*/
		}
		return map;
	}
	
	public boolean addModifiedLocation(String oldLocation,String newLocation){
		
		boolean stat = true;
		stat = createModifiedLocationElement(metaInfoDoc,oldLocation,newLocation);
		this.writeDocToFile(metaInfoDoc,filePath);
		return stat;
	}
	
	private boolean createModifiedLocationElement(Document doc,String oldLocation,String newLocation){		
		setValueForUnsavedLocData(doc,true);
		Element root = doc.getDocumentElement();
		Node modHeader = root.getElementsByTagName(MODIFIED_LOCS_HEADER_NAME).item(0);
		Element modLocElement = doc.createElement(MODIFIED_LOC_HEADER_NAME);				
		modHeader.appendChild(modLocElement);
		this.createTextNodeForParent(doc,modLocElement,MODIFIED_LOC_OLD_LOC,oldLocation);
		this.createTextNodeForParent(doc,modLocElement,MODIFIED_LOC_NEW_LOC,newLocation);
		return true;
	}
	
	private void setValueForUnsavedLocData(Document doc,boolean val){
		Element root = doc.getDocumentElement();
		Node modHeader = root.getElementsByTagName(MODIFIED_LOCS_HEADER_NAME).item(0);		
		NamedNodeMap map = modHeader.getAttributes();
		Node unsavedDataNode = map.getNamedItem(MODIFIED_LOC_ATTRIBUTE_NAME);
		unsavedDataNode.setNodeValue(String.valueOf(val));		
	}
	
	private void initializeMetaInfoDoc(Document doc){		
		createRootElement(doc);
		createHeaderForModifiedLocations(doc,false);
	}
	
	private Element createHeaderForModifiedLocations(Document doc,boolean isUnsavedDataPresent){
		Element header = this.createElementWithAttribute(doc,MODIFIED_LOCS_HEADER_NAME,
											MODIFIED_LOC_ATTRIBUTE_NAME,String.valueOf(isUnsavedDataPresent));
		Element root = doc.getDocumentElement();		
		root.appendChild(header);
		return header;
	}
	
	private Element createRootElement(Document doc){
		Element root = doc.createElement(KCatalogConstants.MP3_XML_ROOT);		
		doc.appendChild(root);
		return root;
	}
	
	public static KCatalogXMLSchemaMetaInfo getInstance(){
		currentInstance = (null == currentInstance)?
								new KCatalogXMLSchemaMetaInfo() : currentInstance;
		return currentInstance;
	}
}
