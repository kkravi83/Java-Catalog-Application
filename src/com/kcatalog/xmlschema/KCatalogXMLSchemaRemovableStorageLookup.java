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
import com.kcatalog.common.KCatalogCommonUtility;
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
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;

public class KCatalogXMLSchemaRemovableStorageLookup  extends KCatalogXMLSchemaMp3AttributeLookUp{
	private String fileName = "commentLocLookup.xml";
	public static final String LOOKUP_TYPE_LOC = "fileLocation";
	public static final String MAP_ATTR_VAL = "comment";
		
	public KCatalogXMLSchemaRemovableStorageLookup(){
		this.attributeType = LOOKUP_TYPE_LOC;	
		this.attributeMapVal = MAP_ATTR_VAL;
	}
	
	public String getFilePath(){				
		String databaseDir = 
		 KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_DATABASE_LOCATION);
		String filePath = databaseDir + KCatalogConfigOptions.getSeparator()
										+ fileName;
		return filePath;		
	}
	
	public ArrayList getCommentsForAllPermanentLocation(){
		ArrayList locList = getAllAttributeValues();
		ArrayList permLoc = new ArrayList();
		//System.out.println("getCommentsForAllPermanentLocation locList" + locList.size());
		Iterator it = locList.iterator();
		while(it.hasNext()){
			String loc = (String)it.next();
			if(KCatalogCommonUtility.isPermanentLocation(loc.trim(),true)){
				//System.out.println("getCommentsForAllPermanentLocation isPermanentLocation" + loc);
				permLoc.add(loc);
			}
		}
		Iterator locs = permLoc.iterator();
		ArrayList commentList = new ArrayList();
		while(locs.hasNext()){
			String loc = (String)locs.next();
			ArrayList lookupLocationFileList = lookupLocationFile(loc.trim());
			//System.out.println("getCommentsForAllPermanentLocation lookupLocationFileList" + lookupLocationFileList.size());
			if(lookupLocationFileList.size() > 0){
				commentList.add(lookupLocationFileList.get(0));
			}
		}
		return commentList;
	}
	
	
	
}

