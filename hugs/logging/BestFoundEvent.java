package hugs.logging;

import hugs.Solution;
import hugs.Score;
import java.io.*;

public class BestFoundEvent extends LogEvent {

   public BestFoundEvent (Solution s){
      endSolution = s;
   }

   protected void printBody (PrintWriter p) {
      p.println("TYPE bestFoundEvent");
   }
}
