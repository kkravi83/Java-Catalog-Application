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
import javax.swing.*;
import javax.swing.text.*;

public  class KCatalogGuiNumberField extends JTextField{
		KCatalogGuiNumberField(){
			super();
		}
		
		public Document createDefaultModel(){
			return new NumericDocument();
		}
		
		public class NumericDocument extends PlainDocument{
			public void insertString(int offs,String str,AttributeSet a){
				if(null == str || "".equals(str.trim())){
					return;
				}
				try{
					Integer.valueOf(str.trim());		
					super.insertString(offs,str,a);
				}catch(Exception e){
					return;
				}				
			}
		}
}