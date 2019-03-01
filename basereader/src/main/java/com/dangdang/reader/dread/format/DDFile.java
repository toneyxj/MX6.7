package com.dangdang.reader.dread.format;

import java.io.File;

import com.dangdang.execption.FileFormatException;
import com.dangdang.zframework.log.LogM;

public class DDFile {

	public static final int CATEGORY_NORMAL = 0;
	public static final int CATEGORY_COMICS = 1;

	private String filePath;
	private long fileSize = 0;
	
	private FileType fileType = FileType.EPUB;
	
	public DDFile(String filePath) throws FileFormatException {
		super();
		this.filePath = filePath;
		init();
	}

	private void init() throws FileFormatException {
		
		final String extName = getExtName().toLowerCase();
		if(extName.equals(FileType.TXT.getName())){
			fileType = FileType.TXT;
		} else if (extName.equals(FileType.EPUB.getName())){
			fileType = FileType.EPUB;
		} else if (extName.equals(FileType.PDF.getName())){
			fileType = FileType.PDF;
		}else if (extName.equals(FileType.PART.getName())){
			fileType = FileType.PART;
		}
		
		try {
			final File file = new File(filePath);
			fileSize = file.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getExtName() throws FileFormatException{
		int index = filePath.lastIndexOf(".");
		String extName = "";
		if(index == -1 || filePath.length() - index > 10){
			extName= FileType.PART.name();
//			throw new FileFormatException(" file format not support : " + filePath);
		}else{
			try {
				extName = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
			} catch (Exception e) {
				LogM.e(getClass().getSimpleName(), " getExtName() error ");
				throw new FileFormatException(" file format error : " + filePath);
			}
		}

		return extName;
	}

	public String getFilePath() {
		return filePath;
	}
	

	/*public void setFilePath(String filePath) {
		this.filePath = filePath;
	}*/

	public FileType getFileType() {
		return fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	@Override
	public boolean equals(Object o) {
		
		if(o == null || !(o instanceof DDFile)){
			return false;
		}
		final DDFile other = (DDFile) o;
		return fileType == other.getFileType() && filePath.equals(other.filePath);
	}

	public boolean equalsType(DDFile ddFile){
		
		return fileType == ddFile.getFileType();
	}
	
	public static boolean isEpub(String bookFile){
		
		DDFile ddFile = newDDFile(bookFile);
		
		return isEpub(ddFile);
	}
	
	public static boolean isEpub(DDFile ddFile){
		
		boolean isEpub = false;
		if(ddFile != null && ddFile.getFileType() == FileType.EPUB){
			isEpub = true;
		}
		return isEpub;
		
	}
	
	public static boolean isTxt(String bookFile){
		
		DDFile ddFile = newDDFile(bookFile);
		
		return isTxt(ddFile);
	}
	
	public static boolean isTxt(DDFile ddFile){
		
		boolean isTxt = false;
		if(ddFile != null && ddFile.getFileType() == FileType.TXT){
			isTxt = true;
		}
		
		return isTxt;
	}
	
	public static boolean isPdf(DDFile ddFile){
		return ddFile != null && ddFile.getFileType() == FileType.PDF;
	}
	public static boolean isPart(DDFile ddFile){
		return ddFile != null && ddFile.getFileType() == FileType.PART;
	}
	private static DDFile newDDFile(String bookFile) {
		DDFile ddFile = null;
		try {
			ddFile = new DDFile(bookFile);
		} catch (FileFormatException e) {
			e.printStackTrace();
		}
		return ddFile;
	}

	public static enum FileType {
		
		TXT("txt"), EPUB("epub"), PDF("pdf"),PART("part");
		
		FileType(String name){
			mName = name;
		}
		
		private String mName;
		
		public String getName(){
			return mName;
		}
		
	}
	
}
