package hugs.apps.delivery;

import hugs.Node;
import java.awt.*;

public class DeliveryNode extends Node {

   public int x;
   public int y;
   private double start; // 0 to 1, can start delivery after this time
   private double end; // 0 to 1, must start delivery by this time
   private int orders; // number of orders

   public double getStart () {return start;}
   public double getEnd () {return end;}
   public int getOrders () {return orders;}
   
   public DeliveryNode (int x, int y, int orders) {
       this(x,y,0.0,1.0,orders);
   }
   public DeliveryNode (int x, int y, double start, double end, int orders) {
      super(null);
      this.x = x;
      this.y = y;
      this.start = start;
      this.end = end;
      this.orders = orders;
   }

   public String toString(){
      return "n" + id + "(" + x + "," + y + ")";
   }
}
