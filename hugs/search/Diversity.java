package hugs.search;

import hugs.*;
import hugs.support.*;
import java.util.*;
import java.io.*;

public class Diversity implements Serializable{

   private Mobilities mobilities;
   private int[] indexToId;
   private int[] idToIndex;
   private int numHigh;
   private int freezePoint;
   private int moveCount;
   private double sumDiversity;
   private double[] idToDiversity;
   private double minDiversity;
   private boolean[] frozen;
   
   public Diversity (Mobilities mobilities){
      this(mobilities,1.1);
   }
   
   public Diversity (Mobilities mobilities, double minD) {
      minDiversity = minD;
      this.mobilities = mobilities;
      idToIndex = new int[mobilities.size()];
      indexToId = new int[mobilities.size()];
      idToDiversity = new double[mobilities.size()];
      frozen = new boolean[mobilities.size()];
      numHigh = 0;
      for (int i = mobilities.size(); i-->0;)
         if ( mobilities.getMobility(i) == Mobilities.HIGH )
	     numHigh++;
      freezePoint = (int) ( (double) numHigh * minDiversity );
      if ( freezePoint > numHigh ) freezePoint = numHigh - 1;
      reset();
   }

   public void reset (){
       for (int i = mobilities.size(); i-->0;){
	   idToIndex[i] = -1;
	   indexToId[i] = -1;
	   frozen[i] = false;
       }
       int count = 0;
       for (int i = mobilities.size(); i-->0;)
	   if ( mobilities.getMobility(i) == Mobilities.HIGH ){
	       idToIndex[i] = count;
	       idToDiversity[count] = 0;
	       indexToId[count++] = i;
	   }
	   else 
	       idToIndex[i] = -1;
       for (int i = mobilities.size(); i-->0;)
	   idToDiversity[i] = 0;
       moveCount = 0;
       sumDiversity = 0;
   }

   public double registerMove (Node[] moved) {
      double sumD = 0;
      double count = 0;
      if ( numHigh > 0 )
         for (int i = moved.length; i-->0;){
            int id = moved[i].getId();
            int index = idToIndex[id];
            if ( mobilities.getMobility(id) == Mobilities.HIGH ){ // ignore medium's that move
               sumD += (double)index/(double)numHigh;
               // System.out.println("moved " + id + ": " + (double)index/(double)numHigh);
               count++;
            }
            // else System.out.println("moved but was not high: " + id + " " + mobilities.getMobility(id));
         }
      double aveD = (count > 0) ? sumD/count : 0;
      sumDiversity += aveD;
      //System.out.println("aveD: " + aveD);
      // System.out.println("average diversity: " + aveDiversity());
      moveCount++;
      for (int i = moved.length; i-->0;){
         int id = moved[i].getId();
         if ( idToIndex[id] >= 0 )
            increaseDiversity (id,idToDiversity[id] + 1);
      }
      return aveD;
   }

   public double aveDiversity (){
      if ( moveCount > 0 ) return sumDiversity/moveCount;
      return 1;
   }
   
   private void increaseDiversity (int id, double d){
      int orig = idToIndex[id];
      int pos = orig;
      while ( pos > 0 && idToDiversity[indexToId[pos-1]] <= d )
         pos--;
      // move d to pos
      // System.out.println("move " + id + " from "+ orig +  " to " + pos + " of " + numHigh);
      
      int min = pos+1;
      for (int i = orig; i-->pos;){
         indexToId[i+1] = indexToId[i];
         idToIndex[indexToId[i]] = i+1;
      }
      
      indexToId[pos] = id;
      idToIndex[id] = pos;
      idToDiversity[id] = d;
   }

   public void print () {
      for (int i = 0;  i < numHigh; i++ )
         System.out.print(indexToId[i] + "(" + idToDiversity[indexToId[i]]
                          + ") ,");
      System.out.println();
   }

   public void freeze () {
      int count = 0;
      if ( aveDiversity() < minDiversity ){
         // System.out.println("freezing before: " + mobilities);
         for (int i = freezePoint; i-->0; ){
            int id = indexToId[i];
            if ( mobilities.getMobility(id) == Mobilities.HIGH ){
               count++;
               frozen[id] = true;
               anyFrozen = true;
               mobilities.setMobility(id,Mobilities.MED);
               // System.out.print(id + " ");
            }
         }
         // System.out.println("freezing after: " + mobilities);
      }
      // System.out.println();
   }

   private boolean anyFrozen = false;
   public void unfreeze() {
      if ( !anyFrozen) return;
      for (int i = frozen.length; i-->0;)
         if ( frozen[i] ){
            frozen[i] = false;
            mobilities.setMobility(i,Mobilities.HIGH);
         }
      anyFrozen = false;
   }
   
   public static void main (String[] args){
      Mobilities mobilities = new Mobilities(8);
      Diversity diversity = new Diversity(mobilities);
      diversity.print();

      for (int i = 0; i < 200; i++){
         int pick = (int)(Math.random()* mobilities.size());
         double newD = diversity.idToDiversity[pick] + .1;
         System.out.println("set " + pick + " to " + newD);
         diversity.increaseDiversity(pick, newD);
         diversity.print();
      }
   }
}
