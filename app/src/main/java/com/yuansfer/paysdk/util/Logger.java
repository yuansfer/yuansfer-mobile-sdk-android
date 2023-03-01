package com.yuansfer.paysdk.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {

   private TextView tvLogger;
   private ArrayList logs = new ArrayList<String>();
   private final static int MAX_LOG = 1000;
   private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
   private static Handler handler = new Handler(Looper.getMainLooper());
   private Object lock = new Object();

   public Logger(TextView attachView) {
      tvLogger = attachView;
   }

   public void log(String toLog) {
      synchronized(lock) {
         if (logs.size() >= MAX_LOG) {
            logs.remove(logs.size() - 1);
         }
         logs.add(dateFormat.format(new Date()) + ":" + toLog + "\n");
         StringBuilder sb = new StringBuilder("");
         for (Object log : logs) {
            sb.insert(0, log);
         }
         handler.post(new Runnable() {
            @Override
            public void run() {
               tvLogger.setText(sb.toString());
            }
         });
      }
   }
}
