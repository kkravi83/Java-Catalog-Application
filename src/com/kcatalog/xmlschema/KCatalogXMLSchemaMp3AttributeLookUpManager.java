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
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUp;
import com.kcatalog.xmlschema.KCatalogXMLSchemaRemovableStorageLookup;
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
import java.util.Iterator;
import java.util.StringTokenizer;



public class KCatalogXMLSchemaMp3AttributeLookUpManager extends KCatalogXMLSchemaBase {
	private static KCatalogXMLSchemaMp3AttributeLookUpManager currentInstance = null;
	
	private HashMap lookUpMap = new HashMap();
	private KCatalogXMLSchemaRemovableStorageLookup removableStorageLookup = null;				
					
	private KCatalogXMLSchemaMp3AttributeLookUpManager() {
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM));
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_GENRE,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_GENRE));
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_TITLE,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_TITLE));
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ARTIST,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ARTIST));
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM));
		lookUpMap.put(KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_COMMENT,
							new KCatalogXMLSchemaMp3AttributeLookUp(
									KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_COMMENT));
		removableStorageLookup = new KCatalogXMLSchemaRemovableStorageLookup();
		Iterator it = lookUpMap.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			KCatalogXMLSchemaMp3AttributeLookUp lookup = 
				(KCatalogXMLSchemaMp3AttributeLookUp) lookUpMap.get(key);
			if(!lookup.isLookUpFileExists()){
				lookup.createLookUpFile();
			}
			
		}
		
	}	
	
	public ArrayList getXMLFileLocationIds(String searchAttribute,String attributeValue){
		KCatalogXMLSchemaMp3AttributeLookUp lookup= (KCatalogXMLSchemaMp3AttributeLookUp)
					 									lookUpMap.get(searchAttribute);
		return lookup.lookupLocationFile(attributeValue);
	} 
	
	public ArrayList getAllAttributeValues(String searchAttribute){
		KCatalogXMLSchemaMp3AttributeLookUp lookup= (KCatalogXMLSchemaMp3AttributeLookUp)
					 									lookUpMap.get(searchAttribute);
		return lookup.getAllAttributeValues();
	}
	
	public static KCatalogXMLSchemaMp3AttributeLookUpManager
								getInstance(){
		currentInstance = (null == currentInstance) ? 
							new KCatalogXMLSchemaMp3AttributeLookUpManager():
							 currentInstance;
		return currentInstance;
	}
	
	public void deleteMp3FileDto(Mp3FileDto dto,String locationId){
		KCatalogXMLSchemaMp3AttributeLookUp obj = null;
		if(!isEmpty(dto.getFileLocation())){			
			removableStorageLookup.deleteAttribute(dto.getFileLocation(),dto.getComment());
		}
		if(!isEmpty(dto.getAlbum())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
					 lookUpMap.get(
			 			KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM);
			obj.deleteAttribute(dto.getAlbum(),locationId);
		}
		if(!isEmpty(dto.getArtist())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
					 lookUpMap.get(
			 			KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ARTIST);
			obj.deleteAttribute(dto.getArtist(),locationId);
		}
		
		if(!isEmpty(dto.getGenre())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_GENRE);
			obj.deleteAttribute(dto.getGenre(),locationId);
		}
		
		if(!isEmpty(dto.getComment())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_COMMENT);
			obj.deleteAttribute(dto.getComment(),locationId);
		}
		
		if(!isEmpty(dto.getTitle())){		
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_TITLE);
			obj.deleteAttribute(dto.getTitle(),locationId);
		}
		
	}
	
	public void addMp3FileDto(Mp3FileDto dto,String locationId){
		KCatalogXMLSchemaMp3AttributeLookUp obj = null;
		if(!isEmpty(dto.getFileLocation())){			
			removableStorageLookup.write(dto.getFileLocation(),dto.getComment());
		}
		if(!isEmpty(dto.getAlbum())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
					 lookUpMap.get(
			 			KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM);
			obj.write(dto.getAlbum(),locationId);
		}
		if(!isEmpty(dto.getArtist())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
					 lookUpMap.get(
			 			KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ARTIST);
			obj.write(dto.getArtist(),locationId);
		}
		
		if(!isEmpty(dto.getGenre())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_GENRE);
			obj.write(dto.getGenre(),locationId);
		}
		
		if(!isEmpty(dto.getComment())){
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_COMMENT);
			obj.write(dto.getComment(),locationId);
		}
		
		if(!isEmpty(dto.getTitle())){		
			obj =
				(KCatalogXMLSchemaMp3AttributeLookUp)
				 	lookUpMap.get(
				 		KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_TITLE);
			obj.write(dto.getTitle(),locationId);
		}
		
		
	}
	
	public ArrayList getCommentsForAllPermanentLocation(){
		return removableStorageLookup.getCommentsForAllPermanentLocation();
	}
	
	private boolean isEmpty(String str){
		boolean res = false;
		if( null == str || "".equals(str.trim())
								|| KCatalogConstants.MESSAGE_NA.equals(str.trim())){
			res = true;
		}
		return res;
	}
}
