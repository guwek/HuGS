package hugs.logging;

import java.util.*;
import java.io.*;
import hugs.Score;
import hugs.utils.*;

public class Logger {
   
   private Log log;
   private PrintWriter writer;
   private int startSeconds;
   
   public Logger (String fileName,
                  Score startScore,
                  List inputs){
      String textFile = fileName + ".txt";
      log = new Log(fileName+".obj",textFile,startScore,inputs);
      try {
         writer = new PrintWriter (new FileWriter(new File(textFile)),true);
      }
      catch ( Exception e) {
         System.out.println("Can't open printwriter for " + textFile + ": " + e);
         writer = null;
      }
      if ( writer != null ) 
         log.openTextFile(writer);
      startSeconds = log.getStartSeconds();
   }

   public void add (LogEvent e) {
      log.add(e);
      e.setSeconds(Utils.getSeconds() - startSeconds);
      e.print(writer);
   }

   public void save () {
      log.save();
   }

   public void close () {
      log.closeTextFile(writer);
   }

   public static void printLog (String logFileName, String outputFileName) {
      Log log = (Log) Utils.readObject(logFileName);
      PrintWriter writer;
      try {
         writer = new PrintWriter (new FileWriter(new File(outputFileName)),true);
      }
      catch ( Exception e) {
         System.out.println("Can't open printwriter for " + outputFileName + ": " + e);
         writer = null;
      }
      if ( writer == null || log == null ) return;
      log.openTextFile(writer);
      List events = log.getEvents();
      for (Iterator i = events.iterator(); i.hasNext();) 
         ((LogEvent) i.next()).print(writer);
      writer.close();
         
   }

   public static void processArg (String args[], int pos) {
      if ( args[pos].equals("-printLog") ) 
         printLog (args[pos+1],args[pos+2]);
   }
   public static void main(String args[]) {
      for (int i = 0; i < args.length; i++)
         processArg(args,i);
   }
   
}

