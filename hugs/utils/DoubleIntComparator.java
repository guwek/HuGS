package hugs.utils;


import java.util.*;

public class DoubleIntComparator implements Comparator {

    private boolean minFirst;
    public DoubleIntComparator(boolean minFirst){
	this.minFirst = minFirst;
    }

   public int compare (Object o1, Object o2) {
      if ( o1 == null ) return 1;
      if ( o2 == null ) return -1;
      if ( ((DoubleIntPair)o1).d < ((DoubleIntPair)o2).d ) 
         return minFirst ? -1 : 1;
      if ( ((DoubleIntPair)o1).d > ((DoubleIntPair)o2).d ) 
	  return minFirst ? 1 : -1;
      return 0;
   }

   public boolean equals (Object obj ) {
      return obj == this;
   }

}

