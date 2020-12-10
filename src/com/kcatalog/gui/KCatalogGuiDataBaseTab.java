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
import com.kcatalog.gui.KCatalogGuiSearchTabAction;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.event.ActionEvent;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.gui.KCatalogGuiNumberField;
import java.util.ArrayList;
import javax.swing.text.*;


public class KCatalogGuiDataBaseTab  extends JDialog{
	
	
	
	Box verticalBox = Box.createVerticalBox();
	
	Box verticalBox1 = Box.createVerticalBox();
	Box verticalBox2 = Box.createVerticalBox();
	Box verticalBox3 = Box.createVerticalBox();
	
	KCatalogGuiDataBaseTab currentRef = this;
	
	int width = 0;
	int height = 0;
	KCatalogGui mainFrame = null;
	public JLabel maxMp3PerFileTxt = new JLabel("Maximum records per XML file ");
	public JTextField maxMp3PerFile =  new KCatalogGuiNumberField();
		
	public JLabel bkupFreqTxt1 = new JLabel("Back up DB after ");
	public JTextField bkupFreq = new KCatalogGuiNumberField();
	public JLabel bkupFreqTxt2= new JLabel("Startups ");
	
	public JLabel xmlFilePrefixTxt = new JLabel("Prefix for XML Files in DB ");
	public JTextField xmlFilePrefix = new JTextField();
	
	public JLabel permanentLocationsTxt = new JLabel("Removal Storage Medium   ");
	public JList permanentLocations = new JList();
	
	public JButton addPermanentLocationButton = new JButton("Add");
	public JButton removePermanentLocationButton = new JButton("Remove");
	
	
	public JLabel databaseLocationTxt = new JLabel("KCatalog Database location		 ");
	public JTextField databaseLocation = new JTextField();
	public JButton databaseLocationButton = new JButton("Choose");
	
	public JLabel databaseBkupLocationTxt = new JLabel("KCatalog Database Backup Location");
	public JTextField databaseBkupLocation = new JTextField();
	public JButton databaseBkupLocationButton = new JButton("Choose");
	
	public JButton saveOptionsButton = new JButton("Save");
	public JButton cancelOptionsButton = new JButton("Close");
	
	public JLabel playListLocationTxt = new JLabel("Location to create playlists");
	public JTextField playListLocation = new JTextField();
	public JButton playListLocationButton = new JButton("Choose");
	
	
	
	public JLabel addFolderLocationTxt = new JLabel("Add Mp3 from folder to database");
	public JTextField addFolderLocation = new JTextField();
	public JButton addFolderLocationButton = new JButton("Choose");
	
	public JLabel synchronizeDBTxt = new JLabel("Remove invalid entries from database");
	
	public JButton synchronizeDBButton = new JButton("Synchronize");
	public JButton addFolderButton = new JButton("Add");
	public JButton stopUpdatingButton = new JButton("Stop");
	
	public JLabel addFolderCommentTxt = new JLabel("Comment to identify Mp3's added");
	public JTextField addFolderComment = new JTextField();	
	
	public JLabel statusMessage = new JLabel("");
	
	public JLabel deleteCommentTxt = new JLabel("Delete records with comment");
	public JTextField deleteComment = new JTextField();	
	
	
	public JLabel associatedAppTxt = new JLabel("Application to play Mp3's");
	public JTextField associatedApp = new JTextField();
	public JButton associatedAppButton = new JButton("Choose");
	
	
	public JLabel associatedFMTxt = new JLabel("Application to open folders'");
	public JTextField associatedFM = new JTextField();
	public JButton associatedFMButton = new JButton("Choose");
	
	public JLabel maxMp3PerViewTxt = new JLabel("Search result per page");
	public JTextField maxMp3PerView = new KCatalogGuiNumberField();
	
	JPanel locationPanel = new JPanel();
	JPanel appPanel = new JPanel();
	JPanel permanentPanel = new JPanel();
	JPanel miscPanel = new JPanel();
	
	
	JPanel managementPanel = new JPanel();
	JPanel optionsPanel = new JPanel();
	Box verticalMainBox = Box.createVerticalBox();
	int labelWidth = 180;
	int textBoxWidth = 250;
	
