package hugs.logging;

import hugs.*;
import java.io.*;

public class Restore implements Serializable {

   public int timeLeft;
   public Solution best;
   public String info;

   public Restore (int timeLeft, Solution best, String info) {
      this.timeLeft = timeLeft;
      this.best = best;
      this.info = info;
   }

   public String toString () { return "[Restore " + timeLeft + ", " + best + ", " + info + "]";}
}


