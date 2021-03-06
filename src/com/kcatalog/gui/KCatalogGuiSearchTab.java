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
import com.kcatalog.gui.KCatalogGuiNumberField;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.gui.KCatalogGuiSearchTabAction;
import com.kcatalog.common.KCatalogCommonUtility;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.event.ActionEvent;


public class KCatalogGuiSearchTab  extends JDialog{
	

	JPanel matchTypePanel = new JPanel();
	JRadioButton isExactRadio = new JRadioButton(" Exact Match");
	JRadioButton isLikeRadio = new JRadioButton(" Matches Like ");
	ButtonGroup matchRadioGroup = new ButtonGroup();

	JPanel operationTypePanel = new JPanel();
	JRadioButton isAndRadio = new JRadioButton(" And Operation");
	JRadioButton isOrRadio = new JRadioButton(" OR Operation");
	ButtonGroup operationRadioGroup = new ButtonGroup();

	BasicArrowButton prevButton = new BasicArrowButton(SwingConstants.WEST);
	BasicArrowButton nextButton = new BasicArrowButton(SwingConstants.EAST);
	JButton searchButton = new JButton("Search");

	JLabel displayingText = new JLabel("Displaying page");
	JTextField pageNoText =  new KCatalogGuiNumberField();
	JLabel displayingTextOf = new JLabel("out of ");
	BasicArrowButton alternateButton = new BasicArrowButton(SwingConstants.SOUTH);

	JTable searchValueTable = new JTable(new KCatalogGuiSearchValueTableModel(false));	
	JScrollPane searchValueTableScroll = null;
	
	JTable searchResultTable = new JTable(new KCatalogGuiSearchValueTableModel(true));
	JScrollPane searchResultTableScroll = null;
	
	JButton selectAll = new JButton("Select All");
	JButton deselectAll = new JButton("Deselect All");
	JButton play = new JButton("Play");
	
	JButton resetButton = new JButton("..");
	JPanel completeSearch = null;
	Box verticalBox = Box.createVerticalBox();
	KCatalogGuiSearchTab currentRef = this;
	JPanel searchResultPanel = new JPanel();
	int width = 0;
	int height = 0;
	KCatalogGui mainFrame = null;
	
	public KCatalogGuiSearchTab(JFrame owner,boolean modal){
		super((Frame)owner,modal);
		mainFrame = (KCatalogGui)owner;		
	}
	
	public void setVisible(boolean stat){
		this.getContentPane().add(BorderLayout.NORTH,
					getVerticalBoxForSearchTab(700,540,mainFrame));
		this.getContentPane().setSize(this.width,this.height);
		this.setSize(this.width,this.height);
		this.setTitle("Advanced Search");
		super.setVisible(stat);
	}
	
	public Box getVerticalBoxForSearchTab(int width,int height,
								KCatalogGui mainFrame){
		this.width = width;
		this.height = height;
		this.mainFrame = mainFrame;
		addSearchOptionPanel();
		addSearchResultPanel();
		setNamesForComponents();
		setListenersForButtons();		
		return verticalBox;
	}
	
	private void setListenersForButtons(){
		ButtonListener bl = new ButtonListener();
		prevButton.addActionListener(bl);
		nextButton.addActionListener(bl);
		searchButton.addActionListener(bl);
		selectAll.addActionListener(bl);
		deselectAll.addActionListener(bl);
		play.addActionListener(bl);	
		alternateButton.addActionListener(bl);
		resetButton.addActionListener(bl);
	}
	
	private void setNamesForComponents(){
		prevButton.setName("prevButton");
		nextButton.setName("nextButton");
		searchButton.setName("searchButton");
		selectAll.setName("selectAllButton");
		deselectAll.setName("deselectAllButton");
		play.setName("playButton");		
		alternateButton.setName("alternateButton");
		resetButton.setName("resetButton");
	}
		
	private void addSearchResultPanel(){
		
		TitledBorder searchResultPanelBorder =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Mp3 Search Results");
		searchResultPanel.setBorder(searchResultPanelBorder);
		searchResultPanel.setMaximumSize(new Dimension(
				(int)completeSearch.getMaximumSize().getWidth(),
				(int)(height - completeSearch.getMaximumSize().getHeight()-50)));
		searchResultPanel.setPreferredSize(searchResultPanel.getMaximumSize());
//		System.out.println(searchResultPanel.getMaximumSize());
		Box vb1 = Box.createVerticalBox();
	//	vb1.add(addRadioButtons());
		vb1.add(addAlternateSearchButtons());
		//vb1.add(Box.createVerticalStrut(2));
		vb1.add(addAlternateSearchSubmitButton());
		//vb1.add(Box.createVerticalStrut(2));
		vb1.add(addSearchResultTable());
		vb1.add(addSearchResultButtons());
	
		
		searchResultPanel.add(vb1);
		Box horz = Box.createHorizontalBox();
		horz.add(searchResultPanel);

		verticalBox.add(horz);
		//verticalBox.add(Box.createVerticalStrut(5));
	}

