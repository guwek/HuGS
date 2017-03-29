package hugs.search;

import hugs.*;
import java.util.*;

public class TabuNGreedyThread extends MultiSearchThread {


   public String getSearchName () { return "tabuNGreedy";}
   
   protected GreedyThread getGreedy () { return new GreedyThread();}
   protected TabuThread getTabu() { return new TabuThread();}

   public TabuNGreedyThread () {
      this(5);
   }

   public TabuNGreedyThread (int cycle) {
      super(null,cycle);
      addSearch(getTabu());
      addSearch(getGreedy());
   }

}
