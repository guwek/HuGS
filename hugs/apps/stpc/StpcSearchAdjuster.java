package hugs.apps.stpc;

import hugs.*;
import hugs.support.*;
import java.util.*;

public class StpcSearchAdjuster implements SearchAdjuster
{
   public static boolean doAbsValues = false;
   
   private List inputs = new ArrayList();
   public BooleanParameter absValues = new BooleanParameter("Absolute Values",doAbsValues);
   
   {
      inputs.add(absValues);
   }
   
   public List getInputs () { return inputs; }
   public void precompute (Solution s) {
   }
   public String toString () {
      return "[StpcSearchAdjuster: absValues=" + absValues.value + "]";
   }
}
