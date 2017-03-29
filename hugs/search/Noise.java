package hugs.search;

import hugs.*;
import java.util.*;
import java.io.*;

public class Noise implements Serializable {

   private Mobilities mobilities;
   private int[] highs;
   private int numHigh;
   private List frozen = new ArrayList();
   private double noise;
    
   public Noise (Mobilities mobilities, double noise){
      this.mobilities = mobilities;
      highs = new int[mobilities.size()];
      this.noise = noise;
      numHigh = 0;
      for (int i = 0; i < highs.length; i++)
	  if ( mobilities.getMobility(i) == Mobilities.HIGH )
	      highs[numHigh++] = i;
   }

   public void freeze () {
       if (noise == 0) return;
       for (int i = numHigh; i-->0;)
	   if ( mobilities.getMobility(highs[i]) == Mobilities.HIGH &&
		Math.random() < noise ){
	       mobilities.setMobility(highs[i],Mobilities.MED);
	       frozen.add(new Integer(highs[i]));
	   }
   }
   

   public void unfreeze () {
       for (Iterator i = frozen.iterator(); i.hasNext();){
	   int id = ((Integer) i.next()).intValue();
	   mobilities.setMobility(id,Mobilities.HIGH);
         }
       frozen.clear();
   }
}
