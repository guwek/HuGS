package hugs.apps.stpc;

import hugs.Node;
import java.awt.*;

public class StpcNode extends Node {

   public int x;
   public int y;
	private int id;
   private int orders; // number of orders

   public int getOrders () {return orders;}
   
   public StpcNode (int id, int x, int y, int orders) {
      super(null);
      this.x = x;
      this.y = y;
      this.orders = orders;
      this.id = id;
   }

	public int getId() { return id; }
	
   public String toString(){
      return "n" + id + "(" + x + "," + y + ")";
   }
}
