package hugs.apps.jobshop;

import hugs.*;
import hugs.support.*;
import java.util.*;

public class JobshopSearchAdjuster implements SearchAdjuster {

   private List inputs = new ArrayList();
   public BooleanParameter moveEarlier = new BooleanParameter("move earlier",false);
   public BooleanParameter moveLater = new BooleanParameter("move later",false);
   public IntParameter budget = new IntParameter("budget",0);
   public boolean secondary; // any secondary objective function
   public int maxMakespan = 0;
   {
      inputs.add(moveEarlier);
      inputs.add(moveLater);
      inputs.add(budget);
   }
   public List getInputs () { return inputs; }
   public void precompute (Solution s) {
      secondary = moveEarlier.value || moveLater.value;
      maxMakespan = ((JobshopScore)((JobshopSolution)s).getScore()).getMakespan() 
	  + budget.value;
   }
   public String toString () {
      return "[JobshopSearchAdjuster: moveEarlier=" + moveEarlier.value + ", moveLater=" + moveLater.value + ", budget=" + budget.value + "]";
   }
}
