package hugs.support;

import hugs.*;
import java.util.*;

public class IntScore  implements Score {

   public int value;
   
   public IntScore (int value) {
      this.value = value;
   }

   // ignore searchAdjuster
   public boolean isBetter (Score score, SearchAdjuster searchAdjuster ){
      return isBetter(score);
   }
   
   public boolean isBetter (Score score){
      if ( score == null ) return true;
      return value < ((IntScore)score).value;
   }

   public Score copy () {
      return new IntScore(value);
   }
   public String comparisonString (Score score){
      if ( score == null )
         return value + " (no change)";
      int delta = value - ((IntScore)score).value;
      return value + " (" + ((delta > 0 ) ? "+" : "") + delta + ")";
   }

   public double toDouble () {
      return (double) value;
   }
   public String toString () {
      return "" + value;
   }

}
