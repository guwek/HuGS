package hugs.apps.crossing;

import hugs.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;

public class CrossingMove implements Move {
   private Node[] froms;
   private Point[] tos;
   private int ply;
   
   public CrossingMove (List fromList, List toList){
      this(fromList,toList,fromList.size());
   }
   public CrossingMove (List fromList, List toList, int ply){
      this.ply = ply;
      froms = new Node[fromList.size()];
      tos = new Point[fromList.size()];
      if ( ply < fromList.size() ) {
         System.out.println("CrossingMove WRONG PLY " + ply + " != " + fromList.size());
      }
      for (int i = fromList.size(); i-->0;){
         froms[i] = (Node) fromList.get(i);
         tos[i] = (Point) toList.get(i);
      }
   }
   public CrossingMove (Node[] froms, Point[] tos){
      this(froms,tos,froms.length);
   }
   public CrossingMove (Node[] froms, Point[] tos, int ply){
      this.ply = ply;
      this.froms = froms;
      this.tos = tos;
  }

  public Node[] getMoved () { return froms;}

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      int ply = froms.length;
      for (int i = 0; i < ply; i++){
         mobilities.setMobility(froms[i].getId(),value);
      }
   }

  public int getPly(){return ply;}
  public Solution tryMove (Solution current){
    Solution trial = doMove(current);
    trial.computeScore();
    if ( trial.getScore().isBetter(current.getScore()) )
       return trial;
    return null;
  }

    public void operateOn (Solution trial ) {
	CrossingSolution s = (CrossingSolution) trial;
	for (int i = froms.length; i-->0;) 
	    s.setLocation(froms[i].getId(),tos[i]);
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }   


  public Solution doMove (Solution current){
     CrossingSolution trial = (CrossingSolution) current.copy();
     operateOn(trial);
     trial.computeScore();
     return trial;
  }

  public String toString(){
    String s = "[";
    for (int i = 0; i < froms.length; i++){
      if (i > 0) s = s + ",";
      s = s + "move " + froms[i] + " from " + ((CrossingSolution)Hugs.getCurrentSolution()).getLocation(froms[i].getId()) + " to " +  tos[i] + ";";
    }
    s = s + "]";
    return s;
  }
}
