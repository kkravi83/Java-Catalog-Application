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
import com.kcatalog.gui.KCatalogGuiSettingsDlg;


import com.kcatalog.common.KCatalogDto;
import com.kcatalog.fileutils.FileManager;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.applogic.KCatalogUpdateMp3InfoDto;
import com.kcatalog.common.KCatalogCommonDto;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;


import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;


public class KCatalogGuiSettingsAction {
	KCatalogGui mainFrame = null;
	Mp3FileDto searchMp3FileDto = null;	
	ActionEvent event = null;
	KCatalogGuiSettingsDlg settingsDlgRef = null;
	static KCatalogXMLSchemaSearch fs = null;
	
	public KCatalogGuiSettingsAction(KCatalogGui mainFrame,KCatalogGuiSettingsDlg settingsDlgRef,	
										ActionEvent event){		
		
		this.event = event;
		this.settingsDlgRef = settingsDlgRef;
		this.mainFrame = mainFrame;
	}
	
	public void processAction(){
		String buttonName = ((JButton)(event.getSource())).getName();
		if("associatedAppButton".equals(buttonName)){
			selectAssociatedApplication();
		}
		if("saveSettingsButton".equals(buttonName)){
			saveSettings();
		}
		
	}
	
	private void saveSettings(){
		String maxNo = settingsDlgRef.maxMp3PerView.getText().trim();
		String appPath = settingsDlgRef.associatedApp.getText().trim();
		if("".equals(maxNo)){
			throw new KCatalogException("Please select an application to play Mp3");	
		}
		if("".equals(appPath) ||
				"0".equals(appPath)){
			throw new KCatalogException("Please enter a valid value in Maximum Mp3 per view");	
		}
		KCatalogConfigOptions.setOptionValue(
			KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW,maxNo);
		KCatalogConfigOptions.setOptionValue(
			KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD,appPath);
		//JOptionPane.showMessageDialog(settingsDlgRef,"Saved Successfully, It is recommended to restart the application");
		KCatalogStatusManager.showMessages(settingsDlgRef,"Saved Successfully, It is recommended to restart the application");
	}
	
	private void selectAssociatedApplication(){
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(settingsDlgRef);
			int rval = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION==rval){				
			File f = fc.getSelectedFile();
			settingsDlgRef.associatedApp.setText(f.getAbsolutePath());
		}		
	}
}
