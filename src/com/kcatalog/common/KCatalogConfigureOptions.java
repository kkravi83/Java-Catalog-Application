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

import com.kcatalog.common.*;
import java.util.*;
import java.io.*;

public class KCatalogConfigureOptions{
	private String configType = "";
	private String currentDir = "";
	public static String CONFIG_TYPE_LINUX = "linux";
	public static String CONFIG_TYPE_WIN = "windows";
	private static final String MAX_MP3_PER_XML = "100";
	private static final String FILE_NAME_PREFIX = "mp3Info";
	private static final String PERMANENT_LOCATIONS = " " ;
	private static final String ASSOCIATED_APP_CMD = "xmms";
	private static final String MAX_MP3_PER_VIEW_BROWSE = "1000";
	private static final String MAX_MP3_PER_VIEW = "200";
	private static final String ASSOCIATED_FILE_MANAGER = "konqueror";
	private static final String PLAYLIST_LOCATION = "playlist";
	private static final String DBBKUP_LOCATION = "backup";
	private static final String RECOVER_DB_STARTUP = "none";
	private static final String BACKUP_DB_FREQ = "10";
	private static final String DATABASE_LOCATION = "database";
			
	private void setOptionsForOS(){
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_INSTALLATIONFOLDER,
							currentDir);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_DATABASE_LOCATION,
							currentDir + 
								KCatalogConfigOptions.getSeparator()+
									DATABASE_LOCATION);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_PLAYLIST_LOCATION,
							currentDir + 
								KCatalogConfigOptions.getSeparator()+
									PLAYLIST_LOCATION);
											
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_DBBKUP_LOCATION,
							currentDir + 
								KCatalogConfigOptions.getSeparator()+
									DBBKUP_LOCATION);
		
									
		createDirIfNotExist(currentDir + KCatalogConfigOptions.getSeparator()+
									DATABASE_LOCATION);
		createDirIfNotExist(currentDir + KCatalogConfigOptions.getSeparator()+
									PLAYLIST_LOCATION);
		
		createDirIfNotExist(currentDir + KCatalogConfigOptions.getSeparator()+
									DBBKUP_LOCATION);
	}
	
	private void createDirIfNotExist(String path){
		File dir = new File(path);
		if( !dir.exists() || dir.isFile()){
			dir.mkdirs();
		}
	}
	
	private void setOptionsForLinux(){
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER, "konqueror");
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD,
							"xmms");	
	}
	
	private void setOptionsForWin(){
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_FILE_MANAGER, "c:\\windows\\explorer");
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_ASSOCIATED_APP_CMD,
						"c:\\program files\\winamp\\winamp.exe");	
	}
	
	private void setCommonOptions(){
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_MAX_MP3_PER_XML,
					MAX_MP3_PER_XML);	
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_FILE_NAME_PREFIX,
					FILE_NAME_PREFIX);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_PERMANENT_LOCATIONS,
							new ArrayList());	
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW,
					MAX_MP3_PER_VIEW);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_RECOVER_DB_STARTUP,
						RECOVER_DB_STARTUP);	
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_MAX_MP3_PER_VIEW_BROWSE,MAX_MP3_PER_VIEW_BROWSE);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_BACKUP_DB_FREQ
		                            ,BACKUP_DB_FREQ);
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_DEBUG
		                            ,"false");
		KCatalogConfigOptions.setOptionValue(KCatalogConstants.CONFIG_LIMIT_MAX_SIZE
		                            ,"true");
	}
	
	
	private void setLookAndFeel(){
		KCatalogLookAndFeelManager mg = new KCatalogLookAndFeelManager();
		mg.setDefaultLNF(KCatalogConstants.SYSTEM_LNF_NAME);
		
	}
	
	public KCatalogConfigureOptions(String configType,String currentDir){
		this.configType = configType;
		this.currentDir = currentDir;
	}
	
	public void writeConfigFile(){
		setOptionsForOS();
		setCommonOptions();	
		if(CONFIG_TYPE_LINUX.equals(configType)){
			setOptionsForLinux();
		}
		if(CONFIG_TYPE_WIN.equals(configType)){
			setOptionsForWin();
		}
	}	
	
	
}