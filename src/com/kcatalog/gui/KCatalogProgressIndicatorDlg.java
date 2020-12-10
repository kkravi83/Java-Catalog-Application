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

import com.kcatalog.gui.KCatalogGuiBrowseTableModel;
import com.kcatalog.gui.KCatalogGuiMp3TableRenderer;
import com.kcatalog.gui.KCatalogGuiSearchTabAction;
import com.kcatalog.gui.KCatalogGuiTreeModel;
import com.kcatalog.gui.KCatalogGuiProgressStopInterface;
import com.kcatalog.gui.KCatalogGuiBrowseTabAction;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.event.ActionEvent;

import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import java.util.ArrayList;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.border.*;

public class KCatalogProgressIndicatorDlg extends JDialog 									implements Runnable {
	int width = 300;
	int height = 150;
	Dimension size = new Dimension(width,height);
	String title = "";
	String textMessage = "";
	public JProgressBar pb = new JProgressBar();
	Box vb = Box.createVerticalBox();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	JLabel statusMessage1 = new JLabel("");
	JLabel statusMessage2 = new JLabel("");
	JDialog d = new JDialog();
	JButton stopButton = new JButton("Stop");
	Component mainFrame = null;
	private boolean stopButtonPresent = false;
	private static KCatalogProgressIndicatorDlg currentRef = null;
	private KCatalogGuiProgressStopInterface sl = null;
	
	KCatalogProgressIndicatorDlg(){
		this.setSize(size);
		//this.title = title;
		this.textMessage = "";
		this.mainFrame = null;
		currentRef = this;
		//setTitle(title);
		setVisibleLocation();
		addCommonComponents();
	}
	
	
	public KCatalogProgressIndicatorDlg(Component owner,String title,
						String textMessage,int width,int height,
							KCatalogGuiProgressStopInterface sl,
								boolean stopButtonPresent){
		super((JFrame)owner,title,true);
		this.stopButtonPresent = stopButtonPresent;				
		size = new Dimension(width,height);
		this.width =width;
		this.height = height;
		this.setSize(size);
		//this.title = title;
		this.textMessage = textMessage;				
		this.mainFrame = owner;
		this.sl = sl;
		currentRef = this;
		//setTitle(title);
		setVisibleLocation();
		addCommonComponents();
		setNamesForComponents();
		setListenersForComponents();
		
	}
	
	private void setNamesForComponents(){
		stopButton.setName("stopButton");
	}
	
	private void setListenersForComponents(){
		StopButtonListener sl = new StopButtonListener();
		stopButton.addActionListener(sl);
	}
	
	public KCatalogProgressIndicatorDlg(Component owner,
					String title,String textMessage){
		super((JFrame)owner,title,true);
		this.setSize(size);
		//this.title = title;
		this.textMessage = textMessage;				
		this.mainFrame = owner;
		currentRef = this;
		//setTitle(title);
		setVisibleLocation();
		addCommonComponents();
	}
	
	KCatalogProgressIndicatorDlg(Component owner,
						String title){
		super((JFrame)owner,title,true);
		this.setSize(size);
		this.mainFrame = owner;
		//this.title = title;
		//this.setTitle(title);		
		currentRef = this;
		setVisibleLocation();
		addCommonComponents();
	}
	
	

	public static KCatalogProgressIndicatorDlg getCurrentRef(){
		if(null != currentRef){
			return currentRef;
		}else{
			return new KCatalogProgressIndicatorDlg();
		}
	}
			
	private void setVisibleLocation(){
		this.setLocation((int)( (screenSize.getWidth()/2) -  (size.getWidth()/2)),
							(int)( (screenSize.getHeight()/2) - 
									(size.getHeight()/2)));
	}
	
	private void addCommonComponents(){
		
	
		JPanel p = new JPanel();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	//	vb.add(Box.createVerticalStrut(height/7));
		vb.add(getProgressTextBox());
		vb.add(getProgressBarComponent());		
		if(this.stopButtonPresent){			
			vb.add(Box.createVerticalStrut(3));
			vb.add(getStopButton());
		}
		p.add(BorderLayout.NORTH,vb);
	//	vb.add(Box.createVerticalStrut(height/4));
		getContentPane().add(p);		
		this.setBackground(p.getBackground());
		
	}
	
	private Component getStopButton(){
		Box horz = Box.createHorizontalBox();
		horz.add(Box.createHorizontalGlue());		
		horz.add(stopButton);
		stopButton.setPreferredSize(new Dimension(75,30));
		stopButton.setMargin(new Insets(7,7,7,7));
		horz.add(Box.createHorizontalGlue());
		return horz;
	}
	private Component getProgressTextBox(){
		Box horz = Box.createHorizontalBox();
		horz.add(Box.createHorizontalStrut(20));
		statusMessage1.setPreferredSize(new Dimension(			
				width,20));
		statusMessage2.setPreferredSize(new Dimension(
				width,20));
		Box vb = Box.createVerticalBox();
		Box hz1 = Box.createHorizontalBox();
		hz1.add(Box.createHorizontalStrut(4));
		hz1.add(statusMessage1);
		hz1.add(Box.createHorizontalStrut(37));
		vb.add(Box.createVerticalStrut(10));
		vb.add(hz1);
		vb.add(Box.createVerticalStrut(2));
		Box hz2 = Box.createHorizontalBox();
		//hz2.add(Box.createHorizontalStrut(1));
		hz2.add(Box.createHorizontalStrut(4));
		hz2.add(statusMessage2);		
		hz2.add(Box.createHorizontalStrut(37));
		vb.add(hz2);
		vb.add(Box.createVerticalStrut(3));
		horz.add(vb);
		//horz.add(Box.createHorizontalGlue());
		return horz;		
	}
	
	private Component getProgressBarComponent(){
		Box horz = Box.createHorizontalBox();
		horz.add(Box.createHorizontalGlue());
		pb.setPreferredSize(new Dimension(
				width/2,10));
		horz.add(pb);
		horz.add(Box.createHorizontalGlue());
		return horz;
	}
	
	public void setString(String message1,String message2){
		statusMessage1.setText(formatMessage(message1.trim()));
		statusMessage2.setText(formatMessage(message2.trim()));
	}
	
	private String formatMessage(String text){
		if(text.length() < 35 ){
			return text;
		}
		StringBuffer str = new StringBuffer(text);
		
		str.replace(30,text.length()-3,".....");
		return str.toString();
		
	}
	
	public void startProgress(){		
		pb.setIndeterminate(true);
		if(!"".equals(textMessage)){
			statusMessage2.setText(textMessage);
		}				
	}
	
	public void startDialog(){
		if(null != mainFrame){
			mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run(){
		//startProgress();
		//show();	
		setVisible(true);
	/*	try{
			synchronized(this){
				this.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
	}
	
	public void stopProgress(){				
	/*	try{
			synchronized(this){
				this.notify();		
			}
		}catch(Exception e){
			e.printStackTrace();
		}	*/	
//		System.out.print("stopping progress");		
		synchronized(this){
			
			try{
				
				this.wait(250);
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}
		if(null != mainFrame){
			mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		setVisible(false);			
	}
	
	class StopButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			stopButton.setEnabled(false);
			if(null != sl ){
				sl.onClickStopButton();
			}
			stopButton.setEnabled(true);
		}
	}
}