	KCatalogGuiDataBaseTab(KCatalogGui mainFrame){
		super((Frame)mainFrame,"KCatalog Settings",true);
		this.setSize(600,520);
		this.getContentPane().add(BorderLayout.NORTH,getVerticalBoxForDBTab(600,520,mainFrame));
	}
	
	public Box getVerticalBoxForDBTab(int width,int height,								KCatalogGui mainFrame){
		this.width = width;
		this.height = height;
		this.mainFrame = mainFrame;
		addComponentsToTab();				
		setNamesForComponents();
		setListenersForButtons();			
		return verticalBox;
	}
	
	private void addComponentsToTab(){
		JPanel mainPanel = new JPanel();
		Box vb = Box.createVerticalBox();
		vb.add(Box.createVerticalStrut(5));	
		vb.add(addLocationItems());	
		vb.add(Box.createVerticalStrut(5));	
		vb.add(addApplicationItems());
		vb.add(Box.createVerticalStrut(5));	
		vb.add(addPermanentLocationItem());	
		vb.add(Box.createVerticalStrut(5));	
		vb.add(addMiscItems());	
		vb.add(Box.createVerticalStrut(5));	
		vb.add(addActionButtons());	
		vb.add(Box.createVerticalStrut(5));		
		//vb.add(addFirstRow());		
		mainPanel.add(BorderLayout.NORTH,vb);
		JScrollPane sp = new JScrollPane(mainPanel);
		
		sp.setPreferredSize(new Dimension(width,height));
		verticalBox.add(sp);
		setSizeForComponents();
		setValuesForComponents();
	}		
	
