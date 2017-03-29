package hugs.search;

import hugs.*;
import java.util.*;

public class TabuNGreedyPrecompute extends TabuNGreedyThread implements PrecomputeThread {
   
   public String getSearchName () { return "tabuNGreedyPrecompute";}

   private TabuPrecompute tabu;
   
   public void setPrecompute (Precompute p) {
      tabu.setPrecompute(p);
   }
   protected void considerSolution (Solution s) {
      tabu.considerSolution(s);
   }
   protected TabuThread getTabu() {
      if ( tabu == null )
         tabu = new TabuPrecompute();
      return tabu;
   }
   

}
