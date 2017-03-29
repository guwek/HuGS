package hugs.search;

import hugs.*;
import java.util.*;

public class TabuPrecompute extends TabuThread implements PrecomputeThread {
   
   Precompute precompute;
   public void setPrecompute (Precompute p) {
      this.precompute = p;
   }

   private Solution last = null;
   protected void considerSolution (Solution s) {
      // if ( s != null )  System.out.println("@@@ score: " + s.getScore());
      if ( last != null && s != null &&
           precompute != null && 
           !s.getScore().isBetter(last.getScore())) {
         // System.out.println("register: " + last);
         precompute.registerSolution(last);
      }
      last = s;
   }
}
