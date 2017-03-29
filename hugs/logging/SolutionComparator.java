package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class SolutionComparator implements Comparator, Serializable {
   public int compare (Object o1, Object o2) {
      if ( o1 == null ) return 1;
      if ( o2 == null ) return -1;
      if ( ((Solution)o1).getScore().isBetter(((Solution)o2).getScore()) )
         return -1;
      if ( ((Solution)o2).getScore().isBetter(((Solution)o1).getScore()) )
         return 1;
      return 1;
   }

   public boolean equals (Object obj ) {
      return obj == this;
   }

}
