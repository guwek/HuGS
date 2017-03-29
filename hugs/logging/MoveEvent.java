package hugs.logging;

import hugs.Move;
import hugs.Solution;
import java.io.*;

public class MoveEvent extends LogEvent {

   private Move move;
   public MoveEvent (Move move, Solution s){
      this.move = move;
      endSolution = s;
   }

   protected void printBody (PrintWriter p) {
      p.println("MOVE " + move.toString());
   }
}
