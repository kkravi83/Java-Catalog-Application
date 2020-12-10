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
import com.kcatalog.fileutils.Mp3FileDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class KCatalogSchemaMp3FileDto extends
										Mp3FileDto{
	Document doc = null;
	Node parentLocationNode = null;
	Node mp3FileNode = null;
	String xmlFileName = "";
		
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName; 
	}

	public String getXmlFileName() {
		return xmlFileName; 
	}
	
	public void setDoc(Document doc) {
		this.doc = doc; 
		
	}

	public void setParentLocationNode(Node parentLocationNode) {
		this.parentLocationNode = parentLocationNode; 
	}

	public void setMp3FileNode(Node mp3FileNode) {
		this.mp3FileNode = mp3FileNode; 
	}

	public Document getDoc() {
		return this.doc; 
	}

	public Node getParentLocationNode() {
		return parentLocationNode; 
	}

	public Node getMp3FileNode() {
		return mp3FileNode; 
	}
}
