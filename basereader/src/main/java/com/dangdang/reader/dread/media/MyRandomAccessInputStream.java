package com.dangdang.reader.dread.media;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.dangdang.reader.dread.jni.EpubWrap;

/**
 * @author luxu
 */
public class MyRandomAccessInputStream extends RandomAccessInputStream {

	
	private EpubWrap mDRWrap;
	private RandomAccessFile tmpRaf = null;
	
	static {
		System.loadLibrary("ddlayoutkit");
	}
	
	public MyRandomAccessInputStream(File file, long offset) throws IOException {
		this(new RandomAccessFile(file, "r"), true, offset, file);
	}

	public MyRandomAccessInputStream(File file) throws IOException {
		this(new RandomAccessFile(file, "r"), true, 0, file);
	}

	public MyRandomAccessInputStream(RandomAccessFile raf) throws IOException {
		this(raf, false, 0, null);
	}

	public MyRandomAccessInputStream(RandomAccessFile raf,
			boolean sympathyClose, long offset, File file) throws IOException {
		super(raf, sympathyClose, offset, file);
		
		if(file != null){
			tmpRaf = new RandomAccessFile(file, "r");
		}
		mDRWrap = new EpubWrap();
	}
	
	@Override
	public void position(long position) throws IOException {
		super.position(position);
		tmpRaf.seek(position);
	}
	
	@Override
	public long skip(long n) throws IOException {
		long rs = super.skip(n);
		this.tmpRaf.seek(this.tmpRaf.getFilePointer() + n);
		return rs;
	}

	/**
	 * 最小解密大小
	 */
	private final int minUnit = 64*1024;//64K
	private int preLen = 0;
	private final int unitLen = 5*minUnit;//
	private byte[] unitCacheByte;
	
	public int read(byte[] b, int off, int len) throws IOException {
		
		int result = len;
		
		if(preLen % unitLen == 0){
			int tmpBytesLen = getTmpBytesLen();
			byte[] tmpBytes = new byte[tmpBytesLen];
			printLog(" create bytes 1, " + tmpBytesLen);
			int unitResult = tmpRaf.read(tmpBytes, 0, tmpBytesLen);
			byte[] tmpDestBytes = getTmpDestBytes(tmpBytesLen);
			if(mDRWrap.decryptMedia(tmpBytes, tmpDestBytes)){
				unitCacheByte = tmpDestBytes;
			} else {
				printLogE(" read byte decrypt false unitLen=" + unitLen + ",unitResult=" + unitResult + "," + tmpBytesLen);
			}
			printLog(" read 2 tmpRaf , unitResult " + unitResult + ",decryptLen=" + unitCacheByte.length + ",[" + preLen + "-" + raf.length() + "]");
			tmpBytes = null;
		} else {
			//printLogE(" read byte ------- " + ",[" + preLen + "-" + raf.length() + "]");
		}
		subBytes(unitCacheByte, b, preLen % unitLen, len);
		preLen+=result;
		
		//result = super.read(b, off, len);
		
		/*printLog(" read 2, off=" + off + ",len=" + len 
				+ ",result=" + result + ",[" + preLen + "-" + raf.length() + "]");*/
		
		return result;
	}

	protected int getTmpBytesLen() throws IOException {
		int tmpUnitLen = unitLen;
		long surplus = tmpRaf.length() - preLen;
		tmpUnitLen = (int) (surplus < unitLen ? surplus : unitLen);
		return tmpUnitLen;
	}

	protected byte[] getTmpDestBytes(int unitResult) {
		byte[] tmpDestBytes = null;
		if(unitCacheByte != null && unitResult == unitCacheByte.length){
			tmpDestBytes = unitCacheByte;
		} else {
			tmpDestBytes = new byte[unitResult];
			printLog(" create bytes 2, " + unitResult);
		}
		return tmpDestBytes;
	}
	
	protected byte[] subBytes(byte[] src, byte[] result, int begin, int count) {
        for (int i = begin; i < begin + count; i++){
        	result[i - begin] = src[i];
        }
        return result;
    }
	
	protected void copyBytes(byte[] src, byte[] result){
		for(int i = 0; i < src.length; i++){
			result[i] = src[i];
		}
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		
		if(tmpRaf != null){
			tmpRaf.close();
		}
		unitCacheByte = null;
		System.gc();
		System.gc();
	}
}
