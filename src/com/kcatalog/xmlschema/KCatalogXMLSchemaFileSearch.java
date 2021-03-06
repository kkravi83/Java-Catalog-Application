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
import com.kcatalog.xmlschema.KCatalogXMLSchemaFileReader;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogCommonDto;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KCatalogXMLSchemaFileSearch extends KCatalogXMLSchemaBase{
	public KCatalogXMLSchemaFileSearch() {
		
	}	
	private static int noOf = 1;
	public ArrayList searchFile(String filePath,
									Mp3FileDto searchMp3FileDto,
									String operationType,
									boolean exactMatch,
									int limitStart,
									int limitEnd){
		//System.out.println("noOf " + noOf++);
		//tempNo = 1;
		KCatalogXMLSchemaFileReader reader =
						new KCatalogXMLSchemaFileReader(filePath);
		ArrayList unfilteredList = reader.readMp3DtoListFromXML();
		//System.out.println("unfilteredList " + unfilteredList.size());
		return getFilteredList(unfilteredList,searchMp3FileDto,operationType,
										exactMatch,limitStart,limitEnd);
	}
	
	public ArrayList getFilteredList(  ArrayList unfilteredList,
										Mp3FileDto searchMp3FileDto,
										String operationType,
										boolean exactMatch,
										int limitStart,
										int limitEnd){
		int rangeCheck = 1;
		
		ArrayList filteredList = new ArrayList();
		//System.out.println("unfilteredList " + unfilteredList.size());
		//System.out.println("**********************");
		for(int i=0;i<unfilteredList.size();i++){
			Mp3FileDto mp3FileDto = (Mp3FileDto) unfilteredList.get(i);
		 //	System.out.println("gte filer b4 " + " fn "+mp3FileDto.getFileName() 
		   	//	 				+ " c " + mp3FileDto.getComment());
			boolean match = checkMp3FileDtoMatch(mp3FileDto,
				 								    searchMp3FileDto,
													operationType,
													exactMatch);
		    //System.out.println("getFilteredList "  + match);
		   if(match && rangeCheck >= limitStart && rangeCheck <= limitEnd){
		   	filteredList.add(mp3FileDto);
		  		/*System.out.println("gte filer " + " fn "+mp3FileDto.getFileName() 
		   						+ " c " + mp3FileDto.getComment()
		   						+" search string " + searchMp3FileDto.getFileName());
		   		*/		   	
		   	rangeCheck ++;		   	
		   }
		   
		   if(rangeCheck == limitEnd){
		   	break;
		   }
		}
		/*if(filteredList.size() != 0 ){
			//System.out.println("matching records "+tempNo++);
		}else{
			System.out.println("filteredList.size() "+filteredList.size()
			+ searchMp3FileDto.getFileName());
		}*/
		//System.out.println("----------------------------");
	
		return filteredList;
	}
	
	private boolean checkMp3FileDtoMatch(Mp3FileDto mp3FileDto,
											Mp3FileDto searchMp3FileDto,
											String operationType,
											boolean exactMatch){
		
		if(KCatalogConstants.SEARCH_OPERATION_TYPE_AND.equals(operationType)){
		/*	System.out.println("checkMp3FileDtoMatch " +searchMp3FileDto.getFileLocation()+
					searchMp3FileDto.getComment());*/
			return checkMatchUsingAnd(mp3FileDto,
										searchMp3FileDto,
											exactMatch);
		}
		if(KCatalogConstants.SEARCH_OPERATION_TYPE_OR.equals(operationType)){
			return checkMatchUsingOr(mp3FileDto,
										searchMp3FileDto,
											exactMatch);
		}
		if(KCatalogConstants.SEARCH_OPERATION_TYPE_FILE_LOC_DEL_IF_DIFF_DATA.equals(
										operationType)){
			return checkFileLocMatchUsingAnd(mp3FileDto,
											searchMp3FileDto
												);
		}
		if(KCatalogConstants.SEARCH_OPERATION_TYPE_RECURSIVE_LOC.equals(
										operationType)){
			return checkRecursiveFileMatch(mp3FileDto,
											searchMp3FileDto,
											exactMatch
												);
		}
		return false;
				
	}
	
	private boolean checkRecursiveFileMatch(Mp3FileDto mp3FileDto,
										Mp3FileDto searchMp3FileDto,
										boolean exactMatch){
		String loc1 = mp3FileDto.getFileLocation().trim();
		String  loc2 = searchMp3FileDto.getFileLocation().trim();
		loc1 = KCatalogCommonUtility.adjustForSeparator(loc1,loc2);
		loc2 = KCatalogCommonUtility.adjustForSeparator(loc2,loc1);
		if(!loc1.startsWith(loc2)){
			//System.out.println("loc1 " + loc1 + "loc2 " +loc2);
			return false;
		}
		searchMp3FileDto.setFileLocation("");
		boolean stat =  checkMatchUsingOr(mp3FileDto,
										searchMp3FileDto,
											exactMatch);
		//System.out.println("stat = "  +stat + " " +exactMatch);
		searchMp3FileDto.setFileLocation(loc2);
		return stat;
	}
	
	private boolean checkFileLocMatchUsingAnd(Mp3FileDto mp3FileDto,
										Mp3FileDto searchMp3FileDto){
		boolean result = false;
		String loc1 = mp3FileDto.getFileLocation();
		String loc2 = 	searchMp3FileDto.getFileLocation();
		String name1 = mp3FileDto.getFileName();
		String name2  = 	searchMp3FileDto.getFileName();
		loc1 = (loc1 == null)?"":loc1.trim();
		loc2 = (loc2 == null)?"":loc2.trim();
		name1 = (name1 == null)?"":name1.trim();		
		name2 = (name2 == null)?"":name2.trim();
		if( "".equals(loc1) ||
				 "".equals(loc2) ||
				 		 "".equals(name1) ||
				 		 	 "".equals(name2)){
			return false;
		}
		result = loc1.equalsIgnoreCase(loc2) && name1.equalsIgnoreCase(name2);
																							
		return result;
	}
	
	public boolean checkMatchForUpdate(Mp3FileDto mp3FileDto,
										Mp3FileDto searchMp3FileDto){
		boolean result = false;
	
		result = isExactMatchForUpdate(mp3FileDto.getFileLocation(),
									searchMp3FileDto.getFileLocation()) &&
				 isExactMatchForUpdate(mp3FileDto.getFileName(),
									searchMp3FileDto.getFileName()) &&
				 isExactMatchForUpdate(mp3FileDto.getAlbum(),
									searchMp3FileDto.getAlbum()) &&
				 isExactMatchForUpdate(mp3FileDto.getArtist(),
									searchMp3FileDto.getArtist()) &&
			     isExactMatchForUpdate(mp3FileDto.getGenre(),
									searchMp3FileDto.getGenre() )&&					
				 isExactMatchForUpdate(mp3FileDto.getTitle(),
									searchMp3FileDto.getTitle()) &&
				 isExactMatchForUpdate(mp3FileDto.getComment(),
									searchMp3FileDto.getComment()) ;
				 isExactMatchForUpdate(mp3FileDto.getYear(),
										searchMp3FileDto.getYear()) ;																				
		return result;
	}
	
	

	
	
	public ArrayList searchFile(String filePath,
									Mp3FileDto searchMp3FileDto,
									String operationType,
									boolean exactMatch)
									{
		return searchFile(filePath,searchMp3FileDto,operationType,exactMatch,1,9999);		
	}
	



}
	
	