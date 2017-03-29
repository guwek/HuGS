package hugs.apps.delivery;

import hugs.*;
import hugs.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.util.*;

public class DeliverySolution implements Solution{

   public static boolean trace = false;

   // BUG, some of these should be moved to Delivery.java
   public static double TIME_PER_DISTANCE = .004;
   private double drivingTimeFactor = 100; // range is 0 to 1
   private double latenessFactor = 100000000;
   private double deliveriesFactor = 100000; // percentage of deliveries
   
   private int[] order;
   private DeliveryProblem problem;
   private int numVisited;
   private double[] arrivals; // computed
   private double[] departures; // computed
   private double distance; // computed
   private double drivingTime; // computed
   private double percentUndelivered; // computed
   private double cost; // computed
   private double deliveries; // computed
   private double time;
   private double lateness; // computed
   private DeliveryScore score;
   
   public DeliverySolution (int size) {
      order = new int[size];
      arrivals = new double[size+1];
      departures = new double[size+1];
      distance = 0;
      drivingTime = 0;
      percentUndelivered = 0;
      time = 0;
      deliveries = 0;
      lateness = 0;
      numVisited = 0;
   }
      
   public DeliverySolution (DeliveryProblem problem) {
      this(problem.size());
      this.problem = problem;
      initializeSolution();
   }

   public boolean equals (Solution s) {
      DeliverySolution solution = (DeliverySolution) s;
      if ( numVisited == solution.numVisited ) return false;
      for (int i = 0; i < numVisited; i++)
         if ( order[i] != solution.order[i] )
            return false;
      return true;
   }
   
   public Solution copy () {
      DeliverySolution solution = new DeliverySolution(this.problem.size());
      solution.problem = this.problem;
      solution.numVisited = this.numVisited;
      int size = problem.size();
      solution.order = new int[size];
      for (int i = 0; i < size; i++)
         solution.order[i] = this.order[i];
      solution.computeCost();
      // solution.cost = this.cost;
      return (Solution) solution;
   }

   public double getArrival (int i) { return arrivals[i]; }
   public double getDeparture (int i) { return departures[i]; }
   public DeliveryNode getNode (int i) { return problem.getNode(order[i]); }
   public int getId (int i) { return order[i]; }
   public int getIdAfter (int i) { return order[getAfter(i)]; }

   public void swap (int first) {
      if ( first +1 == numVisited ){
         System.out.println("WARNING: trying to swap last customer!");
         return;
      }
      // oldScore = getScore();
      int after = first + 1;
      int temp = order[first];
      order[first] = order[after];
      order[after] = temp;
      computeScore();
      // return getScore() - oldScore;
   }

   private int getBefore (int i) {
      if ( i == 0 ) return order.length -1;
      return i - 1;
   }

   private int getAfter (int i) {
      int next = i + 1;
      if ( next < order.length ) return next;
      return 0;
   }

    public Score computeScore (SearchAdjuster adjuster, 
			       java.util.List marked)  {
	return computeScore();
    }

   public Score computeScore (){
      computeCost();
      return (Score) score;
   }
   
   public Score getScore (){ return (Score) score; }

   private void driveLeg (DeliveryNode from, DeliveryNode to, int index) {
      if ( trace )
         System.out.println("drive from " + from + " to " + to);
      double dist = distance(from,to);
      double addedTime = dist * TIME_PER_DISTANCE;
      drivingTime += addedTime;
      distance += dist;
      time += addedTime;
      arrivals[index] = time;
      if ( DeliveryProblem.TIME_WINDOWS ) {
         if ( time < to.getStart() )
            time = to.getStart();
         else if ( time > to.getEnd() )
            lateness += (time - to.getEnd());
      }
      departures[index] = time;
      deliveries += to.getOrders();
   }

