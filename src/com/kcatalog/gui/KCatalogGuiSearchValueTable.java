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
package com.kcatalog.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;

public class KCatalogGuiSearchValueTable implements TableModel{
	
	String[][] data= new String[2][8];
	KCatalogGuiSearchValueTable(){
		data[0][0] = "File Name";
		data[0][1] = "File Location";
		data[0][2] = "Artist";
		data[0][3] = "Album";
		data[0][4] = "Genre";
		data[0][5] = "Title";
		data[0][6] = "Year";
		data[0][7] = "Comment";
		
		for(int i=0;i<8;i++){
			data[0][i]="";
		}
	}
	
	public int getRowCount() {
		// TODO: Add your code here
		return 2;
	}

	public int getColumnCount() {
		// TODO: Add your code here
		return 8;
	}

	public String getColumnName(int columnIndex) {
		// TODO: Add your code here
		return data[0][columnIndex];
	}

	public Class getColumnClass(int columnIndex) {
		// TODO: Add your code here
		return new String("").getClass();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO: Add your code here
		if(rowIndex == 0){
			return false;
		}
		return true;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO: Add your code here
		return data[rowIndex][columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO: Add your code here
		if(null == aValue ){
			aValue = (Object) new String("");
		}
		
		data[rowIndex][columnIndex] = aValue.toString();
	}

	public void addTableModelListener(TableModelListener l) {
		// TODO: Add your code here
	}

	public void removeTableModelListener(TableModelListener l) {
		// TODO: Add your code here
	}
}
