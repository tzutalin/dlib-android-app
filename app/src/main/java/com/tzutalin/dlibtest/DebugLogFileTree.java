/*
 * Copyright (c) 2017-present. Tzutalin
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tzutalin.dlibtest;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import timber.log.Timber;

/**
 * Created by tzutalin on 2017/2/23.
 */
public class DebugLogFileTree extends Timber.DebugTree {

    private static final String TWO_SPACE = "  ";
    private final String mLogDir;
    private final String mFilePath;
    private final LogWriterWorker mLogWriterWorker;

    public DebugLogFileTree(String dir) {
        mLogDir = dir;
        mFilePath = mLogDir + File.separator + "dlib.log";
        mLogWriterWorker = new LogWriterWorker();
        mLogWriterWorker.start(this);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        super.log(priority, tag, message, t);
        mLogWriterWorker.put(formatLog(priority, tag, message, t));
    }

    private String formatLog(int priority, String tag, String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        if (throwable != null && message == null) {
            message = getStackTraceString(throwable);
        }
        if (message == null) {
            message = "No message/exception is set";
        }

        sb.append(getTimeStamp()).append(TWO_SPACE);
        sb.append(getThreadSignature()).append(TWO_SPACE);
        sb.append(tag).append(":").append(getPriorityString(priority)).append(TWO_SPACE);
        sb.append(message);
        return sb.toString();
    }

    private String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private String getThreadSignature() {
        Thread t = Thread.currentThread();
        String name = t.getName();
        int id = android.os.Process.myTid();
        return String.format("%s:%d", name, id);
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }

    public String getPriorityString(int priority) {
        if (priority == Log.ASSERT) {
            return "A";
        } else if (priority == Log.ERROR) {
            return "E";
        } else if (priority == Log.WARN) {
            return "W";
        } else if (priority == Log.INFO) {
            return "I";
        } else if (priority == Log.DEBUG) {
            return "D";
        } else if (priority == Log.VERBOSE) {
            return "V";
        }
        return "";
    }

    private static class LogWriterWorker implements Runnable {
        private WeakReference<DebugLogFileTree> mWeakRef;
        private BufferedWriter mBufferedWriter;
        private BlockingQueue<String> mQueue;

        public void start(@NonNull DebugLogFileTree tree) {
            if (isStart() == false) {
                mWeakRef = new WeakReference<DebugLogFileTree>(tree);
                mQueue = new LinkedBlockingQueue<>();
                new Thread(this).start();
            }
        }

        // Producer on any thread
        public void put(@NonNull String msg) {
            if (mQueue == null) return;
            try {
                mQueue.put(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Consumer on LogWriterWorker's thread
        @Override
        public void run() {
            // Open a new log file
            if (isLogFileOpen() == false)
                open(mWeakRef.get().mFilePath);
            String log;
            try {
                while ((log = mQueue.take()) != null) {
                    appendLog(log);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mQueue.clear();
                mQueue = null;
                close();
            }
        }

        private boolean isStart() {
            return mQueue != null;
        }

        private boolean isLogFileOpen() {
            return mBufferedWriter != null;
        }

        private boolean open(@NonNull String newFileName) {
            if (TextUtils.isEmpty(newFileName)) return false;
            File logFile = new File(newFileName);
            // Create log file if not exists.
            if (!logFile.exists()) {
                try {
                    File parent = logFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            // Create buffered writer.
            try {
                mBufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (Exception e) {
                e.printStackTrace();
                close();
                return false;
            }
            return true;
        }

        private boolean close() {
            if (mBufferedWriter != null) {
                try {
                    mBufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    mBufferedWriter = null;
                }
            }
            return true;
        }

        private void appendLog(@NonNull String flattenedLog) {
            try {
                mBufferedWriter.write(flattenedLog);
                mBufferedWriter.newLine();
                mBufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

