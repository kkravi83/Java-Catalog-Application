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

import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import java.awt.Component;

import com.kcatalog.gui.KCatalogProgressIndicatorDlg;

public class KCatalogStatusManager {
	
	public static String STATUS_FATAL = "7";
	public static String STATUS_ERROR = "6";
	public static String STATUS_WARNING = "5";	
	public static String STATUS_INFORMATION = "4";
	
	private static HashMap statusIdMap = new HashMap();
	private static Component parent = null;
	
	public static void setParentForMessage(Component parentWin){
		parent = parentWin;
	}
	
	public static void addToMessageMap(String message){
		
		String key = message.trim();
		if(!statusIdMap.containsKey(key)){
			statusIdMap.put(key,STATUS_INFORMATION);
		}
	}
	
	public static void addToErrorMap(String message,	
									String severity){
		
		String key = message.trim();
		if(!statusIdMap.containsKey(key)){
			statusIdMap.put(key,severity);
		}
	}
	
	public static HashMap getStatusMap(){
		return statusIdMap;
	}
	
	public static boolean isEmpty(){
		return statusIdMap.isEmpty();
	}
	
	public static void clear(){
		statusIdMap.clear();
	}
	
	public static void showMessages(Component overrideParent){
		KCatalogProgressIndicatorDlg.getCurrentRef().stopProgress();
		if(overrideParent != null ){
			parent = overrideParent;
		}
		
		Iterator it = statusIdMap.keySet().iterator();
		String title = "KCatalog";
		while(it.hasNext()){
			String key = (String)it.next();
			String severity = (String)statusIdMap.get(key);
			if(STATUS_INFORMATION.equals(severity)){
				JOptionPane.showMessageDialog(parent,
									 key,title,JOptionPane.INFORMATION_MESSAGE);												
			}
			if(STATUS_FATAL.equals(severity)){
				JOptionPane.showMessageDialog(parent,
							"Fatal Error: " + key,
							title,
							JOptionPane.ERROR_MESSAGE
							);
			}
			if(STATUS_ERROR.equals(severity)){
				JOptionPane.showMessageDialog(parent,
							key,
							title,
							JOptionPane.ERROR_MESSAGE
							);
			}
			if(STATUS_WARNING.equals(severity)){
				JOptionPane.showMessageDialog(parent,
							key,
							title,
							JOptionPane.WARNING_MESSAGE
							);
			}
		}			
	}
	
	
	public static void showMessages(Component parent,String message){
		KCatalogProgressIndicatorDlg.getCurrentRef().stopProgress();
		JOptionPane.showMessageDialog(parent,message,"KCatalog",JOptionPane.INFORMATION_MESSAGE);
	}
}


