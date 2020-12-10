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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class KCatalogXMLSchemaMp3AttributeLookUp extends KCatalogXMLSchemaBase {
		
	public static final String LOOKUP_TYPE_ARTIST = "artist";
	public static final String LOOKUP_TYPE_ALBUM  = "album";
	public static final String LOOKUP_TYPE_GENRE = "genre";
	public static final String LOOKUP_TYPE_TITLE = "title";
	public static final String LOOKUP_TYPE_COMMENT = "comment";
	public static final String MATCH_ALL_ATTRIBUTE_VAL = "*.*";
	
	private String FILE_PATH_ARTIST = "mp3ArtistLookup.xml";
	private String FILE_PATH_ALBUM = "mp3AlbumLookup.xml";
	private String FILE_PATH_GENRE = "mp3GenreLookup.xml";
	private String FILE_PATH_TITLE  = "mp3TitleLookup.xml";
	private String FILE_PATH_COMMENT = "mp3CommentLookup.xml";
			
	
	protected String attributeType = LOOKUP_TYPE_ARTIST;
	protected String attributeMapVal = KCatalogConstants.LOOKUP_LOCATION_ID;
	
	public KCatalogXMLSchemaMp3AttributeLookUp(String attributeType) {
		this.attributeType = attributeType;		
	}	
	
	protected KCatalogXMLSchemaMp3AttributeLookUp() {			
	}	
	
	
	public String getFilePath(){
		String fileName = FILE_PATH_ARTIST;
		if(LOOKUP_TYPE_ARTIST.equals(attributeType)){
			fileName = 	FILE_PATH_ARTIST;
		}
		if(LOOKUP_TYPE_ALBUM.equals(attributeType)){
			fileName = 	FILE_PATH_ALBUM;
		}
		if(LOOKUP_TYPE_GENRE.equals(attributeType)){
			fileName = 	FILE_PATH_GENRE;	
		}
		if(LOOKUP_TYPE_TITLE.equals(attributeType)){
			fileName = 	FILE_PATH_TITLE;
		}
		if(LOOKUP_TYPE_COMMENT.equals(attributeType)){
			fileName = 	FILE_PATH_COMMENT;
		}
		String databaseDir = KCatalogConfigOptions.
							getOptionValue(KCatalogConstants.CONFIG_DATABASE_LOCATION);
		String filePath = databaseDir + KCatalogConfigOptions.getSeparator()
										+ fileName;
		return filePath;		
	}
	
	public boolean isLookUpFileExists(){
				
		File lookupFile = new File(getFilePath());
		if(!lookupFile.isFile()){		
			return false;
		}
		return true;
	}
	
	public void createLookUpFile(){
		try{
			if(isLookUpFileExists()){
				throw new 
				    KCatalogException("Cannot create lookup");
			}
			Document doc = this.getNewDocumentFromFactory();			
			Element header = createHeader(doc);			
			doc.appendChild(header);
			writeDocToFile(doc,getFilePath());		
		}catch(Exception e){
			throw new KCatalogException(
				"Error creating look up file for " + attributeType ,e);
		}
	}
	
	private  Element createHeader(Document doc){
			Element header = doc.createElement(KCatalogConstants.CONFIG_ROOT);			
			return header;
	}
	
	public void deleteAttribute(String attributeValue,String locationId){
		if(!isLookUpFileExists()){
			createLookUpFile();
		}
		String lookupFilePath = getFilePath();
		try{
			Document doc = this.getDocumentFromFactory(lookupFilePath);
			Element header = doc.getDocumentElement();
	
			if(lookupAttributeAlreadyExist(header,attributeValue,locationId)){
				addCountForAttribute(doc,attributeValue,locationId,-1);
				writeDocToFile(doc,lookupFilePath);
			}
			
		}catch(Exception e){
			throw new KCatalogException(
				      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
		}		
	}
	
	public void write(String attributeValue,String locationId){
		if(!isLookUpFileExists()){
			createLookUpFile();
		}
		String lookupFilePath = getFilePath();
		try{
			Document doc = this.getDocumentFromFactory(lookupFilePath);
			Element header = doc.getDocumentElement();
	
			if(lookupAttributeAlreadyExist(header,attributeValue,locationId)){
				addCountForAttribute(doc,attributeValue,locationId,1);
			}else{
				Element newElement = createNewElement(doc,
								attributeValue,locationId);
				header.appendChild(newElement);			
			}
			writeDocToFile(doc,lookupFilePath);
		}catch(Exception e){
			throw new KCatalogException(
				      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
		}		
	}
	
	private boolean lookupAttributeAlreadyExist(Element header,String attributeValue,
								String locationId){
		ArrayList locationIdlist
				= lookupLocationFile(attributeValue);				
		for(int i =0;i<locationIdlist.size();i++){
			String locationIdTemp = (String)locationIdlist.get(i);
			if(locationIdTemp.trim().equals(locationId.trim())){
				return true;
			}
		}	
		return false;
												
	}
	
	private void addCountForAttribute(Document doc,String attributeValue,
													String locationId,int val){
		if( !isLookUpFileExists()){
			throw new KCatalogException(
				KCatalogConstants.ERROR_READING_LOCATION_LOOKUP_FILE);
		}
		try{
			String containedField = null;			
			Element header = doc.getDocumentElement();
			NodeList childNodes = header.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node = childNodes.item(i);
				String nodeName = node.getNodeName();
				nodeName = ( nodeName == null)?"":nodeName.trim();
				if( attributeType.equals(nodeName)){
					NamedNodeMap attrMap = node.getAttributes();
					String attributeValueFromDB = 
								attrMap.getNamedItem("value").getNodeValue();
					if(attributeValueFromDB.trim().equals(attributeValue.trim())){
						 containedField = 
							this.getTextNodeWithName(node,attributeMapVal);
						if(containedField.equals(locationId)){
							addCountToFileIDNode(node,val);
						}
					}
				}
			}	
			writeDocToFile(doc,getFilePath());				
			
		}catch(Exception e){
			throw new KCatalogException(
				      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
		}		
	}
	
	private int addCountToFileIDNode(Node parentNode,int addVal){
		String childNodeName = attributeMapVal; 		
		childNodeName = childNodeName.replace('\n',' ').trim();
		int i = 0;
		NodeList list = parentNode.getChildNodes();		
		int oldValue = 0;
		while(i < list.getLength()){
			Node node = list.item(i);
			i++;
			String nodeName = node.getNodeName().replace('\n',' ').trim();		
			if(nodeName.equals(childNodeName)){								
				NamedNodeMap map = node.getAttributes();
				Node attrNode = map.getNamedItem("mp3Count");
				String val = attrNode.getNodeValue();
				val = val.trim();
				int intVal = Integer.valueOf(val).intValue();
				oldValue = intVal;
				intVal = intVal + addVal;
				intVal = ( intVal < 0 )? 0: intVal;
				attrNode.setNodeValue(String.valueOf(intVal));
			}
		}
		return oldValue;
	}
	
	public ArrayList getAllAttributeValues(){
		ArrayList attributeValList = new ArrayList();
		if( !isLookUpFileExists()){
			throw new KCatalogException(
				KCatalogConstants.ERROR_READING_LOCATION_LOOKUP_FILE);
		}
		try{
			String containedField = null;
			Document doc = getDocumentFromFactory(getFilePath());
			Element header = doc.getDocumentElement();
			NodeList childNodes = header.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node = childNodes.item(i);
				String nodeName = node.getNodeName();
				nodeName = ( nodeName == null)?"":nodeName.trim();
				if( attributeType.equals(nodeName)){
					NamedNodeMap attrMap = node.getAttributes();
					String attributeValueFromDB = 
								attrMap.getNamedItem("value").getNodeValue().trim();					
					if(addCountToFileIDNode(node,0) != 0){
					 	if(!attributeValList.contains(attributeValueFromDB)){
					 		attributeValList.add(attributeValueFromDB);
					 	}					
					}
				}
			}			
			
		}catch(Exception e){
			throw new KCatalogException(
				      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
		}
		return attributeValList;	
	}
	
	public ArrayList lookupLocationFile(String attributeValue){
		ArrayList locationIdList = new ArrayList();
		if( !isLookUpFileExists()){
			throw new KCatalogException(
				KCatalogConstants.ERROR_READING_LOCATION_LOOKUP_FILE);
		}
		try{
			String containedField = null;
			Document doc = getDocumentFromFactory(getFilePath());
			Element header = doc.getDocumentElement();
			NodeList childNodes = header.getChildNodes();
			for(int i = 0;i < childNodes.getLength();i++){
				Node node = childNodes.item(i);
				String nodeName = node.getNodeName();
				nodeName = ( nodeName == null)?"":nodeName.trim();
				if( attributeType.equals(nodeName)){
					NamedNodeMap attrMap = node.getAttributes();
					String attributeValueFromDB = 
								attrMap.getNamedItem("value").getNodeValue();
					if(MATCH_ALL_ATTRIBUTE_VAL.equals(attributeValue.trim()) ||
						attributeValueFromDB.trim().equals(attributeValue.trim())){
						if(addCountToFileIDNode(node,0) != 0){
						 	containedField = 
							this.getTextNodeWithName(node,
								attributeMapVal);
						 	locationIdList.add(containedField);
						 }else{
						 	header.removeChild(node);
						 }
					}
				}
			}			
			
		}catch(Exception e){
			throw new KCatalogException(
				      KCatalogConstants.ERROR_ACESSING_LOOKUP_FILE,e);
		}
		return locationIdList;		
	}
		
	private Element createNewElement(Document doc,String attributeValue,String locationId){
		Element attributeElement = doc.createElement(attributeType);																	
		attributeElement.setAttribute("value",attributeValue.trim());
		Element locationIdNode = doc.createElement(attributeMapVal);
		attributeElement.appendChild(locationIdNode);
		locationIdNode.setAttribute("mp3Count","1");
		CDATASection textNode = doc.createCDATASection(attributeMapVal);			
		textNode.setData(locationId.trim());
		locationIdNode.appendChild(textNode);
		return attributeElement;
	}
}
