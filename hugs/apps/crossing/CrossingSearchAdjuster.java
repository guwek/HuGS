package hugs.apps.crossing;

import hugs.*;
import hugs.support.*;
import java.util.*;

public class CrossingSearchAdjuster implements SearchAdjuster {

   private List inputs = new ArrayList();
   public BooleanParameter moveLeft = new BooleanParameter("move left",false);
   public BooleanParameter moveRight = new BooleanParameter("move right",false);
   public IntParameter budget = new IntParameter("budget",0);
   public boolean secondary; // any secondary objective function
   public int maxCrossing = 0;
   {
      inputs.add(moveLeft);
      inputs.add(moveRight);
      inputs.add(budget);
   }
   public List getInputs () { return inputs; }
   public void precompute (Solution s) {
      secondary = moveLeft.value || moveRight.value;
      maxCrossing = ((CrossingScore)((CrossingSolution)s).getScore()).value + budget.value;
   }
   public String toString () {
      return "[CrossingSearchAdjuster: moveLeft=" + moveLeft.value + ", moveRight=" + moveRight.value + ", budget=" + budget.value + "]";
   }
}
