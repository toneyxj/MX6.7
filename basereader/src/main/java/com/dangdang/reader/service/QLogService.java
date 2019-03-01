package com.dangdang.reader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;


public class QLogService extends Service implements Runnable {

	private static final String TAG="QLogService";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	
	private static final String LOGCAT_CMD = "logcat ";
	private static final String KILL_CMD = "kill";
	private static final String PS_CMD = "ps logcat";

	private static final String FILE_COUNT = "3";
	private static final String FILE_SIZE = "512";
	private static final String FORMAT = "threadtime";
	
	// file location and file name
	public static final String LOG_FOLDER_NAME = "log";
	public static final String LOG_FILE_NAME = DDApplication.getApplication().getPackageName()+"_log.txt";
	

	private static final String FILTER = 
			// our application info
			DDApplication.getApplication().getPackageName()+":D "+
					" System.err:W"+
					" System.err:E"+
					" dangdang:E"+
					" dangdang:I"+
			" ActivityManager:I " +
			" AndroidRuntime:E "
			+" *:S ";
	
	
	private String mLogPath;
	Process proc = null;

	private static final Runtime mRuntime = Runtime.getRuntime();

	@Override
	public void onCreate() {
		super.onCreate();
		
		killLogcatProcess();
		
		if(mkLogDir()){
			startWriteLog();
		}
	}

	@Override
	public void run() {
		if (proc == null) {
			try {
				String command = buildCmd(LOGCAT_CMD, " -v ", FORMAT,
						" -f ", mLogPath, " -r",FILE_SIZE, " -n ", FILE_COUNT,
						" -s ", FILTER);
				proc = mRuntime.exec(command);
				LogM.i(TAG,"logcat started successfully.");
			} catch (IOException e) {
				LogM.i(TAG,"start logcat error: "+e);
			} finally {
			}
		}
	}

	
	
	private void startWriteLog() {
		new Thread(this).start();
	}

	public void onDestroy() {
		super.onDestroy();
		LogM.i(TAG,"onDestroy called.");
		if (proc != null) {
			proc.destroy();
			proc = null;
		}
	
	}

	private static void killLogcatProcess(){
		String[] pids = queryLogcat();
		
		// must be above below line, otherwise, this log will not be printed.
		LogM.i(TAG,"logcat killed ,pids: "+Arrays.toString(pids));
		killLogcat(pids);
		
	}
	private boolean mkLogDir() {
		File logDir = new File(DangdangFileManager.getAppRootDir()+LOG_FOLDER_NAME);
//		File logDir = DirUtil.getExternalFilesDir(this,LOG_FOLDER_NAME);
		if (logDir != null){
			if (!logDir.exists())
				logDir.mkdirs();
			mLogPath = new File(logDir, LOG_FILE_NAME).getAbsolutePath();
			return true;
		}
		LogM.e(TAG, "mkLogDir------return false;");
		return false;
	}

	public static void startService(Context ctx) {
		ctx.startService(new Intent(ctx, QLogService.class));
	}
	public static void stopService(Context ctx) {
	
		killLogcatProcess();
		ctx.stopService(new Intent(ctx, QLogService.class));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	

	private static String buildKillCmd(String[] pids){
		return buildCmd(KILL_CMD, pids);
	}

    private static String buildCmd(String cmd, String... args) {
        StringBuilder sb = new StringBuilder();
        char space = ' '; // add space between args
        sb.append(cmd);
        for (String pid : args) {
            sb.append(space).append(pid);
        }

        return sb.toString();
    }

//	private static String buildCmd(String cmd, String... args) {
//		StringBuilder sb = new StringBuilder();
////		char space = ' ';
//		sb.append(cmd);
//		for (String pid : args) {
//			sb.append(pid);
//		}
//		return sb.toString();
//	}

    
    //-------------------------------function to kill logcat----------------------------------
    /**
	 * logcat sample:
	 * 
	 * USER     PID   PPID  VSIZE  RSS     WCHAN    PC         NAME
	 * app_65   29336 29323 2148   1792    ffffffff 00000000 S logcat
	 */
	private static String[] parseLogCatIds(List<String> lines) {

		if (lines.size() == 0) {
			return EMPTY_STRING_ARRAY;
		}

		Pattern p = Pattern.compile("\\s+");
		
		List<String> pids = new ArrayList<String>(lines.size());

		for (String line : lines) {
			String[] array = p.split(line);
			pids.add(array[1]);
		}

		return pids.toArray(new String[0]);
	}

	private static void killLogcat(String[] pids) {

		if (pids.length > 0) {
			String killCmd = buildKillCmd(pids);
			try {
				Process p = mRuntime.exec(killCmd);
				try {
					p.waitFor();
				} catch (InterruptedException e) {
				}
				p.destroy();
			} catch (IOException e) {
				LogM.e(TAG,e.getMessage());
			}
		}
	}

	private static String[] queryLogcat() {
		Process p = null;
		try {
			p = mRuntime.exec(PS_CMD);
		} catch (IOException e) {
			LogM.e(TAG,"Query logcat error: "+ e);
		}
		if (p == null) {
			return EMPTY_STRING_ARRAY;
		}
		BufferedReader input = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		
		String line = null;
		List<String> lines = new ArrayList<String>();

		try {
			// ignore the first line, it's the title.
			input.readLine();
			while ((line = input.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			LogM.e(TAG,"parse command line error: "+ e);
		} finally {
			p.destroy();
			p = null;
			try {
				input.close();
			} catch (IOException e) {
			}
			input = null;
		}

		if (lines.size() == 0) {
			return EMPTY_STRING_ARRAY;
		} else {
			return parseLogCatIds(lines);
		}
	}
}
