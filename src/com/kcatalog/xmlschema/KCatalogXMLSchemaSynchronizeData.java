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
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;
import com.kcatalog.xmlschema.KCatalogXMLSchemaLookup;
import com.kcatalog.xmlschema.KCatalogSchemaMp3FileDto;
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUpManager;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import com.kcatalog.common.KCatalogCommonUtility;

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
import com.kcatalog.fileutils.FileManager;


public class KCatalogXMLSchemaSynchronizeData extends KCatalogXMLSchemaBase{
	
	private String deletedFileName = "";
	private Boolean stopFlag = Boolean.valueOf(false);
	
	public void setStopFlag(boolean stopFlag){
		synchronized(this.stopFlag){
			this.stopFlag = Boolean.valueOf(stopFlag);
		}
	}
	
	public boolean getStopFlag(){
		synchronized(stopFlag){
			return stopFlag.booleanValue();
		}
	}
	
	/*boolean updateStatus = false;
	
	public void setUpdateStatus(boolean updateStatus){
		this.updateStatus = updateStatus;
	}
	
	public synchronized boolean getStopFlag(){
		KCatalogProgressIndicatorDlg dlg = KCatalogProgressIndicatorDlg.getCurrentRef();
		dlg
	}
			
	public synchronized void setCurrentMp3(String currentMp3){
		
	}*/
	
	
	public void synchronizeMp3Files(ArrayList mp3DtoList){
		
		KCatalogXMLSchemaSearch ss = 	
					KCatalogXMLSchemaSearch.newInstance();
		for(int i=0;i<mp3DtoList.size();i++){
			//if(!getStopFlag()){
				Mp3FileDto dto = (Mp3FileDto)mp3DtoList.get(i);
				ArrayList result = ss.searchForMp3File(dto,
						KCatalogConstants.SEARCH_OPERATION_TYPE_AND,true)
						;		
				if(result.size() == 1){
					Mp3FileDto dto1 =(Mp3FileDto)result.get(0);
					//setCurrentMp3(dto1.getFileName());
					synchronizeMp3File(dto1);		
				}			
			//}
		}
		//setCurrentMp3("complete");		
		ss.close();
	}
	
	public void deleteMp3Files(ArrayList mp3DtoList){
		//System.out.println("inside deleteMp3Files");
		KCatalogXMLSchemaSearch ss = 	
					KCatalogXMLSchemaSearch.newInstance();
		for(int i=0;i<mp3DtoList.size();i++){
			Mp3FileDto dto = (Mp3FileDto)mp3DtoList.get(i);
			ArrayList result = ss.searchForMp3File(dto,KCatalogConstants.SEARCH_OPERATION_TYPE_AND,true);		
			//System.out.println("gonna delete " + result.size());
			if(result.size() == 1){				
				deleteMp3File((Mp3FileDto)result.get(0));
			}			
		}
		ss.close();
	}
	
	private void deleteMp3File(Mp3FileDto dto){
		KCatalogSchemaMp3FileDto ksDto = 
					(KCatalogSchemaMp3FileDto)dto;
		deleteMp3FileFromSchema(ksDto);			
		writeDocToFile(ksDto.getDoc(),ksDto.getXmlFileName());
		KCatalogXMLSchemaMp3AttributeLookUpManager 
						lookUpmanager = KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
		String locId = this.getLocationIdFromXMLFilePath(ksDto.getXmlFileName());
		lookUpmanager.deleteMp3FileDto(dto,locId);
	}
	
	private void synchronizeMp3File(Mp3FileDto dto){
		KCatalogSchemaMp3FileDto ksDto = 
				(KCatalogSchemaMp3FileDto)dto;
		if(!checkIfMp3FileExist(ksDto)){
			deleteMp3FileFromSchema(ksDto);			
			writeDocToFile(ksDto.getDoc(),ksDto.getXmlFileName());
			KCatalogXMLSchemaMp3AttributeLookUpManager 
						lookUpmanager = KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
			String locId = this.getLocationIdFromXMLFilePath(ksDto.getXmlFileName());
			lookUpmanager.deleteMp3FileDto(dto,locId);
		}
		FileManager fl = new FileManager();
		String mp3FilePath = ksDto.getFileLocation() +
						KCatalogConfigOptions.getSeparator() +
								ksDto.getFileName();
		
		Mp3FileDto newDto = fl.getMp3FileDtoFromPath(mp3FilePath,ksDto.getComment());		
		boolean isSame = checkMatchUsingAnd(dto,newDto,true);
		if(!isSame){
			updateMp3FileDto(ksDto,newDto);
			writeDocToFile(ksDto.getDoc(),ksDto.getXmlFileName());
		}
	}
	
