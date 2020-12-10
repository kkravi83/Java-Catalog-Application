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
public interface KCatalogConstants {
	//Error messages
	public static final String ERROR_MESSAGEID_COMMON = "Fatal Error Occured";
	public static final String ERROR_MESSAGE_OPEN_CONFIG = "Error opening config.xml file ";
	public static final String ERROR_CONFIG_FILE_HEADER = "Error opening config.xml file ";
	public static final String ERROR_DATABASE_DIR = "Database directory does not exist. Application was not able to create a new one";
	public static final String ERROR_WRITING_DATABASE = "Fatal Error while writing mp3 information to XML database";
	public static final String ERROR_READING_DATABASE = "Fatal Error while reading mp3 information from XML database";
	public static final String ERROR_CREATING_LOCATION_LOOKUP_FILE = "Fatal Error while creating location lookup file";
	public static final String ERROR_READING_LOCATION_LOOKUP_FILE = "Fatal Error while reading location lookup file";
	public static final String ERROR_ACESSING_LOOKUP_FILE = "Cannot access the lookup file";
	public static final String ERROR_WRITING_MP3XML = "Error writing mp3Xml File";
	//config file entries
	public static final String CONFIG_ROOT = "KCatalogConfig";
	public static final String CONFIG_INSTALLATIONFOLDER= "installationFolder";
	public static final String CONFIG_DATABASE_LOCATION = "dataBaseLocation";
	public static final String CONFIG_MAX_MP3_PER_XML = "maximumMp3PerXml";
	public static final String CONFIG_FILE_NAME_PREFIX = "dataBaseFileNamePrefix";
	public static final String CONFIG_MAX_MP3_PER_VIEW = "maximumMp3PerView";
	public static final String CONFIG_PERMANENT_LOCATIONS = "permanentLocationList" ;
	public static final String CONFIG_ASSOCIATED_APP_CMD = "associatedAppCmdLine";
	public static final String CONFIG_MAX_MP3_PER_VIEW_BROWSE = "maximumMp3PerViewInBrowse";
	public static final String CONFIG_ASSOCIATED_FILE_MANAGER = "associatedFileManager";
	public static final String CONFIG_PLAYLIST_LOCATION = "playListLocation";
	public static final String CONFIG_DBBKUP_LOCATION = "databaseBackupLocation";
	public static final String CONFIG_RECOVER_DB_STARTUP = "retrieveBackUp";
	public static final String CONFIG_BACKUP_DB_FREQ = "backupFreq";
	public static final String CONFIG_CURRENT_START_COUNT = "currentStartCount";
	public static final String CONFIG_DEBUG = "debug";
	public static final String CONFIG_LIMIT_MAX_SIZE = "limitMaxSize";
	public static final String SYSTEM_LNF_NAME = "System Look And Feel";
	//MP3 XML schema constants
	public static final String MP3_XML_ROOT = "KCatalogSchema";
	public static final String MP3_XML_FILELOCATION = "fileLocation";
	public static final String MP3_XML_MP3FILE= "mp3File";
	public static final String MP3_XML_FILENAME= "fileName";
	public static final String MP3_XML_COMMENT= "comment";
	public static final String MP3_XML_TITLE= "title";
	public static final String MP3_XML_ALBUM = "album";
	public static final String MP3_XML_DURATION = "duration";
	public static final String MP3_XML_GENRE = "genre";
	public static final String MP3_XML_ARTIST = "artist";
	public static final String MP3_XML_YEAR = "year";
	public static final String MP3_XML_NO_OF_RECORDS = "currentNoOfRecords";
	//Search constants
	public static final String SEARCH_OPERATION_TYPE_OR = "OR";
	public static final String SEARCH_OPERATION_TYPE_AND = "AND";
	public static final String SEARCH_OPERATION_TYPE_FILE_LOC_DEL_IF_DIFF_DATA = "FLOCONLY";
	public static final String SEARCH_OPERATION_TYPE_RECURSIVE_LOC = "recursiveSearchForMp3s";
	//Look up file
	public static final String LOCATION_LOOK_UP_FILE = "mp3LocationLookUp.xml";
	public static final String LOOKUP_LOCATION_ID = "containedFileId";
	public static final String LAST_ADDED_LOCATION_ID = "lastAddedContainedField";
	
	//Messages
	public static final String MESSAGE_READING_MP3_FILE_INFO = "Reading information of Mp3 : ";
	public static final String MESSAGE_NA = "NA";
	public static final String MESSAGE_EMPTY = "Blank";
	
	public static final String LICENCE_MESSAGE = 
	
 " \n"+
 "   KCatalog - Mp3 Catalog Utility BETA Version                        \n"+
 "   Copyright (C) 2006 by krishnakumar                                     \n"+
 "   For bug reports and suggestions mail to krishnakumar.kr@gmail.com      \n"+
 "                                                                          \n"+
 "   This program is free software; you can redistribute it and/or modify   \n"+
 "   it under the terms of the GNU General Public License as published by   \n"+
 "   the Free Software Foundation; either version 2 of the License, or      \n"+
 "   (at your option) any later version.                                   \n"+
 "                                                                          \n"+
 "   This program is distributed in the hope that it will be useful,        \n"+
 "   but WITHOUT ANY WARRANTY; without even the implied warranty of         \n"+
 "   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          \n"+
 "   GNU General Public License for more details.    						 \n"+
 " \n";
} 
