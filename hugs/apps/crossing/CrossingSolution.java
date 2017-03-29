package hugs.apps.crossing;

import hugs.support.*;
import hugs.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class CrossingSolution implements Solution {

   private Point[] locations;
   private java.util.List edges = null;
   private CrossingProblem crossingProblem = null;
   
   public CrossingSolution(int num){
      locations = new Point[num];
      if ( Hugs.THIS != null )
         crossingProblem = (CrossingProblem) Hugs.THIS.getProblem();
   }

  public Solution copy(){
    int size = locations.length;
    CrossingSolution s = new CrossingSolution(size);
    for (int i = size; i-->0;)
	s.locations[i] = new Point(locations[i].x,locations[i].y);
    s.score = this.score;
    return s;
  }
  public void setLocation (int num, Point point){
     locations[num] = point;
  }
  public Point getLocation (int num){return locations[num];}
  public Point getLocation (Node node){return locations[node.getId()];}
  public Node getNode (Point p){ return getNode(p.x,p.y); }
  public Node getNode (int x, int y){
     return getNode(crossingProblem.getNodes(),x,y);
  }
   public Node getNode (Node[] nodes, int x, int y){
     for (int i = locations.length;i-->0;)
        if (locations[i].x == x && locations[i].y == y)
           return nodes[i];
     return null;
  }

   private java.util.List[] edgeList = null;
   private void makeEdgeList () {
      int size = crossingProblem.getNodes().length;
      edgeList = new java.util.List[size];
      for (int i = size; i-->0;)
         edgeList[i] = new ArrayList();
      for (Iterator i = edges.iterator(); i.hasNext();) {
         Edge e = (Edge) i.next();
         edgeList[e.from.getId()].add(e);
         edgeList[e.to.getId()].add(e);
      }
   }

   // only used for visualization, so don't compute edgeList unless you are going
   // to see this solution.
   public int getCrosses (int num) {
      if ( edgeList == null ) makeEdgeList();
      int count = 0;
      java.util.List list = edgeList[num];
      for (Iterator i = list.iterator(); i.hasNext();) {
         Edge e = (Edge)i.next();
         count += e.getNumCrossed();
      }
      return count;
   }
   
  public int size () { return locations.length; }

  private CrossingScore score;

  public Score computeScore () {
     return computeScore(null,null);
  }
  public Score computeScore (SearchAdjuster adjuster, java.util.List marked) {
     if ( edges == null ){
        java.util.List toClone = ((CrossingProblem)Hugs.THIS.getProblem()).getEdges();
        edges = new ArrayList(toClone.size());
        for (Iterator i = toClone.iterator(); i.hasNext();)
           edges.add(((Edge)i.next()).clone());
     }
     int intersections = Edge.computeAllCrosses((CrossingProblem) Hugs.THIS.getProblem(),this,edges);
     if ( adjuster == null || !((CrossingSearchAdjuster)adjuster).secondary ) {
        score =  new CrossingScore(intersections);
        return score;
     }
     int sum = 0;
     for (Iterator i = marked.iterator(); i.hasNext();)
        sum+= getLocation((Node)i.next()).x;
     // System.out.println("@@ crossingSol: " + marked.size() + " " + sum);
     score = new CrossingScore(intersections,sum);
     return score;
  }

   public Score getScore(){
      if ( score != null ) return score;
      return computeScore();
   }

   public void checkGoodMove (int node1, int node2) throws Exception{
      if ( locations[node1].y != locations[node2].y ||
           (locations[node1].x+1) != locations[node2].x ) {
         System.out.println("CrossingSolution: badSwap" + locations[node1] + " and " + locations[node2]);
      }
      /*
      if ( locations[node1].y != locations[node2].y ||
           (locations[node1].x+1) != locations[node2].x ) {
         System.out.println("CrossingSolution: badSwap" + locations[node1] + " and " + locations[node2]);
         throw new Exception("bad");
         // System.exit(1);
      }
      */
   }
   public void swap (int node1, int node2) {
      Point temp = locations[node1];
      locations[node1] = locations[node2];
      locations[node2] = temp;
      // computeScore();
   }

   
   public void print (){
      System.out.println("solution: " + computeScore());
      for (int y = 0; y < crossingProblem.getXSize(); y++){
         for (int x = 0; x < crossingProblem.getYSize(); x++)
            System.out.print(" " + getNode(x,y).getId());
         System.out.println();
      }
   }

   // for debugging
   public boolean check (){
      for (int y = 0; y < crossingProblem.getYSize(); y++){
         for (int x = 0; x < crossingProblem.getXSize(); x++)
            if ( getNode(x,y) == null ) {
               System.out.println("CHECK: failing on " + x + "," + y);
               return false;
            }
      }
      return true;
   }

    public void precompute () {} 
   
   public void print (Mobilities mobilities){
      System.out.println("solution: " + computeScore());
      for (int y = 0; y < crossingProblem.getYSize(); y++){
         for (int x = 0; x < crossingProblem.getXSize(); x++)
            System.out.print(" " + getNode(x,y).getId() + "(" +
                             mobilities.getMobility(getNode(x,y).getId()) + ")");
         System.out.println();
      }
   }
}


