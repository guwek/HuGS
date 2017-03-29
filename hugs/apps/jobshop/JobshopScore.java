package hugs.apps.jobshop;

import hugs.*;
import java.util.*;
import java.io.*;

public class JobshopScore implements Score
{

   private static final int UNPACKED_FACTOR = 100000;
   private int[] makespans;
    private int makespan;
   private int unpacked;
   private static final String label = "makespan";
   
    public boolean isFeasible () { return unpacked == 0 ;}
    private int maxSpan;
    private int sumPositionOfMarked;

    private boolean secondary = false;
    public JobshopScore (JobshopSolution solution, int unpacked,
			 JobshopSearchAdjuster adjuster, List marked) {
	
	int size = solution.problem.machineCount;
	int lastJob = solution.problem.jobCount-1;
	makespans = new int[size];
	for (int i = 0; i < size; i++) {
	    JobshopNode n = solution.getNodeOnMachine(i,lastJob);
	    int op = n.getOp();
	    int j = n.getJob();
	    makespans[i] = solution.start[j][op] + solution.problem.duration[j][op];
	}
	Arrays.sort(makespans);
	this.unpacked = unpacked;
	this.makespan = makespans[makespans.length-1];
	if ( adjuster != null  && adjuster.secondary ) {
	    this.secondary = true;
	    sumPositionOfMarked = 0;
	    for (Iterator i = marked.iterator(); i.hasNext();) {
		JobshopNode n = (JobshopNode) i.next();
		sumPositionOfMarked += solution.start[n.getJob()][n.getOp()];
	    }
	}
   }

   public JobshopScore (int[] makespans, int unpacked ) {
      this.makespans = makespans;
      this.unpacked = unpacked;
   }

   public Score copy () {
      int size = makespans.length;
      int[] copy = new int[size];
      for (int i = 0; i < size; i++)
         copy[i] = makespans[i];
      return new JobshopScore(copy,unpacked);
   }

   public boolean isBetter (Score score) {
       JobshopScore s = (JobshopScore)score;
       if ( unpacked < s.unpacked ) return true;
       if ( unpacked > s.unpacked ) return false;
       if ( compareMakespans(score) == Boolean.TRUE ) return true;
       return false;
   }

    public boolean isBetter (Score score, SearchAdjuster searchAdjuster) {
	JobshopSearchAdjuster adjuster = (JobshopSearchAdjuster) searchAdjuster;
	if ( adjuster == null || !adjuster.secondary )
	    return isBetter(score);
	JobshopScore compare = (JobshopScore) score;
	if ( unpacked < compare.unpacked ) return true;
	if ( unpacked > compare.unpacked ) return false;
	if ( adjuster.budget.value > 0 ) {
	    boolean amOk = makespan <= adjuster.maxMakespan;
	    boolean compareOk = compare.makespan <= adjuster.maxMakespan;
	    if ( amOk != compareOk ) return amOk;
	}
	else {
	    Boolean b = compareMakespans(compare);
	    if ( b == Boolean.TRUE ) return true;
	    if ( b == Boolean.FALSE ) return false;
	   
       }
       if ( adjuster.moveEarlier.value ) {
	   if (  sumPositionOfMarked < compare.sumPositionOfMarked ) return true; 
	   if (  sumPositionOfMarked > compare.sumPositionOfMarked ) return false;
       }
       if ( adjuster.moveLater.value ) {
	   if (  sumPositionOfMarked < compare.sumPositionOfMarked ) return false; 
	   if (  sumPositionOfMarked > compare.sumPositionOfMarked ) return true;
       }
       if ( compareMakespans(score) == Boolean.TRUE ) return true;
       return false;
   }

    
    // ignores secondary
    private Boolean compareMakespans (Score score ) {
	JobshopScore s = (JobshopScore)score;	
	int stop = Math.max(0,makespans.length - Jobshop.spanSize);
	for (int i = makespans.length; i-- > stop; )
	    if ( makespans[i] < s.makespans[i] )
		return Boolean.TRUE;
	   else if ( s.makespans[i] < makespans[i] )
	       return Boolean.FALSE;
	return null;
    }

   public String comparisonString (Score score) {
      JobshopScore s = (JobshopScore)score;
      int unpackedDelta = unpacked - s.unpacked;
      String string = "";
      if ( s.makespans.length != makespans.length ) {
         string += makespans[makespans.length-1];
      }
      else {
         int stop = Math.max(0,makespans.length - Jobshop.spanSize);
         for (int i = makespans.length; i-- > stop; ) {
            int makespanDelta = makespans[i] - s.makespans[i];
            string += makespans[i];
            if ( Math.abs(makespanDelta) > 0 )
               string += "(" + makespanDelta + ") ";
            else string += " ";
         }
      }
      if ( unpacked > 0 || Math.abs(unpackedDelta) > 0 ) {
         string += " unpacked=" + unpacked;
         if ( Math.abs(unpackedDelta) > 0 ) 
            string += "(" + unpackedDelta + ")";
      }
      if ( sumPositionOfMarked > 0 ) 
	  string += ", sumPos=" + sumPositionOfMarked;
      return label + ": " + string;
   }

   public double toDouble() {
      return makespans[makespans.length-1] + unpacked * UNPACKED_FACTOR;
   }
         
   public String toString (){
      String score = "";
      int stop = Math.max(0,makespans.length - Jobshop.spanSize);
      for (int i = makespans.length; i-- > stop; )
         score =  score + makespans[i] + ", ";
      if ( unpacked > 0 ) 
         score += ", unpacked=" + unpacked;
      if ( sumPositionOfMarked > 0 ) 
	  score += ", sumPos=" + sumPositionOfMarked;
      return label + ": " + score;
   }

    public int getMakespan () { return makespan; }
   
}
