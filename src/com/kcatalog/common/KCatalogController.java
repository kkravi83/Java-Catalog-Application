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
import com.kcatalog.fileutils.FileManager;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.applogic.KCatalogUpdateMp3InfoDto;
import com.kcatalog.common.KCatalogCommonDto;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.xmlschema.KCatalogXMLSchemaWriter;
import com.kcatalog.gui.KCatalogGui;
import com.kcatalog.gui.KCatalogGuiDataBaseTab;
import com.kcatalog.gui.KCatalogProgressIndicatorDlg;
import com.kcatalog.gui.KCatalogGuiProgressStopInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import com.kcatalog.common.KCatalogUpdateControllerInterface;

public class KCatalogController implements Runnable, KCatalogUpdateControllerInterface,
									KCatalogGuiProgressStopInterface{
	
	
	private String statusMessage = "";
	private KCatalogDto kcatalogDto = null;
	private KCatalogGui mainFrame = null;
	private KCatalogGuiDataBaseTab dbTabRef = null;
	private Boolean stopFlag = Boolean.valueOf(false);
	KCatalogXMLSchemaWriter writer = 
									new KCatalogXMLSchemaWriter();
	public void setStopFlag(boolean stopStatus){
		
		synchronized(stopFlag){
			this.stopFlag = Boolean.valueOf(stopStatus);
		}
	}
	
	public void setParameters(KCatalogDto kcatalogDto,
								KCatalogGui mainFrame,						
								 KCatalogGuiDataBaseTab dbTabRef){
		this.kcatalogDto = kcatalogDto;
		this.mainFrame = mainFrame;
		this.dbTabRef = dbTabRef;
		
	}
	
	public String getCurrentFileProcessed(){
		synchronized(statusMessage){
			return statusMessage;
		}
	}
	
	public boolean isCompleted(){

		return true;
	}

	public void run(){
		updateFileInformation();
	}	
		
	private FileManager getFileManager(KCatalogUpdateMp3InfoDto updateDto){
		FileManager fm = null;
		if( null == updateDto.getSelectedFileLocList()){
			 fm = new FileManager(updateDto.getDirectoryPath().trim(),
							updateDto.getComment().trim(),
							updateDto.getIsRecursive(),
							this);
		}else{
			fm = new FileManager(updateDto.getSelectedFileLocList(),
							updateDto.getComment().trim(),
							updateDto.getIsRecursive(),
							this);
		}
		return fm;
	}
															
	public  void updateFileInformation(){
		long t1 = System.currentTimeMillis();
		KCatalogUpdateMp3InfoDto updateDto = 
		 						(KCatalogUpdateMp3InfoDto)kcatalogDto;
		KCatalogProgressIndicatorDlg dlg = new KCatalogProgressIndicatorDlg(mainFrame,
																	"KCatalog","Please wait",																	
																300,150,this,true);
		String message = "  Writing information about file : ";			
		KCatalogXMLSchemaWriter writer = 
									new KCatalogXMLSchemaWriter();
		prepareFrame(); 
		dlg.startDialog();
		dlg.startProgress();
		FileManager fm = getFileManager(updateDto);		
	
		Thread readThread = new Thread(fm);
		readThread.start();
		Mp3FileDto mp3FileDto = null;
		while( !fm.isCompleted() ||
					(fm.getMp3DtoListSize() != 0)){
			
			if(fm.getMp3DtoListSize() == 0){
				try{
				//	System.out.println("waiting for read");
					synchronized(this){
						this.wait();
					}
				//	System.out.println("waiting for read done");
				}catch(Exception e){
				//	System.out.println("exception ");
					throw new KCatalogException("Fatal Error Occured",e);
				}
			}	
			mp3FileDto = fm.getCurrentMp3FileDto();												
			if(null == mp3FileDto){
				break;
			}
			dlg.setString(message,  mp3FileDto.getFileName());
			writer.writeMp3DtoToXMLSchema(mp3FileDto);				
			if(getStopFlagStatus()){				
				fm.setStopFlag(true);
				try{				
					synchronized(readThread){
						readThread.notifyAll();
					}
				//	System.out.println("joining read");
					readThread.join();
				//	System.out.println("joining read done");
				}catch(Exception e){
					throw new KCatalogException("Fatal Error Occured",e);
				}				
				dlg.stopProgress();
				cleanupFrame();
				return;
			}		
		}	
		dlg.stopProgress();
		cleanupFrame();					
	}
	
	private void prepareFrame(){
	}
	
	private void cleanupFrame(){
		KCatalogStatusManager.showMessages(mainFrame,"Database Updated");
	}
	
	public boolean getStopFlagStatus(){
		synchronized(stopFlag){
			return stopFlag.booleanValue();
		}
	}

	public void updateDBAndWriteUIStatus(Mp3FileDto mp3FileDto) {	
	}

	public void onClickStopButton() {		
		setStopFlag(true);
	}
	

	
	class ReadWriteMp3Info implements Runnable{
		private String dirPath = "";
		private String comment = "";
		
		ReadWriteMp3Info(String dirPath,String comment){
			this.dirPath = dirPath;
			this.comment = comment;
		}
		public void run(){
		//	System.out.println("Total size = " + 
		//		FileManager.getAllMp3InFolder(dirPath,comment);		
		}
		
	}
	
	
}
