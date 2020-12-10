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
import com.kcatalog.gui.KCatalogGuiDataBaseTab;
import com.kcatalog.gui.KCatalogGuiSettingsAction;

import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogStatusManager;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.event.ActionEvent;

public class KCatalogGuiSettingsDlg extends JDialog {

	KCatalogGui mainFrame = null;			
	private static KCatalogGuiSettingsDlg settingsDlg = null;
	Container dlgCp = this.getContentPane();
	Box verticalBox = Box.createVerticalBox();
	public JLabel associatedAppTxt = new JLabel("Application to play Mp3's");
	public JTextField associatedApp = new JTextField();
	public JButton associatedAppButton = new JButton(" Choose ");
	
	public JLabel maxMp3PerViewTxt = new JLabel("Maximum Mp3's to be listed in a page");
	public JTextField maxMp3PerView = new JTextField();
	public JButton saveSettingsButton = new JButton(" Save ");
	private KCatalogGuiSettingsDlg currentRef = null;
	
	private KCatalogGuiSettingsDlg(KCatalogGui mainFrame){
		this.mainFrame = mainFrame;
		this.setSize(500,200);
		settingsDlg = this;		
		this.setTitle("KCatalog Settings");
		addComponents();
		setSettingFieldValues();
		setNamesForComponents();
		setListenersForComponents();		
		dlgCp.add(BorderLayout.NORTH,verticalBox);
	}
	

	
	public static KCatalogGuiSettingsDlg getInstance(KCatalogGui mainFrame){		
		if(null == settingsDlg){			
			return new KCatalogGuiSettingsDlg(mainFrame);
		}else{
			return settingsDlg;	
		}
	}
	
	private void setListenersForComponents(){
		ButtonListener bl = new ButtonListener();
		saveSettingsButton.addActionListener(bl);
		associatedAppButton.addActionListener(bl);
	}
	
	private void setNamesForComponents(){
		saveSettingsButton.setName("saveSettingsButton");
		associatedAppButton.setName("associatedAppButton");
	}
	
	private void addComponents(){
		verticalBox.add(Box.createVerticalStrut(20));
		verticalBox.add(addAssociatedAppComponent());
		verticalBox.add(Box.createVerticalStrut(20));
		verticalBox.add(addMaxMp3PerPageComponent());
		verticalBox.add(Box.createVerticalStrut(20));
		verticalBox.add(addSaveComponent());
		verticalBox.add(Box.createVerticalStrut(20));
	}
	
	private Box addSaveComponent(){
		Box horzBox = Box.createHorizontalBox();						
		horzBox.add(Box.createHorizontalStrut(100));
		saveSettingsButton.setMargin(new Insets(3,30,3,30));
		horzBox.add(saveSettingsButton);
		horzBox.add(Box.createHorizontalStrut(100));
		return horzBox;
	}
	
	private Box addMaxMp3PerPageComponent(){
		Box horzBox = Box.createHorizontalBox();
		int width = (int)getSize().getWidth();
		horzBox.add(Box.createHorizontalStrut(width/30));							
		horzBox.add(maxMp3PerViewTxt);
		horzBox.add(Box.createHorizontalStrut(10));
		maxMp3PerView.setMinimumSize(new Dimension(10,20));
		maxMp3PerView.setPreferredSize(new Dimension(30,20));
		maxMp3PerView.setMaximumSize(new Dimension(30,20));		
		horzBox.add(maxMp3PerView);			
		horzBox.add(Box.createHorizontalStrut(width/3 + 24));			
		return horzBox;
	}
	
	private Box addAssociatedAppComponent(){
		Box horzBox = Box.createHorizontalBox();
		int width =  (int)getSize().getWidth();
		horzBox.add(Box.createHorizontalStrut(width/20));					
		horzBox.add(associatedAppTxt);
		horzBox.add(Box.createHorizontalStrut(10));
		associatedApp.setMinimumSize(new Dimension(10,20));
		associatedApp.setPreferredSize(new Dimension(120,20));
		associatedApp.setMaximumSize(new Dimension(200,20));		
		horzBox.add(associatedApp);				
		horzBox.add(Box.createHorizontalStrut(10));
		horzBox.add(associatedAppButton);
		//horzBox.add(Box.createHorizontalStrut(40));
		return horzBox;
	}
	
	private void setSettingFieldValues(){
		maxMp3PerView.setText(KCatalogConfigOptions.getOptionValue(
						KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW));
		associatedApp.setText(KCatalogConfigOptions.getOptionValue(
						KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD));
	}
	
	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			KCatalogGuiSettingsAction action =
									new KCatalogGuiSettingsAction(mainFrame,settingsDlg,e);
			action.processAction();
		}
	}
}


