package com.dangdang.reader.dread.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dangdang.execption.UnZipException;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Book.ResourceFile;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.EpubBook;
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.format.epub.EpubChapter;

/**
 * xml： dom
 */
public class ParserEpubN {
	
	
	private final String ENCODING = "UTF-8";
	//private String mBookDir = "";
	//private String mContainerPath = "";
	private String mOpfPath = "";
	private String mNcxPath = "";
	private ZipUtil mUnZip;
	
	public ParserEpubN() {
		
	}
	
	public boolean hasFileDirectory(String epubFile, String bookDir){
		
		File file = new File(bookDir);
		
		return file.isDirectory();
	}
	
	public boolean epubExists(String epubFile){
		
		File file = new File(epubFile);
		
		return file.exists();
	}
	
	public void unZipEpub(String epubFile, String bookDir) throws UnZipException{
		
		printLog(" start unZipEpub ");
		mUnZip = new ZipUtil();
		mUnZip.unZip(epubFile, bookDir);
		printLog(" end unZipEpub ");
	}
	
	public void parseContainerOpfAndNcx(String bookDir){
		
		/*parseContainer(bookDir);
		parserOpf(bookDir);
		parseNcx(bookDir);*/
		
	}
	
	public String parseVersion(String bookDir){
		
		String version = "";
		String versionDir = bookDir + "dangdang";
		File file = new File(versionDir);
		if(file.exists()){
			FileInputStream inStream = null;
			try {
				inStream = new FileInputStream(file);
				byte[] buffer = new byte[inStream.available()];
				int ret = inStream.read(buffer);
				if(ret > -1){
					version = new String(buffer, ENCODING);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		printLog(" [ parseVersion  "+ version +" ] ");
		
		return version;
		
	}
	
	public Map<String, ResourceFile> parseContainer(String bookDir) throws FileNotFoundException{
		
		Map<String, ResourceFile> resourceMap = null;
		
		//mBookDir = bookDir;
		String mContainerPath = bookDir + "META-INF/container.xml";
		/*String containerParentPath = mBookDir + "META-INF/";
		
		final File containerParentFile = new File(containerParentPath);
		if(!containerParentFile.exists()){
			printLog("[ containerParentPath not found ("+ containerParentPath +") ]");
			throw new FileNotFoundException("[ containerParentPath file not found ("+ containerParentPath +") ]");
		}
		
		final File[] containerFiles = containerParentFile.listFiles();
		if(containerFiles == null || containerFiles.length == 0){
			printLog("[ container.xml file not found ]");
			throw new FileNotFoundException("[ container.xml file not found ]");
		}
		
		final File containerFile = containerFiles[0];
		mContainerPath = containerFile.getAbsolutePath();*/
		
		final File containerFile = new File(mContainerPath);
		if(!containerFile.exists()){
			printLog("[ containerParentPath not found ("+ containerFile +") ]");
			throw new FileNotFoundException("[ containerParentPath file not found ("+ containerFile +") ]");
		}
		
		resourceMap = new HashMap<String, Book.ResourceFile>();
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(containerFile);
			Document doc = buildDocument(inStream);
			Element rootFileE = (Element) doc.getElementsByTagName("rootfile").item(0);
			mOpfPath = bookDir + rootFileE.getAttribute("full-path");
			
			/*NodeList resFiles = doc.getElementsByTagName("file");
			int len = resFiles.getLength();
			for (int i = 0; i < len; i++) {
				Element resElement = (Element) resFiles.item(i);
				String dir = (bookDir + resElement.getAttribute("uri")).toLowerCase();
				boolean isEncrtyped = "true".equals(resElement.getAttribute("encrypt"));
				resourceMap.put(dir, new ResourceFile(dir, isEncrtyped));
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		
		return resourceMap;
	}

	public List<Chapter> parserOpf(String path) throws FileNotFoundException{
		
		List<Chapter> htmlList = null;
		Map<String, String> itemMaps = null;
		
		File file = new File(path);
		if(!file.exists()){
			printLog("[ parserOpf() file not found ("+ path +") ]");
			throw new FileNotFoundException("[ parserOpf() file not found ("+ path +") ]");
		}
		
		String opfParent = file.getParent() + File.separator;
		printLog("[ parserOpf() opfParent ("+ opfParent +") ]");
		
		InputStream inStream = null;
		try {
			htmlList = new ArrayList<Chapter>();
			itemMaps = new HashMap<String, String>();
			
			int count = 0;
			inStream = new FileInputStream(file);
			Document doc = buildDocument(inStream);
			
			final NodeList itemList = doc.getElementsByTagName("item");
			for(int i = 0, len = itemList.getLength(); i < len; i++){
				Element e = (Element) itemList.item(i);
				String id = e.getAttribute("id");
				String href = e.getAttribute("href");
				String hrefPath = opfParent + href;
				itemMaps.put(id, hrefPath);
			}
			
			Element ncxE = (Element) doc.getElementsByTagName("spine").item(0);
			final String ncxId = ncxE.getAttribute("toc");
			mNcxPath = itemMaps.get(ncxId);
			
			
			final NodeList itemrefItems = doc.getElementsByTagName("itemref");
			for(int i = 0, len = itemrefItems.getLength(); i < len; i++){//paytip.html
				Element e = (Element)itemrefItems.item(i);
				String idref = e.getAttribute("idref");
				if(Book.PAYTIP.equalsIgnoreCase(idref)){
					continue;
				}
				String hrefPath = itemMaps.get(idref);
				EpubChapter html = new EpubChapter(hrefPath);
				if(!htmlList.contains(html)){
					htmlList.add(html);
				}
			}
			
			
			printLog("[ count = "+ count +" ]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		
		return htmlList;
	}
	
	public List<BaseNavPoint> parseNcx(String path) throws FileNotFoundException{
		
		List<BaseNavPoint> navPointList = null;
		File file = new File(path);
		if(!file.exists()){
			printLog("[ parseNcx() file not found ("+ path +") ]");
			throw new FileNotFoundException("[ parseNcx() file not found ("+ path +") ]");
		}
		
		String ncxParent = file.getParent()  + File.separator;
		printLog("[ parseNcx() opfParent ("+ ncxParent +") ]");
		
		InputStream inStream = null;
		try {
			navPointList = new ArrayList<BaseNavPoint>();
			
			inStream = new FileInputStream(file);
			Document doc = buildDocument(inStream);
			
			EpubNavPoint navPoint = null;
			final Element navMapE = (Element)doc.getElementsByTagName("navMap").item(0);
			final NodeList nodeList = navMapE.getChildNodes();//doc.getElementsByTagName("navPoint");
			
			for(int i = 0, len = nodeList.getLength(); i < len; i++){
				
				Node node = nodeList.item(i);
				if(node instanceof Element){
					
					navPoint = new EpubNavPoint();
					Element e = (Element)node;
					try {
						buildOneNavPoint(ncxParent, navPoint, e);
					} catch (Exception e1) {
						e1.printStackTrace();
						continue;
					}
					
					final NodeList subNodes = e.getElementsByTagName("navPoint");
					if(subNodes == null){
						continue;
					}
					EpubNavPoint subNav = null;
					for(int j = 0, sLen = subNodes.getLength(); j < sLen; j++){
						
						Element subE = (Element) subNodes.item(j);
						if(e.equals(subE)){
							continue;
						}
						
						subNav = new EpubNavPoint(); 
						subNav.parentNav = navPoint;
						try {
							buildOneNavPoint(ncxParent, subNav, subE);
						} catch (Exception e1) {
							e1.printStackTrace();
							continue;
						}
						
						navPoint.addSubNavPoint(subNav);
					}
					
					navPointList.add(navPoint);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return navPointList;
	}

	private void buildOneNavPoint(String ncxParent, EpubNavPoint navPoint,
			final Element e) {
		final Element textE = (Element) e.getElementsByTagName("text").item(0);
		final Element contentE = (Element) e.getElementsByTagName("content").item(0);
		
		navPoint.lableText = textE.getFirstChild().getNodeValue();
		
		String src = contentE.getAttribute("src");
//		printLog(name + " parseNavPoint src ["+ src +"]");
		String shortSrc = src;
		if(src.contains("#")){
			int lastIndex = src.lastIndexOf("#");
			if(lastIndex != -1){
				shortSrc = src.substring(0, lastIndex);
			} 
			navPoint.anchor = src.substring(lastIndex + 1, src.length());
		}
		navPoint.shortSrc = shortSrc;
		navPoint.fullSrc = ncxParent + src;
	}
	
	private void closeStream(InputStream inStream) {
		if(inStream != null){
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private Document buildDocument(InputStream inStream) {
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docBuilder.parse(inStream);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * /sdcard/epubtest/1900089315_new/OPS/Content.opf
	 * @return
	 */
	public String getOpfPath() {
		return mOpfPath;
	}

	public String getNcxPath() {
		return mNcxPath;
	}
	
	public String getOpsPath(){
		String path = "";
		if(mOpfPath != null && mOpfPath.trim().length() > 0){
			path = mOpfPath.substring(0, mOpfPath.lastIndexOf("/") + 1);
		}
		return path;
	}
	
	public void destory(){
		if(mUnZip != null){
			mUnZip.finishUnZip();
		}
	}

	public void printLog(String log){
		//LogM.i(getClass().getSimpleName(), log);
	}
	
	public String readInnerZipFile(String zipfile, String filename) {
		ZipFile innerZipFile = null;
		InputStream input = null;
		String content = "";
		try {
			innerZipFile = new ZipFile(zipfile);
			@SuppressWarnings("rawtypes")
			Enumeration entries = innerZipFile.entries();
			ZipEntry entryIn = null;
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();
				if (entry.getName().compareToIgnoreCase(filename) == 0) {
					entryIn = entry;
					break;
				}
			}

			if (entryIn != null) {
				input = innerZipFile.getInputStream(entryIn);
				byte[] b = new byte[(int)entryIn.getSize()];
				int len = input.read(b);
				if (len > 0) {
					content = new String(b, ENCODING);
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (innerZipFile != null) {
					innerZipFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return content;
	}
	
	private String parserVersionInner(final InputStream inStream, final int fileSize){
		
		String version = "";
		//InputStream inStream = null;
		try {
			//inStream = new FileInputStream(file);
			byte[] buffer = new byte[fileSize];
			int ret = inStream.read(buffer);
			if(ret > -1){
				version = new String(buffer, ENCODING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		printLog(" [ parseVersion  "+ version +" ] ");
		return version;
	}
	
	private Map<String, ResourceFile> parseContainerInner(final InputStream inStream) throws FileNotFoundException{
		
		Map<String, ResourceFile> resourceMap = null;
		
		resourceMap = new HashMap<String, Book.ResourceFile>();
		try {
			Document doc = buildDocument(inStream);
			Element rootFileE = (Element) doc.getElementsByTagName("rootfile").item(0);
			//mOpfPath = mBookDir + rootFileE.getAttribute("full-path");
			mOpfPath = rootFileE.getAttribute("full-path");
			
			/*NodeList resFiles = doc.getElementsByTagName("file");
			int len = resFiles.getLength();
			for (int i = 0; i < len; i++) {
				Element resElement = (Element) resFiles.item(i);
				//String dir = (mBookDir + resElement.getAttribute("uri")).toLowerCase();
				String dir = (resElement.getAttribute("uri")).toLowerCase();
				boolean isEncrtyped = "true".equals(resElement.getAttribute("encrypt"));
				resourceMap.put(dir, new ResourceFile(dir, isEncrtyped));
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		
		return resourceMap;
	}
	
	private List<Chapter> parserOpfInner(final InputStream inStream, final String opsPath) throws FileNotFoundException{
		
		List<Chapter> htmlList = null;
		Map<String, String> itemMaps = null;
		
		try {
			htmlList = new ArrayList<Chapter>();
			itemMaps = new HashMap<String, String>();
			
			int count = 0;
			Document doc = buildDocument(inStream);
			
			final NodeList itemList = doc.getElementsByTagName("item");
			for(int i = 0, len = itemList.getLength(); i < len; i++){
				Element e = (Element) itemList.item(i);
				String id = e.getAttribute("id");
				String href = e.getAttribute("href");
				String hrefPath = href;//String hrefPath = opfParent + href;
				itemMaps.put(id, hrefPath);
			}
			
			Element ncxE = (Element) doc.getElementsByTagName("spine").item(0);
			final String ncxId = ncxE.getAttribute("toc");
			mNcxPath = itemMaps.get(ncxId);
			
			final NodeList itemrefItems = doc.getElementsByTagName("itemref");
			for(int i = 0, len = itemrefItems.getLength(); i < len; i++){//paytip.html
				Element e = (Element)itemrefItems.item(i);
				String idref = e.getAttribute("idref");
				if(Book.PAYTIP.equalsIgnoreCase(idref)){
					continue;
				}
				String hrefPath = itemMaps.get(idref);
				EpubChapter html = new EpubChapter(opsPath + hrefPath);
				//html.path = opsPath + hrefPath;//hrefPath;
				if(!htmlList.contains(html)){
					htmlList.add(html);
				}
			}
			
			
			printLog("[ count = "+ count +" ]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		
		return htmlList;
	}
	
	private List<BaseNavPoint> parseNcxInner(final InputStream inStream, final String opsPath) throws FileNotFoundException{
		
		List<BaseNavPoint> navPointList = null;
		try {
			navPointList = new ArrayList<BaseNavPoint>();
			
			Document doc = buildDocument(inStream);
			
			String ncxParent = opsPath;
			EpubNavPoint navPoint = null;
			final Element navMapE = (Element)doc.getElementsByTagName("navMap").item(0);
			final NodeList nodeList = navMapE.getChildNodes();//doc.getElementsByTagName("navPoint");
			
			for(int i = 0, len = nodeList.getLength(); i < len; i++){
				
				Node node = nodeList.item(i);
				if(node instanceof Element){
					
					navPoint = new EpubNavPoint();
					Element e = (Element)node;
					try {
						buildOneNavPoint(ncxParent, navPoint, e);
					} catch (Exception e1) {
						e1.printStackTrace();
						continue;
					}
					
					final NodeList subNodes = e.getElementsByTagName("navPoint");
					if(subNodes == null){
						continue;
					}
					EpubNavPoint subNav = null;
					for(int j = 0, sLen = subNodes.getLength(); j < sLen; j++){
						
						Element subE = (Element) subNodes.item(j);
						if(e.equals(subE)){
							continue;
						}
						
						subNav = new EpubNavPoint(); 
						subNav.parentNav = navPoint;
						try {
							buildOneNavPoint(ncxParent, subNav, subE);
						} catch (Exception e1) {
							e1.printStackTrace();
							continue;
						}
						
						navPoint.addSubNavPoint(subNav);
					}
					
					navPointList.add(navPoint);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
		}
		return navPointList;
	}
	
	public final static String VersionFileName = "dangdang";
	public final static String ContainerPath = "META-INF/container.xml";
	
	public void buildBook(final EpubBook book, final String epubFile, final String bookDir) throws FileNotFoundException{
		
		if(epubExists(epubFile)){
			buildBookInner(book, epubFile);
		} else {
			buildBookNoInner(book, bookDir);
		}
		
		
	}
	
	public void buildBookNoInner(final EpubBook book, final String bookDir) throws FileNotFoundException{
		
		final String epubVersion = parseVersion(bookDir);
		book.setVersion(epubVersion);
		
		//Map<String, ResourceFile> fileMap = 
		parseContainer(bookDir);
		//mOneBook.setFileMap(fileMap);
		
		String opfPath = getOpfPath();
		List<Chapter> chapterList = parserOpf(opfPath);
		book.setChapterList(chapterList);
		
		String ncxPath = getNcxPath();
		List<BaseNavPoint> navPointList = parseNcx(ncxPath);
		book.setNavPointList(navPointList);
		book.setOpsPath(getOpsPath());
		
	}

	private void buildBookInner(final EpubBook book, final String epubFile) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(epubFile);
			final ZipEntry versionEntry = zipFile.getEntry(VersionFileName);
			final String version = parserVersionInner(zipFile.getInputStream(versionEntry), (int)versionEntry.getSize());
			
			final ZipEntry containerEntry = getContainerEntry(zipFile.entries());
			final Map<String, ResourceFile> fileMap = parseContainerInner(zipFile.getInputStream(containerEntry));
			
			final String tOps = mOpfPath.substring(0, mOpfPath.lastIndexOf("/") + 1);
			final String opsPath = epubFile + ":" + tOps;
			
			final ZipEntry opfEntry = zipFile.getEntry(mOpfPath);//getOpfEntry(zipFile.entries());
			final List<Chapter> chapterList = parserOpfInner(zipFile.getInputStream(opfEntry), opsPath);
			
			final ZipEntry ncxEntry = zipFile.getEntry(tOps + mNcxPath);//getNcxEntry(zipFile.entries());
			final List<BaseNavPoint> navPointList = parseNcxInner(zipFile.getInputStream(ncxEntry), opsPath);
			
			printLog(" buildBook " + fileMap + ", chapterList = " + chapterList + ", navPointList = " + navPointList);
			
			
			
			book.setVersion(version);
			//book.setFileMap(fileMap);
			book.setChapterList(chapterList);
			book.setNavPointList(navPointList);
			book.setOpsPath(opsPath);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(zipFile != null){
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private ZipEntry getContainerEntry(Enumeration<? extends ZipEntry> entries){
		ZipEntry temEntry = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			printLog(" getContainerEntry entry = " + entry);
			if(ContainerPath.equalsIgnoreCase(entry.getName())){
				temEntry = entry;
				break;
			} 
		}
		return temEntry;
	}
	
	private ZipEntry getOpfEntry(Enumeration<? extends ZipEntry> entries){
		if(mOpfPath == null){
			return null;
		}
		
		ZipEntry temEntry = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			printLog(" getOpfEntry entry = " + entry);
			
			if(entry.getName().equalsIgnoreCase(mOpfPath)
					|| entry.getName().toLowerCase().contains(mOpfPath.toLowerCase())){
				temEntry = entry;
				break;
			} 
		}
		return temEntry;
	}
	
	private ZipEntry getNcxEntry(Enumeration<? extends ZipEntry> entries){
		
		if(mNcxPath == null){
			return null;
		}
		
		ZipEntry temEntry = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			printLog(" getNcxEntry entry = " + entry);
			
			if(entry.getName().toLowerCase().contains(mNcxPath.toLowerCase()) 
							|| entry.getName().equalsIgnoreCase(mNcxPath)){
				temEntry = entry;
				break;
			}
		}
		return temEntry;
	}
	
	private ZipEntry getOneEntry(Enumeration<? extends ZipEntry> entries, String findFlag){
		
		ZipEntry temEntry = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			printLog(" getOneEntry entry = " + entry);
			
			if(entry.getName().toLowerCase().contains(findFlag.toLowerCase()) 
							|| entry.getName().equalsIgnoreCase(findFlag)){
				temEntry = entry;
				break;
			}
		}
		return temEntry;
		
	}
	
	public boolean checkExistOne(String epubFile, String findFlag){
		
		boolean exist = false;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(epubFile);
			ZipEntry tmpEntry = getOneEntry(zipFile.entries(), findFlag);
			exist = (tmpEntry != null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(zipFile != null){
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return exist;
	}
	
}
