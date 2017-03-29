package hugs.logging;

import java.io.*;
import hugs.Solution;
import java.util.*;

public abstract class LogEvent implements Serializable {

   protected   Solution endSolution;
   protected int seconds;

   public Solution getEndSolution () { return endSolution; }
   public int getSeconds () { return seconds; }
   public void setSeconds (int seconds){
      this.seconds = seconds;
   }
   // meant to be overriden
   protected void printBody (PrintWriter p){
   }
   public void print (PrintWriter p){
      p.println("START_LOG_EVENT");
      p.println("TIME " + seconds);
      printBody(p);
      if ( endSolution != null )
         p.println("END_SOLUTION_SCORE " + endSolution.getScore());
      p.println("END_LOG_EVENT");
   }
}
