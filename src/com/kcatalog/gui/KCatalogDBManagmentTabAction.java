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

import com.kcatalog.gui.KCatalogGuiSearchValueTableModel;
import com.kcatalog.gui.KCatalogGuiMp3TableRenderer;
import com.kcatalog.gui.KCatalogGuiSearchTab;
import com.kcatalog.gui.KCatalogGui;
import com.kcatalog.gui.KCatalogGuiSearchTab;
import com.kcatalog.gui.KCatalogGuiDataBaseTab;

import com.kcatalog.common.KCatalogDto;
import com.kcatalog.fileutils.FileManager;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.applogic.KCatalogUpdateMp3InfoDto;
import com.kcatalog.common.KCatalogCommonDto;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;
import com.kcatalog.common.KCatalogController;
import com.kcatalog.common.KCatalogDto;
import com.kcatalog.applogic.KCatalogUpdateMp3InfoDto;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogSynchronizeController;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSynchronizeData;
import com.kcatalog.common.KCatalogCommonUtility;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;

public class KCatalogDBManagmentTabAction{
	
	private boolean isExactMatch = false;
	private String operationType = "";
	KCatalogGui mainFrame = null;
	Mp3FileDto searchMp3FileDto = null;
	
	ActionEvent event = null;
	KCatalogGuiDataBaseTab dbTabRef = null;
	static KCatalogXMLSchemaSearch fs = null;
	
	private int maxRecordsPerXML = 0;
	private String xmlFilePrefix = "";
	private ArrayList permanentLocations = new ArrayList();
	private String dbLocation = "";
	private String folderLocation = "";
	private String deleteComment = "";
	private String appPath = "";
	private String associatedFMPath = "";
	private String maxNo = "";
	private String plLocation = "";
	private String dbBkupLocation = "";
	private String bkupFreq = "";
	private static KCatalogController kc = null;
	private static KCatalogSynchronizeController syncControl = null;
	
	KCatalogDBManagmentTabAction(KCatalogGui mainFrame,KCatalogGuiDataBaseTab dbTabRef,	
										ActionEvent event){		
		
		this.event = event;
		this.dbTabRef = dbTabRef;
		this.mainFrame = mainFrame;
	}
	
	public void processAction(){
		
		initializeFields();
		String buttonName = ((JButton)(event.getSource())).getName();
		if("addFolderLocationButton".equals(buttonName)){
			addFolderLocation();
		}
		if("databaseLocationButton".equals(buttonName)){
			addDBLocation();
		}
		if( "playListLocationButton".equals(buttonName)){
			addPlayListLocation();
		}
		if( "addFolderButton".equals(buttonName)){			
			addFolderToDB();
		}	
		if( "stopButton".equals(buttonName)){			
			stopUpdate();
		}		
		if( "saveOptionsButton".equals(buttonName)){			
			saveOptions();
		}	
		if("cancelOptionsButton".equals(buttonName)){
			cancelOptions();
		}
		if( "addPermanentLocationButton".equals(buttonName)){			
			addPermanentLocations();
		}	
		if( "removePermanentLocationButton".equals(buttonName)){			
			removeSelectedLocations();
		}	
		if( "synchronizeDBButton".equals(buttonName)){			
			synchronizeDB();
		}	
		if( "stopSynchronizeDBButton".equals(buttonName)){			
			stopSynchronizeDB();
		}	
			
		if("associatedAppButton".equals(buttonName)){
			selectAssociatedApplication();
		}
		if("associatedFMButton".equals(buttonName)){
			selectAssociatedFM();
		}		
		
	}
		
	
	private void cancelOptions(){
		dbTabRef.setVisible(false);
	}
	
