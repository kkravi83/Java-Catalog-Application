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
package com.kcatalog.fileutils;

import com.kcatalog.fileutils.M3UPlayListFile;
import org.apache.xml.utils.XMLChar;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogConfigOptions;
import java.util.*;
import java.io.*;
import java.io.FilenameFilter;
import java.io.File;

public class M3UPlayListManager  {
	private  HashMap playListFilesMap = new HashMap();
	private  static M3UPlayListManager currentInstance = null;
	private String playlistDirPath = KCatalogConfigOptions.getOptionValue(
							KCatalogConstants.CONFIG_PLAYLIST_LOCATION);
	private String defaultPlaylistName = "currentPlaylist";
							
	private M3UPlayListManager() {		
	}	
	
	public String getPlayListPath(String playListName){
		M3UPlayListFile file = 
			(M3UPlayListFile)this.playListFilesMap.get(playListName);		
		return file.getPlayListPath();
	}
		
	public HashMap searchForSimilarMp3InPlaylists(Mp3FileDto dto){
		Iterator it = playListFilesMap.keySet().iterator();
		HashMap map = new HashMap();
		while(it.hasNext()){
			String playListName = (String)it.next();
			M3UPlayListFile file =
			 			(M3UPlayListFile)playListFilesMap.get(playListName);
			ArrayList searchResult = file.searchForSimilarMp3InPlaylist(dto);
			if(searchResult.size() > 0){
				map.put(playListName,searchResult);
			}
		}
		return map;
	}
	
	public ArrayList searchPlaylistsForDto(ArrayList mp3FileDtoList){
		Iterator it = playListFilesMap.keySet().iterator();
		ArrayList list = new ArrayList();		
		Iterator mp3Iterator = mp3FileDtoList.iterator();
		while(mp3Iterator.hasNext()){
			Mp3FileDto dto = (Mp3FileDto)mp3Iterator.next(); 
			while(it.hasNext()){
				String playListName = (String)it.next();
				M3UPlayListFile file =
			 			(M3UPlayListFile)playListFilesMap.get(playListName);
				ArrayList searchResult = file.searchForMp3InPlaylist(dto);
				if(searchResult.size() > 0 &&
							!list.contains(playListName)){
					list.add(playListName);
				}
			}
		}
		return list;
	}
	
	public String getPlaylistInfoForMp3(Mp3FileDto dto){				
		Iterator it = playListFilesMap.keySet().iterator();		
		StringBuffer playListInfo = new StringBuffer("");
		while(it.hasNext()){
			String playListName = (String)it.next();
			M3UPlayListFile file =
			 			(M3UPlayListFile)playListFilesMap.get(playListName);
			if(null != file){			
				if(file.containsMp3(dto)){
					playListInfo.append(file.getPlayListInfo())
								.append("\n");
				}
			}
		}
		String res = playListInfo.toString();
		if("".equals(res)){
			res = "No Information could be found";
		}
		return res;
	}
	
	public void populatePlaylistMap(){
		File playlistDir = new File(playlistDirPath);
		playListFilesMap.clear();
		String [] m3uFiles = playlistDir.list(new M3UFileFilter());
		for(int i=0;i<m3uFiles.length;i++){			
			String playListName = new File(m3uFiles[i]).getName();			
			int index = playListName.indexOf("m3u");
			if(-1 == index){
				index = playListName.indexOf("M3U");
			}
			playListName = playListName.substring(0,index-1);
			M3UPlayListFile playlistFile = new M3UPlayListFile(playListName);
			if(playlistFile.isValid()){
				playListFilesMap.put(playListName,playlistFile);
			}
		}
		
	}
	
	public String getDefaultPlaylistName(){
		return defaultPlaylistName;
	}
	
	public void addToDefaultPlaylist(ArrayList mp3DtoList){
		M3UPlayListFile mlf = new M3UPlayListFile(defaultPlaylistName);
		if(!mlf.isValid()){
			mlf.createNewList();
		}	
		mlf.addMp3ListToPlaylist(mp3DtoList);
	}
	
	public void createDefaultPlaylist(){
		M3UPlayListFile mlf = new M3UPlayListFile(defaultPlaylistName);
		mlf.createNewList();
	}
	
	public void deleteDefaultPlaylist(){
		M3UPlayListFile mlf = new M3UPlayListFile(defaultPlaylistName);
		if(mlf.isValid()){
			mlf.deletePlaylist();
		}
	}
	
	public String getDefaultPlaylistPath(){
		M3UPlayListFile mlf = new M3UPlayListFile(defaultPlaylistName);
		String path = "";
		if(!mlf.isValid()){
			mlf.createNewList();
		}		
		path = mlf.getPlayListPath();		
		return path;
	}
	
	public ArrayList getPlayLists(){
		Iterator it = playListFilesMap.keySet().iterator();
		ArrayList list = new ArrayList();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	public boolean fileAlreadyExist(String fileName){
		boolean fl = false;
		M3UPlayListFile file = new M3UPlayListFile(fileName);
		if(file.fileAlreadyExist()){
			fl = true;
		}
		return fl;
	}
	
	public void createNewPlayList(String fileName){
		M3UPlayListFile file = new M3UPlayListFile(fileName);
		file.createNewList();
		populatePlaylistMap();				
	}
	
	public ArrayList getMp3DtoFromPlaylist(String fileName){
		M3UPlayListFile file = 
				(M3UPlayListFile)this.playListFilesMap.get(fileName);		
		ArrayList result = ( null == file)?
					new ArrayList():file.getMp3DtoListInPlayList(); 
		return result;
	}
	
	public void addToPlayList(String fileName,ArrayList mp3DtoList){
		M3UPlayListFile file = 
				(M3UPlayListFile)this.playListFilesMap.get(fileName);		
		file.addMp3ListToPlaylist(mp3DtoList);
	}
	
	public void deleteFromPlayList(String fileName,ArrayList mp3DtoList){
		M3UPlayListFile file = 
				(M3UPlayListFile)this.playListFilesMap.get(fileName);		
		file.deleteMp3InfoFromPlayList(mp3DtoList);
	}
	
	public void deleteFromPlayLists(ArrayList playListNames,ArrayList mp3DtoList){
		Iterator it = playListNames.iterator();
		while(it.hasNext()){
			String fileName = (String)it.next();
			deleteFromPlayList(fileName.trim(),mp3DtoList);	
		}				
	}
	
	public void deletePlayList(String fileName){
		M3UPlayListFile file = 
				(M3UPlayListFile)this.playListFilesMap.get(fileName);		
		file.deletePlaylist();
		populatePlaylistMap();
	}
	
	public void mapLocations(HashMap locationMap){
		ArrayList playLists = getPlayLists();
		Iterator playListsIterator = playLists.iterator();
		while(playListsIterator.hasNext()){
			String fileName = (String)playListsIterator.next();
			//System.out.println("mapLocations " + fileName);
			M3UPlayListFile file = 
				(M3UPlayListFile)this.playListFilesMap.get(fileName);	
			file.mapLocations(locationMap);
		}
	}
	
	public static M3UPlayListManager getInstance(){
		currentInstance = (null == currentInstance)?
					new M3UPlayListManager() : currentInstance;
		return currentInstance;
	}

	class M3UFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			boolean fl = false;
			if(name.trim().endsWith(".m3u") ||
					name.trim().endsWith(".M3U") ){
				fl = true;
			}
			return fl;
		}	
	}
	
}
