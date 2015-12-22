package com.hardrubic.util;

import ad2.hardrubic.com.androiddemo20.BuildConfig;
import android.util.Log;

/**
 * 仅在debug模式下输出log.
 * 不适合并发情况下使用.
 *
 */
public class LogUtils {
	static String className;
	static String methodName;
	static int lineNumber;
	
    private LogUtils(){
        /* Protect from instantiations */
    }

	public static boolean isDebuggable() {
		return BuildConfig.DEBUG;
	}

	private static String createLog( String log ) {

		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(methodName);
		builder.append(":");
		builder.append(lineNumber);
		builder.append("]");
		builder.append(log);
		
		return builder.toString();
	}
	
	private static void getMethodNames(StackTraceElement[] sElements){
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message){
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods  
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void i(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}
	
	public static void d(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}
	
	public static void v(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}
	
	public static void w(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}
	
	public static void wtf(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}
}
