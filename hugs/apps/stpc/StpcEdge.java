package hugs.apps.stpc;

import hugs.Node;
import java.io.*;

public class StpcEdge implements Serializable, Comparable {

   public int from;
   public int to;
	public double distance;
	public boolean orig;
	
   public StpcEdge (int nodeFrom, int nodeTo, double d, boolean orig) {
		this.from = nodeFrom;
		this.to   = nodeTo;
		this.distance = d;
		this.orig = orig;
   }

   public String toString(){
      return "e(" + from + "," + to + ", " + distance + ")";
   }
   
   public int compareTo(Object e)
   {
   	StpcEdge x = (StpcEdge) e;
   	return compareTo(x);
   }

   public int compareTo(StpcEdge e)
   {
   	if (this.distance == e.distance)
			return 0;
		else
		   return (this.distance < e.distance) ? -1 : 1;
   }
}
