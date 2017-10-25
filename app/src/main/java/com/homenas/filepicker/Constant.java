package com.homenas.filepicker;

import android.Manifest;

/**
 * Created by engss on 24/10/2017.
 */

public class Constant {

    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    public static final int HANDLE_CLICK_DELAY = 150;

    public static final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String ARG_FILTER = "arg_filter";
    public static final String STATE_START_PATH = "state_start_path";
    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_CURRENT_PATH = "arg_current_path";
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_CLOSEABLE = "arg_closeable";
    public static final String ARG_FILE_PATH = "arg_file_path";
    public static final String RESULT_FILE_PATH = "result_file_path";
    public static final String STATE_CURRENT_PATH = "state_current_path";

}
