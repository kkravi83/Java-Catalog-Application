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
import com.kcatalog.xmlschema.KCatalogXMLSchemaSynchronizeData;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import java.awt.*;

public class KCatalogSynchronizeController implements Runnable{
	private String statusMessage = "";
	private String comment = "";
	private KCatalogGui mainFrame = null;
	private KCatalogGuiDataBaseTab dbTabRef = null;
	
	KCatalogXMLSchemaSynchronizeData sync = 
								new KCatalogXMLSchemaSynchronizeData();
	
	public void setStopFlag(boolean stopStatus){				
		sync.setStopFlag(stopStatus);		
	}
	
	public boolean getStopFlag(){				
		return sync.getStopFlag();	
	}
	
	public void setParameters(String comment,
								KCatalogGui mainFrame,						
								 KCatalogGuiDataBaseTab dbTabRef){
		this.comment = comment;
		this.mainFrame = mainFrame;
		this.dbTabRef = dbTabRef;
	}
	
	public String getCurrentFileProcessed(){
		synchronized(statusMessage){
			return statusMessage + sync.getDeletedFileName();
		}
	}

/*	public void startSynchronize(){
		new Thread(this).start();
		updateStatus();
	}*/
	
	public void updateStatus(){		
		while(!sync.getStopFlag()){

			if(!"".equals(getCurrentFileProcessed())){
				dbTabRef.statusMessage.setText("Deleted Information about Mp3  "+ getCurrentFileProcessed());
			}else{
							dbTabRef.statusMessage.setText("Processing....");
			}
		}
		cleanupFrame();
	}
	
	public void run(){	
		new Thread(new synchronizeMp3Info()).start();
		updateStatus();
	}
	
	private void prepareFrame(){
		dbTabRef.enableAllComponents(false);
		
		
		dbTabRef.synchronizeDBButton.setText("Stop");
		dbTabRef.synchronizeDBButton.setName("stopSynchronizeDBButton");
		dbTabRef.synchronizeDBButton.setMargin(new Insets(1,10,1,10));		
		dbTabRef.synchronizeDBButton.setEnabled(true);
		dbTabRef.synchronizeDBButton.updateUI();
		JLabel statusMessage = dbTabRef.statusMessage;
		statusMessage.setText("Processing please wait...");
	}
	
	private void cleanupFrame(){
		setStopFlag(true);
		JLabel statusMessage = dbTabRef.statusMessage;
		statusMessage.setText("");		
		dbTabRef.enableAllComponents(true);		
		dbTabRef.synchronizeDBButton.setText("Synchronize");
		dbTabRef.synchronizeDBButton.setName("synchronizeDBButton");
		dbTabRef.synchronizeDBButton.setMargin(new Insets(1,1,1,1));
	}
	
	class synchronizeMp3Info implements Runnable{
		
		public void run(){
			prepareFrame();
			int n = sync.removeInvalidEntries(comment);		
			/*JOptionPane.showMessageDialog(mainFrame,"Deleted Information about "+
							n+ " Mp3's from DB");*/
			KCatalogStatusManager.showMessages(mainFrame,"Deleted Information about "+
							n+ " Mp3's from DB");
		}
	}
}
