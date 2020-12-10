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
import com.kcatalog.xmlschema.KCatalogXMLSchemaNotesManager;
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
import com.kcatalog.gui.KCatalogGui;
import com.kcatalog.gui.KCatalogGuiNotesDlg;
import java.util.ArrayList;
import javax.swing.text.*;

public class KCatalogGuiNotesDlgAction {

	private ActionEvent actionEvent = null;
	private ItemEvent itemEvent = null;
	private KCatalogGui mainFrame = null;
	private KCatalogGuiNotesDlg notesDlgRef = null;
	
	KCatalogGuiNotesDlgAction(KCatalogGui mainFrame,
					KCatalogGuiNotesDlg notesDlgRef,
						ActionEvent actionEvent){
		this.actionEvent = actionEvent;
		this.notesDlgRef = notesDlgRef;
		this.mainFrame = mainFrame;
	}
	
	KCatalogGuiNotesDlgAction(KCatalogGui mainFrame,
					KCatalogGuiNotesDlg notesDlgRef,
						ItemEvent itemEvent){
		this.itemEvent = itemEvent;
		this.notesDlgRef = notesDlgRef;
		this.mainFrame = mainFrame;
	}
	
	public void processActionEvent(){
		JButton button = (JButton)actionEvent.getSource();
		String name = button.getName();
		if("saveButton".equals(name)){
			saveButtonClicked();
		}
		if("cancelButton".equals(name)){
			cancelButtonClicked();
		}
	}
	
	private void saveButtonClicked(){
		if( null != notesDlgRef.labelsComboBox.getSelectedItem() &&
			!"".equals(((String)notesDlgRef.labelsComboBox.getSelectedItem()).trim())){
			String label =
		  	((String)notesDlgRef.labelsComboBox.getSelectedItem())
		  							.trim();
			String notes = notesDlgRef.notesData.getText().trim();
			KCatalogXMLSchemaNotesManager nm = 
						new KCatalogXMLSchemaNotesManager();
			nm.addNotes(label,notes);
			JOptionPane.showMessageDialog(notesDlgRef,
					"Database succesfully updated!",
							"KCatalog..",
						JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void cancelButtonClicked(){
		notesDlgRef.setVisible(false);
	}
	
	public void processItemEvent(){
		
		if(null != itemEvent && null != itemEvent.getSource() &&
			null != notesDlgRef){
			JComboBox cb = (JComboBox)itemEvent.getSource();		
			if( "optionsComboBox".equals(cb.getName())){
				notesDlgRef.labelsComboBox.removeAllItems();
				notesDlgRef.addItemsToLabelComboBox(
					notesDlgRef.getListForSelectedOption());
								
			}else{
				String label = (String)cb.getSelectedItem();
				KCatalogXMLSchemaNotesManager nm = 
						new KCatalogXMLSchemaNotesManager();
				String notes = nm.getNotesForLabel(label);
				notesDlgRef.notesData.setText(notes);
			}
		}
	}
}