	private void selectAssociatedFM(){
		JFileChooser fc = new JFileChooser();		
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){				
			File f = fc.getSelectedFile();
			dbTabRef.associatedFM.setText(f.getAbsolutePath());
		}		
	}
	
	private void selectAssociatedApplication(){
		JFileChooser fc = new JFileChooser();		
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){				
			File f = fc.getSelectedFile();
			dbTabRef.associatedApp.setText(f.getAbsolutePath());
		}		
	}
	
	private void stopSynchronizeDB(){
		if(syncControl != null){
			syncControl.setStopFlag(true);
		}
	}
	
	private void synchronizeDB(){
		syncControl = new KCatalogSynchronizeController();
		syncControl.setParameters(deleteComment,mainFrame,dbTabRef);
		new Thread(syncControl).start();			
	}
		
	private void removeSelectedLocations(){
		JList permanentLocations = dbTabRef.permanentLocations;
		Object[] selectedLocations = permanentLocations.getSelectedValues();
		if(null == selectedLocations ||
					selectedLocations.length == 0){
			throw new KCatalogException("No locations selected");
		}
		ListModel model = permanentLocations.getModel();				
		ArrayList newList = new ArrayList();
		ArrayList selectedList = new ArrayList();
		for(int i=0;i<selectedLocations.length;i++){
			selectedList.add((String)selectedLocations[i]);
		}
		for(int i =0 ;i<model.getSize();i++){
			String location = (String)model.getElementAt(i);			
			if(!selectedList.contains((location))){
				newList.add(location);
			}
		}
		permanentLocations.setListData(newList.toArray());
	}
	
	private void addPermanentLocations(){
		JList permanentLocations = dbTabRef.permanentLocations;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){	
			ArrayList permanentLocationsList = 	new ArrayList();
			ListModel model = permanentLocations.getModel();
			for(int i =0 ;i<model.getSize();i++){
				String location = (String)model.getElementAt(i);			
				permanentLocationsList.add(location);				
			}							
			permanentLocationsList.add(fc.getSelectedFile().getAbsolutePath().trim());
			Object[] listData = permanentLocationsList.toArray();
			permanentLocations.setListData(listData);			
		}		
	}
	
	private void saveOptions(){
		validateFields();
		saveOptionsToConfig();
	}
	
	private void saveOptionsToConfig(){
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_DATABASE_LOCATION,dbLocation);
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_PLAYLIST_LOCATION,plLocation);
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_MAX_MP3_PER_XML,String.valueOf(maxRecordsPerXML));						
	/*	KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_FILE_NAME_PREFIX,xmlFilePrefix);	*/
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_PERMANENT_LOCATIONS ,permanentLocations);								
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD,appPath);
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER,associatedFMPath);		
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_DBBKUP_LOCATION,dbBkupLocation);					
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_BACKUP_DB_FREQ,bkupFreq);					
		KCatalogConfigOptions.setOptionValue(
						KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW,maxNo);
		//JOptionPane.showMessageDialog(mainFrame,"Saved successfully");
		KCatalogStatusManager.showMessages(mainFrame,"Saved successfully");
	}
	
	private void validateFields(){
		boolean flag = false;
		if(maxRecordsPerXML == 0){
			throw new KCatalogException("Please enter a valid no of records per XML");
		}
		if("".equals(dbLocation.trim())){
			throw new KCatalogException("Please enter a valid Database location");
		}
		/*if("".equals(xmlFilePrefix.trim())){
			throw new KCatalogException("Please enter a valid XML file prefix");
		}*/
		
		if("".equals(appPath)){
			throw new KCatalogException("Please select an application to play Mp3");	
		}
		if("".equals(associatedFMPath)){
			throw new KCatalogException("Please select an application to open folders with");	
		}
		if("".equals(dbBkupLocation)){
			throw new KCatalogException("Please select a location to backup database");				
		}
		if("".equals(bkupFreq)){
			throw new KCatalogException("Please select automatic backup frequency");				
		}
		if("".equals(plLocation)){
				throw new KCatalogException("Please select location to save playlists generated by KCatalog");	
		}
		
		if("".equals(maxNo)){
			throw new KCatalogException("Please enter a value in maximum number field");	
		}
	}
	
	private void stopUpdate(){
		if(kc != null){
			kc.setStopFlag(true);
		}
	}
	
	private void addFolderToDB(){
		String folderPath = dbTabRef.addFolderLocation.getText().trim();		
		String comment = dbTabRef.addFolderComment.getText().trim();
		if("".equals(comment)){
			throw new KCatalogException("Please enter a comment");
		}
		if("".equals(folderPath)){
			throw new KCatalogException("Please select a folder");
		}
		File fl = new File(folderPath);		
		if(!fl.exists() || !fl.isDirectory()){
			throw new KCatalogException("Folder " + folderPath + " doesnot exist");
		}
		KCatalogDto kcDto= getUpdateDto(folderPath,comment);
		kc = new KCatalogController();
		kc.setParameters(kcDto,mainFrame,dbTabRef);
	
		new Thread(kc).start();
	}
	

	
	private KCatalogDto getUpdateDto(String folderPath,String comment){
		KCatalogUpdateMp3InfoDto kcDto = new KCatalogUpdateMp3InfoDto();
		kcDto.setDirectoryPath(folderPath);
		kcDto.setComment(comment);		
		return kcDto;
	}
	
	private void addFolderLocation(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){
			dbTabRef.addFolderLocation.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
		
	private void addPlayListLocation(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){
			dbTabRef.playListLocation.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void addDBLocation(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){
			dbTabRef.databaseLocation.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
			
	private void initializeFields(){
		
		xmlFilePrefix = dbTabRef.xmlFilePrefix.getText().trim();	 
	 	getPermanentLocations();
		dbLocation = KCatalogCommonUtility.removeSeparatorAtEnd(
						dbTabRef.databaseLocation.getText().trim());
		plLocation = KCatalogCommonUtility.removeSeparatorAtEnd(
						dbTabRef.playListLocation.getText().trim());
		folderLocation = KCatalogCommonUtility.removeSeparatorAtEnd(
					dbTabRef.addFolderLocation.getText().trim());
		deleteComment = dbTabRef.deleteComment.getText().trim();
		appPath = KCatalogCommonUtility.removeSeparatorAtEnd(
						dbTabRef.associatedApp.getText().trim());
		associatedFMPath = KCatalogCommonUtility.removeSeparatorAtEnd(
					dbTabRef.associatedFM.getText().trim());
		dbBkupLocation = KCatalogCommonUtility.removeSeparatorAtEnd(
						dbTabRef.databaseBkupLocation.getText().trim());
		bkupFreq = dbTabRef.bkupFreq.getText().trim();
		maxNo = dbTabRef.maxMp3PerView.getText().trim();
		try{
			maxRecordsPerXML = Integer.valueOf(
						dbTabRef.maxMp3PerFile.getText().trim()).intValue();
		}catch(Exception e){
			maxRecordsPerXML = 0;
		}
	}
	
	private void getPermanentLocations(){
		ListModel list = dbTabRef.permanentLocations.getModel();
		int size = list.getSize();
		for(int i =0;i<size;i++){															
			permanentLocations.add(list.getElementAt(i).toString());
		}
	}
		
		
}





