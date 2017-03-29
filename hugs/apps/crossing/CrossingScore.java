package hugs.apps.crossing;

import hugs.support.*;
import hugs.*;

public class CrossingScore extends IntScore {

   private int sumPositionOfMarked = 0;
   public String label = "intersections";
   public CrossingScore (int intersections) {
      super(intersections);
   }
   public CrossingScore (int intersections, int pos) {
      super(intersections);
      this.sumPositionOfMarked = pos;
   }

   public String comparisonString (Score score){
      return label + ": " + super.comparisonString(score);
   }
   
   public boolean isBetter (Score score, SearchAdjuster searchAdjuster ){
      CrossingSearchAdjuster a = (CrossingSearchAdjuster) searchAdjuster;
      if ( !a.secondary )
         return value < ((IntScore)score).value;
      CrossingScore compare = (CrossingScore) score;
      // System.out.println("@@ ts: " + sumPositionOfMarked +" "+compare.sumPositionOfMarked);
      if ( a.budget.value > 0 ) {
         boolean amOk = value <= a.maxCrossing;
         boolean compareOk = compare.value <= a.maxCrossing;
         if ( amOk != compareOk ) return amOk;
      }
      if ( a.moveRight.value ){ 
         if ( sumPositionOfMarked > compare.sumPositionOfMarked ) return true;
         if ( sumPositionOfMarked < compare.sumPositionOfMarked ) return false;
      }
      else if ( a.moveLeft.value ){ 
         if ( sumPositionOfMarked > compare.sumPositionOfMarked ) return false;
         if ( sumPositionOfMarked < compare.sumPositionOfMarked ) return true;
      }
      return value < compare.value;
   }

   public String toString () {
      return label + ": " +value;
   }
}
