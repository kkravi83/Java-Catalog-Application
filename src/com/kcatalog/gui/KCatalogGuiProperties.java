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
import javax.swing.plaf.basic.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import com.kcatalog.gui.KCatalogGuiBrowseTableModel;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.gui.KCatalogGuiMp3TableRenderer;
import com.kcatalog.gui.KCatalogGuiSearchTabAction;
import com.kcatalog.gui.KCatalogGuiTreeModel;

import com.kcatalog.gui.KCatalogGuiBrowseTabAction;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.text.DateFormat;

import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogException;

import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.fileutils.FileManager;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.ArrayList;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.border.*;
import de.vdheide.mp3.ID3;
import de.vdheide.mp3.MP3File;

public class KCatalogGuiProperties {
	
	public static void showAdditionalInfoForMp3Files(
							Component mainFrame,ArrayList mp3FileList){
		KCatalogGuiProperties pr = new KCatalogGuiProperties();
		if(mp3FileList.size() == 1){
			pr.showInfoAboutFile(mainFrame,(Mp3FileDto)mp3FileList.get(0));
		}else{
			if(mp3FileList.size() > 1 ){
				pr.showInfoForGroupFile(mainFrame,mp3FileList);
			}
		}	
	}
	
	public static void showAdditionalInfoForFolder(
						Component mainFrame,String folder,ArrayList childList){
		KCatalogGuiProperties pr = new KCatalogGuiProperties();
		if(folder.trim().endsWith(KCatalogGuiTreeModel.label)){
			pr.showInfoForCommentFolder(mainFrame,folder);
		}		
	}
	
	private void showInfoForCommentFolder(Component mainFrame,String folder){
		int index = folder.lastIndexOf(KCatalogConfigOptions.getSeparator());
		String message = "Label for removable storage media " +
							folder.substring(0,index);		
		KCatalogStatusManager.showMessages(mainFrame,message);	
	}
	
	private void showInfoForGroupFile(Component mainFrame,ArrayList list){
		Mp3FileDto dto = (Mp3FileDto) list.get(0);
		String message = "Location = " + dto.getFileLocation() + "\n"
					+ list.size() + " Files Selected ";		
		KCatalogStatusManager.showMessages(mainFrame,message);
	}
	
	private void showInfoAboutFile(Component mainFrame,
								Mp3FileDto dto){
		String fileLocation = 
					dto.getFileLocation() + KCatalogConfigOptions.getSeparator()
							+ dto.getFileName();
		MP3File fileInfo = null;
		try{
			fileInfo = new MP3File(fileLocation);
		}catch(Exception e){
			throw new KCatalogException(
				"Sorry,Additional Information could not be obtained");
		}
		String message = getMessage(fileInfo);
		
		//JOptionPane.showMessageDialog(mainFrame,message,"KCatalog...",JOptionPane.INFORMATION_MESSAGE);
		KCatalogStatusManager.showMessages(mainFrame,message);
	}
	
	private String getMessage(MP3File file){
		String str = "";
		try{
			 str = 
			 	formatString("Artist Webpage",file.getArtistWebpage().getTextContent())
				+formatString("Band",file.getBand().getTextContent())
				+formatString("Comments",file.getComments().getTextContent())
				+formatString("Commercial Information",file.getCommercialInformation().getTextContent())
				+formatString("Composer ",file.getComposer().getTextContent())			
				+formatString("Copyright Text",file.getCopyrightText().getTextContent())
				+formatString("Date ",file.getDate().getTextContent())
				+formatString("File Size",file.getFilesize().getTextContent())
				+formatString("Length",String.valueOf(file.getLength()) )
				+formatString("Time",file.getTime().getTextContent() ) 
				+formatString("Track",file.getTrack().getTextContent());
				
		   
		}catch(Exception e){
		}	
		str = str +formatString("Modification Date",String.valueOf(file.lastModified()) );		
		return str;	
	}
	
	private String formatString(String name, String value){
		String line = "";		
		if(value == null || "".equals(value)){
			return line;
		}
		if("Length".equals(name)){
			try{
				long val = Long.valueOf(value).longValue();
				long min = (long)(val / 60);
				long sec =   val - (min * 60);
				value = String.valueOf(min) + ":" + String.valueOf(sec) + "mins";
			}catch(Exception e){
			
			}
		}
		if("Modification Date".equals(name)){
			
			DateFormat df = DateFormat.getDateInstance();
			value = df.format(new Date(Long.valueOf(value).longValue()));
		}
		
		return name + " : " + value + "\n";
	}
}


