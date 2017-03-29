package hugs.apps.protein;

import hugs.*;

public class ProteinScore implements Score {

   public static double BREAK_FACTOR = 100000;
   
   private int energy;
   private int breaks;
   private int markedDistance;
   
   public ProteinScore (int energy, int breaks) {
      this(energy,breaks,0);
   }
   
   public ProteinScore (int energy, int breaks, int markedDistance) {
      this.energy = energy;
      this.breaks = breaks;
      this.markedDistance = markedDistance;
   }

   public Score copy () { return new ProteinScore(energy,breaks,markedDistance); }

   public int getEnergy () { return energy; }

   public boolean isBetter (Score score, SearchAdjuster searchAdjuster) {
      ProteinSearchAdjuster a = (ProteinSearchAdjuster) searchAdjuster;
      if ( !a.secondary )
         return isBetter(score);
      ProteinScore compare = (ProteinScore) score;
      if ( breaks < compare.breaks ) return true;
      if ( breaks > compare.breaks ) return false;
      if ( a.budget.value > 0 ) {
         boolean amOk = energy <= a.maxEnergy;
         boolean compareOk = compare.energy <= a.maxEnergy;
         if ( amOk != compareOk ) return amOk;
      }
      else {
         if ( energy < compare.energy ) return true;
         if ( energy > compare.energy ) return false;
      }
      if ( a.close.value ) {
         if ( markedDistance < compare.markedDistance ) return true;
         if ( markedDistance > compare.markedDistance ) return false;
      }
      return energy < compare.energy;
   }

   public boolean isBetter (Score score) {
      ProteinScore s = (ProteinScore)score;
      if ( breaks < s.breaks ) return true;
      if ( breaks > s.breaks ) return false;
      return energy < s.energy;
   }

   public String comparisonString (Score score) {
      ProteinScore s = (ProteinScore)score;
      int breaksDelta = breaks - s.breaks;
      int energyDelta = energy - s.energy;
      String string = "energy=" + energy;
      if ( Math.abs(energyDelta) > 0 )
         string += "(" + ((energyDelta > 0 ) ? "+" : "") + energyDelta + ")";
      if ( breaks > 0 || Math.abs(breaksDelta) > 0 ) {
         string += " breaks=" + breaks;
         if ( Math.abs(breaksDelta) > 0 )
            string += "(" + ((breaksDelta > 0 ) ? "+" : "") + breaksDelta + ")";
      }
      if ( markedDistance > 0 )
      string += ", mD=" + markedDistance;
      return string;
   }
   
   public String toString (){
      String score = "energy=" + energy;
      if ( breaks > 0 )
         score += " breaks=" + breaks;
      if ( markedDistance > 0 )
         score += ", mD=" + markedDistance;
      return score;
   }

   public double toDouble () {
      return (double)  (energy + breaks * BREAK_FACTOR);
   }
}
