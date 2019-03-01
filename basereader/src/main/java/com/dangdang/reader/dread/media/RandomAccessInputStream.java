package com.dangdang.reader.dread.media;

/* RandomAccessInputStream
 *
 * Created on May 21, 2004
 *
 * Copyright (C) 2004 Internet Archive.
 *
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 * Heritrix is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * any later version.
 *
 * Heritrix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Heritrix; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.util.Log;

/**
 * Wraps a RandomAccessFile with an InputStream interface.
 * 
 * @author gojomo
 */
public class RandomAccessInputStream extends InputStream {

	/**
	 * Reference to the random access file this stream is reading from.
	 */
	protected RandomAccessFile raf = null;
	

	/**
	 * When mark is called, save here the current position so we can go back on
	 * reset.
	 */
	private long markpos = -1;
	/**
	 * True if we are to close the underlying random access file when this
	 * stream is closed.
	 */
	private boolean sympathyClose;
	

	/**
	 * Constructor.
	 * 
	 * If using this constructor, caller created the RAF and therefore its
	 * assumed wants to control close of the RAF. The RAF.close is not called if
	 * this constructor is used on close of this stream.
	 * 
	 * @param raf
	 *            RandomAccessFile to wrap.
	 * @throws IOException
	 */
	public RandomAccessInputStream(RandomAccessFile raf) throws IOException {
		this(raf, false, 0, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File to get RAFIS on. Creates an RAF from passed file. Closes
	 *            the created RAF when this stream is closed.
	 * @throws IOException
	 */
	public RandomAccessInputStream(final File file) throws IOException {
		this(new RandomAccessFile(file, "r"), true, 0, file);
	}

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File to get RAFIS on. Creates an RAF from passed file. Closes
	 *            the created RAF when this stream is closed.
	 * @param offset
	 * @throws IOException
	 */
	public RandomAccessInputStream(final File file, final long offset)
			throws IOException {
		this(new RandomAccessFile(file, "r"), true, offset, file);
	}

	/**
	 * @param raf
	 *            RandomAccessFile to wrap.
	 * @param sympathyClose
	 *            Set to true if we are to close the RAF file when this stream
	 *            is closed.
	 * @param offset
	 * @throws IOException
	 */
	public RandomAccessInputStream(final RandomAccessFile raf,
			final boolean sympathyClose, final long offset, final File file) throws IOException {
		super();
		this.sympathyClose = sympathyClose;
		this.raf = raf;
		if (offset > 0) {
			//this.raf.seek(offset);
			position(offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		printLog(" read 1 ");
		return this.raf.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	
	public int read(byte[] b, int off, int len) throws IOException {
		
		int result = this.raf.read(b, off, len);
		/*printLog(" read 2, off=" + off + ",len=" + len 
				+ ",result=" + result + ",[" + preLen + "-" + raf.length() + "]");*/
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		printLog(" read 3 ");
		return this.raf.read(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		printLog(" skip( " + n);
		this.raf.seek(this.raf.getFilePointer() + n);
		return n;
	}

	public long position() throws IOException {
		return this.raf.getFilePointer();
	}

	public void position(long position) throws IOException {
		printLog(" position( " + position);
		this.raf.seek(position);
	}

	public int available() throws IOException {
		long amount = this.raf.length() - this.position();
		return (amount >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) amount;
	}

	public boolean markSupported() {
		return true;
	}

	public synchronized void mark(int readlimit) {
		try {
			printLog(" mark( " + readlimit);
			this.markpos = position();
		} catch (IOException e) {
			// Set markpos to -1. Will cause exception reset.
			this.markpos = -1;
		}
	}

	public synchronized void reset() throws IOException {
		if (this.markpos == -1) {
			throw new IOException("Mark has not been set.");
		}
		printLog(" reset " + markpos);
		position(this.markpos);
	}

	public void close() throws IOException {
		try {
			printLog(" close " );
			super.close();
		} finally {
			if (this.sympathyClose) {
				this.raf.close();
			}
		}
	}
	
	protected void printLog(String log) {
		Log.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		Log.e(getClass().getSimpleName(), log);
	}
}