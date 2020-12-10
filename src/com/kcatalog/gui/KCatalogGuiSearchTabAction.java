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


import com.kcatalog.common.KCatalogDto;
import com.kcatalog.fileutils.FileManager;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.applogic.KCatalogUpdateMp3InfoDto;
import com.kcatalog.common.KCatalogCommonDto;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSearch;

import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;

public class KCatalogGuiSearchTabAction {
	
	private boolean isExactMatch = false;
	private String operationType = "";
	KCatalogGui mainFrame = null;
	Mp3FileDto searchMp3FileDto = null;
	
	ActionEvent event = null;
	KCatalogGuiSearchTab searchTabRef = null;
	static KCatalogXMLSchemaSearch fs = null;
	
	KCatalogGuiSearchTabAction(KCatalogGui mainFrame,KCatalogGuiSearchTab searchTabRef,	
										ActionEvent event){		
		
		this.event = event;
		this.searchTabRef = searchTabRef;
		this.mainFrame = mainFrame;
	}
	
	public void processAction(){
		initializeFields();
		String buttonName = ((JButton)(event.getSource())).getName();
		if("searchButton".equals(buttonName)){
			performSearchAndUpdateTable();
		
		}
		if("nextButton".equals(buttonName)){
			getSearchResults(getNextSearchResultsPageNo());
		}
		
		if("prevButton".equals(buttonName)){
			getSearchResults(getPrevSearchResultsPageNo());
		}
		
		if("alternateButton".equals(buttonName)){
			getSearchResults(getCurrentSearchResultsPageNo());
		}
		
		if("selectAllButton".equals(buttonName)){
			selectAll(true);
		}
		if("deselectAllButton".equals(buttonName)){
			selectAll(false);
		}
		if("playButton".equals(buttonName)){
			playSelectedFiles();
		}		
		if("resetButton".equals(buttonName)){
			resetSearchValueTable();
		}		
		//searchTabRef.searchValueTable.validate();
		searchTabRef.searchValueTable.updateUI();
	}
	
	private void resetSearchValueTable(){
		JTable tb = searchTabRef.searchValueTable;
		for(int i=0;i<tb.getRowCount();i++){
			for(int j =0;j<tb.getColumnCount();j++){
				tb.setValueAt("",i,j);
			}
		}
	}
	
	private void playSelectedFiles(){
		ArrayList selectedFilePathList = getSelectedFilePathList();
		String m3uFilePath = createPlayListFile(selectedFilePathList);
		playM3uList(m3uFilePath);
	}
	
	private void playM3uList(String m3uFilePath){
	/*	try{
			Runtime rm = Runtime.getRuntime();	
			String commandLine = KCatalogConfigOptions.getOptionValue(
					KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD);
			rm.exec(commandLine+" " +m3uFilePath);
		}catch(Exception e){
			e.printStackTrace();
			throw new KCatalogException("Cannot execute program");
		}*/
		KCatalogCommonUtility.playM3uList(m3uFilePath);
		
	}
	
	private String createPlayListFile(ArrayList selectedFilePathList){
		return KCatalogCommonUtility.generateM3uFile(selectedFilePathList);
	}
	
	private ArrayList getSelectedFilePathList(){
		JTable tb = searchTabRef.searchResultTable;
		boolean emptyTable = true;
		ArrayList list = new ArrayList();
		for(int i=0;i<tb.getRowCount();i++){
			if(!isRowEmpty(tb,i,1)){
				if( ((JCheckBox)tb.getValueAt(i,0)).isSelected()){
					String fileName = (String)tb.getValueAt(i,1);
					String dirPath = (String)tb.getValueAt(i,2);
					String filePath = dirPath + File.separator + fileName;								
					list.add(filePath);
					emptyTable = false;
				}
			}
		}
		if(emptyTable){
			throw new KCatalogException("No files to play");
		}
		return list;
	}
	
	
	private void selectAll(boolean isSelected){
		JTable tb = searchTabRef.searchResultTable;
		boolean emptyTable = true;
		for(int i=0;i<tb.getRowCount();i++){
			if(!isRowEmpty(tb,i,1)){
				JCheckBox check = (JCheckBox)tb.getValueAt(i,0);
				check.setSelected(isSelected);
				emptyTable = false;
			}
		}
		if(emptyTable){
			throw new KCatalogException("Table is Empty");
		}
	}
	
	private int getPrevSearchResultsPageNo(){
	
		JTextField pageText = searchTabRef.pageNoText;
		
		String pageNoStr = pageText.getText();
		int currPageNo = 0;
		try{
			currPageNo = Integer.valueOf(pageNoStr).intValue();
		}catch(Exception e){
			throw new KCatalogException("Invalid page number");
		}
		int prevPageNo = currPageNo - 1;					
		return prevPageNo;
	}
	
	private int getNextSearchResultsPageNo(){
	
		JTextField pageText = searchTabRef.pageNoText;
		
		String pageNoStr = pageText.getText();
		int currPageNo = 0;
		try{
			currPageNo = Integer.valueOf(pageNoStr).intValue();
		}catch(Exception e){
			throw new KCatalogException("Invalid page number");
		}
		int nextPageNo = currPageNo + 1;					
		return nextPageNo;
	}
	
