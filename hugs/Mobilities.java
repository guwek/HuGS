package hugs;

import java.util.*;
import java.io.*;
   
public class Mobilities implements Serializable {
  public static final int HIGH = 2;
  public static final int MED = 1;
  public static final int LOW = 0;
  private int[] values;
  private int[] counts;
  public Mobilities (int num){
    values = new int[num];
    counts = new int[HIGH+1];
    counts[LOW] = 0;
    counts[MED] = 0;
    counts[HIGH] = num;
    for (int i = 0; i < num; i++)
      values[i] = HIGH;
  }

   public int size() { return values.length; }
   public Object clone () {
      Mobilities mobilities = new Mobilities(values.length);
      for (int i = values.length; i--> 0;)
         mobilities.values[i] = this.values[i];
      for (int i = counts.length; i-->0;)
         mobilities.counts[i] = this.counts[i];
      return mobilities;
   }

   public void copyInto (Mobilities mobilities) {
      for (int i = values.length; i--> 0;){
         //          if ( this.values[i] != mobilities.values[i] )
         // System.out.println("copyInto: setting " + i + " to " + values[i]);
         this.values[i] = mobilities.values[i];
      }
      for (int i = counts.length; i-->0;)
         this.counts[i] = mobilities.counts[i];
   }

  public void setAll(int value){
    counts[LOW] = 0;
    counts[MED] = 0;
    counts[HIGH] = 0;
    counts[value] = values.length;
    for (int i = 0; i < values.length; i++)
      values[i] = value;
  }

   public int getCount(int type){
      if ( type >= 0 && type < counts.length)
         return counts[type];
      return -1;
   }
      
  public void setMobility(int num, int value){
    counts[values[num]]--;
    counts[value]++;
    values[num] = value;
  }
  public boolean allSame(){return counts[LOW] == values.length ||
			     counts[MED] == values.length ||
			     counts[HIGH] == values.length;}

  private void addRandom(Node[] nodes, int mobility, Vector vector){
    boolean[] used = new boolean[values.length];
    for (int i = counts[mobility]; i-->0;){
      int target = (int) (Math.random() * (double)(i+1));
      int index = 0;
      int count = 0;
      while (count < target){
	if (used[index] == true || values[index] == mobility) count++;
	index++;
      }
      while (values[index] != mobility || used[index] == true)
	index++;
      vector.addElement(nodes[index]);
      used[index] = true;
    }
  }

  public Vector computeRandomOrderHighs(){
    return computeRandomOrderHighs(Hugs.THIS.getProblem().getNodes());
  }
  public Vector computeRandomOrderHighs(Node[] nodes){
    if (allSame()){
      Vector v = new Vector(values.length);
      addRandom(nodes,HIGH,v);
      return v;
    }
    // not all same
    Vector v = new Vector(counts[HIGH]);
    addRandom(nodes,HIGH,v);
    return v;  
  }

  public int getMobility (int num){return values[num];}
  public int getMobility (Node node){return values[node.getId()];}

  public String toString () {
      return "[mobilities " + counts[HIGH] + "/" + counts[MED] + "/" + counts[LOW] + "]";
   }
}