	public int removeInvalidEntries(String deleteRecordsWithComment){
		int noOfRecordsDeleted = 0;
		String lastLocationId = KCatalogXMLSchemaLookup.getLastAddedLocationId();
		
		for( int i=1;i<= Integer.valueOf(lastLocationId.trim()).intValue();i++){
			String xmlFilePath = getXmlFilePath(String.valueOf(i));								
			if(!new File(xmlFilePath).exists()){				
				return noOfRecordsDeleted;
			}
				
			KCatalogXMLSchemaFileSearch fs 
									= new KCatalogXMLSchemaFileSearch();
			Mp3FileDto searchMp3FileDto = new Mp3FileDto();
			searchMp3FileDto.setAlbum("");
			searchMp3FileDto.setDuration("");
			searchMp3FileDto.setArtist("");
			searchMp3FileDto.setTitle("");
			searchMp3FileDto.setComment("");
			searchMp3FileDto.setFileLocation("");
			searchMp3FileDto.setFileName("");
			searchMp3FileDto.setYear("");
			searchMp3FileDto.setGenre("");							
			ArrayList matchingRecords = 
								fs.searchFile( xmlFilePath,
												searchMp3FileDto,
												KCatalogConstants.
												   SEARCH_OPERATION_TYPE_AND,
												false);
		
			KCatalogSchemaMp3FileDto kMp3FileDto = null;
			for(int mp3FNo=0;mp3FNo<matchingRecords.size();mp3FNo++){
				kMp3FileDto = (KCatalogSchemaMp3FileDto)matchingRecords.get(mp3FNo);				
				
				if(checkDeletionConditions(kMp3FileDto,deleteRecordsWithComment)){
					//System.out.println("deleting " + kMp3FileDto.getFileName());
					boolean stat = deleteMp3FileFromSchema(kMp3FileDto);
					if(stat){
						KCatalogXMLSchemaMp3AttributeLookUpManager 
						lookUpmanager = KCatalogXMLSchemaMp3AttributeLookUpManager.getInstance();
						String locId = this.getLocationIdFromXMLFilePath(kMp3FileDto.getXmlFileName());
						lookUpmanager.deleteMp3FileDto(kMp3FileDto,locId);
						setDeletedFileName(kMp3FileDto.getFileName());
						noOfRecordsDeleted++;						
					}
				}
			}								
			if(kMp3FileDto != null){
				writeDocToFile(kMp3FileDto.getDoc(),xmlFilePath);
			}
			if(getStopFlag()){
				return noOfRecordsDeleted;
			}
		}
		setStopFlag(true);
		return 	noOfRecordsDeleted;	
	}
	
	
	private void setDeletedFileName(String delFileName){
		synchronized(deletedFileName){
			deletedFileName = delFileName;
		}
	}
	
	public String getDeletedFileName(){
		synchronized(deletedFileName){
			return deletedFileName;
		}
	}
	
    private boolean checkDeletionConditions(Mp3FileDto kMp3FileDto,String deleteByComment){
    	
    	String comment = kMp3FileDto.getComment();
    	if(comment == null ){
    		comment = "";
    	}
    	if(comment.trim().equalsIgnoreCase(deleteByComment)){
    		return true;
    	}
    	String mp3FilePath = getMp3FileLocation(kMp3FileDto);
    	if(!new File(mp3FilePath).exists()){
    		String fileLocation = kMp3FileDto.getFileLocation().trim();
    		ArrayList skipLocationList = (ArrayList)KCatalogConfigOptions.getObjectOptionValue(
    						KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
    		for(int i =0;i<skipLocationList.size();i++){
				String skipLocation = (String)skipLocationList.get(i);
				fileLocation = 
				 KCatalogCommonUtility.adjustForSeparator(fileLocation,skipLocation);
				skipLocation = 
				 KCatalogCommonUtility.adjustForSeparator(skipLocation,fileLocation);
				if(fileLocation.trim().startsWith(skipLocation)){
					return false;
				}
			}
    		return true;
    	}
    	    	    			
    	return false;
    }
	
	
	private String getMp3FileLocation(Mp3FileDto mp3FileDto){
		String mp3FilePath = mp3FileDto.getFileLocation() + File.separator + 
												mp3FileDto.getFileName();
		return mp3FilePath;
	}
}
