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
import com.kcatalog.gui.KCatalogGui;
import com.kcatalog.gui.KCatalogGuiNotesDlgAction;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;
import com.kcatalog.xmlschema.KCatalogXMLSchemaNotesManager;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.text.*;

public class KCatalogGuiNotesDlg extends JDialog{

	private int width = 0;
	private int height = 0;
	private KCatalogGui mainFrame = null;
	
	JLabel optionsComboBoxTxt = new JLabel("Select Option");
	String[] options = new String[]{
				"All",
				"Disks with Notes",
				"Disks without Notes"
	};
	int allIndex = 0;
	int diskWithIndex = 1;
	int diskWithoutIndex = 2;
	
	JComboBox optionsComboBox =  new JComboBox(
					options);
	JLabel labelsComboBoxTxt = new JLabel("Select Option");
	JComboBox labelsComboBox =  new JComboBox();
	JTextArea notesData = new JTextArea();
	JButton saveButton = new JButton("Save");
	JButton cancelButton = new JButton("close");
	private KCatalogGuiNotesDlg currentRef = null;
					
	Box verticalBox = Box.createVerticalBox();
	
	KCatalogGuiNotesDlg(KCatalogGui mainFrame){
		super((Frame)mainFrame,"KCatalog Notes Manager",true);
		this.setSize(480,235);
		this.getContentPane().add(BorderLayout.NORTH,getVerticalBoxForDBTab(480,
		                  235,mainFrame));
		currentRef = this;
	}

	private Box getVerticalBoxForDBTab(int width,int height,
							KCatalogGui mainFrame){
		this.width = width;
		this.height = height;
		this.mainFrame = mainFrame;
		addComponentsToVerticalBox();
		setNamesForComponents();
		setListenersForComponents();
		populateDefaultValues();
		return verticalBox;
	}
	
	private void populateDefaultValues(){
		
		labelsComboBox.removeAllItems();		
		optionsComboBox.setSelectedIndex(allIndex);
		addItemsToLabelComboBox(getListForSelectedOption());
	}
	
	public ArrayList getListForSelectedOption(){
		ArrayList result = null;
		String searchAttribute = KCatalogXMLSchemaSearch.ATTR_COMMENT;
						
		KCatalogXMLSchemaSearch ss = 	
			               KCatalogXMLSchemaSearch.newInstance();	
		ArrayList labelList =
			          ss.getAttributeValuesForMp3(searchAttribute);	
		if( allIndex == optionsComboBox.getSelectedIndex()){			
			result = labelList;
		}
		if( diskWithIndex == optionsComboBox.getSelectedIndex()){
			result = getDiskWithNotes(labelList);
		}
		if( diskWithoutIndex == optionsComboBox.getSelectedIndex()){
			result = getDiskWithoutNotes(labelList);
		}
		return result;
	}
	
	private ArrayList getDiskWithoutNotes(ArrayList allList){
		Iterator it = allList.iterator();
		ArrayList result = new ArrayList();
		KCatalogXMLSchemaNotesManager nm = new KCatalogXMLSchemaNotesManager();
		while(it.hasNext()){
			String label = (String) it.next();
			if("".equals(nm.getNotesForLabel(label).trim())){
				result.add(label);
			};
		}
		return result;
	}
	
	private ArrayList getDiskWithNotes(ArrayList allList){
		Iterator it = allList.iterator();
		ArrayList result = new ArrayList();
		KCatalogXMLSchemaNotesManager nm = new KCatalogXMLSchemaNotesManager();
		while(it.hasNext()){
			String label = (String) it.next();
			if(!"".equals(nm.getNotesForLabel(label).trim())){
				result.add(label);
			};
		}
		return result;
	}
	
	public void addItemsToLabelComboBox(ArrayList labelList){
		Iterator it = labelList.iterator();
		while(it.hasNext()){
			labelsComboBox.addItem(it.next());
		}
	}
	
	private void setListenersForComponents(){
		ButtonListener bl = new ButtonListener();
		ComboBoxListener cl = new ComboBoxListener();
		saveButton.addActionListener(bl);
		cancelButton.addActionListener(bl);
		optionsComboBox.addItemListener(cl);
		labelsComboBox.addItemListener(cl);
	}
	
	private void setNamesForComponents(){
		saveButton.setName("saveButton");
		cancelButton.setName("cancelButton");
		optionsComboBox.setName("optionsComboBox");
		labelsComboBox.setName("labelsComboBox");
	}
	
	private void addComponentsToVerticalBox(){
		verticalBox.add(Box.createVerticalStrut(7));
		verticalBox.add(getSelectionComboBoxes());
		verticalBox.add(Box.createVerticalStrut(7));
		verticalBox.add(getNotesTextArea());
		verticalBox.add(Box.createVerticalStrut(7));
		verticalBox.add(getButtons());
		verticalBox.add(Box.createVerticalStrut(1));
	}
	
	private Component getButtons(){
		JPanel p = new JPanel();
		Box hb = Box.createHorizontalBox();
		hb.add(saveButton);
		hb.add(Box.createHorizontalStrut(4));
		hb.add(cancelButton);
		p.add(BorderLayout.NORTH,hb);
		return p;
	}
	
	private Component getNotesTextArea(){
		JPanel panel = new JPanel();
		Box hb = Box.createHorizontalBox();
		JScrollPane sp = new JScrollPane(notesData);
		sp.setPreferredSize(new Dimension(410,75));
		hb.add(sp);
		panel.add(BorderLayout.NORTH,hb);		
		panel.setPreferredSize(new Dimension(410,110));
		TitledBorder border =
			new TitledBorder(LineBorder.createGrayLineBorder(),"Notes");
		panel.setBorder(border);
		return panel;
	}
	
	public void setVisible(boolean stat){
		if(stat){
			
			setLocation(mainFrame.getLocation());			
		}
		super.setVisible(stat);
	}
	
	private Component getSelectionComboBoxes(){
		Box hb = Box.createHorizontalBox();
		
		hb.add(optionsComboBoxTxt);
		hb.add(Box.createHorizontalStrut(4));
		hb.add(optionsComboBox);
		optionsComboBox.setPreferredSize(new Dimension(120,20));
		hb.add(Box.createHorizontalStrut(20));
		hb.add(labelsComboBoxTxt);
		hb.add(Box.createHorizontalStrut(4));
		hb.add(labelsComboBox);
		labelsComboBox.setPreferredSize(new Dimension(180,20));
		
		JPanel comboPanel = new JPanel();
		comboPanel.add(BorderLayout.NORTH,hb);
		return comboPanel;
	}
	
	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try{
				KCatalogGuiNotesDlgAction action 
				  = new KCatalogGuiNotesDlgAction(
				                    mainFrame,currentRef,e);
				action.processActionEvent();
			}catch(Exception exception){
				KCatalogException.logIfDebugTrue(exception);
			}
		}
	}
	
	class ComboBoxListener implements ItemListener{
	 	public void itemStateChanged(ItemEvent e){			
			try{
				KCatalogGuiNotesDlgAction action 
				  = new KCatalogGuiNotesDlgAction(
				                    mainFrame,currentRef,e);
				action.processItemEvent();
				
			}catch(Exception exception){
				KCatalogException.logIfDebugTrue(exception);
			}	
		}
	}
}









