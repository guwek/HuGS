package hugs.apps.crossing;

import hugs.*;
import java.util.*;
import java.awt.*;
import java.math.*;
import java.lang.reflect.Array;
import java.io.*;


public class Edge implements Serializable, Cloneable {
  public Node from;
  public Node to;
  
   // private static int totalNumCrossed;
   
   private boolean crossed = false;
   private int numCrossed = 0;
   private int delta = 0;
   private Point start;
   private Point end;
   private double slope;
   private boolean isSlopeDefined = true;
   private double length;
   
   public Edge(Node from, Node to){
      this.from = from;
      this.to = to;
      //update();
   }

   public boolean equals (Edge edge){
      return ( this.from == edge.from &&
               this.to == edge.to);
   }

   public boolean equalsAny (java.util.List edges ){
      for (Iterator i = edges.iterator(); i.hasNext();)
         if ( equals((Edge) i.next()) ) return true;
      return false;
   }
   
   public Object clone () {
      Edge e = new Edge(from,to);
      /*
      e.crossed = this.crossed;
      e.numCrossed = this.numCrossed;
      e.delta = this.delta;
      e.start = this.start;
      e.end = this.end;
      e.slope = this.slope;
      e.isSlopeDefined = this.isSlopeDefined;
      e.length = this.length;
      */
      return e;
   }
   
   public int getNumCrossed(){
      return numCrossed;
   }
   public int getDelta(){ return delta;}
   public void setDelta(int delta){ this.delta = delta;}


   // public static int getTotalNumCrossed(){ return totalNumCrossed;}
   
   
   public boolean isCrossed(){ return crossed;}
   public void update(Solution s){
      CrossingSolution solution = (CrossingSolution) s;
      Point p1 = solution.getLocation(from.getId());
      // Point point1 = new Point(p1.x * Node.X_FACTOR,p1.y*Node.Y_FACTOR);
      Point point1 = new Point(p1.x,p1.y);
      Point p2 = solution.getLocation(to.getId());
      // Point point2 = new Point(p2.x * Node.X_FACTOR,p2.y*Node.Y_FACTOR);
      Point point2 = new Point(p2.x,p2.y);
      if ( point1.y < point2.y ){
         start = point1;
         end = point2;
      }
      else{
       start = point2;
       end = point1;
    }  
    double deltaX = (end.x - start.x);
    double deltaY = (end.y - start.y);
    slope = (deltaX == 0) ? 0 : deltaY / deltaX;
    isSlopeDefined = (deltaX != 0);
    length = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
  }

  private boolean inside(double x, double y){
    if ((x <= start.x) || (x >= end.x)) return false;
    if (start.y < end.y) 
      if ((y <= start.y) || (y >= end.y))
        return false;
      else return true;
    if ((y <= end.y) || (y >= start.y))
      return false;
    return true;
  }

  public boolean crosses (Edge edge){
    if (crossesAux(edge)){
      numCrossed++;
      crossed = true;
      edge.crossed = true;
      edge.numCrossed++;
      return true;
    }
    return false;
  }

  private boolean inLine(double x, double y){
    if (x <= start.x || x >= end.x) return false;
    if (slope == 0) return (y == start.y);
    double diff = ((x-start.x)/(y-start.y)) - ((end.x - x)/(end.y-y));
    return (diff < 0.01);
  }

   
   public String toString(){
      return "[Edge: " + from + "," + to + "]";
   }
   // this is for layered problems
   private boolean crossesAux (Edge edge){
      if (this.start.y != edge.start.y) return false;
      if (this.start.x > edge.start.x)
         return (this.end.x < edge.end.x);
      if (this.start.x < edge.start.x)
         return (this.end.x > edge.end.x);
      return false;
   }
   
   public static int computeAllCrosses(){
      return computeAllCrosses((CrossingProblem)Hugs.THIS.getProblem());
   }

   public static int computeAllCrosses(CrossingProblem problem){
      return computeAllCrosses(problem,(CrossingSolution)Hugs.getCurrentSolution());
   }
   
   public static int computeAllCrosses(CrossingProblem problem, CrossingSolution solution){
      return computeAllCrosses(problem,solution,problem.getEdges());
   }

   public static int computeAllCrosses(CrossingProblem problem, CrossingSolution solution, java.util.List edges){
      int size = edges.size();
      for (int i = 0; i<size;i++){
         Edge e = (Edge) edges.get(i);
         e.crossed = false; 
         e.numCrossed = 0;
         e.update(solution);
      }
      int score = 0;
      for (int i = 0; i<size;i++){
         Edge edge1 = (Edge) edges.get(i);
         for (int j = i+1; j<size;j++){
            Edge edge2 = (Edge) edges.get(j);
            if ( edge1.crosses(edge2) )
               score++;
         }
      }
      return score;
   }

   

}


