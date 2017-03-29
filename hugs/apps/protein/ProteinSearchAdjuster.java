package hugs.apps.protein;

import hugs.*;
import hugs.support.*;
import java.util.*;

public class ProteinSearchAdjuster implements SearchAdjuster {

   public static boolean doDiag = true;
   public static boolean doRotates = false;
   public static boolean doTwos = false;
   public static boolean doSeeks = false;


   
   private List inputs = new ArrayList();
   public BooleanParameter diags = new BooleanParameter("diagnals",doDiag);
   public BooleanParameter rotates = new BooleanParameter("rotates",doRotates);
   public BooleanParameter twos = new BooleanParameter("twos",doTwos);
   public BooleanParameter seeks = new BooleanParameter("seeks",doSeeks);
   public BooleanParameter close = new BooleanParameter("move closer",false);
   public IntParameter budget = new IntParameter("budget",0);
   
   public boolean secondary; // any secondary objective function
   public int maxEnergy = 0;
   {
      inputs.add(diags);
      inputs.add(rotates);
      inputs.add(twos);
      inputs.add(seeks);
      inputs.add(close);
      inputs.add(budget);
   }
   
   public List getInputs () { return inputs; }
   public void precompute (Solution s) {
      secondary = close.value;
      maxEnergy = ((ProteinScore)((ProteinSolution)s).getScore()).getEnergy() + budget.value;
   }
   public String toString () {
      return "[ProteinSearchAdjuster: diags=" + diags.value + ", rotates="
         + rotates.value + ", twos=" + twos.value + ", seeks=" + seeks +
         ",close " + close.value + ",budget=" + budget.value + "]";
   }
}
