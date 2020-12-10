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
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;

public class KCatalogGuiBrowseTableModel extends DefaultTableModel{
	
	Object[][] data= null;
	Object[] columnName = null;
	int rowCount = 0;
	int columnCount = 0;
	public static int INDEX_NAME = 0;
	public static int INDEX_LOCATION = 1;
	public static int INDEX_ARTIST = 2;
	public static int INDEX_ALBUM = 3;
	public static int INDEX_GENRE = 4;	
	public static int INDEX_TITLE = 5;
	public static int INDEX_DURATION = 6;
	public static int INDEX_YEAR = 7;	
	public static int INDEX_COMMENT = 8;
	
	private boolean ascendingData = false;

	public boolean isAscendingData(){
		return ascendingData;
	}
	
	public void setAscendingData(boolean ascendingData){
		this.ascendingData = ascendingData;
	}
	
	public KCatalogGuiBrowseTableModel(){
		rowCount = 0;
		columnCount = 9;
		setDataForTable();
	}	
		
	public KCatalogGuiBrowseTableModel(Object[][] data){
		this.data = data;
		rowCount = data.length;
		columnCount = 9;
	/*	if(data.length != 0 ){
			columnCount = data[0].length;
		}*/
		setColumnNames();
	}
	
	private void setColumnNames(){		
		columnName = new Object[columnCount];			
		columnName[this.INDEX_NAME] = "File Name";
		columnName[this.INDEX_LOCATION] = "File Location";
		columnName[this.INDEX_ARTIST] = "Artist";
		columnName[this.INDEX_ALBUM] = "Album";
		columnName[this.INDEX_GENRE] = "Genre";
		columnName[this.INDEX_TITLE] = "Title";
		columnName[this.INDEX_DURATION] = "Duration";	
		columnName[this.INDEX_YEAR] = "Year";
		columnName[this.INDEX_COMMENT] = "Comment";
	}
	
	private void setDataForTable(){	
			
		data= new Object[rowCount][columnCount];
		setColumnNames();
		for(int i=0;i<rowCount;i++){
			for(int j=0;j<columnCount;j++){
				data[i][j] = "";
			}
		}

	}

	public int getRowCount() {
		// TODO: Add your code here
		return rowCount;
	}

	public int getColumnCount() {
		// TODO: Add your code here
		return columnCount;
	}
 
	public String getColumnName(int columnIndex) {
		// TODO: Add your code here
		if(columnIndex >= this.columnCount){
			return "";
		}
		return columnName[columnIndex].toString();
	}

	public Class getColumnClass(int columnIndex) {
		
		return new String().getClass();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
	
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO: Add your code here
		if(rowIndex >= this.rowCount || columnIndex >= this.columnCount){
			return "";
		}
		return data[rowIndex][columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO: Add your code here
		if(rowIndex >= this.rowCount || columnIndex >= this.columnCount){
			return ;
		}
		Object result = aValue;
		if(null == result ){
			result = (Object) new String("");
		}		
		data[rowIndex][columnIndex] = result;
	}
}
