package hugs.apps.jobshop;

import hugs.Node;
import java.awt.*;

public class JobshopNode extends Node {

   private int job;
   private int op;
   private int machine;
   
   public JobshopNode (int job, int op, int machine) {
      super(null);
      this.job = job;
      this.op = op;
      this.machine = machine;
      // System.out.println(id + " new jobshopNode, job " + job + ", op " + op +                 ",machine = " + machine);
   }

   public int getJob () { return job; }
   public int getOp () { return op; }
   public int getMachine () { return machine; }
   
   public String toString(){
      return "n" + id + "(" + job + "," + op + " on " + machine + ")";
   }
}
