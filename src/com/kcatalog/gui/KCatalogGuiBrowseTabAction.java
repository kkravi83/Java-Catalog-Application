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
import javax.swing.event.*;

import javax.swing.border.LineBorder;

import com.kcatalog.gui.KCatalogGuiSearchValueTableModel;
import com.kcatalog.gui.KCatalogGuiMp3TableRenderer;
import com.kcatalog.gui.KCatalogGuiSearchTab;
import com.kcatalog.gui.KCatalogGui;
import com.kcatalog.gui.KCatalogGuiBrowseTab;
import com.kcatalog.gui.KCatalogGuiBrowseTableModel;
import com.kcatalog.gui.KCatalogGuiProperties;
import com.kcatalog.gui.KCatalogProgressIndicatorDlg;

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
import com.kcatalog.xmlschema.KCatalogXMLSchemaFileSearch;
import com.kcatalog.xmlschema.KCatalogXMLSchemaLookup;
import com.kcatalog.xmlschema.KCatalogXMLSchemaLocationMapping;
import com.kcatalog.xmlschema.KCatalogXMLSchemaMp3AttributeLookUp;
import com.kcatalog.xmlschema.KCatalogXMLSchemaSynchronizeData;
import com.kcatalog.fileutils.M3UPlayListFile;
import com.kcatalog.fileutils.M3UPlayListManager;
import javax.swing.tree.*;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;

public class KCatalogGuiBrowseTabAction {
	
	KCatalogGui mainFrame = null;
	
	MouseEvent mouseEvent = null;
	ActionEvent actionEvent = null;
	TreeSelectionEvent treeSelectionEvent = null;
	
	KCatalogGuiBrowseTab browseTabRef = null;
	
	static KCatalogXMLSchemaSearch fs = null;
	private String searchString = "";
	private boolean isRecursive = false;
	private String parentLocation = "";
	private String searchSelection = "";
	private HashMap searchResultMap = null;
	
	KCatalogGuiBrowseTabAction(KCatalogGui mainFrame,KCatalogGuiBrowseTab browseTabRef,	
						TreeSelectionEvent treeSelectionEvent){		
		
		this.treeSelectionEvent = treeSelectionEvent;
		this.browseTabRef = browseTabRef;
		this.mainFrame = mainFrame;
	}
	
	KCatalogGuiBrowseTabAction(KCatalogGui mainFrame,KCatalogGuiBrowseTab browseTabRef,	
								MouseEvent mouseEvent){		
		
		this.mouseEvent = mouseEvent;
		this.browseTabRef = browseTabRef;
		this.mainFrame = mainFrame;
	}
	
	KCatalogGuiBrowseTabAction(KCatalogGui mainFrame,KCatalogGuiBrowseTab browseTabRef,	
								ActionEvent actionEvent){
										
		this.actionEvent = actionEvent;
		this.browseTabRef = browseTabRef;
		this.mainFrame = mainFrame;
	}
	
	public void processPlayListAction(){
		if(actionEvent.getSource().getClass().equals(new JMenuItem().getClass())){
			addSelectedFilesFromTableToPlayList();
		}
	}
	
	public void processPlayListDirAction(){
		if(actionEvent.getSource().getClass().equals(new JMenuItem().getClass())){
			addDirContentsToPlayList();
		}
	}
	
	private void addDirContentsToPlayList(){
		JMenuItem menuItem = (JMenuItem)actionEvent.getSource();
		boolean recursive = false;
		if(browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){
			int stat = JOptionPane.showConfirmDialog(mainFrame,
							"Add Subfolders also?",		
							"KCatalog.." ,
							JOptionPane.YES_NO_OPTION);
			recursive = 
				(stat == JOptionPane.NO_OPTION)?false:true;
				
				
		}
		ArrayList mp3DtoList = getMp3FileDtoListForSelectedComponent(recursive);
		String playListName = ((JMenuItem)actionEvent.getSource()).getName().trim();
		M3UPlayListManager m3uPlayListManager = M3UPlayListManager.getInstance();
		m3uPlayListManager.addToPlayList(playListName,mp3DtoList);
		JOptionPane.showMessageDialog(mainFrame,
					"Added to playlist successfully",
							"KCatalog..",
								JOptionPane.INFORMATION_MESSAGE);
	}
	
	private ArrayList getMp3FileDtoListForSelectedComponent(boolean recursive){
		ArrayList list = null;
		if(browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){			
			TreePath [] tp = browseTabRef.locationTree.getSelectionPaths();
			if(!recursive){
				list = getMp3ListForFolder(tp);
			}else{
				list = getMp3DtoListRecursivelyForSelectedLoc();
			}
		}else{
			TreePath [] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();
			if(browseTabRef.BROWSE_PLAYLIST.equals(browseTabRef.browseMode)){				
				list = getMp3ListForPlayList(tp);
			}else{
				list = getMp3ListForMp3Tree(tp);
			}
		}
		return list;
	}
	