	private int getCurrentSearchResultsPageNo(){
	
		JTextField pageText = searchTabRef.pageNoText;
		
		String pageNoStr = pageText.getText();
		int currPageNo = 0;
		try{
			currPageNo = Integer.valueOf(pageNoStr).intValue();
		}catch(Exception e){
			throw new KCatalogException("Invalid page number");
		}
		return currPageNo;							
	}
	
	private void getSearchResults(int pageNo){			
		if(!fs.isPageNoValid(pageNo)){
			throw new KCatalogException("No more files left");
		}
		ArrayList mp3ResultList = fs.getSearchResult(pageNo);	
		if(mp3ResultList.size()==0){
			throw new KCatalogException("No results found for this search criteria");
		}
		clearResultTable();
		setDataInResultTable(mp3ResultList);
		updateRelatedFields(pageNo);
		
	}
	
	private void performSearchAndUpdateTable(){
		
		//JOptionPane.showMessageDialog(mainFrame,"Helo");			
		setSearchMp3FileDto();
		ArrayList mp3ResultList = performSearch();
		if(mp3ResultList.size()==0){
			throw new KCatalogException("No results found for this search criteria");
		}
		clearResultTable();
		setDataInResultTable(mp3ResultList);		
		updateRelatedFields(1);
	}
	
	private void updateRelatedFields(int pageNo){
		searchTabRef.displayingTextOf.setText("out of " + String.valueOf(fs.getMaxPageNo()));
		searchTabRef.pageNoText.setText(String.valueOf(pageNo));
	}
	
	private void setDataInResultTable(ArrayList mp3ResultList){
		
		for(int i=0;i<mp3ResultList.size();i++){
			Mp3FileDto mp3FileDto = (Mp3FileDto)mp3ResultList.get(i);
			setResultRowFromDto(mp3FileDto,i);
			
		}		
	}
	
	private void clearResultTable(){
		JTable tb = searchTabRef.searchResultTable;
		for(int i =0;i<tb.getRowCount();i++){
			((JCheckBox)tb.getValueAt(i,0)).setSelected(false);
			for(int j=1;j<tb.getColumnCount();j++){
				tb.setValueAt("",i,j);
			}
		}
	}
	private void setResultRowFromDto(Mp3FileDto mp3FileDto,
											int rownum){
		JTable tb = searchTabRef.searchResultTable;
		tb.setValueAt(mp3FileDto.getFileName(),rownum,1);
		tb.setValueAt(mp3FileDto.getFileLocation(),rownum,2);
		tb.setValueAt(mp3FileDto.getArtist(),rownum,3);
		tb.setValueAt(mp3FileDto.getAlbum(),rownum,4);
		tb.setValueAt(mp3FileDto.getGenre(),rownum,5);
		tb.setValueAt(mp3FileDto.getTitle(),rownum,6);
		tb.setValueAt(mp3FileDto.getYear(),rownum,7);
		tb.setValueAt(mp3FileDto.getComment(),rownum,8);
		
		
	}
	private ArrayList performSearch(){
		if(fs != null ){
			fs.close();
		}
		fs = KCatalogXMLSchemaSearch.newInstance(
											searchMp3FileDto,operationType,isExactMatch,false);													
		
		ArrayList rs = fs.getSearchResult(1);
		
		return	rs;
	}
	
	private void initializeFields(){
		if(searchTabRef.isExactRadio.isSelected()){
			isExactMatch = true;
		}
		if(searchTabRef.isAndRadio.isSelected()){
			operationType = KCatalogConstants.SEARCH_OPERATION_TYPE_AND;			
		}else{
			operationType = KCatalogConstants.SEARCH_OPERATION_TYPE_OR;
		}	
						
	
		
	}
	
	private boolean isRowEmpty(JTable tb,int rowNum,int startCol){
		boolean emptyFlag = true;
		for(int i=startCol;i<tb.getColumnCount();i++){
			if(!"".equals((((String)tb.getValueAt(rowNum,i)).trim())) ){
				emptyFlag = false;
				break;
			}
		}
		return emptyFlag;
	}
	
	private void setSearchMp3FileDto(){
		searchMp3FileDto = new Mp3FileDto();
		JTable tb = searchTabRef.searchValueTable;
		boolean emptyFlag = true;
		emptyFlag = isRowEmpty(tb,0,0);
		if(emptyFlag){
			//KCatalogStatusManager.addToErrorMap("Cannot search without a Search Criteria",KCatalogStatusManager.STATUS_ERROR);
			throw new KCatalogException("Cannot search without a Search Criteria");
				
		}
		searchMp3FileDto.setFileName(((String)tb.getValueAt(0,0)).trim());
		searchMp3FileDto.setFileLocation(((String)tb.getValueAt(0,1)).trim());
		searchMp3FileDto.setArtist(((String)tb.getValueAt(0,2)).trim());
		searchMp3FileDto.setAlbum(((String)tb.getValueAt(0,3)).trim());
		searchMp3FileDto.setGenre(((String)tb.getValueAt(0,4)).trim());
		searchMp3FileDto.setTitle(((String)tb.getValueAt(0,5)).trim());
		searchMp3FileDto.setYear(((String)tb.getValueAt(0,6)).trim());
		searchMp3FileDto.setComment(((String)tb.getValueAt(0,7)).trim());
	
		
	}
}
