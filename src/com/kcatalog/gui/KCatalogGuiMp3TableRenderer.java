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
import javax.swing.*;
import javax.swing.table.TableCellRenderer;



public class KCatalogGuiMp3TableRenderer implements TableCellRenderer{

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cellObject = null;
		
		
		if( "SL".equals(table.getColumnName(column))){
		
			JCheckBox checkBox = (JCheckBox)value;
			checkBox.setMaximumSize(new Dimension(10,10));
			cellObject = checkBox;
		}else{
			if(value == null){
				value = "";
			} 			
			String str = value.toString();
			JTextArea field = new JTextArea(str);
		//	field.setAutoscrolls(true);
		//	field.setMinimumSize(new Dimension(100,100));
			field.setWrapStyleWord(false);
			field.setLineWrap(false);
			cellObject = field;
		}
		return cellObject;												
	}
}
