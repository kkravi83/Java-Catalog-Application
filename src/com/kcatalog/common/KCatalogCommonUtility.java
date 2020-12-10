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
package com.kcatalog.common;

import com.kcatalog.common.KCatalogCommonDto;
import com.kcatalog.common.KCatalogConstants;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.gui.KCatalogGuiTreeModel;
import com.kcatalog.fileutils.Mp3FileDto;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;



public class KCatalogCommonUtility {
	public static void playM3uList(String m3uFilePath){
		
			
		String commandLine = KCatalogConfigOptions.getOptionValue(
				KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD);
		executeProgram(commandLine,new Object[]{m3uFilePath});				
	}
	
	public static void executeProgram(String commandLine,Object[] args){
		String command = commandLine;
		Object[] arg = args;
		int len = args.length;
		String[] cmd = new String[len+1];
		cmd[0] = commandLine;
		System.arraycopy(args,0,(Object[])cmd,1,len);
		final String[] execCmd = cmd;
		//System.out.println(commandLine);
		Runnable r = new Runnable(){
						public void run(){
							try{
								Runtime rm = Runtime.getRuntime();	
								rm.exec(execCmd);
							}catch(Exception e){
								e.printStackTrace();
								throw new KCatalogException("Cannot execute program");
							}
						}
					  };	
		Thread t = new Thread(r);
		t.start();
	}
	
	public static String generateM3uFile(String filePath){
		ArrayList list = new ArrayList();
		list.add(filePath);
		return generateM3uFile(list);
	}
	
	public static String generateM3uFile(ArrayList selectedFilePathList){
		try{
			FileOutputStream op = new FileOutputStream("playList.m3u");
			for(int i=0;i<selectedFilePathList.size();i++){
				String filePath = (String)selectedFilePathList.get(i) + "\n";
				op.write(filePath.getBytes());
			}
			op.flush();
			op.close();
			return new File("playList.m3u").getAbsolutePath();
		}catch(Exception e){
			throw new KCatalogException("Cannot write playlist");
		}		
	}
	
	public static Mp3FileDto getMp3FileDtoFromLoc(String fileLoc){
		Mp3FileDto searchMp3FileDto = new Mp3FileDto();
		searchMp3FileDto.setFileName("");
		searchMp3FileDto.setFileLocation(fileLoc.trim());
		searchMp3FileDto.setArtist("");
		searchMp3FileDto.setAlbum("");
		searchMp3FileDto.setDuration("");
		searchMp3FileDto.setGenre("");
		searchMp3FileDto.setTitle("");
		searchMp3FileDto.setYear("");
		searchMp3FileDto.setComment("");
		return searchMp3FileDto;
	}
	
	public static String getCommentIfPresent(String fileLoc){
		int midIndex = fileLoc.indexOf(KCatalogGuiTreeModel.label);
		if(midIndex == -1){
			return "";
		}
		int firstIndex = midIndex -1 ;
		int lastIndex = fileLoc.indexOf(KCatalogConfigOptions.getSeparator(),midIndex);
		if(lastIndex == -1){
			return fileLoc.substring(midIndex + 
				KCatalogGuiTreeModel.label.length(),fileLoc.length());		
		}else{
			return fileLoc.substring(midIndex +
					KCatalogGuiTreeModel.label.length(),lastIndex);		
		}						
	}
	
	public static String removeCommentIfPresent(String fileLoc){		
		int midIndex = fileLoc.indexOf(KCatalogGuiTreeModel.label);
		if(midIndex == -1){
			return fileLoc;
		}
		int firstIndex = midIndex -1 ;
		int lastIndex = fileLoc.indexOf(KCatalogConfigOptions.getSeparator(),midIndex);
		if(lastIndex == -1){
			return fileLoc.substring(0,midIndex-1);		
		}
		
		return fileLoc.substring(0,midIndex) + fileLoc.substring(lastIndex,fileLoc.length());		
		
	}
	
	public static String getTimeInMinutes(long secTime){
		String value = "0:00";
		long val = secTime;
		
		try{			
			long min = (long)(val / 60);
			long sec =   val - (min * 60);
			value = String.valueOf(min) + ":" + String.valueOf(sec);
		}catch(Exception e){
		
		}	
		//System.out.println("getTimeInMinutes " +  secTime + " " +value);
		return value;
	}
	
