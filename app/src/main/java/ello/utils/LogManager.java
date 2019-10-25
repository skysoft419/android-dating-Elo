package ello.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category LogManager
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.util.Log;

import ello.BuildConfig;

/*****************************************************************
 LogManager
 ****************************************************************/

public class LogManager {

    private static final String TAG = "MCL";

    /**
     * Log Level Error
     **/
    public static void e(String message) {
        if (BuildConfig.DEBUG) Log.e(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static void w(String message) {
        if (BuildConfig.DEBUG) Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static void i(String message) {
        if (BuildConfig.DEBUG) Log.i(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static void d(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static void v(String message) {
        if (BuildConfig.DEBUG) Log.v(TAG, buildLogMsg(message));
    }

    private static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);

        return sb.toString();

    }

}