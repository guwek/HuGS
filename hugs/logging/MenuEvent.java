package hugs.logging;

import hugs.Solution;
import hugs.Score;
import java.io.*;

public class MenuEvent extends LogEvent {

   String type;
   public MenuEvent (Solution s, String type){
      endSolution = s;
      this.type = type;
   }

   protected void printBody (PrintWriter p) {
      p.println("TYPE menuEvent " + type);
   }
}
