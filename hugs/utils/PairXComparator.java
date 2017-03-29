package hugs.utils;


import java.util.*;
import java.awt.Point;
   
public class PairXComparator implements Comparator {

    private boolean minFirst;
    public PairXComparator(boolean minFirst){
	this.minFirst = minFirst;
    }

   public int compare (Object o1, Object o2) {
      if ( o1 == null ) return 1;
      if ( o2 == null ) return -1;
      if ( ((Point)o1).x < ((Point)o2).x ) 
         return minFirst ? -1 : 1;
      if ( ((Point)o1).x > ((Point)o2).x ) 
	  return minFirst ? 1 : -1;
      return 0;
   }

   public boolean equals (Object obj ) {
      return obj == this;
   }

}

