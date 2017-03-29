package hugs.logging;

import hugs.Solution;
import hugs.Score;
import java.io.*;

public class SearchEvent extends LogEvent {

   String type;
   public SearchEvent (Solution s, String type){
      endSolution = s;
      this.type = type;
   }

   protected void printBody (PrintWriter p) {
      p.println("TYPE searchEvent " + type);
   }
}
