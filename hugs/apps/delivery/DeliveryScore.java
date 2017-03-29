package hugs.apps.delivery;

import hugs.*;
import hugs.utils.*;

public class DeliveryScore implements Score {

   public static final double TOLERANCE = .000001;
   public static final double UNDEL_FACTOR = 1000;
   public static final double DISTANCE_FACTOR = 1;
   public static final double LATE_FACTOR = 1000000;

   private double lateness;
   private double deliveries;
   private double distance;

   public DeliveryScore (double deliveries, double distance, double lateness){
      this.lateness = lateness;
      this.deliveries = deliveries;
      this.distance = distance;
   }

   public Score copy () {
      return new DeliveryScore(deliveries,distance,lateness);
   }

   public boolean isBetter (Score score, SearchAdjuster adjuster) {
      return isBetter(score);
   }
   
   public boolean isBetter (Score score) {
      DeliveryScore s = (DeliveryScore)score;
      double latenessDelta = lateness - s.lateness;
      if ( Math.abs(latenessDelta) > TOLERANCE )
         return lateness < s.lateness;
      double deliveriesDelta = deliveries - s.deliveries;
      if ( Math.abs(deliveriesDelta) > TOLERANCE )
         return deliveries < s.deliveries;
      double distanceDelta = distance - s.distance;
      if ( Math.abs(distanceDelta) > TOLERANCE )
         return distance < s.distance;
      return false;
   }

   public boolean isFeasible () {
      return lateness < TOLERANCE ;
   }
   public double getDistance () {return distance;}
   public double getDeliveries () {return deliveries;}

   public String comparisonString (Score score) {
      DeliveryScore s = (DeliveryScore)score;
      double latenessDelta = lateness - s.lateness;
      double deliveriesDelta = deliveries - s.deliveries;
      double distanceDelta = distance - s.distance;
      String string = "delivered= " + Utils.doubleToString((100-deliveries),2) + "%";
      if ( Math.abs(deliveriesDelta) > TOLERANCE )
         string += "(" + Utils.doubleToString(deliveriesDelta,2) + ")";
      string += " distance=" + Utils.doubleToString(distance,2) + "%";
      if ( Math.abs(distanceDelta) > TOLERANCE )
         string += "(" + Utils.doubleToString(distanceDelta,2) + ")";
      if ( lateness > TOLERANCE || Math.abs(latenessDelta) > TOLERANCE ) {
         string += " late=" + Utils.doubleToString(lateness,2);
         if ( Math.abs(distanceDelta) > TOLERANCE )
            string += "(" + Utils.doubleToString(latenessDelta,2) + ")";
      }
      return string;
   }

   public double toDouble() {
      return deliveries*UNDEL_FACTOR + distance * DISTANCE_FACTOR +
         lateness*LATE_FACTOR;
   }
         
   public String toString (){
      String score = "delivered= " + Utils.doubleToString((100-deliveries),2) + "%" 
         + " distance=" + Utils.doubleToString(distance,2)+ "%";
      if ( lateness > TOLERANCE )
         score += " late=" + Utils.doubleToString(lateness,2);
      return score;
   }
   
}
