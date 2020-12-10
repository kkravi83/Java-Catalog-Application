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

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import com.kcatalog.common.KCatalogStatusManager;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.common.KCatalogCommonUtility;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;

public class KCatalogGuiSplashScreen extends JWindow implements Runnable {
	private Dimension size = new Dimension(330,127 + 20);
	private String imageLoc = "";
	private String imageName = "startup.jpg";
	private ImageIcon imageIcon = null;
	private JLabel imageLabel = new JLabel();
	JProgressBar pbar = new JProgressBar();
	Dimension screenSize = null;	
	int noSteps = 0;
	int currStep = 1;
		
	
	public KCatalogGuiSplashScreen(Dimension screenSize,int noSteps) {
		setSize(size);
		imageLoc = KCatalogCommonUtility.getConfigPath();
		imageLoc = imageLoc + KCatalogConfigOptions.getSeparator()
						+	imageName;	
		this.screenSize = screenSize;		
		this.noSteps = noSteps;		
		
		setSizeForComponents();
		setVisibleLocation();
		setValueForComponents();
		addComponentsToFrame();
		this.getContentPane().setBackground(Color.white);
		//pbar.setBackground(Color.white);
	}
	
	private void setSizeForComponents(){
		pbar.setPreferredSize(new Dimension(
						(int)size.getWidth(),20));
	}
	public void endProgress(){
		pbar.setValue(noSteps);
		pbar.setValue(pbar.getMaximum());
//		System.out.println(pbar.getValue());
	}
	
	public void incrementProgress(){
		pbar.setValue(currStep++);
	}
	
	public void incrementAndSetText(String message){				
		pbar.setValue(currStep++);
		setText(message);
			
	}
	
	public void setText(String message){
		pbar.setString(message);
	}
	
	private void addComponentsToFrame(){
		Box boxPanel = Box.createVerticalBox();
		boxPanel.add(imageLabel);
		boxPanel.add(pbar);		
		this.getContentPane().add(boxPanel);
	}
	
	private void setVisibleLocation(){
		this.setLocation((int)( (screenSize.getWidth()/2) -  (size.getWidth()/2)),
							(int)( (screenSize.getHeight()/2) - 
										(size.getHeight()/2)));
	}
	
	private void setValueForComponents(){
		imageIcon = new ImageIcon(imageLoc);
		imageLabel = new JLabel(imageIcon);
		pbar.setMinimum(1);
		pbar.setMaximum(noSteps);
		pbar.setStringPainted(true);
		pbar.setIndeterminate(false);
		pbar.setVisible(true);
		
	}
	
	

	public void run(){
		
	}
}