	private Component addActionButtons(){
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(saveOptionsButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(cancelOptionsButton);
		return buttonBox;
	}
	
	private void setSizeForComponents(){
		databaseLocationTxt.setPreferredSize(new Dimension(200,20));
		playListLocationTxt.setPreferredSize(new Dimension(200,20));
		databaseBkupLocationTxt.setPreferredSize(new Dimension(200,20));
		associatedAppTxt.setPreferredSize(new Dimension(200,20));
		associatedFMTxt.setPreferredSize(new Dimension(200,20));
		permanentLocationsTxt.setPreferredSize(new Dimension(200,20));
		maxMp3PerFileTxt.setPreferredSize(new Dimension(200,20));
		maxMp3PerViewTxt.setPreferredSize(new Dimension(200,20));
		bkupFreqTxt1.setPreferredSize(new Dimension(200,20));
		
		
		databaseLocation.setPreferredSize(new Dimension(250,20));
		playListLocation.setPreferredSize(new Dimension(250,20));
		databaseBkupLocation.setPreferredSize(new Dimension(250,20));
		associatedApp.setPreferredSize(new Dimension(250,20));
		associatedFM.setPreferredSize(new Dimension(250,20));
		maxMp3PerFile.setPreferredSize(new Dimension(250,20));
		maxMp3PerView.setPreferredSize(new Dimension(250,20));
		bkupFreq.setPreferredSize(new Dimension(250,20));
		
	//	permanentLocations.setPreferredSize(new Dimension(250,20));		
		
		databaseBkupLocationButton.setPreferredSize(new Dimension(100,20));
		playListLocationButton.setPreferredSize(new Dimension(100,20));
		databaseLocationButton.setPreferredSize(new Dimension(100,20));
		associatedAppButton.setPreferredSize(new Dimension(100,20));
		associatedFMButton.setPreferredSize(new Dimension(100,20));
		bkupFreqTxt2.setPreferredSize(new Dimension(100,20));
		//addPermanentLocationButton.setPreferredSize(new Dimension(75,20));
		removePermanentLocationButton.setPreferredSize(new Dimension(75,20));
		
		
		
	//	locationPanel.setPreferredSize(new Dimension(400,200));	
	//	appPanel.setPreferredSize(new Dimension(200,90));	
	}
	
	private Component addMiscItems(){
		TitledBorder miscPanelBorder =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Options");
		miscPanel.setBorder(miscPanelBorder);
		
		Box maxPerXmlBox = Box.createHorizontalBox();
		maxPerXmlBox.add(this.maxMp3PerFileTxt);
		maxPerXmlBox.add(this.maxMp3PerFile);
		
		Box maxPerSearchBox = Box.createHorizontalBox();
		maxPerSearchBox.add(this.maxMp3PerViewTxt);
		maxPerSearchBox.add(this.maxMp3PerView);

		Box maxPerDBBkupBox = Box.createHorizontalBox();
		maxPerDBBkupBox.add(this.bkupFreqTxt1);
		maxPerDBBkupBox.add(this.bkupFreq);
		maxPerDBBkupBox.add(Box.createHorizontalStrut(5));
		maxPerDBBkupBox.add(this.bkupFreqTxt2);
		
		Box vb = Box.createVerticalBox();
		vb.add(maxPerXmlBox);
		vb.add(Box.createVerticalStrut(5));	
		vb.add(maxPerSearchBox);
		vb.add(Box.createVerticalStrut(5));	
		vb.add(maxPerDBBkupBox);
		
		miscPanel.add(BorderLayout.NORTH,vb);
				
		return miscPanel;
		
	}
	
	private Component addPermanentLocationItem(){
		TitledBorder permanentPanelBorder =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Removable Storage Media");
		permanentPanel.setBorder(permanentPanelBorder);
		Box vb = Box.createVerticalBox();		
		Box permanentLocBox = Box.createHorizontalBox();
		permanentLocBox.add(this.permanentLocationsTxt);
		
		JScrollPane sp = new JScrollPane(permanentLocations,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
								JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);			
		sp.setPreferredSize(new Dimension(250,70));
		
		permanentLocBox.add(sp);
							
		Box vbPermanentButtons = Box.createVerticalBox();
		vbPermanentButtons.add(Box.createVerticalStrut(5));
		//addPermanentLocationButton.setPreferredSize(new Dimension(80,40));
		vbPermanentButtons.add(addPermanentLocationButton);
		addPermanentLocationButton.setMargin(new Insets(0,14,0,14));
		vbPermanentButtons.add(Box.createVerticalStrut(5));
		//removePermanentLocationButton.setPreferredSize(new Dimension(80,40));
		//removePermanentLocationButton.setMargin(new Insets(3,7,3,7));
		vbPermanentButtons.add(removePermanentLocationButton);		
		vbPermanentButtons.add(Box.createVerticalStrut(5));				
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(75,70));
		p.add(BorderLayout.NORTH,vbPermanentButtons);
		permanentLocBox.add(Box.createHorizontalStrut(8));
		permanentLocBox.add(p);
		
		permanentPanel.add(BorderLayout.NORTH,permanentLocBox);
						
				
		return permanentPanel;				
	}
	
	private Component addApplicationItems(){				
		TitledBorder appPanelBorder =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Applications");
		appPanel.setBorder(appPanelBorder);
		
		Box vb = Box.createVerticalBox();
		
		Box playAppBox = Box.createHorizontalBox();
		playAppBox.add(this.associatedAppTxt);
		playAppBox.add(this.associatedApp);
		playAppBox.add(Box.createHorizontalStrut(5));
		playAppBox.add(this.associatedAppButton);
		
		Box openAppBox = Box.createHorizontalBox();
		openAppBox.add(this.associatedFMTxt);
		openAppBox.add(this.associatedFM);
		openAppBox.add(Box.createHorizontalStrut(5));
		openAppBox.add(this.associatedFMButton);
		
		vb.add(playAppBox);
		vb.add(Box.createVerticalStrut(5));	
		vb.add(openAppBox);
		
		appPanel.add(BorderLayout.NORTH,vb);
				
		return appPanel;
			
	}
	
