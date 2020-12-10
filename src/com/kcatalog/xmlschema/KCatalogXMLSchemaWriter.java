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
import com.kcatalog.xmlschema.KCatalogXMLSchemaFileSearch;
import com.kcatalog.xmlschema.KCatalogXMLSchemaLookup;
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUp;
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUpManager;
import com.kcatalog.xmlschema.KCatalogSchemaMp3FileDto;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.FileOutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult
;import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class KCatalogXMLSchemaWriter extends
										KCatalogXMLSchemaBase {
											
		public static boolean writeMp3DtoToXMLSchema(
								Mp3FileDto mp3FileDto){
			
			return new KCatalogXMLSchemaWriter().writeMp3Dto(mp3FileDto);
		}
	
		
		
		private boolean writeMp3Dto(Mp3FileDto mp3FileDto){
			
			if(!new File(getLookUpFilePath()).exists()){
				KCatalogXMLSchemaLookup.createLookUpFile();
			}	
	/*		String temp = mp3FileDto.getAlbum();*/
		
		/*	System.out.println(mp3FileDto.getFileLocation());
			System.out.println(mp3FileDto.getFileName());
			System.out.println(mp3FileDto.getAlbum());
			System.out.println(mp3FileDto.getArtist());
			System.out.println(mp3FileDto.getComment());
			System.out.println(mp3FileDto.getGenre());
			System.out.println(mp3FileDto.getTitle());
			System.out.println(mp3FileDto.getYear());*/
		
			boolean recordExist = checkAndDeleteIfRecordAlreadyExist(mp3FileDto);	
		//	System.out.println("recordExist " + recordExist);
			if(mp3FileDto.getFileName() == null ||
					"".equals(mp3FileDto.getFileName().trim())){
				return false;
			}
			if(recordExist){
				return false;
			}
		
			//System.out.println("Going to write new record "
			 //+ mp3FileDto.getFileName());
			
			return writeNewRecord(mp3FileDto);
			
		}
		
		public void writeNewRecordsInPlayListMetaInfo(String filePath,
														ArrayList mp3DtoList){
						
			Document doc = null;
			if( !new File(filePath).exists()){
				doc = this.getNewDocumentFromFactory();
				createHeader(doc);
			}else{
				doc = this.getDocumentFromFactory(filePath);
			}
			Iterator it = mp3DtoList.iterator();
			while(it.hasNext()){
				Mp3FileDto mp3FileDto = (Mp3FileDto)it.next();
				writeMp3DtoToDoc(doc,mp3FileDto);
			}
			this.writeDocToFile(doc,filePath);
		}
		
		private boolean writeNewRecord(Mp3FileDto mp3FileDto){
			
			String prevLocationId = KCatalogXMLSchemaLookup.getLastAddedLocationId();
			
			String newLocationId = getNewLocationId(prevLocationId);
			String newFilePath = getXmlFilePath(newLocationId);
			Document doc = null;
			if( !new File(newFilePath).exists()){
				doc = this.getNewDocumentFromFactory();
				createHeader(doc);
			}else{
				doc = this.getDocumentFromFactory(newFilePath);
			}
		//	System.out.println("newFilePath " +newFilePath );
			KCatalogXMLSchemaMp3AttributeLookUpManager manager
				= KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
			KCatalogXMLSchemaLookup.write(mp3FileDto.getFileLocation().trim(),
												newLocationId);
			manager.addMp3FileDto(mp3FileDto,newLocationId);
			writeMp3DtoToDoc(doc,mp3FileDto);
			
			try{
				TransformerFactory tf = TransformerFactory.newInstance();			
				Transformer tm = tf.newTransformer();
				tm.setOutputProperty(OutputKeys.INDENT,"yes");
				DOMSource src = new DOMSource(doc);
				FileOutputStream fos = new FileOutputStream(new File(newFilePath));			
				StreamResult sr = new StreamResult(fos);
				tm.transform(src,sr);
				fos.close();
			}catch(Exception e){
				throw new KCatalogException(
					KCatalogConstants.ERROR_WRITING_MP3XML,e);
			}
			return true;			
		}
		
		private void writeMp3DtoToDoc(Document doc,Mp3FileDto mp3FileDto){
			Element header = doc.getDocumentElement();
			String noOfRecords = header.getAttribute(
									KCatalogConstants.MP3_XML_NO_OF_RECORDS).trim();
			String newNoOfRecords = String.valueOf(
										Integer.valueOf(noOfRecords).intValue() + 1); 
			header.setAttribute(KCatalogConstants.MP3_XML_NO_OF_RECORDS,
												newNoOfRecords);
			NodeList fileLocationList = header.getChildNodes();
			Node currentFileLocationNode = null;
			String fileLocation = "";
			for(int i=0;i<fileLocationList.getLength();i++){
				Node fileLocationNode = fileLocationList.item(i);
				if(fileLocationNode.getNodeName().equals(
								KCatalogConstants.MP3_XML_FILELOCATION)){
					NamedNodeMap attrMap = fileLocationNode.getAttributes();
					
					fileLocation = attrMap.getNamedItem("value").
															getNodeValue();
					/*System.out.println("outside " + i + " "+ fileLocation +
								" " + mp3FileDto.getFileLocation() );*/
					if(mp3FileDto.getFileLocation().trim().equals(
											fileLocation.trim())){
						/*	System.out.println("*************************");
							System.out.println(mp3FileDto.getFileLocation());
							System.out.println("*************************");*/
							currentFileLocationNode = fileLocationNode;
							break;
					}				
					
				}
			}																			
		//	System.out.println("writeMp3DtoToDoc " + fileLocation);
					
			if(currentFileLocationNode == null){
				Element tempElement = createElementWithAttribute(doc,
							KCatalogConstants.MP3_XML_FILELOCATION,
							"value",
							mp3FileDto.getFileLocation().trim());
				header.appendChild(tempElement);
				currentFileLocationNode = (Node)tempElement;
			}
			addMp3FileToFileLocationElement(doc,currentFileLocationNode,
													mp3FileDto);
			
		}
		
		protected void addMp3FileToFileLocationElement(Document doc,
												Node fileLocationElement,
												Mp3FileDto mp3FileDto){
			Element mp3FileElement= createElementWithAttribute(doc,
									KCatalogConstants.MP3_XML_MP3FILE,
									null,
									null);				
			fileLocationElement.appendChild(mp3FileElement);
			
		
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_FILENAME,
									mp3FileElement,
									mp3FileDto.getFileName());
			
			if(mp3FileDto.getAlbum() == null)
					mp3FileDto.setAlbum("");
			if(mp3FileDto.getArtist() == null)
					mp3FileDto.setArtist("");
					
			if(mp3FileDto.getComment() == null)
					mp3FileDto.setComment("");
					
			if(mp3FileDto.getGenre() == null)
					mp3FileDto.setGenre("");
			if(mp3FileDto.getDuration() == null)
					mp3FileDto.setDuration("");
			if(mp3FileDto.getYear() == null)
					mp3FileDto.setYear("");
			if(mp3FileDto.getTitle() == null)
					mp3FileDto.setTitle("");
		
			
									
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_ALBUM,
									mp3FileElement,
									mp3FileDto.getAlbum());
			
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_ARTIST,
									mp3FileElement,
									mp3FileDto.getArtist());
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_COMMENT,
									mp3FileElement,
									mp3FileDto.getComment());
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_GENRE,
									mp3FileElement,
									mp3FileDto.getGenre());
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_DURATION,
									mp3FileElement,
									mp3FileDto.getDuration());
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_YEAR,
									mp3FileElement,
									mp3FileDto.getYear());
			addElementToMp3FileNode(doc,
									KCatalogConstants.MP3_XML_TITLE,
									mp3FileElement,
									mp3FileDto.getTitle());
		}
		
		void addElementToMp3FileNode(Document doc,
										String elementName,
										Element mp3FileElement,
										String textValue
										){
			Element element = this.createElementWithAttribute(
											doc,
											elementName,
											null,
											null);
			createTextNodeForParent(doc,
									element,
									elementName,
									textValue
									);
			mp3FileElement.appendChild(element);
		}
		
		private void createHeader(Document doc){
			Element header = doc.createElement(KCatalogConstants.MP3_XML_ROOT);
			header.setAttribute(KCatalogConstants.MP3_XML_NO_OF_RECORDS,"0");
			doc.appendChild(header);
		}
		
		private String getNewLocationId(String prevLocationId){
		
			if("0".equals(prevLocationId)){
				return "1";
			}
			boolean flag = true;
			String currentLocationId = "1";
			while(flag){
				
				String xmlFilePath = getXmlFilePath(currentLocationId);
				int no = Integer.valueOf(
						getCurrentNoOfRecordsInXML(xmlFilePath).trim()).intValue();
				String maxNoStr = KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_MAX_MP3_PER_XML);
				int maxNo = Integer.valueOf(maxNoStr.trim()).intValue();
				if( no < maxNo ){
					return currentLocationId;
				}
				currentLocationId = String.valueOf(
										Integer.valueOf(currentLocationId.trim()).
												intValue() + 1);
				if(!new File(getXmlFilePath(currentLocationId)).isFile()){
					break;
				}								
			}
				
			return currentLocationId;
		}
		

	
		
		private boolean checkAndDeleteIfRecordAlreadyExist(Mp3FileDto mp3FileDto){
			ArrayList prevLocationIdList = 
					KCatalogXMLSchemaLookup.getLocationFileId(
										mp3FileDto.getFileLocation().trim());
			
		
			for(int i = 0;i<prevLocationIdList.size();i++){
			
				
				String xmlFilePath = getXmlFilePath((String)prevLocationIdList.get(i));				
			//	System.out.println("checkIfRecordAlreadyExist "+ xmlFilePath
			//				+ " " +mp3FileDto.getFileName());
				
				if(!new File(xmlFilePath).exists()){
				
					return false;
				}
				
				KCatalogXMLSchemaFileSearch fs 
										= new KCatalogXMLSchemaFileSearch();
				ArrayList matchingRecords = 
									fs.searchFile( xmlFilePath,
													mp3FileDto,
													KCatalogConstants.
													   SEARCH_OPERATION_TYPE_FILE_LOC_DEL_IF_DIFF_DATA
													,true);
				if(matchingRecords.size() > 0){
			//		System.out.println("checkIfRecordAlreadyExist matchinrecords"+ matchingRecords.size());
					if(isSameDataInDtos((Mp3FileDto)matchingRecords.get(0),
								mp3FileDto)){
						return true;
					}else{
				//		System.out.println("going to delete " +
				//						mp3FileDto.getFileName());
						KCatalogSchemaMp3FileDto kMp3FileDto = (KCatalogSchemaMp3FileDto)matchingRecords.get(0);
						deleteMp3FileFromSchema(kMp3FileDto);
						writeDocToFile(kMp3FileDto.getDoc(),xmlFilePath);
						KCatalogXMLSchemaMp3AttributeLookUpManager 
						lookUpmanager = KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
						String locId = this.getLocationIdFromXMLFilePath(kMp3FileDto.getXmlFileName());
						lookUpmanager.deleteMp3FileDto(kMp3FileDto,locId);						
						return false;
					}
					
				}else{
				//	System.out.println("matchingRecords.size() "+ matchingRecords.size());
							
				}
			}
			return false;
		}
		
	
		
		private boolean isSameDataInDtos(Mp3FileDto mp3DtoFromFile,
												Mp3FileDto mp3Dto){
				KCatalogXMLSchemaFileSearch fs 
										= new KCatalogXMLSchemaFileSearch();									
				//System.out.println(fs.checkMatchForUpdate(mp3DtoFromFile,
				//						mp3Dto));
				return fs.checkMatchForUpdate(mp3DtoFromFile,
										mp3Dto);
		}
}

