package hugs.logging;

import hugs.*;
import java.util.*;
import java.io.*;
import hugs.utils.*;

public class SearchCollection implements Serializable,Score {

   public Object[] permutation;
   public List values;
   public List matches;

   private String permuteString;
   private int maxLastTime = 0;
   public double average;
   
   public SearchCollection (Object[] permutation, List values, List matches) {
      this.permutation = permutation;
      this.values = values;
      this.matches = matches;
      computeAverage(100000000);
      permuteString = "inputs={";
      for (int j = permutation.length; j-->0;){
         Object o = permutation[j];
         if ( o instanceof Double )
            o = Utils.doubleToString(((Double)o).doubleValue(),2);
         permuteString += (((ParamValues)values.get(j)).name + "=" + o + " ");
      }
      permuteString+="}";
   }

    public String getPermuteString() {
	return permuteString;
    }

    public double[] getScoreByTime (int inc) {
	computeAverage(100000000);
	int size = maxLastTime / inc;
	double scores[]  = new double[size+1];
	for (int i = 0; i <= size; i++) {
	    computeAverage(i*inc);
	    scores[i] = average;
	}
	return scores;
    }

   public void computeAverage (int time){
      maxLastTime = 0;
      double sum = 0;
      double count = 0;
      for (Iterator i = matches.iterator(); i.hasNext();){
         Trial trial = (Trial) i.next();
         // System.out.println("Trial: " + trial);
         SearchTrace trace = trial.getResult();
         List reports = trace.getReports();
         SearchReport last = null;
         int lastTime = 0;
         for (Iterator j = reports.iterator(); j.hasNext();){
            SearchReport report = (SearchReport) j.next();
            if ( report.getTime() <= time &&
                 (last == null || report.getTime() > lastTime) ){
               lastTime = report.getTime();
               last = report;
            }
         }
	 if ( lastTime > maxLastTime)
	     maxLastTime = lastTime;
         // System.out.println("last: " + last);
         // System.out.println("last score: " + last.getScore());
	 if (last !=  null )
	     sum += last.getScore().toDouble();
         count++;
      }
      if ( count > 0 )
         average = sum/count;
      else average = 0;
   }

   public double toDouble () { return average; }
   public boolean isBetter (Score score, SearchAdjuster searchAdjuster) {
      return isBetter(score);
   }
   
   public boolean isBetter (Score score) {
      if ( score == null ) return true;
      return average < score.toDouble();
   }
   public String comparisonString (Score score) {
      return "[no compairson string for for searchcollaction]";
   }
   public Score copy () {
      System.out.println("No copy method for SearchCollection");
      System.exit(0);
      return null;
   }

   public String toString () {
      return "[Collection: " + permuteString + " num=" + matches.size() + ", average " + average + "]";
   }
}