	private Component addLocationItems(){				
		TitledBorder locationPanelBorder =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Database Location");
		locationPanel.setBorder(locationPanelBorder);
		locationPanel.setVisible(true);
		Box vb = Box.createVerticalBox();
		
		Box dbLocationBox = Box.createHorizontalBox();
		dbLocationBox.add(this.databaseLocationTxt);
		dbLocationBox.add(this.databaseLocation);
		dbLocationBox.add(this.databaseLocationButton);
		
		Box playListLocationBox = Box.createHorizontalBox();
		playListLocationBox.add(this.playListLocationTxt);
		playListLocationBox.add(this.playListLocation);
		playListLocationBox.add(Box.createHorizontalStrut(5));
		playListLocationBox.add(this.playListLocationButton);
		
		Box dbLocationBkupBox = Box.createHorizontalBox();
		dbLocationBkupBox.add(this.databaseBkupLocationTxt);
		dbLocationBkupBox.add(this.databaseBkupLocation);
		dbLocationBkupBox.add(Box.createHorizontalStrut(5));
		dbLocationBkupBox.add(this.databaseBkupLocationButton);
		
		vb.add(dbLocationBox);
		vb.add(Box.createVerticalStrut(5));	
		vb.add(playListLocationBox);
		vb.add(Box.createVerticalStrut(5));	
		vb.add(dbLocationBkupBox);
		
		locationPanel.add(BorderLayout.NORTH,vb);		
		return locationPanel;
		
	}
	
	private void setListenersForButtons(){
		ButtonListener bl = new ButtonListener();
		addPermanentLocationButton.addActionListener(bl);
		removePermanentLocationButton.addActionListener(bl);
		databaseLocationButton.addActionListener(bl);
		playListLocationButton.addActionListener(bl);
		saveOptionsButton.addActionListener(bl);
		cancelOptionsButton.addActionListener(bl);
		addFolderLocationButton.addActionListener(bl);
		synchronizeDBButton.addActionListener(bl);
		addFolderButton.addActionListener(bl);
		associatedAppButton.addActionListener(bl);
		associatedFMButton.addActionListener(bl);
	
	}	
	private void setNamesForComponents(){	
		addPermanentLocationButton.setName("addPermanentLocationButton");
	  	removePermanentLocationButton.setName("removePermanentLocationButton");
		databaseLocationButton.setName("databaseLocationButton");
		playListLocationButton.setName("playListLocationButton");
		saveOptionsButton.setName("saveOptionsButton");
		cancelOptionsButton.setName("cancelOptionsButton");
		addFolderLocationButton.setName("addFolderLocationButton");
	    synchronizeDBButton.setName("synchronizeDBButton");
	    addFolderButton.setName("addFolderButton");
	    associatedAppButton.setName("associatedAppButton");
	    associatedFMButton.setName("associatedFMButton");
	}
		
	private void setValuesForComponents(){
		ArrayList permanentLocationsList = 					
				(ArrayList)KCatalogConfigOptions.getObjectOptionValue(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
		Object[] listData = permanentLocationsList.toArray();
		permanentLocations.setListData(listData);
		
		this.databaseBkupLocation.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_DBBKUP_LOCATION));
		