   private double leaveLastNode;
   public double computeCost (){
      cost = 0;
      time = 0;
      distance = 0;
      drivingTime = 0;
      deliveries = 0;
      lateness = 0;
      int size = numVisited;
      if ( trace ) {
         System.out.println("numVisited: " + numVisited);
         System.out.print("Path: ");
         for (int i = 0; i < size; i++)
            System.out.print(order[i] + " ");
         System.out.println();
      }
      DeliveryNode pos = problem.getDepot();
      arrivals[0] = 0;
      departures[0] = 0;
      for (int i = 0; i < size; i++){
         DeliveryNode next = problem.getNode(order[i]);
         driveLeg(pos,next,i);
         if ( trace ){
            System.out.println(i + ": d=" + Utils.doubleToString(distance) +
                               ",lt= " + Utils.doubleToString(lateness) +
                               ",del= " + deliveries +
                               ",arr= " + Utils.doubleToString(arrivals[i])
                               + ",dep= " + Utils.doubleToString(departures[i]));
         }
         pos = next;
      }
      leaveLastNode = time;
      driveLeg(pos,problem.getDepot(),size);
      if ( !DeliveryProblem.TIME_WINDOWS){
         if ( drivingTime > 1 ) 
            lateness = drivingTime -1;
         else
            lateness = 0;
      }
      int maxDeliveries = problem.getMaxDeliveries();
      percentUndelivered = 100 * (1- (double) deliveries / (double) maxDeliveries);
      drivingTime = drivingTime * 100;
      cost = lateness*latenessFactor + drivingTime*drivingTimeFactor
         + percentUndelivered * deliveriesFactor;
      if ( trace ){
         System.out.println("percentDelivery = " + percentUndelivered + "lateness= " + lateness + ", drivingTime = " + drivingTime);
         System.out.println(size + ": d=" + Utils.doubleToString(distance) +
                            ",lt= " + Utils.doubleToString(lateness) +
                            ",del= " + deliveries +
                            ",arr= " + Utils.doubleToString(arrivals[size])
                            + ",dep= " + Utils.doubleToString(departures[size]));
         System.out.println("cost= " + cost);
      }
      score = new DeliveryScore(percentUndelivered,drivingTime,lateness);
      return cost;
   }

   
   private double distance (Point one, Point two) {
         double dx = (one.x - two.x) * problem.getXAdjustment();
         double dy = one.y - two.y;
         return Math.sqrt(dx*dx + dy*dy);
      }
   private double distance (DeliveryNode one, DeliveryNode two) {
      double dx = (one.x - two.x) * problem.getXAdjustment();
      double dy = one.y - two.y;
      return Math.sqrt(dx*dx + dy*dy);
   }

   /*
   public String toString () {
      String out = "[";
      for (int i = 0; i < order.length; i++){
         if ( i > 0 ) out = out + ",";
         out = out + order[i];
      }
      out = out + "]";
      return out;
   }
   */

   public void insert (int inserted, int location){
      /*
      if ( numVisited == order.length ){
         System.out.println("can't insert into full route");
         System.exit(0);
      }
      */
      for (int i = numVisited; i--> location;)
         order[i+1]= order[i];
      order[location] = inserted;
      numVisited++;
   }

   public void reinsert (int inserted, int location){
      int value = order[inserted];
      if ( inserted > location )
         for (int i = inserted+1; i-->location;)
            order[i] = order[i-1];
      else if ( inserted < location )
         for (int i = inserted; i<location;i++)
            order[i] = order[i+1];
      order[location] = value;
   }


   // untested!!!
   public double removeDeliveriesDelta (int removed ) {
      DeliveryNode posRemove = problem.getNode(order[removed]);
      double orders = posRemove.getOrders();
      int maxDeliveries = problem.getMaxDeliveries();
      return 100 * ( 0 - (orders / (double) maxDeliveries));
   }
   
   // untested!!!
   public double removeDistanceDelta (int removed ) {
      int index = order[removed];
      DeliveryNode posRemove = problem.getNode(order[removed]);
      DeliveryNode posBefore = removed == 0 ? problem.getDepot() : problem.getNode(index-1);
      DeliveryNode posAfter = removed == (order.length-1) ? problem.getDepot() : problem.getNode(order[removed+1]);
      return distance(posBefore,posAfter) - distance(posBefore,posRemove) - distance(posAfter,posRemove);
   }
   
   public void remove (int removed){
      for (int i = removed; i < numVisited; i++)
         order[i]= order[i+1];
      numVisited--;
   }

   
   public void initializeSolution (){
      // greedyInit();
      numVisited = 1;
      order[0] = Utils.randomInt(problem.size());
      cost = computeCost();
   }

