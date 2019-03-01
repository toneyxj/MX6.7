package com.dangdang.reader.dread.media;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.dangdang.reader.dread.holder.MediaHolder.MediaType;
import com.dangdang.reader.dread.media.FileEntry.FileType;
import com.dangdang.reader.dread.media.StreamOverHttp.PrepareListener;

/**
 * @author luxu
 */
public class AudioService extends BaseMediaService {
	
	private MediaPlayer mPlayer;

	public AudioService() {
		super();
	}
	
	public void prepare(String innerPath, String path, int bookType, PrepareListener l) throws IOException{
		openServer(innerPath, path, MediaType.Audio, FileType.FileInner, bookType, l);
	}
	
	public void init(String innerPath, String path){
		initMedia();
		loadMedia();
	}

	protected void initMedia(){
		if(mPlayer == null){
			mPlayer = new MediaPlayer();
		}
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
	
	protected void loadMedia() {
		try {
			mPlayer.reset();
			mPlayer.setDataSource(getLocalServerPath());//"/sdcard/aVideo/李荣浩-模特(live).mp3"
			mPlayer.prepare();//mPlayer.prepareAsync();
			
			/*int duration = mPlayer.getDuration();
			int position = mPlayer.getCurrentPosition();
			printLog(" loadMedia " + duration + "," + position + "," + getLocalServerPath());
			onDurtion();*/
			//seekBar.setMax(duration);
			//seekBar.setProgress(position);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void onDurtion(){
		MediaListener l = getMediaListener();
		if(l != null){
			l.onDuration(mPlayer.getDuration());
		}
	}
	
	public void playAndPause() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
			} else {
				mPlayer.start();
			}
		} else {
			printLogE(" playAndPause Player is null ");
		}
	}
	
	public boolean isPlaying(){
		return mPlayer != null && mPlayer.isPlaying();
	}
	
	
	public void seekTo(int msec){
		if(mPlayer != null){
			mPlayer.seekTo(msec);
		}
	}
	
	public void stop() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	public MediaPlayer getPlayer(){
		return mPlayer;
	}
	
	public void destroy(){
		stop();
		closeServer();
	}
	
	public void setOnCompletionListener(OnCompletionListener l){
		if(mPlayer != null){
			mPlayer.setOnCompletionListener(l);
		} else {
			printLogE(" setOnCompletionListener l is null ");
		}
	}
	
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l){
		if(mPlayer != null){
			mPlayer.setOnBufferingUpdateListener(l);
		} else {
			printLogE(" OnBufferingUpdateListener l is null ");
		}
	}
	
	public void setOnPreparedListener(OnPreparedListener l){
		if(mPlayer != null){
			mPlayer.setOnPreparedListener(l);
		} else {
			printLogE(" setOnPreparedListener l is null ");
		}
	}
	
	public void setOnErrorListener(OnErrorListener l){
		if(mPlayer != null){
			mPlayer.setOnErrorListener(l);
		} else {
			printLogE(" setOnErrorListener l is null ");
		}
	}
	
	
	

}
