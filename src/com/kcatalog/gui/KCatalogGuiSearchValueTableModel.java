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

public class KCatalogGuiSearchValueTableModel extends AbstractTableModel{

	Object[][] data= null;
	Object[] columnName = null;
	int rowCount = 0;
	int columnCount = 0;
	boolean isSearchResultTable = false;
	
	public KCatalogGuiSearchValueTableModel(boolean isSearchResultTable){
		this.isSearchResultTable = isSearchResultTable;
		if(!isSearchResultTable){
			setDataForSearchOptions();
		}else{
			setDataForSearchResult();
		}
	

	
	}
	
	private void setDataForSearchResult(){
	
		rowCount = Integer.valueOf(KCatalogConfigOptions.getOptionValue(
						KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW).trim()).intValue();
		columnCount = 9;			
		data= new Object[rowCount][columnCount];
		columnName = new Object[columnCount];
		columnName[0] = "SL";
		columnName[1] = "File Name";
		columnName[2] = "File Location";
		columnName[3] = "Artist";
		columnName[4] = "Album";
		columnName[5] = "Genre";
		columnName[6] = "Title";
		columnName[7] = "Year";
		columnName[8] = "Comment";
		for(int i=0;i<rowCount;i++){
			for(int j=1;j<columnCount;j++){
				data[i][0] = new JCheckBox();
				data[i][j] = "";
			}
		}

	}
	
	private void setDataForSearchOptions(){	
		rowCount = 1;
		columnCount = 8;	
		data= new Object[rowCount][columnCount];
		columnName = new Object[columnCount];			
		columnName[0] = "File Name";
		columnName[1] = "File Location";
		columnName[2] = "Artist";
		columnName[3] = "Album";
		columnName[4] = "Genre";
		columnName[5] = "Title";
		columnName[6] = "Year";
		columnName[7] = "Comment";
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
		return columnName[columnIndex].toString();
	}

	public Class getColumnClass(int columnIndex) {
		// TODO: Add your code here
		if("SL".equals(getColumnName(columnIndex))){
		
			return new JCheckBox().getClass();
		}
		return new String().getClass();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO: Add your code here
		if(isSearchResultTable && columnIndex != 0){
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
		Object result = aValue;
		if(null == result ){
			result = (Object) new String("");
		}
		if(isSearchResultTable){
			if(columnIndex == 0){
				boolean prevStat = ((JCheckBox)getValueAt(rowIndex,columnIndex)).isSelected();
				boolean newStat = ((Boolean)aValue).booleanValue();
				if(prevStat == newStat){
					result = new JCheckBox("",false);
				}else{
					result = new JCheckBox("",newStat);
				}
			}
		}else{
			result = result.toString();
		}
		

		data[rowIndex][columnIndex] = result;
	}
}

