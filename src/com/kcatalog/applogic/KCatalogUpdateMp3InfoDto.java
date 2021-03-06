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

package com.kcatalog.applogic;

import com.kcatalog.common.*;
import java.util.*;

public class KCatalogUpdateMp3InfoDto extends KCatalogDto{

	private String directoryPath = "";
	private String comment = "";	
	private boolean isRecursive = false;
	private ArrayList selectedFileLocList = null;
		
	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath; 
	}

	public void setComment(String comment) {
		this.comment = comment; 
	}

	public String getDirectoryPath() {
		return directoryPath; 
	}

	public String getComment() {
		return comment; 
	}
		
	public void setIsRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive; 
	}

	public void setSelectedFileLocList(ArrayList selectedFileLocList) {
		this.selectedFileLocList = selectedFileLocList; 
	}

	public boolean getIsRecursive() {
		return isRecursive; 
	}

	public ArrayList getSelectedFileLocList() {
		return selectedFileLocList; 
	}
}
