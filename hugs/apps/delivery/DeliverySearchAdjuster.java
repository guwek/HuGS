package hugs.apps.delivery;

import hugs.*;
import hugs.support.*;
import java.util.*;

public class DeliverySearchAdjuster implements SearchAdjuster {

   public static boolean doRemoves = true;
   public static boolean doSwaps = true;
   public static boolean doInserts = true;
   public static boolean doSplices = true;

   public static int defaultSpliceSize = 10;
   
   private List inputs = new ArrayList();
   public BooleanParameter removes = new BooleanParameter("removes",doRemoves);
   public BooleanParameter swaps = new BooleanParameter("swaps",doSwaps);
   public BooleanParameter inserts = new BooleanParameter("inserts",doInserts);
   public BooleanParameter splices = new BooleanParameter("splices",doSplices);
   public IntParameter spliceSize = new IntParameter("splice size",defaultSpliceSize);
   
   {
      inputs.add(removes);
      inputs.add(swaps);
      inputs.add(inserts);
      inputs.add(splices);
      inputs.add(spliceSize);
   }
   
   public List getInputs () { return inputs; }
   public void precompute (Solution s) {
   }
   public String toString () {
      return "[DeliverySearchAdjuster: removes=" + removes.value + ", swaps="
         + swaps.value + ", inserts=" + inserts.value + ", splices=" + splices +
         ", size= " + spliceSize.value + "]";
   }
}
