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

import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import java.io.FileOutputStream;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

public abstract class KCatalogXMLSchemaBase {
	
	protected Document getNewDocumentFromFactory(){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
					
			return db.newDocument();
		}catch(Exception e){
			KCatalogException kce = new KCatalogException(
											KCatalogConstants.ERROR_WRITING_DATABASE);			
			throw kce;
		}
		
	}
	
	protected Element createElementWithAttribute(Document doc,
													String elementName,
														String attributeName,
															String attributeValue){
		Element element = doc.createElement(elementName.trim());
		if(attributeName != null){
			element.setAttribute(attributeName.trim(),attributeValue.trim());
		}
		return element;
	}
	
	protected void createTextNodeForParent(Document doc,
						Element parentNode,
						String textNodeName,
						String text){
	/*	Text textNode = doc.createTextNode(textNodeName.trim());
		textNode.setNodeValue(text.trim());
		parentNode.appendChild(textNode);*/
		CDATASection textNode = doc.createCDATASection(textNodeName.trim());
		textNode.setData(text.trim());
		parentNode.appendChild(textNode);
	}
	
	protected String getXmlFilePath(String id){
			String xmlFileNamePrefix = KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_FILE_NAME_PREFIX);
			String databaseLoc = KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_DATABASE_LOCATION)
										+KCatalogConfigOptions.getSeparator();
			String xmlFilePath = databaseLoc +
										xmlFileNamePrefix.trim() +"_" +
							 				 id.trim() +".xml";
			return xmlFilePath;
		}
	
	protected static String getLookUpFilePath(){
			String databaseDir = KCatalogConfigOptions.
							getOptionValue(KCatalogConstants.CONFIG_DATABASE_LOCATION);
			String lookupFilePath = databaseDir + KCatalogConfigOptions.getSeparator()
										+ KCatalogConstants.LOCATION_LOOK_UP_FILE;
			return lookupFilePath;
		}
		
	protected  Document getDocumentFromFactory(String fileName){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();	
			return db.parse(				
					new File(fileName).getAbsolutePath());				
		}catch(Exception e){
			KCatalogException kce = new KCatalogException(					KCatalogConstants.ERROR_WRITING_DATABASE,e);			
		    throw kce;
		}
		
	}
	
	protected static InputStream getInputStreamFromFile(String fileName){
		try{
			File file = new File(fileName);			
			long size = file.length();					
			byte[] data = new byte[(int)size];
			FileInputStream fs = new FileInputStream(file);		
			fs.read(data);
			//fs.close();
			ByteArrayInputStream bis = 
						new ByteArrayInputStream(data);			
			return bis;
		}catch(Exception e){
			throw new KCatalogException("Unable to create input stream",e);
		}
	}
	
	protected String getTextNodeWithName(Node parentNode,String childNodeName){
		String resultText = "";
		childNodeName = childNodeName.replace('\n',' ').trim();
		int i = 0;
		NodeList list = parentNode.getChildNodes();
		
		while(i < list.getLength()){
			Node node = list.item(i);
			i++;
			String nodeName = node.getNodeName().replace('\n',' ').trim();		
			if(nodeName.equals(childNodeName)){
				NodeList textNodes = node.getChildNodes();
				int j =0;
				while(j < textNodes.getLength()){					
					CDATASection textNode = 
								(CDATASection)textNodes.item(j);
					j++;				
					if(textNode != null){
						String tempStr = textNode.getData();
						
						tempStr = ( tempStr == null ) ?"" : tempStr.replace('\n',' ').trim();	
						resultText = resultText + tempStr;
					}
				}
			}
		}
		return resultText;
	}
	
	protected boolean isExactMatchForUpdate(String orginalString,String searchString){
			
		orginalString = (orginalString == null)	?orginalString:orginalString.trim();
		searchString = (searchString == null)	?searchString:searchString.trim();
		if(KCatalogConstants.MESSAGE_NA.equals(orginalString)){
			orginalString = "";
		}
		if(KCatalogConstants.MESSAGE_NA.equals(searchString)){
			searchString = "";
		}		
		if((searchString == null ||
		 			"".equals(searchString)) &&
		 				(orginalString == null ||
		 					"".equals(orginalString)) 
		 			){
		 	return true;
		}
		
		if((searchString == null ||
		 			"".equals(searchString))){
		 	return false;
		}
		
		if((orginalString == null ||
		 			"".equals(orginalString))){
		 	return false;
		}
						
		searchString = searchString.trim();		
	
		return orginalString.trim().equalsIgnoreCase(searchString);
	}
	
	protected boolean isExactMatch(String orginalString,String searchString){
			
		/*System.out.println("isExactMatch " + searchString + " " +orginalString );*/
		if(KCatalogConstants.MESSAGE_EMPTY.equals(orginalString)){
			if("".equals(orginalString.trim()) || 
					KCatalogConstants.MESSAGE_NA.equals(orginalString.trim())){
				return true;
			}
			return false;
		}
		if(KCatalogConstants.MESSAGE_NA.equals(orginalString)){
			orginalString = "";
		}
		if(KCatalogConstants.MESSAGE_NA.equals(searchString)){
			searchString = "";
		}		
		if((searchString == null ||
		 			"".equals(searchString)) &&
		 				(orginalString == null ||
		 					"".equals(orginalString)) 
		 			){
		 	return true;
		}
		
		if((searchString == null ||
		 			"".equals(searchString))){
		 	return true;
		}
		if("*.*".equals(searchString.trim())){
			return true;
		}
		
		if((orginalString == null ||
		 			"".equals(orginalString))){
		 	return false;
		}
						
		searchString = searchString.trim();		
	
		return orginalString.trim().equalsIgnoreCase(searchString);
	}
	
	
	
	
	protected boolean isMatching(String orginalString,String searchString){
		
		if(KCatalogConstants.MESSAGE_NA.equals(orginalString)){
			orginalString = "";
		}
		if(KCatalogConstants.MESSAGE_NA.equals(searchString)){
			searchString = "";
		}
	
		orginalString = (orginalString == null ) ? "" : orginalString.trim();
		searchString = (searchString == null ) ? "" : searchString.trim();
		if("*.*".equals(searchString.trim())){
			return true;
		}
		String TWO_SPACES = "  ";
		String SPACE = " ";
		String temp = "";
		String open = "(";
		String closeAndOpen = ")+(\\\\s)*(\\\\w)*(\\\\s)*(";
		String close = ")+";
		while(!temp.equals(searchString)){
			temp = searchString;
			searchString = searchString.replaceAll(TWO_SPACES,SPACE);								
		}
		
		StringBuffer str = new StringBuffer(searchString.trim());
		
		str.insert(0,open);		
		String str1 = new String(str);
		String str2 = " ";
		while( !str1.equals(str2)){		
			str2 = str1;		
			str1 = str1.trim().replaceFirst(SPACE,closeAndOpen);		
		}
		str = new StringBuffer(str1);
		str = str.append(close);
		try{
			String pattern = new String(str);	
			Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);		
			Matcher matcher = p.matcher(orginalString);
			return matcher.find();
		}catch(Exception e){
			return true;
		}
								
	}
	
	
	protected String getCurrentNoOfRecordsInXML(String xmlFileName){
		if(!new File(xmlFileName).exists()){
			return "0";
		}
		Document doc = getDocumentFromFactory(xmlFileName);
		return doc.getDocumentElement().getAttribute(
									KCatalogConstants.MP3_XML_NO_OF_RECORDS);
	
	}
		
	protected String getLocationIdFromXMLFilePath(String filePath){
		String xmlFileNamePrefix = KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_FILE_NAME_PREFIX)
										+"_";
		int index = filePath.indexOf(xmlFileNamePrefix);
		index = ( -1 == index)? 0:index;
		int len = xmlFileNamePrefix.length();
		String locIdStr = filePath.substring(index+len);
		locIdStr = locIdStr.substring(0,
						locIdStr.indexOf(".xml"));
		try{
			locIdStr = Integer.valueOf(locIdStr).toString();
		}catch(Exception e){
			throw new KCatalogException("Error parsing location id",e);
		}
		
		return locIdStr;				
	}
	
	protected boolean deleteMp3FileFromSchema(KCatalogSchemaMp3FileDto
															mp3FileDto){
			Node node = mp3FileDto.getParentLocationNode();
			node.removeChild(mp3FileDto.getMp3FileNode());
			Document doc = mp3FileDto.getDoc();
			String currNo = doc.getDocumentElement().getAttribute(
										KCatalogConstants.MP3_XML_NO_OF_RECORDS);
			int tempNo = Integer.valueOf(currNo.trim()).intValue() - 1;
			tempNo = (tempNo == -1)? 0:tempNo;
			String newNo = String.valueOf(tempNo);
			
			doc.getDocumentElement().setAttribute(
										KCatalogConstants.MP3_XML_NO_OF_RECORDS,newNo);
		
			return true;
	}
	
	protected void updateMp3FileDto(KCatalogSchemaMp3FileDto orginalDto,
					Mp3FileDto newDto){
			Node node = orginalDto.getParentLocationNode();
			node.removeChild(orginalDto.getMp3FileNode());			
			Document doc = orginalDto.getDoc();		
			KCatalogXMLSchemaWriter writer = 
						new KCatalogXMLSchemaWriter();
			writer.addMp3FileToFileLocationElement(doc,node,newDto);		
			
	}
	
	public static boolean checkIfMp3FileExist(Mp3FileDto dto){
		String filePath = 
			dto.getFileLocation() + KCatalogConfigOptions.getSeparator()
					+ dto.getFileName();
		if(new File(filePath).exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public static void writeDocToFile(Document doc, String xmlFilePath){
		try{
			TransformerFactory tfc = TransformerFactory.newInstance();
			Transformer tm = tfc.newTransformer();
			tm.setOutputProperty(OutputKeys.INDENT,"yes");
			DOMSource src = new DOMSource(doc);
			FileOutputStream fos = new FileOutputStream(new File(xmlFilePath));
			StreamResult sr = new StreamResult(fos);			
			tm.transform(src,sr);
			fos.close();
			
		}catch(Exception e){
			throw new KCatalogException(
				KCatalogConstants.ERROR_WRITING_MP3XML,e);
		}
	
	}
	
	protected boolean checkMatchUsingOr(Mp3FileDto mp3FileDto,
										Mp3FileDto searchMp3FileDto,
										 boolean exactMatch){
		boolean result = false;
		if(exactMatch){
			result = isExactMatchOr(mp3FileDto.getFileLocation(),
										searchMp3FileDto.getFileLocation()) ||
					 isExactMatchOr(mp3FileDto.getFileName(),
										searchMp3FileDto.getFileName()) ||
					 isExactMatchOr(mp3FileDto.getAlbum(),
										searchMp3FileDto.getAlbum()) ||
					 isExactMatchOr(mp3FileDto.getArtist(),
										searchMp3FileDto.getArtist()) ||
				     isExactMatchOr(mp3FileDto.getGenre(),
										searchMp3FileDto.getGenre() )||					
					 isExactMatchOr(mp3FileDto.getTitle(),
										searchMp3FileDto.getTitle()) ||
					 isExactMatchOr(mp3FileDto.getComment(),
										searchMp3FileDto.getComment()) ||
					 isExactMatchOr(mp3FileDto.getYear(),
										searchMp3FileDto.getYear()) ;					
		}else{
			
			result = isMatchingOr(mp3FileDto.getFileLocation(),
										searchMp3FileDto.getFileLocation()) ||
					 isMatchingOr(mp3FileDto.getFileName(),
										searchMp3FileDto.getFileName()) ||
					 isMatchingOr(mp3FileDto.getAlbum(),
										searchMp3FileDto.getAlbum()) ||
					 isMatchingOr(mp3FileDto.getArtist(),
										searchMp3FileDto.getArtist()) ||
				     isMatchingOr(mp3FileDto.getGenre(),
										searchMp3FileDto.getGenre()) ||					
					 isMatchingOr(mp3FileDto.getTitle(),
										searchMp3FileDto.getTitle()) ||
					 isMatchingOr(mp3FileDto.getComment(),
										searchMp3FileDto.getComment()) ||
					 isMatchingOr(mp3FileDto.getYear(),
										searchMp3FileDto.getYear()) ;
			
		}
		return result;
										 	
	}
	
	protected boolean isExactMatchOr(String orginalString,String searchString){
		String invalidString = "!!!!!";
		
		if(KCatalogConstants.MESSAGE_NA.equals(orginalString.trim())){
			orginalString = "";
		}
		if(KCatalogConstants.MESSAGE_NA.equals(searchString.trim())){
			searchString = "";
		}
			
		if( searchString == null ||
					"".equals(searchString.trim())){
			return false;
		}
		if("*.*".equals(searchString.trim())){
			return true;
		}
		if( orginalString == null ||
					"".equals(orginalString.trim())){
	//		System.out.println(" org null " + searchString);
			return false;
		}
	
		return isExactMatch(orginalString,searchString);
	}
	
	protected boolean isMatchingOr(String orginalString,String searchString){
		String invalidString = "!!!!!";
		//System.out.println(orginalString + "  "+searchString);
		if(KCatalogConstants.MESSAGE_NA.equals(orginalString)){
			orginalString = "";
		}
		if(KCatalogConstants.MESSAGE_NA.equals(searchString)){
			searchString = "";
		}
		
		if( searchString == null ||
					"".equals(searchString.trim())){
			return false;
		}
		if("*.*".equals(searchString.trim())){
			return true;
		}
		if( orginalString == null ||
					"".equals(orginalString.trim())){
			return false;
		}
		
		return isMatching(orginalString,searchString);
	}
	
	public boolean checkMatchUsingAnd(Mp3FileDto mp3FileDto,
										Mp3FileDto searchMp3FileDto,
										 boolean exactMatch){
		boolean result = false;
		if(exactMatch){
			result = isExactMatch(mp3FileDto.getFileLocation(),
										searchMp3FileDto.getFileLocation()) &&
					 isExactMatch(mp3FileDto.getFileName(),
										searchMp3FileDto.getFileName()) &&
					 isExactMatch(mp3FileDto.getAlbum(),
										searchMp3FileDto.getAlbum()) &&
					 isExactMatch(mp3FileDto.getArtist(),
										searchMp3FileDto.getArtist()) &&
				     isExactMatch(mp3FileDto.getGenre(),
										searchMp3FileDto.getGenre() )&&					
					 isExactMatch(mp3FileDto.getTitle(),
										searchMp3FileDto.getTitle()) &&
					 isExactMatch(mp3FileDto.getComment(),
										searchMp3FileDto.getComment()) &&
					 isExactMatch(mp3FileDto.getYear(),
										searchMp3FileDto.getYear()) ;
										
					
						
		}else{
			
			result = isMatching(mp3FileDto.getFileLocation(),
										searchMp3FileDto.getFileLocation()) &&
					 isMatching(mp3FileDto.getFileName(),
										searchMp3FileDto.getFileName()) &&
					 isMatching(mp3FileDto.getAlbum(),
										searchMp3FileDto.getAlbum()) &&
					 isMatching(mp3FileDto.getArtist(),
										searchMp3FileDto.getArtist()) &&
				     isMatching(mp3FileDto.getGenre(),
										searchMp3FileDto.getGenre()) &&					
					 isMatching(mp3FileDto.getTitle(),
										searchMp3FileDto.getTitle()) &&
					 isMatching(mp3FileDto.getComment(),
										searchMp3FileDto.getComment()) &&
					 isMatching(mp3FileDto.getYear(),
						 				searchMp3FileDto.getYear()) ;
						
			
		}
/*		System.out.println(result);*/
		return result;
	}
	
	
}