		this.playListLocation.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_PLAYLIST_LOCATION));
		
		this.databaseLocation.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_DATABASE_LOCATION));
		
		this.associatedApp.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD));
											
		this.associatedFM.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER));
		
		this.maxMp3PerFile.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_MAX_MP3_PER_XML));
											
		this.maxMp3PerView.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW));
		this.bkupFreq.setText(KCatalogConfigOptions.getOptionValue(
											KCatalogConstants.CONFIG_BACKUP_DB_FREQ));
											
											
											
	}

	private void addManagementPanel(){
		Box vb1 = Box.createVerticalBox();	
		vb1.add(Box.createVerticalStrut(5));
		vb1.add(addFolderLocationComponent());
		vb1.add(Box.createVerticalStrut(10));
		vb1.add(addCommentComponent());
		vb1.add(Box.createVerticalStrut(10));
		vb1.add(addFolderButton());		
		vb1.add(Box.createVerticalStrut(10));
		vb1.add(addSynchronizeDBComponent());	
		vb1.add(Box.createVerticalStrut(10));
		vb1.add(addSynchronizeDBCommentComponent());
		vb1.add(Box.createVerticalStrut(10));
		vb1.add(addStatusMessageComponent());
		vb1.add(Box.createVerticalStrut(10));
		
	/*	managementPanel.setMaximumSize(
			new Dimension((int)optionsPanel.getMaximumSize().getWidth(),
					(int)(
							optionsPanel.getMaximumSize().getHeight()-
								optionsPanel.getMaximumSize().getHeight()/3
							)));
		managementPanel.setPreferredSize(managementPanel.getMaximumSize());*/
		TitledBorder managementPanelTitle =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Settings");
		managementPanel.setBorder(managementPanelTitle);
		managementPanel.add(BorderLayout.NORTH,vb1);		
		Box horz = Box.createHorizontalBox();
		horz.add(Box.createHorizontalStrut(20));
		horz.add(managementPanel);
		horz.add(Box.createHorizontalStrut(20));		
		verticalBox.add(horz);
	}
	
	private Box addSynchronizeDBCommentComponent(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(60));					
		horzBox.add(deleteCommentTxt);
		horzBox.add(Box.createHorizontalStrut(35));
		deleteComment.setMinimumSize(new Dimension(10,20));
		deleteComment.setPreferredSize(new Dimension(200,20));
		deleteComment.setMaximumSize(new Dimension(200,20));		
		horzBox.add(deleteComment);				
		horzBox.add(Box.createHorizontalStrut(50));
		return horzBox;
	}
	
	private Box addStatusMessageComponent(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(60));					
		horzBox.add(statusMessage);									
		return horzBox;	
	}
	
	private Box addCommentComponent(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(60));					
		horzBox.add(addFolderCommentTxt);
		horzBox.add(Box.createHorizontalStrut(28));
		addFolderComment.setMinimumSize(new Dimension(10,20));
		addFolderComment.setPreferredSize(new Dimension(200,20));
		addFolderComment.setMaximumSize(new Dimension(200,20));		
		horzBox.add(addFolderComment);				
		horzBox.add(Box.createHorizontalStrut(50));
		return horzBox;
	}
	
	private Box addFolderButton(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(240));								
		addFolderButton.setMargin(new Insets(4,10,4,10));
		horzBox.add(addFolderButton);		
		horzBox.add(Box.createHorizontalStrut(200));
		return horzBox;
	}
	
	private Box addSynchronizeDBComponent(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(110));					
		horzBox.add(synchronizeDBTxt);
		horzBox.add(Box.createHorizontalStrut(27));						
		synchronizeDBButton.setMargin(new Insets(2,2,2,2));
		horzBox.add(synchronizeDBButton);	
		horzBox.add(Box.createHorizontalStrut(250));
		return horzBox;
	}
	
	private Box addFolderLocationComponent(){
		Box horzBox = Box.createHorizontalBox();	
		horzBox.add(Box.createHorizontalStrut(90));					
		horzBox.add(addFolderLocationTxt);
		horzBox.add(Box.createHorizontalStrut(28));
		addFolderLocation.setMinimumSize(new Dimension(10,20));
		addFolderLocation.setPreferredSize(new Dimension(200,20));
		addFolderLocation.setMaximumSize(new Dimension(200,20));		
		horzBox.add(addFolderLocation);		
		horzBox.add(Box.createHorizontalStrut(5));
		horzBox.add(addFolderLocationButton);
		return horzBox;
	}
	
	private void addSpaceBetweenComponents(){
		int sp = this.height / 15 ;
		this.verticalBox1.add(Box.createVerticalStrut(sp + 5));
		this.verticalBox2.add(Box.createVerticalStrut(sp));
		this.verticalBox3.add(Box.createVerticalStrut(sp));
	}
	
	private void addOptionPanel(){
		Box vb1 = Box.createVerticalBox();	
		
		addMaxRecordPerFileComponents();		
		addSpaceBetweenComponents();
		addMaxMp3PerSearchPageComponent();
		addSpaceBetweenComponents();
		addXmlFilePrefixComponent();		
		addSpaceBetweenComponents();		
		addAssociatedAppMp3Component();	
		addSpaceBetweenComponents();
		addAssociatedFMComponent();
		addSpaceBetweenComponents();
		addPlayListLocationComponent();		
		addSpaceBetweenComponents();
		addDataBaseLocationComponent();		
		addSpaceBetweenComponents();
		addPermanentLocationComponent();				
		addSpaceBetweenComponents();
		//addSaveButtonComponent();		
	}

	private Box addSaveButtonComponent(){
		Box horzBox = Box.createHorizontalBox();		
		this.verticalBox1.add(Box.createVerticalStrut(30));
		this.verticalBox2.add(saveOptionsButton);
		this.verticalBox3.add(Box.createVerticalStrut(30));
	//	horzBox.add(Box.createHorizontalStrut(width/6));	
		return horzBox;
	}
	
	private Box addAssociatedFMComponent(){
		Box horzBox = Box.createHorizontalBox();			
		verticalBox1.add(Box.createVerticalStrut(10));
		this.verticalBox1.add(associatedFMTxt);		
		associatedFM.setMinimumSize(new Dimension(10,20));
		associatedFM.setPreferredSize(new Dimension(this.textBoxWidth-50,20));
		associatedFM.setMaximumSize(new Dimension(this.textBoxWidth,20));		
		associatedFM.setText(KCatalogConfigOptions.getOptionValue(
								KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER));
		
	//	this.verticalBox2.add(databaseLocation);						
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)associatedFM.getPreferredSize().getHeight() + 70));
		//this.verticalBox3.add(databaseLocationButton);							
		Box hz = Box.createHorizontalBox();
		hz.add(Box.createHorizontalStrut(10));
		hz.add(associatedFM);
		hz.add(Box.createHorizontalStrut(4));
		associatedFMButton.setPreferredSize(new Dimension(80,40));
		associatedFMButton.setMargin(new Insets(3,10,3,10));
		hz.add(associatedFMButton);				
		this.verticalBox2.add(hz);
		return horzBox;
	}
	
	private Box addAssociatedAppMp3Component(){
		Box horzBox = Box.createHorizontalBox();			
		verticalBox1.add(Box.createVerticalStrut(10));
		this.verticalBox1.add(associatedAppTxt);		
		associatedApp.setMinimumSize(new Dimension(10,20));
		associatedApp.setPreferredSize(new Dimension(this.textBoxWidth-50,20));
		associatedApp.setMaximumSize(new Dimension(this.textBoxWidth,20));		
		associatedApp.setText(KCatalogConfigOptions.getOptionValue(
								KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD));
		
	//	this.verticalBox2.add(databaseLocation);						
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)associatedApp.getPreferredSize().getHeight() + 70));
		//this.verticalBox3.add(databaseLocationButton);							
		Box hz = Box.createHorizontalBox();
		hz.add(Box.createHorizontalStrut(10));
		hz.add(associatedApp);
		hz.add(Box.createHorizontalStrut(4));
		associatedAppButton.setPreferredSize(new Dimension(80,40));
		associatedAppButton.setMargin(new Insets(3,10,3,10));
		hz.add(associatedAppButton);				
		this.verticalBox2.add(hz);
		return horzBox;
	}
	
	private Box addPlayListLocationComponent(){
		Box horzBox = Box.createHorizontalBox();			
		verticalBox1.add(Box.createVerticalStrut(20));
		this.verticalBox1.add(playListLocationTxt);		
		playListLocation.setMinimumSize(new Dimension(10,20));
		playListLocation.setPreferredSize(new Dimension(this.textBoxWidth-50,20));
		playListLocation.setMaximumSize(new Dimension(this.textBoxWidth,20));		
		playListLocation.setText(
				KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_PLAYLIST_LOCATION));		
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)playListLocation.getPreferredSize().getHeight() + 70));		
		Box hz = Box.createHorizontalBox();
		hz.add(Box.createHorizontalStrut(10));
		hz.add(playListLocation);
		hz.add(Box.createHorizontalStrut(4));
		playListLocationButton.setPreferredSize(new Dimension(80,40));
		playListLocationButton.setMargin(new Insets(3,10,3,10));
		hz.add(playListLocationButton);				
		
		this.verticalBox2.add(hz);
		return horzBox;
	}
	
	private Box addDataBaseLocationComponent(){
		Box horzBox = Box.createHorizontalBox();			
		verticalBox1.add(Box.createVerticalStrut(20));
		this.verticalBox1.add(databaseLocationTxt);		
		databaseLocation.setMinimumSize(new Dimension(10,20));
		databaseLocation.setPreferredSize(new Dimension(this.textBoxWidth-50,20));
		databaseLocation.setMaximumSize(new Dimension(this.textBoxWidth,20));		
		databaseLocation.setText(
				KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_DATABASE_LOCATION));
		
	//	this.verticalBox2.add(databaseLocation);						
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)databaseLocation.getPreferredSize().getHeight() + 70));
		//this.verticalBox3.add(databaseLocationButton);							
		Box hz = Box.createHorizontalBox();
		hz.add(Box.createHorizontalStrut(10));
		hz.add(databaseLocation);
		hz.add(Box.createHorizontalStrut(4));
		databaseLocationButton.setPreferredSize(new Dimension(80,40));
		databaseLocationButton.setMargin(new Insets(3,10,3,10));
		hz.add(databaseLocationButton);				
		
		this.verticalBox2.add(hz);
		return horzBox;
	}
	
	private Box addPermanentLocationComponent(){
		Box horzBox = Box.createHorizontalBox();								
		permanentLocations.setMinimumSize(new Dimension(this.textBoxWidth-50,20));
		permanentLocations.setPreferredSize(new Dimension(this.textBoxWidth-50,20));
		permanentLocations.setMaximumSize(new Dimension(this.textBoxWidth-50,20));
		ArrayList permanentLocationsList = 					
				(ArrayList)KCatalogConfigOptions.getObjectOptionValue(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
		
		Object[] listData = permanentLocationsList.toArray();
		permanentLocations.setListData(listData);		
		JScrollPane sp = new JScrollPane(permanentLocations,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
								JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setMaximumSize(new Dimension(this.textBoxWidth-70,100));	
		sp.setPreferredSize(sp.getMaximumSize());
		Box vbPermanentButtons = Box.createVerticalBox();
		vbPermanentButtons.add(Box.createVerticalStrut(5));
		//addPermanentLocationButton.setPreferredSize(new Dimension(80,40));
		vbPermanentButtons.add(addPermanentLocationButton);
		addPermanentLocationButton.setMargin(new Insets(3,20,3,20));
		vbPermanentButtons.add(Box.createVerticalStrut(5));
		removePermanentLocationButton.setPreferredSize(new Dimension(80,40));
		removePermanentLocationButton.setMargin(new Insets(3,7,3,7));
		vbPermanentButtons.add(removePermanentLocationButton);		
		vbPermanentButtons.add(Box.createVerticalStrut(5));				
	//	horzBox.add(Box.createHorizontalStrut(10));
		JPanel p = new JPanel();
		p.setPreferredSize(sp.getMaximumSize());
		p.add(BorderLayout.NORTH,vbPermanentButtons);
		
				
		this.verticalBox1.add(Box.createVerticalStrut(
			80));
		this.verticalBox1.add(permanentLocationsTxt);		
		Box hz = Box.createHorizontalBox();
		hz.add(sp);
		p.setPreferredSize(new Dimension(90,100));
		p.setMaximumSize(p.getPreferredSize());
		hz.add(Box.createHorizontalStrut(7));
		hz.add(p);
		this.verticalBox2.add(hz);
		
	//	this.verticalBox3.add(p);	
		
	//	horzBox.add(Box.createHorizontalStrut(width/19));		
		return horzBox;
	}
	
	private Box addMaxMp3PerSearchPageComponent(){
		Box horzBox = Box.createHorizontalBox();										
		maxMp3PerView.setMinimumSize(new Dimension(10,20));
		maxMp3PerView.setPreferredSize(new Dimension(this.textBoxWidth,20));
		maxMp3PerView.setMaximumSize(new Dimension(this.textBoxWidth,20));
		maxMp3PerView.setText(
			KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW));
		maxMp3PerViewTxt.setPreferredSize(
				new Dimension(this.labelWidth,
						(int)maxMp3PerView.getPreferredSize().getHeight()));
			
		this.verticalBox1.add(maxMp3PerViewTxt);
		this.verticalBox2.add(maxMp3PerView);	
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)maxMp3PerView.getPreferredSize().getHeight()));
	//	horzBox.add(Box.createHorizontalStrut(width/5));	
		return horzBox;
	}
	
	private Box addXmlFilePrefixComponent(){
		Box horzBox = Box.createHorizontalBox();										
		xmlFilePrefix.setMinimumSize(new Dimension(10,20));
		xmlFilePrefix.setPreferredSize(new Dimension(this.textBoxWidth,20));
		xmlFilePrefix.setMaximumSize(new Dimension(this.textBoxWidth,20));
		xmlFilePrefix.setText(
				KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_FILE_NAME_PREFIX));
		xmlFilePrefixTxt.setPreferredSize(
				new Dimension(this.labelWidth,
						(int)xmlFilePrefix.getPreferredSize().getHeight()));
			
		this.verticalBox1.add(xmlFilePrefixTxt);
		this.verticalBox2.add(xmlFilePrefix);	
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)xmlFilePrefix.getPreferredSize().getHeight()));
	//	horzBox.add(Box.createHorizontalStrut(width/5));	
		return horzBox;
	}
	
	private Box addMaxRecordPerFileComponents(){
		Box horzBox = Box.createHorizontalBox();
		maxMp3PerFile.setMinimumSize(new Dimension(10,20));
		maxMp3PerFile.setPreferredSize(new Dimension(this.textBoxWidth,20));
		maxMp3PerFile.setMaximumSize(new Dimension(this.textBoxWidth,20));
		maxMp3PerFile.setText(
				KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_MAX_MP3_PER_XML));		
		maxMp3PerFileTxt.setPreferredSize(new Dimension(
					this.labelWidth,
					(int)maxMp3PerFile.getPreferredSize().getHeight()
			));			
		
		this.verticalBox1.add(maxMp3PerFileTxt);									
		this.verticalBox2.add(maxMp3PerFile);		
		this.verticalBox3.add(Box.createVerticalStrut(
				(int)maxMp3PerFile.getPreferredSize().getHeight()));		
		return horzBox;
	}
	
	public void setVisible(boolean stat){
		if(stat){
			
			setLocation(mainFrame.getLocation());
			setValuesForComponents();
		}
		super.setVisible(stat);
	}
	
	public void enableAllComponents(boolean status){
		
		addPermanentLocationButton.setEnabled(status);
	  	removePermanentLocationButton.setEnabled(status);
		databaseLocationButton.setEnabled(status);
		playListLocationButton.setEnabled(status);
		saveOptionsButton.setEnabled(status);
		addFolderLocationButton.setEnabled(status);
	    synchronizeDBButton.setEnabled(status);
	    addFolderButton.setEnabled(status);
	}

	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try{
				KCatalogDBManagmentTabAction action 
					= new KCatalogDBManagmentTabAction(mainFrame,currentRef,e);
				action.processAction();
			}catch(Exception exception){
				KCatalogException.logIfDebugTrue(exception);
			}
		}
	}
	
	
	
}

