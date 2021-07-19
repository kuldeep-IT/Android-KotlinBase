package com.peerbits.base.utils;

import android.util.Log;

import com.peerbits.base.BuildConfig;

import timber.log.Timber;

public class Logger {

    static final String TAG = "Camshop";

    public static void setLoggingEnabled(boolean loggingEnabled) {
        LOGGING_ENABLED = loggingEnabled;
    }

    private static boolean LOGGING_ENABLED = false;

    private static final int STACK_TRACE_LEVELS_UP = 5;

    public static void verbose(String message) {
        if (LOGGING_ENABLED) {
            Log.v(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void Error(String message) {
        if (LOGGING_ENABLED) {
            Timber.e(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    /**
     * Get the current line number. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return int - Current line number.
     */
    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getLineNumber();
    }

    /**
     * Get the current class name. Note, this will only work as called from this
     * class as it has to go a predetermined number of steps up the stack trace.
     * In this case 5.
     *
     * @return String - Current line number.
     */
    private static String getClassName() {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();

        // kvarela: Removing ".java" and returning class name
        return fileName.substring(0, fileName.length() - 5);
    }

    /**
     * Get the current method name. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return String - Current line number.
     */
    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getMethodName();
    }

    /**
     * Returns the class name, method name, and line number from the currently
     * executing log call in the form <class_name>.<method_name>()-<line_number>
     *
     * @return String - String representing class name, method name, and line
     * number.
     */
    private static String getClassNameMethodNameAndLineNumber() {
        return "[" + getClassName() + "." + getMethodName() + "() At " + getLineNumber() + "]: ";
    }


    public static void e(String Msg) {
        LogIt(Log.ERROR, TAG, Msg);
    }

    public static void e(String Tag, String Msg) {
        LogIt(Log.ERROR, Tag, Msg);
    }

    public static void i(String Msg) {
        LogIt(Log.INFO, TAG, Msg);
    }

    public static void i(String Tag, String Msg) {
        LogIt(Log.INFO, Tag, Msg);
    }

    public static void d(String Msg) {
        LogIt(Log.DEBUG, TAG, Msg);
    }

    public static void d(String Tag, String Msg) {
        LogIt(Log.DEBUG, Tag, Msg);
    }

    public static void v(String Msg) {
        LogIt(Log.VERBOSE, TAG, Msg);
    }

    public static void v(String Tag, String Msg) {
        LogIt(Log.VERBOSE, Tag, Msg);
    }

    public static void w(String Msg) {
        LogIt(Log.WARN, TAG, Msg);
    }

    public static void w(String Tag, String Msg) {
        LogIt(Log.WARN, Tag, Msg);
    }

    private static void LogIt(int LEVEL, String Tag, String Message) {
        if (BuildConfig.DEBUG) Log.println(LEVEL, Tag != null ? Tag : TAG, Message);
    }
}