   private void greedyInit () {
      boolean[] visited = new boolean[order.length];
      for (int i = visited.length; i-->0;)
         visited[i] = false;
      visited[0] = true; // depot
      // numVisited= 1;
      // order[0] = problem.getDepot().getId();
      numVisited= 0;
      while ( true ){
         // double bestDist = problem.getXSize() * problem.getYSize();
         double bestCost = 0;
         double bestTime = 1;
         boolean anyGood = false;
         int best = 0;
         for (int i = visited.length; i-->0;)
            if ( !visited[i] ){
               order[numVisited] = i;
               numVisited++;
               computeCost();
               numVisited--;
               // if ( cost < bestCost || ((cost - bestCost) < tolerance && leaveLastNode < bestTime ) ){
               if ( lateness == 0 && leaveLastNode < bestTime ) {
                  anyGood = true;
                  best = i;
                  // bestDist = distance;
                  bestTime = leaveLastNode;
                  bestCost = cost;
               }
            }
         if ( anyGood ){
            // System.out.println(numVisited + ": adding " + best + ", bestCost = " + bestCost + " time= " + bestTime);
            visited[best] = true;
            order[numVisited++] = best;
         }
         else {
            computeCost();
            return;
         }
      }
}

   // return -1 if not on list
   public int getPosition (int id) {
      for (int i = numVisited; i-->0;)
         if ( order[i] == id ) return i;
      return -1;
   }

   public boolean onRoute (int id) {
      return getPosition(id) >= 0;
   }
   
   public int size () {
      return numVisited;
   }

   public void print () {
      System.out.println("Solution:" + this);
      for (int i = 0; i < numVisited; i++)
         System.out.print(order[i] + " ");
      System.out.println();
   }

   public String toString() {
      return "[Sol: unDel= " + percentUndelivered + ",dist= " + drivingTime + ", lateness = " + lateness;
   }

///////////////////////////////////


   public double splice (int first, int target) {
      if ( target <= first ) return 0;
      // int n3 = order[getAfter(first)];
      double delta = spliceDelta(first, target);
      int dist = target - first -1;
      int copy[] = new int[dist];
      for (int i = 0; i < dist; i++)
         copy[i] = order[i+first+1];
      order[first+1] = order[target];
      for (int i = 0; i < dist; i++)
         order[first+2+i] = copy[dist-i-1];
            // HACK
      if ( delta > -1 && delta < 1 ) {
         double oldCost = cost;
         cost = computeCost();
         delta = cost - oldCost;
      }
      else
         cost += delta;
      return delta;
   }

   private double spliceDelta (int first, int target){
      if ( target <= first ) return 0;
      // old: n1 -> n2 (first) -> n3  ...  -> n4 -> n5 (target) -> n6
      // new: n1 -> n2 -> n5 -> n4 -> ... (reverse) ... -> n3 -> n6

      // delta: old: n2->n3, n5->n6
      //        new: n2->n5, n3->n6
      
      DeliveryNode n2 = problem.getNode(order[first]);
      DeliveryNode n3 = problem.getNode(order[getAfter(first)]);
      DeliveryNode n5 = problem.getNode(order[target]);
      DeliveryNode n6 = problem.getNode(order[getAfter(target)]);
      
      double delta;
      delta = (distance(n2,n5) + distance(n3,n6)) - (distance(n2,n3) + distance(n5,n6));
      return delta;
   }

   
   public int closestInsert (int inserted ){
      DeliveryNode insert = problem.getNode(inserted);
      DeliveryNode pos1 = null;
      DeliveryNode pos2 = problem.getDepot();
      int bestPos = -1;
      double bestDist = 0;
      for (int i = 0; i < numVisited; i++){
         pos1 = pos2;
         pos2 = problem.getNode(order[i]);
         double distance = distance(pos1,insert) + distance(pos2,insert) - distance(pos1,pos2);
         if ( bestPos == -1 || distance < bestDist ){
            bestDist = distance;
            bestPos = i;
         }
      }
      pos1 = pos2;
      pos2 = problem.getDepot();
      double distance = distance(pos1,insert) + distance(pos2,insert) - distance(pos1,pos2);
      if ( distance < bestDist || bestPos == -1)
         bestPos = numVisited;
      return bestPos;
   }

    public void precompute () { }
}
