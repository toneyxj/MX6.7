package com.dangdang.reader.dread.format;

import android.content.Context;

import com.dangdang.reader.dread.format.DDFile.FileType;
import com.dangdang.reader.dread.format.epub.EpubBook;
import com.dangdang.reader.dread.format.epub.EpubBookManagerNew;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartBookManager;
import com.dangdang.reader.dread.format.txt.TxtBook;
import com.dangdang.reader.dread.format.txt.TxtBookManagerNew;

public class ManagerFactory {
	
	
	public static Book createBook(final DDFile ddFile){
		
		Book book = null;
		final FileType fileType = ddFile.getFileType();
		if(fileType == FileType.EPUB){
			book = new EpubBook();
		} else if(fileType == FileType.TXT){
			book = new TxtBook();
		}else if(fileType==FileType.PART){
			////TODO 拆分新增
			book = new PartBook();
		}
		book.setFileSize(ddFile.getFileSize());
		
		return book;
	}
	
	/*public static BookManager create(final DDFile ddFile, final Book book){
		BookManager baseManager = null;
		final FileType fileType = ddFile.getFileType();
		if(fileType == FileType.EPUB){
			baseManager = new EpubBookManager(book);
		} else if(fileType == FileType.TXT){
			baseManager = new TxtBookManager(book);
		}
		return baseManager;
	}*/
	
	public static BaseBookManager create(Context context, final DDFile ddFile, final IBook book){
		BaseBookManager baseManager = null;
		final FileType fileType = ddFile.getFileType();
		if(fileType == FileType.EPUB){
			baseManager = new EpubBookManagerNew(context, (Book)book);
		} else if(fileType == FileType.TXT){
			baseManager = new TxtBookManagerNew(context, (Book)book);
		}else if (fileType==FileType.PART){
			baseManager = new PartBookManager(context, (Book)book);
		}
		return baseManager;
	}
	

}