	private Box addSearchResultTable(){
		searchResultTableScroll = new JScrollPane(searchResultTable);
		searchResultTableScroll.setAutoscrolls(true);
 		searchResultTableScroll.setMaximumSize(new Dimension(
        	(int)searchResultPanel.getMaximumSize().getWidth()-30,
        	(int)(searchResultPanel.getMaximumSize().getHeight() - 
        			searchResultPanel.getMaximumSize().getHeight()/3)));
 		searchResultTableScroll.setPreferredSize(searchResultTableScroll.getMaximumSize());        
	 	Box searchResultBox = Box.createHorizontalBox();
		searchResultTable.setDefaultRenderer(new JCheckBox().getClass(),new KCatalogGuiMp3TableRenderer());
		searchResultTable.setDefaultRenderer(new String().getClass(),new KCatalogGuiMp3TableRenderer());
		
	//	searchResultTable.setMinimumSize(new Dimension(1000,10));
       /* searchResultTable.setPreferredScrollableViewportSize(new Dimension(
        	searchResultPanel.getWidth()-30,
        	searchResultPanel.getHeight() - searchResultPanel.getHeight()/10));        */
        searchResultTable.setDefaultEditor(new JCheckBox().getClass(),
        									new DefaultCellEditor(new JCheckBox()));   
        searchResultTable.setPreferredScrollableViewportSize(searchResultTableScroll.getMaximumSize());
       TableColumnModel cm = searchResultTable.getColumnModel();
        
        cm.getColumn(0).setPreferredWidth(25);
        cm.getColumn(0).setMaxWidth(25);
        for(int i=1;i<cm.getColumnCount();i++){
        	cm.getColumn(i).setPreferredWidth(100);        	          
        }
        cm.getColumn(8).setMaxWidth(70); 
        cm.getColumn(7).setMaxWidth(40); 
        cm.getColumn(5).setMaxWidth(40); 
	
		searchResultBox.add(searchResultTableScroll);
		return searchResultBox;
	
	}
	
	private void addSearchOptionPanel(){

		Box vb1 = Box.createVerticalBox();
		
		completeSearch = new JPanel();
		TitledBorder completeSearchTitle =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Mp3 Search Options");
		completeSearch.setBorder(completeSearchTitle);
		
		completeSearch.setPreferredSize(new Dimension(width - 30,height/3));
		completeSearch.setMaximumSize(new Dimension(width - 30,height/3));		Box horz = Box.createHorizontalBox();
		
		vb1.add(addSearchValueTable());
		vb1.add(addRadioButtons());
		completeSearch.add(vb1);
		horz.add(completeSearch);
		//verticalBox.add(Box.createVerticalStrut(1));
		verticalBox.add(horz);
	}

	private Component addSearchValueTable(){

		Box searchValueBox = Box.createHorizontalBox();	
		searchValueTable.setDefaultRenderer(new String("").getClass(),new KCatalogGuiMp3TableRenderer());
		        
		searchValueTableScroll = new JScrollPane(searchValueTable);
		searchValueTableScroll.setAutoscrolls(true); 		
 		searchValueTableScroll.setMaximumSize(
 			new Dimension((int)completeSearch.getMaximumSize().getWidth()-30,
 									(int)completeSearch.getMaximumSize().getHeight()/4));

 		searchValueTable.setPreferredScrollableViewportSize(searchValueTableScroll.getMaximumSize());
		searchValueBox.add(searchValueTableScroll);
		searchValueBox.add(Box.createHorizontalStrut(2));
		resetButton.setMargin(new Insets(1,4,1,4));
		searchValueBox.add(resetButton);
		return searchValueBox;
	}

