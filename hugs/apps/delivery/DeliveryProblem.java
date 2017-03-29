package hugs.apps.delivery;

import hugs.*;
import hugs.utils.*;
import java.util.*;
import java.io.*;

public class DeliveryProblem implements Problem {

   public static int MAX_ORDER = 7;
   public static int MIN_ORDER = 3;
   public static boolean TIME_WINDOWS = false;
   public static double MIN_WINDOW = .2;
   private DeliveryNode[] nodes;
   private int xSize;
   private int ySize;
   private double xAdjustment;
   private DeliverySolution initSolution = null;
   private DeliveryNode depot;
   private int maxDeliveries;

   public DeliveryProblem (int xSize, int ySize, DeliveryNode[] nodes) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.nodes = nodes;
      depot = nodes[0];
      this.xAdjustment = DeliveryVisualization.aspectRatio * 
	  (double) ySize/ (double) xSize;
      maxDeliveries = 0;
      for (int i = 0; i < nodes.length;i++)
	  maxDeliveries += nodes[i].getOrders();
   }

   public DeliveryProblem (int xSize, int ySize, int size) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.xAdjustment = DeliveryVisualization.aspectRatio * 
	  (double) ySize/ (double) xSize;
      // System.out.println("xAdjustment = " + xAdjustment);
      depot = new DeliveryNode(xSize/2,ySize/2,0,1,0);
      boolean[][] taken = new boolean[xSize][ySize];
      maxDeliveries = 0;
      for (int x = 0; x < xSize; x++)
         for (int y = 0; y < ySize; y++)
            taken[x][y] = false;
      taken[depot.x][depot.y] = true;
      nodes = new DeliveryNode[size];
      nodes[0] = depot;
      for (int i = 1; i < size; i++){
         double start = Utils.randomDouble() * (1-MIN_WINDOW);
         double maxWindow = 1-start;
         double end = 0;
         if ( maxWindow < MIN_WINDOW ) end = start + MIN_WINDOW;
         else {
            end = start + 
               Utils.randomDouble() * (maxWindow-MIN_WINDOW) + MIN_WINDOW;
         }
         boolean ok = false;
         int x = depot.x;
         int y = depot.y;
         while ( taken[x][y] ){
            x = Utils.randomInt(xSize);
            y = Utils.randomInt(ySize);
            }
         taken[x][y] = true;
         int order = MIN_ORDER + Utils.randomInt((MAX_ORDER-MIN_ORDER)+1);
         maxDeliveries += order;
         nodes[i] = new DeliveryNode(x,y,start,end,order);
       }
   }

   
   public Solution getInitSolution(){
      if ( initSolution == null )
         initSolution = new DeliverySolution(this);
      return (Solution) initSolution;
   }
   
   public Solution randomSolution(){
      System.out.println("WARNING: should maybe implement randomSolution for DeliveryProblem");
      return getInitSolution();
   }

   public Node[] getNodes () { return nodes;}
   public int size () { return nodes.length; }

   // extra
   public DeliveryNode getNode (int i) { return nodes[i]; }
   public double getXAdjustment() { return xAdjustment;}
   public int getXSize() { return xSize;}
   public int getYSize() { return ySize;}
   public DeliveryNode getDepot() {return depot;}
   public int getMaxDeliveries () {return maxDeliveries;}

  public static DeliveryProblem read (String name) {
      BufferedReader reader = Utils.myOpenFile(name);
      if ( reader == null ) return null;
      String string = Utils.readLineIf(reader);
      int xSize = Utils.stringToInt(string);
      string = Utils.readLineIf(reader);
      int ySize = Utils.stringToInt(string);
      string = Utils.readLineIf(reader);
      int num = Utils.stringToInt(string);
      DeliveryNode[] nodes = new DeliveryNode[num];
      for (int i = 0; i < num; i++) {
	  string = Utils.readLineIf(reader);
	  String[] node = Utils.stringToArray(string);
	  nodes[i] = new DeliveryNode(Utils.stringToInt(node[0]),
				      Utils.stringToInt(node[1]),
				      Utils.stringToInt(node[2]));
      }
      return new DeliveryProblem(xSize,ySize,nodes);

   }

   public static void write (DeliveryProblem problem, String name) {
      List list = new ArrayList();
      list.add(""+problem.xSize);
      list.add(""+problem.ySize);
      int size = problem.nodes.length;
      list.add(""+size);
      for (int i = 0; i < size; i++) 
	  list.add(problem.nodes[i].x + " " +
		   problem.nodes[i].y + " " +
		   problem.nodes[i].getOrders());
      Utils.makeFile(name,list);
   }



}