	public static String getDurationInSeconds(String duration){
		String durationSec = "";		
		if(null == duration || "".equals(duration.trim())
				|| "0:00".equals(duration.trim())){
			durationSec = "0";	
		}else{
			
			int index = duration.indexOf(":");
			if(-1 != index){			
				String min = duration.substring(0,index);			
				String sec = duration.substring(index+1);
				int minInt = getIntValue(min);
				int secInt = getIntValue(sec);
				int total = (minInt * 60) + secInt;
				durationSec = String.valueOf(total);	
			}else{
				try{
					durationSec = Integer.valueOf(duration).toString();
				}catch(Exception e){
					durationSec = "0:00";
				}
			}
		}
		//System.out.println("getDurationInSeconds " +  duration + " " +durationSec);
		return durationSec;
	}
	
	public static String getDateStringFromLong(long dateSec){
		
		Date dt = new Date(dateSec);
		SimpleDateFormat df = new SimpleDateFormat();
		String str = df.format(dt);
		//System.out.println("getDateStringFromLong " +  dateSec + " " +str);
		return str;
	}
	 
	public static String getConfigPath(){
		String currentDir = System.getProperty("user.dir");		
		return new File(currentDir + File.separator + "config").getAbsolutePath();
	}
	
	public static String adjustForSeparator(String adjustStr,String compareStr){
		if(compareStr.startsWith(KCatalogConfigOptions.getSeparator()) &&
				!adjustStr.startsWith(KCatalogConfigOptions.getSeparator()) ) {
			adjustStr = KCatalogConfigOptions.getSeparator() + adjustStr;
		}		
		return adjustStr;
	}
	
	public static int getIntValue(String strInt){
		int intVal = 0;
		try{
			intVal = Integer.valueOf(strInt).intValue();
		}catch(Exception e){
			intVal = 0;
		}
		return intVal;
	}
	
	public static  boolean isPermanentLocation(String dirPath){
		return isPermanentLocation(dirPath,false);
	}
	
	public static  boolean isPermanentLocation(String dirPath,boolean considerChildrenAlso){
		//System.out.println("inside permanent loc " + dirPath);
		ArrayList pLocList = (ArrayList)KCatalogConfigOptions.
				getObjectOptionValue(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS);
		for(int i=0;i<pLocList.size();i++){
			String pLocPath = ((String)pLocList.get(i)).trim();
			//System.out.println("inside permanent loc pLocPath " + pLocPath);
			dirPath = dirPath.trim();
			if(pLocPath.endsWith(KCatalogConfigOptions.getSeparator())){
				pLocPath = pLocPath.substring(0,pLocPath.length()-1);				
			}
			if(dirPath.endsWith(KCatalogConfigOptions.getSeparator())){
				dirPath = dirPath.substring(0,dirPath.length()-1);				
			}
			if(dirPath.trim().equalsIgnoreCase(pLocPath.trim())){
				//System.out.println("inside permanent loc true");
				return true;
				
			}else{
				if(considerChildrenAlso && 
					dirPath.trim().startsWith(pLocPath.trim())){
					return true;
				}
			}
		}
		//System.out.println("inside permanent loc false");
		return false;
	}
	
	public static String escapeCharForBrowser(){
		String escape = "";
		if("/".equals(KCatalogConfigOptions.getSeparator())){
			escape = "\\\\";
		}else{
			escape = "/";
		}
		return escape;
	}
	
	public static String getNewLocFromLocMap(HashMap locationMap,String oldLocation){
		Iterator it = locationMap.keySet().iterator();
		String newLocation = null;		
		while(it.hasNext()){
			String oldLocFromMap = (String)it.next();
			int startIndex = oldLocation.indexOf(oldLocFromMap);
			if(startIndex > -1){
				String newLocFromMap = (String)locationMap.get(oldLocFromMap);
				int oldLocLen = oldLocFromMap.length();				
				int endIndex = startIndex + oldLocLen; 
				StringBuffer tempStr = new StringBuffer(oldLocation);
				newLocation =
				 tempStr.replace(startIndex,endIndex,newLocFromMap).toString();
			}
		}
		return  newLocation;		
	}
	
	public static String formatURLForBrowser(String path){
	//	String space = escapeCharForBrowser() + " ";
	//	path = path.replaceAll(" {1,}",space);
	//	System.out.println(path);
		return path;
	}
	
	public static String removeSeparatorAtEnd(String str){
		String res = str;
		if( null == str || "".equals(str.trim())){
			res = str;
		}else{
			if(str.endsWith(KCatalogConfigOptions.getSeparator())){
				res = str.substring(0,str.length()-1);
			}
		}
		return res;
	}
	
	
	
	
}