	private void addSelectedFilesFromTableToPlayList(){
		JMenuItem menuItem = (JMenuItem)actionEvent.getSource();
		ArrayList mp3DtoList = getSelectedMp3DtoListinTable();
		String playListName = ((JMenuItem)actionEvent.getSource()).getName().trim();
		M3UPlayListManager m3uPlayListManager = M3UPlayListManager.getInstance();		
		m3uPlayListManager.addToPlayList(playListName,mp3DtoList);
		JOptionPane.showMessageDialog(mainFrame,
					"Added to playlist successfully",
							"KCatalog..",
								JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	public void processTreeSelectionAction(){
		if(treeSelectionEvent.getSource().getClass().equals(
							new JTree().getClass())){
			JTree tr = (JTree)treeSelectionEvent.getSource();
			if("locationTree".equals(tr.getName())){
				locationTreeSelected();
			}else{
				mp3TreeSelected();
			}
		}			
	}
	
	private void mp3TreeSelected(){	
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		ArrayList result = null;					
		TreePath[] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();		
		if(null == tp){
			return;
		}    	
		if( browseTabRef.BROWSE_PLAYLIST.equals(browseTabRef.browseMode)){			
			result = getMp3ListForPlayList(tp);
		}else{
			result = getMp3ListForMp3Tree(tp);		
		}
		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		clearResultTable();		
		
		setDataInResultTable(result);		
		
	}
	
	private ArrayList getMp3ListForPlayList(TreePath []tp){
		ArrayList result = new ArrayList();
		if( null == tp || 0 == tp.length ){
			return result;
		}		
		String playListName = tp[0].getLastPathComponent().toString();
		result = M3UPlayListManager.getInstance().getMp3DtoFromPlaylist(playListName);
		return result;
		
	}
	
	private ArrayList getMp3ListForMp3Tree(TreePath[] tp){
		ArrayList result = new ArrayList();
		if( null == tp || 0 == tp.length ){
			return result;
		}
		
		String selection = tp[0].getLastPathComponent().toString();						
		Mp3FileDto dto = this.getSearchDtoForMp3Tree(browseTabRef.browseMode,selection.trim());
		KCatalogXMLSchemaSearch.close();
		KCatalogXMLSchemaSearch ksearch  = 
						KCatalogXMLSchemaSearch.newInstanceWithoutCountCheck(dto,
										KCatalogConstants.SEARCH_OPERATION_TYPE_AND,
										true,
										true);
		int i =1;
		if(null == ksearch){
			return result;
		}
	
		//while(ksearch.isPageNoValid(i)){
		String searchAttribute = this.getSearchAttributeMp3Tree(browseTabRef.browseMode);
		result.addAll(ksearch.getMatchingMp3DtoListBySearchingAllXMLs(searchAttribute,selection.trim()));
							
		//	i++;
		//}		
		ksearch.close();	
		return result;
	}
	
	public void processMouseClickAction(){
		if(mouseEvent.getSource().getClass().equals(
							new JTableHeader().getClass())){
			tableHeaderClicked();
		}
		if(mouseEvent.getSource().getClass().equals(
							new JTree().getClass())){
			JTree tree = (JTree)mouseEvent.getSource();
			String name = tree.getName();
			
			treeClicked();			
		}	
		if(mouseEvent.getSource().getClass().equals(
							new JTable().getClass())){
			mp3ListTableClicked();
		}	
	}
	
	
	
	private void mp3ListTableClicked(){
		
		if(mouseEvent.getButton() == MouseEvent.BUTTON1){
			leftButtonClickTable();
		}
		if(mouseEvent.getButton() == MouseEvent.BUTTON3){
			rightButtonClickTable();
		}								
	}
	
	private void tableHeaderClicked(){
		if(mouseEvent.getButton() == MouseEvent.BUTTON1){
			leftButtonClickTableHeader();
		}	
	}
	
	private void leftButtonClickTableHeader(){
		ArrayList mp3List = getMp3ListFromTable();
		Point clickPoint = mouseEvent.getPoint();
		int columnIndexAtClick = browseTabRef.mp3ListTable.columnAtPoint(clickPoint);
		sortListOnColumnIndex(mp3List,columnIndexAtClick);
	}
	
	private void sortListOnColumnIndex(ArrayList mp3List,int columnIndexAtClick){
		KCatalogGuiBrowseTableModel tm = (KCatalogGuiBrowseTableModel)browseTabRef.mp3ListTable.getModel();
		boolean isAscending = !tm.isAscendingData();
		Collections.sort(mp3List,new Mp3DtoComparator(columnIndexAtClick,isAscending));		
		setDataInResultTable(mp3List);
		KCatalogGuiBrowseTableModel newTm = 
			(KCatalogGuiBrowseTableModel)browseTabRef.mp3ListTable.getModel();
		newTm.setAscendingData(isAscending);
	}
	
	private ArrayList getMp3ListFromTable(){
		ArrayList list = new ArrayList();
		JTable tb = browseTabRef.mp3ListTable;
		int rowCount = tb.getRowCount();
		for(int i=0;i<rowCount;i++){
			Mp3FileDto dto = populateMp3DtoFromTableRow(i);
			list.add(dto);
		}
		return list;
	}
	
	public void processTablePopupMenuAction(){
		KCatalogProgressIndicatorDlg dlg =
				new KCatalogProgressIndicatorDlg(mainFrame,"KCatalog","Please wait.......");			
		String menuName = ((JMenuItem)actionEvent.getSource()).getName();
		//System.out.println(menuName);
		if("playMenu".equals(menuName)){			
		//	dlg.startDialog();
		//	dlg.startProgress();			
			playSelectedFiles();
		//	dlg.stopProgress();			
		}
		
		if("deleteFromPlayListMenu".equals(menuName)){
			deleteFromPlayList();
		}
		
		if("playListInfoMenu".equals(menuName)){
			showPlayListInfo();
		}
		
		if("synchronizeMenu".equals(menuName)){
			dlg.startProgress();
			dlg.startDialog();
			synchronizeFilesInTable();
			dlg.stopProgress();
		}
		if("deleteMenu".equals(menuName)){
			int stat = JOptionPane.showConfirmDialog(mainFrame,
				"Are you sure you want to delete these files from DB",
				"KCatalog..",
				JOptionPane.YES_NO_OPTION);
			if(stat == JOptionPane.NO_OPTION){
				return;
			}
			dlg.startProgress();
			dlg.startDialog();
			deleteSelectedFiles();
			dlg.stopProgress();
		}
		if("propertyMenu".equals(menuName)){
			dlg.startProgress();
			dlg.startDialog();
			showAdditionalInfoForTable();
			dlg.stopProgress();
		}
		if("openContainingFolderMenu".equals(menuName)){
			openContainingFolderMenu();	
		}
	}
	
	private void showPlayListInfo(){
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		JTable mp3Table = browseTabRef.mp3ListTable;				
		ArrayList mp3DtoList = getSelectedMp3DtoListinTable();		
		if(mp3DtoList.size() == 0){
			return;
		}			
		M3UPlayListManager playListManager = 
							M3UPlayListManager.getInstance();
		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		JOptionPane.showMessageDialog(mainFrame,
					playListManager.getPlaylistInfoForMp3((Mp3FileDto)mp3DtoList.get(0)),
						"KCatalog..",
						JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void deleteFromPlayList(){
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
		JTable mp3Table = browseTabRef.mp3ListTable;						
								
		if(browseTabRef.BROWSE_PLAYLIST.
				equals(browseTabRef.browseMode)){
			deleteFromPlayListInPlayListMode();
				
		}else{
			deleteFromPlayListGeneralMode();
		}																		
		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	private void deleteFromPlayListGeneralMode(){
		ArrayList mp3DtoList = getSelectedMp3DtoListinTable();		
		if(mp3DtoList.size() == 0){
			return;
		}	
		M3UPlayListManager playListManager = 
							M3UPlayListManager.getInstance();
		ArrayList playListNames = playListManager.searchPlaylistsForDto(mp3DtoList);						
		if(playListNames.size() > 0 ){
			int stat = JOptionPane.showConfirmDialog(mainFrame,
					"Are you sure you want to delete these files from playlist(s) -"
					+playListNames.toString(),
					"KCatalog.." ,
					JOptionPane.YES_NO_OPTION);
				if(stat == JOptionPane.NO_OPTION){
					return;
				}
		}
		deleteFromPlayLists(playListNames,mp3DtoList);
		String message = "";
		if(0 == playListNames.size()){
			message = "File(s) doesnt belong any of the playlists";
		}else{
			message = "Successfully deleted from playlist(s) - "
				+ playListNames.toString();
		}
		JOptionPane.showMessageDialog(mainFrame,
					message,
					"KCatalog..",
					JOptionPane.INFORMATION_MESSAGE);	
	}
	
	private void deleteFromPlayListInPlayListMode(){
		ArrayList mp3DtoList = getSelectedMp3DtoListinTable();		
		
		int stat = JOptionPane.showConfirmDialog(mainFrame,
				"Are you sure you want to delete these file(s) from playlist",				
				"KCatalog.." ,
				JOptionPane.YES_NO_OPTION);
		if(stat == JOptionPane.NO_OPTION){
			return;
		}		
		if(mp3DtoList.size() == 0){
			return;
		}
		TreePath[] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();		
		ArrayList result = null;					
		if(null == tp){
			return;
		}    				
		String playListName = tp[0].getLastPathComponent().toString().trim();		
		ArrayList data = new ArrayList();
		data.add(playListName);
		deleteFromPlayLists(data,mp3DtoList);
		result = getMp3ListForPlayList(tp);			
		clearResultTable();				
		setDataInResultTable(result);
		JOptionPane.showMessageDialog(mainFrame,
				"Successfully deleted from playlist",
					"KCatalog..",
					JOptionPane.INFORMATION_MESSAGE);	
	}
	
	private void deleteFromPlayLists(ArrayList playLists,
						ArrayList mp3DtoList){
		M3UPlayListManager playListManager = 
							M3UPlayListManager.getInstance();
		playListManager.deleteFromPlayLists(playLists,mp3DtoList);
	}
	
	private void openContainingFolderMenu(){
		JTable mp3Table = browseTabRef.mp3ListTable;		
		int[] selectedIndex = mp3Table.getSelectedRows();
		ArrayList mp3PathList = getMp3FilePathListFromTable(selectedIndex);
		if(mp3PathList.size() == 0){
			return;
		}		
		Iterator iterator = mp3PathList.iterator();		
		while(iterator.hasNext()){
			String filePath = (String)iterator.next();
			String dir = filePath.substring(0,
				filePath.lastIndexOf(KCatalogConfigOptions.getSeparator()));
			String command =                                                          
			     KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER);
			try{
			KCatalogCommonUtility.executeProgram(command,new Object[]{
				KCatalogCommonUtility.formatURLForBrowser(new File(dir).getAbsolutePath())});
			}catch(Exception e){
				e.printStackTrace();
			}
		}			
	}
	
	private void showAdditionalInfoForTable(){
		ArrayList list = getSelectedMp3DtoListinTable();
		KCatalogGuiProperties.showAdditionalInfoForMp3Files(
									mainFrame,list);
		
	}
	
	public void processTreePopupMenuAction(){
		KCatalogProgressIndicatorDlg dlg =
				new KCatalogProgressIndicatorDlg(mainFrame,"KCatalog","Please wait.......");			
		String menuName = ((JMenuItem)actionEvent.getSource()).getName();
		if(!isTreeOperationPermitted(menuName)){
			showTreeOperationNotPermitted();
			return;
		}
		if("playDirMenu".equals(menuName)){
			boolean recursive = false;
			if(browseTabRef.BROWSE_LOCATION.equals(
							browseTabRef.browseMode)){
				int stat = JOptionPane.showConfirmDialog(mainFrame,
							"Play subfolders also?",
							"KCatalog",
							JOptionPane.YES_NO_OPTION);
				recursive =
						(JOptionPane.NO_OPTION == stat)?false:true;
			}
			dlg.startProgress();
			dlg.startDialog();
			playFilesInFolder(recursive);
			dlg.stopProgress();
		}
		
		if("synchronizeDirMenu".equals(menuName)){
			dlg.startProgress();
			dlg.startDialog();
			synchronizeFolder();
			dlg.stopProgress();
		}
		
		if("deleteDirMenu".equals(menuName)){
			
			int stat = JOptionPane.showConfirmDialog(mainFrame,
				"Are you sure you want to delete this folder from DB",
				"KCatalog..",
				JOptionPane.YES_NO_OPTION);
			if(stat == JOptionPane.NO_OPTION){
				return;
			}
			dlg.startProgress();
			dlg.startDialog();
			deleteFolder();
			dlg.stopProgress();
		}
		if("propertyDirMenu".equals(menuName)){
			dlg.startProgress();
			dlg.startDialog();
			showInfoForFolder();
			dlg.stopProgress();
		}
		
		if("mapLocationMenu".equals(menuName)){
			mapLocation();			
		}
		
		if("openDirMenu".equals(menuName)){
			openDirMenu();
		}
	}
	
	private boolean isTreeOperationPermitted(String menuName){
		JTree tr = null;
		if( browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){
			tr = browseTabRef.locationTree;
		}else{		
			tr = browseTabRef.mp3AttributeTree;
		}	
		TreePath tp = tr.getSelectionPath();
		String location = tp.getLastPathComponent().toString().trim();	
		boolean res = true;
		if(KCatalogGuiTreeModel.ROOT_STRING.equals(location)){
			res = false;
		}else{
			if(KCatalogGuiTreeModel.isComment(location) &&
				"mapLocationMenu".equals(menuName)){
				res = false;
			}
		}
		return res;
	}
	
	private void showTreeOperationNotPermitted(){
		JOptionPane.showMessageDialog(mainFrame,
					"Operation not supported",
						"KCatalog..",
						JOptionPane.ERROR_MESSAGE);
	}
	
	private void mapLocation(){
		JTree locationTree = browseTabRef.locationTree;
		TreePath tp = locationTree.getSelectionPath();
		String location = tp.getLastPathComponent().toString().trim();	
		location = KCatalogCommonUtility.removeCommentIfPresent(location);
			KCatalogXMLSchemaLocationMapping mapLoc = 
					KCatalogXMLSchemaLocationMapping.getInstance();					
		String message = "Are you sure you want to map this location to another one ";				
		if(null != mapLoc.contains(location)){
			message = "This location has already been scheduled for mapping. Do you want to change it ?";
							
		}
		int stat = JOptionPane.showConfirmDialog(mainFrame,
				message,
				"KCatalog..",
				JOptionPane.YES_NO_OPTION);
		if(stat == JOptionPane.NO_OPTION){
				return;
		}
		JFileChooser fc = new JFileChooser(new File(location));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		stat = fc.showOpenDialog(mainFrame);
		if(JFileChooser.APPROVE_OPTION == stat){
			String newLocation = fc.getSelectedFile().getAbsolutePath().trim();		
			mapLoc.addToLocationMap(location,newLocation);
			JOptionPane.showMessageDialog(mainFrame,
					"Please save the changes by selecting File -> Save Mapping",
						"KCatalog..",
						JOptionPane.INFORMATION_MESSAGE);
			mainFrame.menuSaveMapping.setEnabled(true);
		}
		
	}
		
	
	
	private void openDirMenu(){
		String path = getFolderPathForOpenCommand();
		if( "".equals(path)){
			return;
		}
		//System.out.println("openDirMenu " + path);
		String command = 
				KCatalogConfigOptions.getOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER);
		try{
		/*KCatalogCommonUtility.executeProgram(command + " " +
		KCatalogCommonUtility.formatURLForBrowser(new File(path).getAbsolutePath()));	*/
		KCatalogCommonUtility.executeProgram(command,new Object[]{
				KCatalogCommonUtility.formatURLForBrowser(new File(path).toURL().toExternalForm())});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String getFolderPathForOpenCommand(){
		String folderPath = "";
		if(browseTabRef.browseMode.equals(
					browseTabRef.BROWSE_LOCATION)){
			JTree locationTree = browseTabRef.locationTree;		
			TreePath tp = locationTree.getSelectionPath();
			String path = tp.getLastPathComponent().toString();
			path = KCatalogCommonUtility.removeCommentIfPresent(path);									
			String root = 
				(String)((KCatalogGuiTreeModel)locationTree.getModel()).getRoot();		
			if(root.equals(path.trim())){
				folderPath = "";
			}		
			folderPath = path;
		}else{
			if(browseTabRef.browseMode.equals(
					browseTabRef.BROWSE_PLAYLIST)){			
			folderPath = KCatalogConfigOptions.getOptionValue(
							KCatalogConstants.CONFIG_PLAYLIST_LOCATION);			
			}
		}
		return folderPath.trim();
	}
	
	private void showInfoForFolder(){
		JTree tr = browseTabRef.locationTree;
		String folder = (String)tr.getLastSelectedPathComponent();
		KCatalogGuiProperties.showAdditionalInfoForFolder(mainFrame,folder,new ArrayList());
	}
	
	private void deleteFolder(){
	
		if(browseTabRef.BROWSE_LOCATION.equals(
							browseTabRef.browseMode)){
			deleteSelectedFolder();
		}else{
			if(browseTabRef.BROWSE_PLAYLIST.equals(
							browseTabRef.browseMode)){
				deleteSelectedPlaylist();
			}else{
				deleteSelectedAttributeFiles();
			}
		}
						
		KCatalogStatusManager.showMessages(mainFrame,"Database succesfully updated");						
	}
	
	private void deleteSelectedAttributeFiles(){
		JTree rt = browseTabRef.mp3AttributeTree;
		TreePath[] tp = rt.getSelectionPaths();		
		ArrayList mp3DtoList = getMp3ListForMp3Tree(tp);
		KCatalogXMLSchemaSynchronizeData sd = 
					new KCatalogXMLSchemaSynchronizeData();
		sd.deleteMp3Files(mp3DtoList);
		clearResultTable();
		browseTabRef.enableTreeMp3Tree(browseTabRef.browseMode);
		
	}
	
	private void deleteSelectedPlaylist(){
		JTree rt = browseTabRef.mp3AttributeTree;
		TreePath[] tp = rt.getSelectionPaths();
		String playlistName = (String)tp[0].getLastPathComponent();
		M3UPlayListManager manager = 
							M3UPlayListManager.getInstance();	
		manager.deletePlayList(playlistName);
		browseTabRef.addPlayListsToMenu();
		clearResultTable();
		browseTabRef.enableTreeMp3Tree(browseTabRef.BROWSE_PLAYLIST);
	}
	
	private void deleteSelectedFolder(){
		JTree rt = browseTabRef.locationTree;
		TreePath[] tp = rt.getSelectionPaths();
		String parent = (String)tp[0].getLastPathComponent();
	
	//	String comment = getCommentFromLabelString(parent);
		recursivelyDeleteFiles(rt,parent);
		String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(parent);
			
		deleteLocationsFromLookUp(rt,parent);
		KCatalogGuiTreeModel dm = (KCatalogGuiTreeModel)rt.getModel();
		dm.resetMap();
		/*rt.revalidate();*/
		rt.updateUI();	
	}
	
	private void deleteLocationsFromLookUp(JTree locTree,String parent){		
		String comment = getCommentFromLabelString(parent);
		if( "".equals(comment)){
			KCatalogXMLSchemaLookup.deleteLocationsRecursively(parent);
		}else{
			for(int i=0;i<locTree.getModel().getChildCount(parent);
									i++){
				String child = (String)locTree.getModel().getChild(parent,i);
				String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(child);
				KCatalogXMLSchemaLookup.deleteLocationsRecursively(fileLoc);
			}	
		}
	}
	
	private ArrayList getMp3DtoListRecursivelyForSelectedLoc(){
		JTree locTree = browseTabRef.locationTree;
		TreePath[] tp = locTree.getSelectionPaths();
		String parent = (String)tp[0].getLastPathComponent();
		ArrayList list = new ArrayList();
		getMp3DtoListRecursively(list,locTree,parent);
		return list;
	}
	
	private void getMp3DtoListRecursively(ArrayList result,
							JTree locTree,String parent){
		String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(parent);
		String comment = getCommentFromLabelString(parent);		
		ArrayList mp3List = performSearch(fileLoc,comment);			
		result.addAll(mp3List);
		for(int i=0;i<locTree.getModel().getChildCount(parent);
								i++){
			String child = (String)locTree.getModel().getChild(parent,i);
			getMp3DtoListRecursively(result,locTree,child);
		}
				
	}
	
	private void recursivelyDeleteFiles(JTree locTree,String parent){		
		String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(parent);
		String comment = getCommentFromLabelString(parent);		
		ArrayList mp3List = performSearch(fileLoc,comment);				
		KCatalogXMLSchemaSynchronizeData sd = 
					new KCatalogXMLSchemaSynchronizeData();
		sd.deleteMp3Files(mp3List);
		for(int i=0;i<locTree.getModel().getChildCount(parent);
								i++){
			String child = (String)locTree.getModel().getChild(parent,i);
			recursivelyDeleteFiles(locTree,child);
		}
	}
	
	private void synchronizeFolder(){
		TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();
		String parent = (String)tp[0].getLastPathComponent();
		String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(parent);											
		File f = new File(fileLoc);
		//System.out.println(parent);
		if(isDescendentOfPermanentLoc(tp[0],parent)){
			throw new KCatalogException("Operation not supported");
		}
	
		if( !f.exists() || !f.isDirectory() ){
			recursivelySynchronizeFiles(browseTabRef.locationTree,parent);
			KCatalogXMLSchemaLookup.deleteLocationsRecursively(fileLoc);	
		}else{
			synchronizeFilesInFolder();
		}		
		JTree rt = browseTabRef.locationTree;
		KCatalogGuiTreeModel dm = (KCatalogGuiTreeModel)rt.getModel();
		dm.resetMap();
		//rt.updateUI();
		rt.validate();		
		KCatalogStatusManager.showMessages(mainFrame,"Database succesfully updated");			
	}
	
	private boolean isDescendentOfPermanentLoc(TreePath tp,
												String parent){
		ArrayList permLocList = (ArrayList)KCatalogConfigOptions.getObjectOptionValue(
									KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
		for(int i=0;i<permLocList.size();i++){
			String permLoc = (String) permLocList.get(i);
			permLoc = KCatalogCommonUtility.adjustForSeparator(permLoc,parent);
			parent = KCatalogCommonUtility.adjustForSeparator(parent,permLoc);
			if(parent.trim().startsWith(permLoc) ||
				permLoc.startsWith(parent)){
				return true;
			}
		}		
		return false;
	}
	private void recursivelySynchronizeFiles(JTree locTree,String parent){		
		String fileLoc = KCatalogCommonUtility.removeCommentIfPresent(parent);
		ArrayList mp3List = performSearch(fileLoc);				
		synchronizeFilesInTable(mp3List);
		for(int i=0;i<locTree.getModel().getChildCount(parent);
								i++){
			String child = (String)locTree.getModel().getChild(parent,i);
			recursivelySynchronizeFiles(locTree,child);
		}
	}
	
	private void synchronizeFilesInFolder(){
		TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();
		ArrayList mp3List = getMp3ListForFolder(tp);
		synchronizeFilesInTable(mp3List);		
	}
	
	private void playFilesInFolder(boolean recursive){
		
		ArrayList mp3List = null;
		if(browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){
			if(browseTabRef.locationTree.getSelectionCount() == 0){
				throw new KCatalogException("Please select a folder");
			}	
			mp3List = getMp3DtoListForPlayLocation(recursive);
		}else{
			if(browseTabRef.mp3AttributeTree.getSelectionCount() == 0){
				throw new KCatalogException("Please make a selection");
			}
			TreePath[] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();
			if(browseTabRef.BROWSE_PLAYLIST.equals(browseTabRef.browseMode)){
				mp3List = getMp3ListForPlayList(tp);
			}else{
				mp3List = getMp3ListForMp3Tree(tp);
			}			
		}			
		ArrayList mp3PathList = getMp3PathListFromDtoList(mp3List);
		if(mp3List.size() == 0){
			throw new KCatalogException("No files in folder to play");
		}
	//	M3UPlayListManager pmanager = 
	//							M3UPlayListManager.getInstance();
	//	pmanager.createDefaultPlaylist();
	//	pmanager.addToDefaultPlaylist(mp3List);
		String path = KCatalogCommonUtility.generateM3uFile(mp3PathList);
		KCatalogCommonUtility.playM3uList(path);
	}
	
	private ArrayList getMp3DtoListForPlayLocation(boolean recursive){
		ArrayList mp3List = null;
		TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();
		String parent = (String)tp[0].getLastPathComponent();		
		if(!recursive){
			mp3List = getMp3ListForFolder(tp);	
		}else{
			mp3List = getMp3DtoListRecursivelyForSelectedLoc();
		}
		return mp3List;
	}
	
	private ArrayList getMp3PathListFromDtoList(ArrayList dtoList){
		ArrayList list = new ArrayList();
		for(int i=0;i<dtoList.size();i++){
			Mp3FileDto dto = (Mp3FileDto)dtoList.get(i);
			String filePath = dto.getFileLocation() + 
								KCatalogConfigOptions.getSeparator() +
								dto.getFileName();
			
			list.add(filePath);
		}
		return list;		
	}
	
	private void deleteSelectedFiles(){	
		
		ArrayList mp3DtoListInTable = getSelectedMp3DtoListinTable();
		KCatalogXMLSchemaSynchronizeData sd = 
					new KCatalogXMLSchemaSynchronizeData();
		sd.deleteMp3Files(mp3DtoListInTable);
		TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();				
		setDataInTableFromTree();	
		//browseTabRef.mp3ListTable.updateUI();
		browseTabRef.mp3ListTable.invalidate();		
		browseTabRef.mp3ListTable.validate();		
		KCatalogStatusManager.showMessages(mainFrame,"Database succesfully updated");		}
	
	private void synchronizeFilesInTable(){
		ArrayList mp3DtoListInTable = getSelectedMp3DtoListinTable();
		synchronizeFilesInTable(mp3DtoListInTable);
		
		KCatalogStatusManager.showMessages(mainFrame,"Database succesfully updated");	
	}
	
	private void synchronizeFilesInTable(ArrayList mp3DtoListInTable){
		KCatalogXMLSchemaSynchronizeData sd = 
					new KCatalogXMLSchemaSynchronizeData();
		sd.synchronizeMp3Files(mp3DtoListInTable);
		TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();				
		setDataInTableFromTree();	
		//browseTabRef.mp3ListTable.updateUI();
		browseTabRef.mp3ListTable.invalidate();
		browseTabRef.mp3ListTable.validate();
	}
	
	private ArrayList getMp3ListInCurrentView(){
		JTable tb = browseTabRef.mp3ListTable;
		int rowCount = tb.getRowCount();
		ArrayList list = new ArrayList();
		if(rowCount == 0){
			return list;
		}				
		for(int i=0;i<rowCount;i++){
			list.add(populateMp3DtoFromTableRow(i));
		}		
		return list;
	}
	
	private ArrayList getSelectedMp3DtoListinTable(){
		JTable tb = browseTabRef.mp3ListTable;
		int selectedRowCount = tb.getSelectedRowCount();
		ArrayList list = new ArrayList();
		if(selectedRowCount == 0){
			return list;
		}
		int[] selectedRowIndex = tb.getSelectedRows();
		
		for(int i=0;i<selectedRowIndex.length;i++){
			list.add(populateMp3DtoFromTableRow(selectedRowIndex[i]));
		}
		
		return list;
	}
	
	private Mp3FileDto populateMp3DtoFromTableRow(int rownum){		
		JTable tb = browseTabRef.mp3ListTable;		
		Mp3FileDto mp3FileDto = new Mp3FileDto();
		mp3FileDto.setFileName((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_NAME));
		mp3FileDto.setFileLocation((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_LOCATION));
		mp3FileDto.setArtist((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_ARTIST));
		mp3FileDto.setAlbum((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_ALBUM));
		mp3FileDto.setGenre((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_GENRE));
		mp3FileDto.setTitle((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_TITLE));
		mp3FileDto.setDuration((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_DURATION));
		mp3FileDto.setYear((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_YEAR));
		mp3FileDto.setComment((String)tb.getModel().getValueAt(rownum,
				KCatalogGuiBrowseTableModel.INDEX_COMMENT));
		return mp3FileDto;
	}
	
	public void processSearchButtonAction(){
		KCatalogProgressIndicatorDlg dlg =
				new KCatalogProgressIndicatorDlg(mainFrame,"KCatalog","Please wait.......");			
		String buttonName = ((JButton)actionEvent.getSource()).getName();
		if("searchButton".equals(buttonName)){			
			dlg.startProgress();		
			dlg.startDialog();
			searchForMp3s();
			dlg.stopProgress();
		}
		if("clear".equals(buttonName)){
			clearSearchResult();
		}
	}

	private void clearSearchResult(){
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		browseTabRef.searchButton.setText("Search");
		browseTabRef.searchButton.setName("searchButton");
		browseTabRef.searchCheck.setSelected(false);
		JTree locationTree = browseTabRef.locationTree;
		TreePath[] tp = locationTree.getSelectionPaths();
		clearResultTable();
		setDataInTableFromTree();
	//	browseTabRef.mp3ListTable.updateUI();
		browseTabRef.mp3ListTable.invalidate();
		browseTabRef.mp3ListTable.validate();
		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
		
	private void searchForMp3s(){
		initializeFields();
		ArrayList result = null;
			
		if(browseTabRef.BROWSE_LOCATION.equals(
							browseTabRef.browseMode)){
			result = performSearchForLocationTree();	
		
		}else{
			result = performSearchForMp3Tree();
		}
			
		clearResultTable();		
		setDataInResultTable(result);
		setRelatedFields();
	//	dlg.stopProgress();				
	}
	
	private ArrayList performSearchForMp3Tree(){
		ArrayList result = null;
		if(browseTabRef.BROWSE_PLAYLIST.equals(
				browseTabRef.BROWSE_PLAYLIST) &&
					isRecursive){
			result = performRecursiveSearchOnPlaylists();
			
		}else{
			result = performNonRecursiveSearchForMp3Tree();
		}
		return result;
	}
	
	private ArrayList performRecursiveSearchOnPlaylists(){
		ArrayList result = new ArrayList();
		M3UPlayListManager playListManager = 
							M3UPlayListManager.getInstance();
		searchResultMap = playListManager.searchForSimilarMp3InPlaylists(getSearchDto());
		Iterator it = searchResultMap.keySet().iterator();
		while(it.hasNext()){
			ArrayList list = (ArrayList)searchResultMap.get(it.next());
			result.addAll(list);
		}
		return result;
	}
	
	private ArrayList performNonRecursiveSearchForMp3Tree(){
		ArrayList mp3InViewList = getMp3ListInCurrentView();
		KCatalogXMLSchemaFileSearch fs = 
						new KCatalogXMLSchemaFileSearch();
		ArrayList result = fs.getFilteredList(mp3InViewList,
										getSearchDto(),
										KCatalogConstants.SEARCH_OPERATION_TYPE_OR,
										false,0,9999);
		return result;
	}
	
	private ArrayList performSearchForLocationTree(){
		ArrayList result = new ArrayList();
		if(isRecursive){
			Mp3FileDto dto = getSearchDto();
			dto.setFileLocation(parentLocation);
			KCatalogXMLSchemaSearch ksearch  = 
							KCatalogXMLSchemaSearch.newInstance(dto,
											KCatalogConstants.SEARCH_OPERATION_TYPE_RECURSIVE_LOC,
											false,
											true);
			int i =1;
			result = new ArrayList();
			while(ksearch.isPageNoValid(i)){
				result.addAll(ksearch.getSearchResult(i));
				i++;
			}
			ksearch.close();
		}else{
			KCatalogXMLSchemaSearch ksearch = 
		 		KCatalogXMLSchemaSearch.newInstance();
			result = ksearch.searchMp3ForLocation(parentLocation,
														isRecursive,
														KCatalogConstants.SEARCH_OPERATION_TYPE_OR,
														getSearchDto(),
														false
														);		
			ksearch.close();
		}
		return result;		
	}
	
	private void setRelatedFields(){
		browseTabRef.searchButton.setText("Clear");
		browseTabRef.searchButton.setName("clear");
	}
	
	private String getSearchAttributeMp3Tree(String searchSelection){
		String searchAttribute = "";
		if(browseTabRef.BROWSE_TITLE.equals(searchSelection)){
			searchAttribute = 
					KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_TITLE;
		}
		if(browseTabRef.BROWSE_ARTIST.equals(searchSelection)){
			searchAttribute = 
					KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ARTIST;
		}
		if(browseTabRef.BROWSE_ALBUM.equals(searchSelection)){
			searchAttribute = 
					KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_ALBUM;
		}
		if(browseTabRef.BROWSE_COMMENT.equals(searchSelection)){
			searchAttribute = 
					KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_COMMENT;
		}
		if(browseTabRef.BROWSE_GENRE.equals(searchSelection)){
			searchAttribute = 
					KCatalogXMLSchemaMp3AttributeLookUp.LOOKUP_TYPE_GENRE;
		}
		return searchAttribute;
	}
	
	private Mp3FileDto getSearchDtoForMp3Tree(String searchSelection,
									String searchString){		
		Mp3FileDto dto = new Mp3FileDto();		
		if(browseTabRef.BROWSE_TITLE.equals(searchSelection)){
			dto.setTitle(searchString);
		}
		if(browseTabRef.BROWSE_ARTIST.equals(searchSelection)){
			dto.setArtist(searchString);
		}
		if(browseTabRef.BROWSE_ALBUM.equals(searchSelection)){
			dto.setAlbum(searchString);
		}
		if(browseTabRef.BROWSE_COMMENT.equals(searchSelection)){
			dto.setComment(searchString);
		}
		if(browseTabRef.BROWSE_GENRE.equals(searchSelection)){
			dto.setGenre(searchString);
		}
		return dto;	
	}
	
	private Mp3FileDto getSearchDto(){
		Mp3FileDto dto = new Mp3FileDto();
		if("All".equals(searchSelection) || 
						"File Name".equals(searchSelection)){
			dto.setFileName(searchString);
		}
		if("All".equals(searchSelection) || 
						"Title".equals(searchSelection)){
			dto.setTitle(searchString);
		}
		if("All".equals(searchSelection) || 
						"Artist".equals(searchSelection)){
			dto.setArtist(searchString);
		}
		if("All".equals(searchSelection) || 
						"Album".equals(searchSelection)){
			dto.setAlbum(searchString);
		}
		if("All".equals(searchSelection) || 
						"Comment".equals(searchSelection)){
			dto.setComment(searchString);
		}
		if("All".equals(searchSelection) || 
						"Genre".equals(searchSelection)){
			dto.setGenre(searchString);
		}
		return dto;	
	}
	
	private void initializeFields(){		
		searchString = browseTabRef.searchPattern.getText().trim();
		if("".equals(searchString)){
			throw new KCatalogException("Cannot search without a search string");
		}
		isRecursive = browseTabRef.searchCheck.isSelected();
		JTree tr = null;
		if(browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){
			tr = browseTabRef.locationTree;
		}else{
			tr = browseTabRef.mp3AttributeTree;
		}
		
		TreePath[] trp = tr.getSelectionPaths();
		if(trp == null ){
			throw new KCatalogException("Please make a selection");
		}
		if(trp != null){
			if(trp.length == 0 ){
							throw new KCatalogException("Please make a selection");
			}
			if(trp.length > 1 ){
						throw new KCatalogException("Please select only one field");
			}
			parentLocation = (String)trp[0].getLastPathComponent();		
			parentLocation = KCatalogCommonUtility.removeCommentIfPresent(parentLocation);
			if("Mp3 Database".equals(parentLocation.trim())){
				parentLocation = "";
			}
			searchSelection = (String)browseTabRef.searchCombo.getSelectedItem();
		}
	}
	
	private void playSelectedFiles(){
		JTable mp3Table = browseTabRef.mp3ListTable;
		
		int[] selectedIndex = mp3Table.getSelectedRows();
		ArrayList mp3PathList = getMp3FilePathListFromTable(selectedIndex);	
		if(mp3PathList.size() == 0){
			return;
		}
		String path = KCatalogCommonUtility.generateM3uFile(mp3PathList);
		KCatalogCommonUtility.playM3uList(path);
	}

	private ArrayList getMp3FilePathListFromTable(int[] index){
		JTable mp3Table = browseTabRef.mp3ListTable;
		ArrayList mp3PathList = new ArrayList();
		for(int i=0;i<index.length;i++){
			String mp3Path = (String)mp3Table.getModel().getValueAt(index[i],
									KCatalogGuiBrowseTableModel.INDEX_LOCATION)+
										 KCatalogConfigOptions.getSeparator() +
										(String)mp3Table.getModel().getValueAt(index[i],
										KCatalogGuiBrowseTableModel.INDEX_NAME);									
			if( !"".equals(mp3Path.trim())){
				mp3PathList.add(mp3Path);
			}				
		}
		return mp3PathList;
	}
	
	private String getFilePath(String fileLoc,
										String fileName){
		if( "".equals(fileLoc.trim()) ||
						"".equals(fileName.trim())){
				return "";
		}								
		return KCatalogCommonUtility.generateM3uFile(fileLoc + KCatalogConfigOptions.getSeparator()
										+ fileName);
	}
		
	private void rightButtonClickTable(){
		JTable mp3Table = (JTable)mouseEvent.getSource();
		int[] selectedIndex = mp3Table.getSelectedRows();
		if(selectedIndex.length == 0){
			return;
		}
		Point p = mouseEvent.getPoint();
		//browseTabRef.unCheckAllPopupMenu();
		
		browseTabRef.rightClickTableMenu.show(mp3Table,(int)p.getX(),
													(int)p.getY());		
		
	}
	
	private void leftButtonClickTable(){
		JTable mp3Table = (JTable)mouseEvent.getSource();
		int[] selectedIndex = mp3Table.getSelectedRows();
		
		if(mouseEvent.getClickCount() ==2 && selectedIndex.length == 1){
			
			String path = getFilePath((String)mp3Table.getModel().getValueAt(selectedIndex[0],
					KCatalogGuiBrowseTableModel.INDEX_LOCATION),			
								(String)mp3Table.getModel().getValueAt(selectedIndex[0],
					KCatalogGuiBrowseTableModel.INDEX_NAME));
			if( "".equals(path)){
				return;
			}			
			KCatalogCommonUtility.playM3uList(path);
		}
	}
	
	private void locationTreeSelected(){
		JTree locationTree = (JTree)treeSelectionEvent.getSource();
		TreePath[] tp = locationTree.getSelectionPaths();				
		setDataInTableFromTree();	
	}
	
	private void treeClicked(){		
	/*	if(mouseEvent.getButton() == MouseEvent.BUTTON1){
			leftButtonClickTree();
		}*/
		if(mouseEvent.getButton() == MouseEvent.BUTTON3){
			rightButtonClickTree();
		}
	}
	
	private void rightButtonClickTree(){
		JTree locationTree = (JTree)mouseEvent.getSource();
		if( locationTree.getSelectionCount() != 1){
			return;
		}
		Point p = mouseEvent.getPoint();
		//browseTabRef.unCheckAllPopupMenu();
		browseTabRef.rightClickTreeMenu.show(locationTree,(int)p.getX(),
													(int)p.getY());
	}
	
	private void leftButtonClickTree(){
		JTree locationTree = (JTree)mouseEvent.getSource();
		TreePath[] tp = locationTree.getSelectionPaths();
		
		
		setDataInTableFromTree();				
	}
	
	private void setDataInTableFromTree(){
		
		ArrayList mp3ResultList = null;
		if(browseTabRef.BROWSE_LOCATION.equals(browseTabRef.browseMode)){
			TreePath[] tp = browseTabRef.locationTree.getSelectionPaths();
			mp3ResultList = getMp3ListForFolder(tp);				
		}else{
			if(browseTabRef.BROWSE_PLAYLIST.equals(
						browseTabRef.browseMode)){
				TreePath[] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();
				mp3ResultList = getMp3ListForPlayList(tp);
			}else{
				TreePath[] tp = browseTabRef.mp3AttributeTree.getSelectionPaths();
				mp3ResultList = getMp3ListForMp3Tree(tp);				
			}
		}
		clearResultTable();
		if(mp3ResultList.size()==0){
			return;		
		}
		setDataInResultTable(mp3ResultList);				
	}
	
	
	
	private ArrayList getMp3ListForFolder(TreePath[] tp){
		ArrayList mp3ResultList = new ArrayList();
		if(tp !=null && tp.length == 1){
			String fileLoc = (String)tp[0].getLastPathComponent();					
			String comment = getCommentFromLabelString(fileLoc);
			fileLoc = KCatalogCommonUtility.removeCommentIfPresent(fileLoc);
			if(isPermanentLocation(fileLoc) && "".equals(comment)){
				return mp3ResultList;
			}			
			mp3ResultList = performSearch(fileLoc,comment);			
		}	
		return mp3ResultList;
	}
	
	private boolean isPermanentLocation(String fileLoc){
		JTree locationTree = this.browseTabRef.locationTree;
		KCatalogGuiTreeModel tmodel = (KCatalogGuiTreeModel)locationTree.getModel();
		return tmodel.isPermanentLocation(fileLoc);
	}
	
	private String getCommentFromLabelString(String fileLoc){
		int midIndex = fileLoc.indexOf(KCatalogGuiTreeModel.label);
		if(midIndex == -1){
			return "";
		}
		int labelLen = KCatalogGuiTreeModel.label.length();
		int labelEndIndex = midIndex + labelLen - 1;		
		int lastIndex = fileLoc.indexOf(KCatalogConfigOptions.getSeparator(),midIndex);
		if(lastIndex == -1){
			return fileLoc.substring(labelEndIndex+1,fileLoc.length());		
		}
		
		return fileLoc.substring(labelEndIndex+1,lastIndex);				
	}
	
	
	
	private void setDataInResultTable(ArrayList mp3ResultList){
		JTable tb = browseTabRef.mp3ListTable;
		Object[][] data = new Object[mp3ResultList.size()][9];
		tb.setModel(new KCatalogGuiBrowseTableModel(data));		
		for(int i=0;i<mp3ResultList.size();i++){									
			Mp3FileDto mp3FileDto = (Mp3FileDto)mp3ResultList.get(i);
			setResultRowFromDto(mp3FileDto,i);			
		}	
		try{
			//tb.updateUI();
			tb.invalidate();
			tb.validate();
		}catch(Exception e){
			//System.out.println("heher");
			e.printStackTrace();
		}
	}
	
	private void setResultRowFromDto(Mp3FileDto mp3FileDto,
											int rownum){
		
		JTable tb = browseTabRef.mp3ListTable;
		//System.out.println("inside set " + mp3FileDto.getFileName());
		tb.getModel().setValueAt(mp3FileDto.getFileName(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_NAME);
		tb.getModel().setValueAt(mp3FileDto.getFileLocation(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_LOCATION);
		tb.getModel().setValueAt(mp3FileDto.getArtist(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_ARTIST);
		tb.getModel().setValueAt(mp3FileDto.getAlbum(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_ALBUM);
		tb.getModel().setValueAt(mp3FileDto.getGenre(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_GENRE);
		tb.getModel().setValueAt(mp3FileDto.getTitle(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_TITLE);
		tb.getModel().setValueAt(mp3FileDto.getDuration(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_DURATION);
		tb.getModel().setValueAt(mp3FileDto.getYear(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_YEAR);
		tb.getModel().setValueAt(mp3FileDto.getComment(),rownum,
				KCatalogGuiBrowseTableModel.INDEX_COMMENT);				
		//System.out.println("table reloading");
		((KCatalogGuiBrowseTableModel)tb.getModel()).fireTableDataChanged();
	}
	
	public  void clearResultTable(){
		JTable tb = browseTabRef.mp3ListTable;
		for(int i =0;i<tb.getRowCount();i++){			
			for(int j=0;j<tb.getColumnCount();j++){
				tb.setValueAt("",i,j);
			}
		}
		//tb.updateUI();		
		tb.invalidate();
		tb.validate();
		tb.updateUI();		
	}
	
	private ArrayList performSearch(String fileLoc,String comment){
		KCatalogXMLSchemaSearch ksearch = 
				 KCatalogXMLSchemaSearch.newInstance();
		ArrayList result = ksearch.getMp3DtoListFromLocation(fileLoc,comment);
		ksearch.close();
		return result;
	}
	
	private ArrayList performSearch(String fileLoc){
		return performSearch(fileLoc,"");
	}
	
	private Mp3FileDto getMp3FileDto(String fileLoc){
		return KCatalogCommonUtility.getMp3FileDtoFromLoc(fileLoc);
	}
	
	class Mp3DtoComparator implements Comparator{
		Mp3FileDto dto1,dto2;
		int indexOfCompareColumn = 0;
		boolean isAscending = true;
		
		public Mp3DtoComparator(int indexOfCompareColumn,boolean isAscending){
			this.indexOfCompareColumn = indexOfCompareColumn;	
			this.isAscending = isAscending;
		}
		
		public int compare(Object o1,Object o2){
			dto1 = (Mp3FileDto) o1;
			dto2 = (Mp3FileDto) o2;
			int res = compare();
			res = (isAscending)? res : -res;
			return res;			
		}
		
		private int compare(){
			Object val1 = getColumnValue(dto1);
			Object val2 = getColumnValue(dto2);
			return compareValues(val1,val2);		
		}
		
		private int compareValues(Object val1,Object val2){
			int val = 0;
			if(null == val1 && null == val2){
				val = 0;
			}
			if(null == val1 ){
				val = 1;
			}
			if(null == val2 ){
				val = -1;
			}
			if(val1.getClass().equals(new String().getClass())){
				val = ((String)val1).compareTo((String)val2);
			}
			if(val1.getClass().equals(new Long(0).getClass())){
				val = ((Long)val1).compareTo((Long)val2);
			}
			return val;
		}
		
		private Object getColumnValue(Mp3FileDto dto){
			Object columnValue = null;
			if(KCatalogGuiBrowseTableModel.INDEX_YEAR == 	
									indexOfCompareColumn){
				try{
					columnValue = Long.valueOf(dto.getYear());
				}catch(Exception e){
					columnValue = Long.valueOf("0");
				}				
			}
			if(KCatalogGuiBrowseTableModel.INDEX_DURATION == 	
									indexOfCompareColumn){
				columnValue = dto.getDuration();		
			}
			if(KCatalogGuiBrowseTableModel.INDEX_NAME == 	
									indexOfCompareColumn){
				columnValue = dto.getFileName();	
			}
			if(KCatalogGuiBrowseTableModel.INDEX_LOCATION == 	
									indexOfCompareColumn){
				columnValue = dto.getFileLocation();	
			}	
			if(KCatalogGuiBrowseTableModel.INDEX_TITLE == 	
									indexOfCompareColumn){
				columnValue = dto.getTitle();	
			}	
			if(KCatalogGuiBrowseTableModel.INDEX_ALBUM == 	
									indexOfCompareColumn){
				columnValue = dto.getAlbum();	
			}	
			if(KCatalogGuiBrowseTableModel.INDEX_ARTIST == 	
									indexOfCompareColumn){
				columnValue = dto.getArtist();	
			}	
			if(KCatalogGuiBrowseTableModel.INDEX_GENRE == 	
									indexOfCompareColumn){
				columnValue = dto.getGenre();	
			}	
			if(KCatalogGuiBrowseTableModel.INDEX_COMMENT == 	
									indexOfCompareColumn){
				columnValue = dto.getComment();					
			}	
			return columnValue;			
		}
		
	}
}


