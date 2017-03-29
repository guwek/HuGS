package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class ScoreComparator implements Comparator {
   public int compare (Object o1, Object o2) {
      if ( ((Score)o1).isBetter((Score)o2) ) return 1;
      if ( ((Score)o2).isBetter((Score)o1) ) return -1;
      return -1;
   }

   public boolean equals (Object obj ) {
      return obj == this;
   }

}
