package hugs.apps.stpc;

import hugs.*;
import hugs.utils.*;

public class StpcScore implements Score {

   public static final double TOLERANCE = .000001;
   public static final double DEL_FACTOR = 1;
   public static final double DISTANCE_FACTOR = 1;

   public static final double ABS_DEL_FACTOR = 1;
   public static final double ABS_DISTANCE_FACTOR = 1;

   protected double delivered; 
   protected double distance;
   static boolean useAbsoluteValues = false;
	protected StpcProblem problem;

   public StpcScore (double distance, double delivered, boolean abs){
      this.delivered = delivered;
      this.distance = distance;
      this.useAbsoluteValues = abs;
      
      this.problem = (StpcProblem) Hugs.THIS.getProblem();
   }

	public double getDelivered() { return delivered; };
	public double getDistance() { return distance; };
	public boolean getAbsValues() { return useAbsoluteValues; };
	
   public Score copy () {
      return new StpcScore(distance, delivered, useAbsoluteValues);
   }

   public boolean isBetter (Score score, SearchAdjuster adjuster) {
      return isBetter(score);
   }
   
   public boolean isBetter (Score score) {
      StpcScore s = (StpcScore)score;
		
		if (useAbsoluteValues != s.useAbsoluteValues)
			return true;
		
		if (!useAbsoluteValues)
	      return this.toDouble() > s.toDouble();
		else
			return this.toDouble() < s.toDouble();

   }

   public String comparisonString (Score score) {
      StpcScore s = (StpcScore)score;
      double deliveredDelta = delivered - s.delivered;
      double distanceDelta = distance - s.distance;
		double res1 = toDouble();
		double res2 = s.toDouble();
		double resultDelta = res1 - res2;
				
      String string = "delivered= " + Utils.doubleToString(delivered,2);
      if ( Math.abs(deliveredDelta) > TOLERANCE )
         string += "(" + Utils.doubleToString(deliveredDelta,2) + ")";
      string += " distance=" + Utils.doubleToString(distance,2);
      if ( Math.abs(distanceDelta) > TOLERANCE )
         string += "(" + Utils.doubleToString(distanceDelta,2) + ")";
		if (useAbsoluteValues)
			string += " undel+dist = " + Utils.doubleToString(toDouble(),2);
		else
			string += " del/dist = " + Utils.doubleToString(toDouble(),2);

      if ( Math.abs(resultDelta) > TOLERANCE )
         string += "(" + Utils.doubleToString(resultDelta,2) + ")";		

      return string;
   }

   public double toDouble()
   {
   	if (useAbsoluteValues)
			return ((problem.getMaxDeliveries() - delivered)*ABS_DEL_FACTOR) + (distance * ABS_DISTANCE_FACTOR);
		else
		{
			if (distance != 0)
				return (delivered*DEL_FACTOR) / (distance * DISTANCE_FACTOR);
			else
				return 0;
		}
   }
         
   public String toString (){
      String score = "delivered= " + Utils.doubleToString(delivered,2)
         + " distance=" + Utils.doubleToString(distance,2);
      
		if (useAbsoluteValues)
			score += " undel+";
		else
			score += " del/";
		
		score += "dist = " + Utils.doubleToString(toDouble(),2);
		return score;
   }
   
}
