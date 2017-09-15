package com.yongyida.robot.settings.utils.log;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.yongyida.robot.settings.app.SettingsApp;
import com.yongyida.robot.settings.bean.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogTool {
    private static final String TAG = "YYDSettings";
    private static final String PATH_LOG = ".YYDSettingsLog";
    private static final String USER_FILE_NAME = File.separator + "log_user.log";
    private static FileOutputStream out = null;
    private static File logFile;

    private static Handler writeLog = new Handler() {
        public void handleMessage(Message msg) {
            String[] logs = (String[]) msg.obj;
            if (logs != null && logs.length >= 2) {
                Log.d(TAG, logs[0]);
                writeLogFile(logs[1]);
            }
        }
    };

    public static void showLog(String className, String methodName, String msg) {
        String log = className + "." + methodName + ": " + msg;
        int mPId = android.os.Process.myPid();
        String record = "D/" + TAG + " (" + String.valueOf(mPId) + "):" + log;
        String[] logs = {log, record};

        if (Constants.DEBUG) {
            Message message = writeLog.obtainMessage();
            message.obj = logs;
            writeLog.sendMessage(message);
        }
    }

    static synchronized void writeLogFile(String string) {
        Context context = SettingsApp.getAppContext();
        String path = context.getFilesDir().getAbsolutePath() + File.separator + PATH_LOG;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        logFile = new File(file.getAbsolutePath() + USER_FILE_NAME);
        try {
            out = new FileOutputStream(logFile, true);
            if (isFileOver5M(logFile.getAbsolutePath())) {
                if (logFile.exists()) {
                    if (logFile.isFile()) {
                        logFile.delete();
                    }
                }
            }
            if (string.length() == 0) {
                return;
            }
            byte[] buffer = (MyDate.getDateEN() + "  " + string + "\n").getBytes();
            if (out != null) {
                out.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isFileOver5M(String filePath) {
        File f = new File(filePath);

        if (f.exists() && f.length() > 1024 * 1024 * 5)
            return true;
        return false;
    }
}
