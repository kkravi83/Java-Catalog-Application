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
import com.kcatalog.xmlschema.KCatalogXMLSchemaBase;
import com.kcatalog.common.KCatalogConfigOptions;
import com.kcatalog.common.KCatalogConstants;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import com.kcatalog.common.KCatalogException;
import com.kcatalog.fileutils.Mp3FileDto;
import com.kcatalog.xmlschema.KCatalogSchemaMp3FileDto;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
//import com.digitprop.tonic.TonicLookAndFeel;
//import com.birosoft.liquid.LiquidLookAndFeel;
import javax.swing.UIManager;
import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

public class KCatalogLookAndFeelManager extends	
 						     KCatalogXMLSchemaBase{
	
	public static String root = "KCatalog";
	public static String lnfElementStr = "lookAndFeel";
	public static String lnfNameStr = "name";
	public static String lnfClassStr = "class";
	public static String defaultIndicStr = "default";
	private static String fileName = "lookAndFeel.xml";
	
	HashMap lnfMap = new HashMap();
	private String defaultLNF = "System Look And Feel";
	
	public KCatalogLookAndFeelManager(){
		if(!exist()){
			createLookAndFeelXML();
		}
		populateLNFMap();
	}
			
/*	public String setDefaultClassName(){
		
	}*/
	public ArrayList getLNFNames(){
		Iterator it = lnfMap.keySet().iterator();
		ArrayList list = new ArrayList();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	private void populateLNFMap(){
		Document doc = this.getDocumentFromFactory(getLNFPath());
		Element root = doc.getDocumentElement();
		NodeList list = root.getElementsByTagName(lnfElementStr);		
		for(int i=0;i < list.getLength();i++){
			Node node = list.item(i);
			NamedNodeMap attrMap = node.getAttributes();
			String name = attrMap.getNamedItem(lnfNameStr).getNodeValue();
			String className = attrMap.getNamedItem(lnfClassStr).getNodeValue();
			String defaultIndic = attrMap.getNamedItem(defaultIndicStr).getNodeValue();
			if(KCatalogConstants.SYSTEM_LNF_NAME.equalsIgnoreCase(name)){
				className = UIManager.getSystemLookAndFeelClassName();
			}
			lnfMap.put(name,className);			
			if( Boolean.valueOf(defaultIndic).booleanValue()
					==	true){
				defaultLNF = name;
			}
		}
	}
	
	public void setDefaultLNF(String nameFromGui){
		Document doc = this.getDocumentFromFactory(getLNFPath());
		Element root = doc.getDocumentElement();
		NodeList list = root.getElementsByTagName(lnfElementStr);
		for(int i=0;i < list.getLength();i++){
			Node node = list.item(i);
			NamedNodeMap attrMap = node.getAttributes();
			String name = attrMap.getNamedItem(lnfNameStr).getNodeValue();
			String className = attrMap.getNamedItem(lnfClassStr).getNodeValue();
			String defaultIndic = attrMap.getNamedItem(defaultIndicStr).getNodeValue();
		/*	System.out.println("className " +
							className +
								" nameFromGui "+
									nameFromGui );	*/
			if(name.equals(nameFromGui)){
				defaultIndic = String.valueOf(true);
			}else{
				defaultIndic = String.valueOf(false);
			}
			attrMap.getNamedItem(defaultIndicStr).setNodeValue(defaultIndic);			
		}
		this.writeDocToFile(doc,getLNFPath());	
	}
	
	public String getDefaultLNFName(){
		return defaultLNF;
	}
	
	public String getDefaultLNFClassName(){
		String className = lnfMap.get(defaultLNF).toString();
		className = getClassNameForSkinLNF(defaultLNF,className);
		return className;
	}
	
	private String getClassNameForSkinLNF(String lnf,String className){		
		String classNm = className;
		if( lnf.trim().startsWith("Skin Look And Feel")){
		//	System.out.println("Skin Look And Fee");
			try{
				className =
				  className.replaceAll("'/",KCatalogConfigOptions.getSeparator());
				SkinLookAndFeel.setSkin(
          			  	SkinLookAndFeel.loadThemePackDefinition(
				                      new File(className).toURL()));
				classNm = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return classNm;
	}
	
	public boolean exist(){
		return new File(getLNFPath()).exists();		
	}
	
	private String getLNFPath(){
		String databaseLoc = KCatalogCommonUtility.getConfigPath();
		return databaseLoc + 
						KCatalogConfigOptions.getSeparator() +
							fileName;
	//	return 	fileName;				
	}
	
	public void setDefaultLookAndFeel(){
	}	
	
	public void createLookAndFeelXML(){
		Document doc = getNewDocumentFromFactory();
		Element rootElem = doc.createElement(root);
		Element systemLNF = getLNFElement(doc,"System Look And Feel",
										"SystemLookAndFeel",
										true);	
		Element crossLNF = getLNFElement(doc,"Cross Platform Look And Feel",
										UIManager.getCrossPlatformLookAndFeelClassName(),
										false);
	/*	Element tonicLNF = getLNFElement(doc,"Tonic Look And Feel",
										new TonicLookAndFeel().getClass().getName(),
										false);
		Element liquidLNF = getLNFElement(doc,"Liquid Look And Feel",
										new LiquidLookAndFeel().getClass().getName(),
										false);*/
		doc.appendChild(rootElem);
		rootElem.appendChild(systemLNF);
		rootElem.appendChild(crossLNF);
/*		rootElem.appendChild(tonicLNF);
		rootElem.appendChild(liquidLNF);*/
		this.writeDocToFile(doc,getLNFPath());
		
	}
	
	private Element getLNFElement(Document doc,String name,String className,boolean defaultIndic){
		Element lnfSystem = doc.createElement(lnfElementStr);
		lnfSystem.setAttribute(lnfNameStr,name);
		lnfSystem.setAttribute(lnfClassStr,className);
		lnfSystem.setAttribute(defaultIndicStr,String.valueOf(defaultIndic));	
		return lnfSystem;		
	}
}

