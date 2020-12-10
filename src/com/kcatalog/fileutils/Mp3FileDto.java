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
import com.kcatalog.common.KCatalogDto;

public class Mp3FileDto extends KCatalogDto{
	private String slNo = "";
	private String fileName = "";
	private String fileLocation = "";
	private String comment = "";
	private String modificationDate = "";
	private String artist = "";
	private String title = "";
	private String track = "";
	private String album = "";
	private String genre = "";
	private String year = "";
	private String locationId = "";
	private String maxLocationId = "";
	private String count = null;
	private Integer locationIdInt = null;
	private String duration = "";
	
	public String getFileName() { 
		return   fileName ;
	}
	public String getFileLocation() { 
		return  fileLocation ;
	}               
	public String getComment() { 
		return   comment ;
	}               
	public String getModificationDate(){
	 	return  modificationDate ;
	}  
	public String getArtist() {
		 return  artist ;
	}               
	public String getTitle() { 
		return   title ;
	}               
	public String getTrack() { 
		return   track ;
	}               
	public String getAlbum() { 
		return   album ;
	}               
	public String getGenre() { 
		return   genre ;
	}               
	
	public String getYear() {
		 return    year ;
	}                                              
	public String getLocationId() {
		 return   locationId ;
	}            
	public String getMaxLocationId() {
		 return   maxLocationId ;
	}                                     
	public String getCount() {
		 return   count ;
	}     
	public Integer getLocationIdInt() {
		 return   locationIdInt ;
	}  
	public String getSlNo() {
		 return   slNo ;
	} 
	public void setSlNo(String slNo) {
		 this.slNo = slNo;
	} 
	public void setFileName(String fileName) { 
		this.fileName = fileName;
	}
	public void setFileLocation(String fileLocation) { 
		this.fileLocation = fileLocation;
	}               
	public void setComment(String comment) { 
		this.comment = comment;
	}               
	public void setModificationDate(String modificationDate){
		 this.modificationDate = modificationDate;
	}  
	public void setArtist(String artist) { 
		this.artist = artist;
	}               
	public void setTitle(String title) {
		 this.title = title;
	}               
	
	public void setAlbum(String album) {
		 this.album = album;
	}               
	public void setGenre(String genre) {
		 this.genre = genre;
	}               
	public void setTrack(String track) { 
		this.track = track;
	}               
	public void setYear(String year) { 
		this.year = year;
	}                                                                                    
	public void setLocationId(String locationId) { 
		this.locationId = locationId;
	}               	
	public void setMaxLocationId(String maxLocationId) { 
		this.maxLocationId = maxLocationId;
	}     
	public void setCount(String count) { 
		this.count = count;
		this.maxLocationId = (count == null)?null:count.toString();
	}  
	public void setLocationIdInt(Integer locationIdInt) { 
		this.locationIdInt = locationIdInt;
		locationId = (locationIdInt == null)?null:locationIdInt.toString();
	}      
	
	public void setDuration(String duration){
		this.duration = duration;
	}
	
	public String getDuration(){
		return duration;
	}
	
	public Mp3FileDto cloneMp3File(){
		Mp3FileDto dto = new Mp3FileDto();
		dto.setFileName(getFileName());
		dto.setFileLocation(getFileLocation()); 
		dto.setAlbum(getAlbum());
		dto.setComment(getComment()); 
		dto.setGenre(getGenre());
		dto.setTitle(getTitle());
		dto.setArtist(getArtist());
		dto.setYear(getYear());
		dto.setDuration(getDuration());
		return dto;
	}
	
	public boolean equals(Object mp3Dto){
		boolean res = false;
		if(mp3Dto instanceof Mp3FileDto){
			Mp3FileDto dto2 = (Mp3FileDto)mp3Dto;
			res= this.fileName.equals(dto2.getFileName()) &&
				this.fileLocation.equals(dto2.getFileLocation()) &&
				this.album.equals(dto2.getAlbum()) &&
				this.comment.equals(dto2.getComment()) &&
				this.genre.equals(dto2.getGenre()) &&
				this.title.equals(dto2.getTitle()) &&
				this.artist.equals(dto2.getArtist());
			
		}else{
			res = false;
		}
		return res;
	}
}

















