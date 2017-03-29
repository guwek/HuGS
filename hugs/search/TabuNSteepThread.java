package hugs.search;

import hugs.*;
import java.util.*;

public class TabuNSteepThread extends MultiSearchThread {


   public String getSearchName () { return "tabuNSteep";}
   
   private SteepThread greedy = new SteepThread();
   private TabuThread tabu = new TabuThread();

   public TabuNSteepThread () {
      this(5);
   }

   public TabuNSteepThread (int cycle) {
      super(null,cycle);
      addSearch(tabu);
      addSearch(greedy);
   }

}