	private Component addAlternateSearchSubmitButton(){
		Box alternateSearchBox = Box.createHorizontalBox();			
		alternateSearchBox.add(Box.createHorizontalStrut(width/2-width/14));
		alternateSearchBox.add(prevButton);
		alternateSearchBox.add(alternateButton);
		alternateSearchBox.add(nextButton);
		alternateSearchBox.add(Box.createHorizontalStrut(width/2-width/15));
		return alternateSearchBox;
	}

	private Component addAlternateSearchButtons(){
		Box alternateSearchBox = Box.createHorizontalBox();
		alternateSearchBox.add(Box.createHorizontalStrut(5));
		alternateSearchBox.add(displayingText);
		alternateSearchBox.add(Box.createHorizontalStrut(20));
		pageNoText.setColumns(5);
		pageNoText.setMaximumSize(new Dimension(30,30));
		alternateSearchBox.add(pageNoText);
		alternateSearchBox.add(Box.createHorizontalStrut(20));
		alternateSearchBox.add(displayingTextOf);
		alternateSearchBox.add(Box.createHorizontalStrut(60));
		return alternateSearchBox;
	}

	private Component addSearchButton(){
		Box searchButtons = Box.createHorizontalBox();

		searchButton.setMargin(new Insets(5,40,5,40));

		searchButtons.add(Box.createHorizontalStrut(70));
		searchButtons.add(searchButton);
		searchButtons.add(Box.createHorizontalStrut(70));

		return searchButtons;
	}

	private Component addSearchResultButtons(){
		Box searchResultButtons = Box.createHorizontalBox();

		selectAll.setMargin(new Insets(2,18,2,18));
		deselectAll.setMargin(new Insets(2,14,2,14));
		play.setMargin(new Insets(2,28,2,28));

		searchResultButtons.add(Box.createHorizontalStrut(100));
		searchResultButtons.add(selectAll);
		searchResultButtons.add(Box.createHorizontalStrut(40));
		
		searchResultButtons.add(play);
		searchResultButtons.add(Box.createHorizontalStrut(40));
		
		searchResultButtons.add(deselectAll);
		searchResultButtons.add(Box.createHorizontalStrut(100));

		return searchResultButtons;
	}
	
	private Component addRadioButtons(){
	
		matchRadioGroup.add(isExactRadio);
		matchRadioGroup.add(isLikeRadio);
		JPanel matchPanel = new JPanel();
		TitledBorder matchPaneTitle =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Pattern Matching ");
		matchPanel.setBorder(matchPaneTitle);

		Box vb1 = Box.createHorizontalBox();
		isLikeRadio.setSelected(true);
		vb1.add(isLikeRadio);
		//vb1.add(Box.createVerticalStrut(1));
		vb1.add(isExactRadio);
		matchPanel.add(vb1);
		matchPanel.setMaximumSize(new Dimension((int)completeSearch.getMaximumSize().getWidth()/2
									,90));
		Box horz = Box.createHorizontalBox();
	//	horz.add(Box.createHorizontalStrut(60));
		horz.add(Box.createHorizontalGlue());
		horz.add(matchPanel);
	//	horz.add(Box.createHorizontalStrut(60));
		horz.add(Box.createHorizontalGlue());
		searchButton.setMargin(new Insets(5,40,5,40));
		horz.add(searchButton);
	//	horz.add(Box.createHorizontalStrut(20));
		horz.add(Box.createHorizontalGlue());
		operationRadioGroup.add(isAndRadio);
		isOrRadio.setSelected(true);
		operationRadioGroup.add(isOrRadio);
		JPanel operationPanel = new JPanel();
		TitledBorder operationPaneTitle =
				new TitledBorder(LineBorder.createGrayLineBorder(),"Operation Type");
		operationPanel.setBorder(operationPaneTitle);
		vb1 = Box.createHorizontalBox();
		vb1.add(isAndRadio);
		//vb1.add(Box.createVerticalStrut(1));
		vb1.add(isOrRadio);
		operationPanel.setMaximumSize(new Dimension((int)completeSearch.getMaximumSize().getWidth()/2
									,90));
		operationPanel.add(vb1);
	//	horz.add(Box.createHorizontalStrut(50));
		
		horz.add(operationPanel);
	//	horz.add(Box.createHorizontalStrut(60));
		horz.add(Box.createHorizontalGlue());
		return horz;
	}

	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try{
				KCatalogGuiSearchTabAction action 
					= new KCatalogGuiSearchTabAction(mainFrame,currentRef,e);
				action.processAction();
			}catch(Exception exception){
				KCatalogException.logIfDebugTrue(exception);
			}
		}
	}
}
