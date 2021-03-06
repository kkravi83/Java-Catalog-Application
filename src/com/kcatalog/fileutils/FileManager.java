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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import de.vdheide.mp3.ID3;
import de.vdheide.mp3.MP3File;

import org.apache.xml.utils.XMLChar;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogUpdateControllerInterface;
public class FileManager implements Runnable {
		
	private  String currentFile = "";
	private  Boolean completed = new Boolean(false);	
	private  int currentMp3DtoPos  = 0;
	private  ArrayList mp3FileDtoList = new ArrayList();
	private  static KCatalogUpdateControllerInterface contr = null;
	private  String dirPath = "";
	private  String comment = "";
	private  final int BUFFER_SIZE = 100;
	private  Boolean stopFlag = Boolean.valueOf(false);
	private boolean isRecursive = true;
	Runnable xmlWriterThread = null;
	ArrayList selectedFilesList = null;
	
	public void setStopFlag(boolean stopFlag){
		synchronized(this.stopFlag){
			this.stopFlag = Boolean.valueOf(stopFlag);
			
		}
	}
	
	private boolean getStopFlag(){
		synchronized(this.stopFlag){
			return stopFlag.booleanValue();
		}
	}
	
	public FileManager(ArrayList selectedFilesList,
							String comment,
								boolean isRecursive,
								Runnable xmlWriterThread){
		this.selectedFilesList = selectedFilesList;		
		this.comment = comment;
		this.xmlWriterThread = xmlWriterThread;
		this.isRecursive = isRecursive;
	}
	
	public FileManager( String dirPath,
							String comment,
								boolean isRecursive,
								Runnable xmlWriterThread){
		this.dirPath = dirPath;
		this.comment = comment;
		this.xmlWriterThread = xmlWriterThread;
		this.isRecursive = isRecursive;
		//System.out.println("xmlWriterThread " + xmlWriterThread);
	}
	
	public FileManager(){
	
	}
	
	public void run(){
		//	System.out.println("Total size = " + 
		if(null == this.selectedFilesList){
			getAllMp3InFolder(dirPath,comment);		
		}else{
			getAllSelectedMp3InFolder(selectedFilesList,
											comment);
		}
		//System.out.println("existing from read thread");
	}
	
	public  Mp3FileDto getCurrentMp3FileDto(){		
		return 	removeDtoFromList();
	}
	
	public  ArrayList getMp3DtoListRef(){
		synchronized(mp3FileDtoList){
			return mp3FileDtoList;
		}
	}
	
	public int  getMp3DtoListSize(){
		synchronized(mp3FileDtoList){
			return mp3FileDtoList.size();
		}
	}
	
	public  void setOnUpdateClass(KCatalogUpdateControllerInterface
											controller){
		contr = controller;
	}
	
	public  void getAllMp3InFolder(String rootDirectory,String comment){
		currentFile = "";
		setCompleted(Boolean.valueOf(false));
		listFilesRecursively(rootDirectory,comment,isRecursive);
	}
	
	public  boolean isCompleted(){
		synchronized(completed){
			return completed.booleanValue();
		}
	}
	
	public  void setCompleted(Boolean flag){
		synchronized(completed){
			 completed = flag;
		}
	}
	
	public  String getCurrentFileName(){
		synchronized(currentFile){
			return currentFile;
		}
	}
	
	private  void setCurrentFileName(String fileName){
		synchronized(currentFile){
			currentFile = fileName;
			 
		}
	}
	
/*	public  ArrayList getAllMp3Files(String rootDirectory){
		ArrayList fileNames = new ArrayList();
		new FileManager().listAllFiles(rootDirectory,fileNames);
		return fileNames;
	}*/
	
/*	public static ArrayList getAllMp3Dtos(ArrayList fileNames,String comment){	
		currentFile = "";
		completed = new Boolean(false);
		return new FileManager().getMp3InfoList(fileNames,comment);		
	}*/
	
	
	public void listFilesRecursively(String rootDirectory,
										String comment,
											boolean isRecursive){
		
		//ArrayList fileNames = new ArrayList();
			
		listAllFiles(rootDirectory,comment,isRecursive);
	//	ArrayList mp3FileDtoList = getMp3InfoList(fileNames,comment);
		setCompleted(new Boolean(true));
	//	return mp3FileDtoList;
	}
	
