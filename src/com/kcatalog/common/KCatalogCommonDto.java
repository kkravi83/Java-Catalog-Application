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
package com.kcatalog.common;
import com.kcatalog.common.KCatalogDto;
import java.util.ArrayList;

public class KCatalogCommonDto extends KCatalogDto{
	
	ArrayList mp3DtoList = null;
	boolean sameFileWithDifferentInfoExist = false;

	public void setMp3DtoList(ArrayList mp3DtoList) {
		this.mp3DtoList = mp3DtoList; 
	}

	public ArrayList getMp3DtoList() {
		return (this.mp3DtoList); 
	}
	
		
	public void setSameFileWithDifferentInfoExist(boolean sameFileWithDifferentInfoExist) {
		this.sameFileWithDifferentInfoExist = sameFileWithDifferentInfoExist; 
	}

	public boolean getSameFileWithDifferentInfoExist() {
		return (this.sameFileWithDifferentInfoExist); 
	}
	
}


	
	