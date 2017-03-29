package hugs.utils;


import java.util.*;
   
public class TripleXComparator implements Comparator {

    private boolean minFirst;
    public TripleXComparator(boolean minFirst){
	this.minFirst = minFirst;
    }

   public int compare (Object o1, Object o2) {
      if ( o1 == null ) return 1;
      if ( o2 == null ) return -1;
      Triple t1 = (Triple) o1;
      Triple t2 = (Triple) o2;
      if ( t1.x < t2.x ) 
         return minFirst ? -1 : 1;
      if ( t1.x > t2.x ) 
         return minFirst ? 1 : -1;
      if ( t1.y < t2.y ) 
         return minFirst ? -1 : 1;
      if ( t1.y > t2.y ) 
         return minFirst ? 1 : -1;
      return 0;
   }

   public boolean equals (Object obj ) {
      return obj == this;
   }

}