	public void getAllSelectedMp3InFolder(ArrayList selectedFilesList,
											String comment){
		currentFile = "";
		setCompleted(Boolean.valueOf(false));
		Iterator it = selectedFilesList.iterator();
		while(it.hasNext() && !getStopFlag()){
			String loc = (String)it.next();
			listAllFiles(loc,comment,isRecursive);
		}
		//System.out.println("Setting comp to true");
		setCompleted(new Boolean(true));
		notifyWriter();
	}					
	
	public void getMp3InfoList(String mp3FilePath,String comment){
		//int i=0;
				
		
		//Iterator it = fileNames.iterator();
		//while( it.hasNext()){
		//	String mp3FilePath = (String)it.next();					
			Mp3FileDto mp3FileDto = getMp3FileDtoFromPath(mp3FilePath,comment);
			setCurrentFileName(mp3FileDto.getFileLocation() + File.separator +
			                            mp3FileDto.getFileName());
			addDtoToList(mp3FileDto);
			/*if(contr != null){
				contr.updateDBAndWriteUIStatus(mp3FileDto);
			}*/
		//	mp3DtoListRef = mp3FileDtoList;
										
	}
	
	private  Mp3FileDto removeDtoFromList(){
		Mp3FileDto dto = null;
		synchronized(mp3FileDtoList){
			if(mp3FileDtoList.size() != 0){
				dto = (Mp3FileDto)mp3FileDtoList.remove(0);
			}
		}
		return dto;
	}
	
	private  void addDtoToList(Mp3FileDto mp3FileDto){		
			try{
			
				while(getMp3DtoListSize() >= BUFFER_SIZE &&
							!getStopFlag()){
					synchronized(this){						
						this.wait(25);						
					}
				}
								
				mp3FileDtoList.add(mp3FileDto);
				//System.out.println("xmlWriterThread " + xmlWriterThread);
				if(!getStopFlag()){
					synchronized(xmlWriterThread){
						if(mp3FileDtoList.size() == 1){
				//			System.out.println("notifying read thread ");
							xmlWriterThread.notify();
						//	System.out.println("done ");
						}
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();			
			throw new KCatalogException("Fatal Error Occured ",e);
		}
		
	}
	
	public Mp3FileDto getMp3FileDtoFromPath(String mp3FilePath,
													String comment){
			MP3File mp3File = null;
			Mp3FileDto mp3FileDto = null;
			try{
				mp3File = new MP3File(mp3FilePath);
			/*	System.out.println("\nAlbum : " + mp3File.getAlbum().getTextContent() +
							 	"\nArtis : "  + mp3File.getArtist().getTextContent() +
							 	"\nGenre : "  + mp3File.getGenre().getTextContent() +
							 	"\nTitle : "  + mp3File.getTitle().getTextContent() +
							 	"\nTrack : " +  mp3File.getTrack().getTextContent() +
							 	"\nYear  : "  +  mp3File.getYear().getTextContent());*/
				mp3FileDto	= populateMp3FileDto(mp3File,comment);
			}catch(Exception e){
			//		System.out.println("Error getiing info "+mp3FilePath);
				mp3FileDto = new Mp3FileDto();
				File f = new File(mp3FilePath);
				mp3FileDto.setFileLocation(f.getParent());
				mp3FileDto.setFileName(f.getName());
			}
			 
			return mp3FileDto;
	}

	private Mp3FileDto populateMp3FileDto(MP3File mp3File,String comment){
	
		Mp3FileDto mp3FileDto = null;		
		try{
			mp3FileDto = new Mp3FileDto();	
			mp3FileDto.setFileName(formatString(mp3File.getName()));
			mp3FileDto.setFileLocation(formatString(mp3File.getParent()));
			mp3FileDto.setComment(formatString(comment));
			mp3FileDto.setModificationDate(String.valueOf(mp3File.lastModified()));
			mp3FileDto.setArtist(formatString(mp3File.getArtist().getTextContent()));
			mp3FileDto.setTitle(formatString(mp3File.getTitle().getTextContent()));	
			mp3FileDto.setAlbum(formatString(mp3File.getAlbum().getTextContent()));	
			mp3FileDto.setDuration(formatString(mp3File.getLength()));	
			mp3FileDto.setGenre(formatString(mp3File.getGenre().getTextContent()));
			mp3FileDto.setTrack(formatString(mp3File.getTrack().getTextContent()));
			mp3FileDto.setYear(formatString(mp3File.getYear().getTextContent()));	
		}catch(Exception e){
			//System.out.println("Error getting mp3 file info " +
//							mp3FileDto.getFileLocation() + " " +
//							mp3FileDto.getFileName());
			// e.printStackTrace();
			
		}
		return mp3FileDto;
	}
	
	private String formatString(long time){
	
		return KCatalogCommonUtility.getTimeInMinutes(time);
	}
	
	private String formatString(String input){
		String output = "";
		if(input == null || "".equals(input)){			
			return KCatalogConstants.MESSAGE_NA;
		}
		for(int i =0;i<input.length();i++){
			char ch = input.charAt(i);
			if(!XMLChar.isValid(ch)){
				//System.out.println(ch + " not java leter");
				ch = ' ';
			}
			
			output = output + String.valueOf(ch);
		}
		return output.trim();	
	}
	
	private boolean isVaildXMLCharacter(char ch){
			if(Character.isLetterOrDigit(ch)){
				return true;
			}
			if(Character.isWhitespace(ch)){
				return true;
			}
			
			String validChars = "&()~!@$%^*()_-+=\\|\"';:/?,.[]";
			int index = validChars.indexOf(String.valueOf(ch));
			return (index == -1)?false:true;
			
	}
	
	public void listAllFiles(String currentFileNameStr,
								String comment, boolean isRecursive){
		Stack lockStack = new Stack();
		boolean addedMp3sInRootDir = false;
		lockStack.push(currentFileNameStr);
		while(!lockStack.empty()){
			String loc = (String)lockStack.pop();
			File currentFile = new File(loc);
			if(isCompleted()){
				return;
			}	
			if(currentFile.isFile()){
			//fileNames.add(currentFile.getPath());										
				getMp3InfoList(loc,comment);
				if(getStopFlag()){				
					return;
				}				
			}
			else{
				if(currentFile.isDirectory() &&
						( isRecursive || !addedMp3sInRootDir )){
					addedMp3sInRootDir = true;
					String[] childFileNames= currentFile.list(getFilenameFilter());					
					int i = 0;
					while(i < childFileNames.length){
						String childFile = "";
						if(loc.charAt(loc.length()-1) !=
							File.separatorChar){
							 childFile =
								  loc+File.separator+childFileNames[i++];
						}else{
							 childFile = loc+childFileNames[i++];
						}	
						lockStack.push(childFile);											
					}
				}
			}		
		
		}
		
	}
	
	private void notifyWriter(){
		synchronized(xmlWriterThread){
					
//			System.out.println("notifying read thread ");
			xmlWriterThread.notify();
//			System.out.println("done ");
		}
	}
	
	public static FilenameFilter getFilenameFilter(){
		return filter;
	}
	
	static	FilenameFilter filter = new FilenameFilter(){
				public boolean accept(File dir,String name){
					boolean apt = false;
					
					if( name.toLowerCase().endsWith("mp3")){
						apt = true;
						
					}
					else{
						String currentFileName = dir.getAbsolutePath(); 
							
						String childFile = "";
						if(currentFileName.charAt(currentFileName.length()-1) !=
					 	File.separatorChar){
							childFile = currentFileName+File.separator+name;
						}else{
							childFile = currentFileName+name;
						}		
						File fl = new File(childFile);
						if(fl.isDirectory())
							apt= true;
					}				
					return apt;
				}
	};
		
}


