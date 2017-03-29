package hugs.apps.protein;

import hugs.utils.*;
import hugs.*;
import java.util.*;
import java.io.*;

public class ProteinProblem implements Problem {

   public static boolean useSpecial = false;
   
   private ProteinNode[] nodes;
   private int xSize;
   private int ySize;
    private hugs.Solution initSolution = null;

   /*
     {bbbbbbbbbbbb
                               wbwb
                               wwbbwwbbwwbwwbbwwbbwwbwwbb
                               wwbbwwbwbw
                               bbbbbbbbbbbb}
   */
   private boolean[] sample = {true,true,true,true,true,true,true,true,true,true,true,true,
                               false,true,false,true,
                               false,false,true,true,false,false,true,true,false,false,true,false,false,true,true,false,false,true,true,false,false,true,false,false,true,true,
                               false,false,true,true,false,false,true,false,true,false,
                               true,true,true,true,true,true,true,true,true,true,true,true};


   public ProteinProblem (int xSize, int ySize, boolean[] sample) {
      this.xSize = xSize;
      this.ySize = ySize;
      nodes = new ProteinNode[sample.length];
      System.out.println("LENGTH " + sample.length);
      for (int i = 0; i < sample.length; i++)
         nodes[i] = new ProteinNode(sample[i]);
   }

   public ProteinProblem (int xSize, int ySize, int size) {
      this.xSize = xSize;
      this.ySize = ySize;
      if ( useSpecial ) {
         nodes = new ProteinNode[sample.length];
         System.out.println("LENGTH " + sample.length);
         for (int i = 0; i < sample.length; i++){
            // boolean b = Utils.randomInt(2) == 0;
            nodes[i] = new ProteinNode(sample[i]);
         }
      }
      else {
         nodes = new ProteinNode[size];
         for (int i = 0; i < size; i++){
            int p = Utils.randomInt(2);
            boolean b = (p == 0);
            System.out.print(p);
            nodes[i] = new ProteinNode(b);
         }
      }
   }
   

   public static void write (ProteinProblem problem, String name) {
      List list = new ArrayList();
      list.add(""+problem.xSize);
      list.add(""+problem.ySize);
      int size = problem.nodes.length;
      boolean[] booleans = new boolean[size];
      for (int i = 0; i < size; i++)
         booleans[i] = problem.nodes[i].getValue();
      list.add(booleansToString(booleans));
      Utils.makeFile(name,list);
               
   }
   
   public static ProteinProblem read (String name) {
      BufferedReader reader = Utils.myOpenFile(name);
      if ( reader == null ) return null;
      String string = Utils.readLineIf(reader);
      int xSize = Utils.stringToInt(string);
      string = Utils.readLineIf(reader);
      int ySize = Utils.stringToInt(string);
      string = Utils.readLineIf(reader);
      return new ProteinProblem(xSize,ySize,ProteinProblem.stringToBooleans(string));
   }

   public static String booleansToString (boolean[] booleans) {
      boolean b = booleans[0];
      String string = "";
      int count = 0;
      for (int i = 1; i <= booleans.length; i++) {
         count++;
         if ( i == (booleans.length ) || booleans[i] != b ) {
            if ( b ) string += " B ";
            else string += " W ";
            string += count;
            if ( i < booleans.length ) 
               b = booleans[i];
            count = 0;
         }
      }
      System.out.println(string);
      return string;
   }
   
   public static boolean[] stringToBooleans (String string) {
      StringTokenizer tokenizer = new StringTokenizer(string);
      List list = new ArrayList();
      while ( tokenizer.hasMoreElements() ){
         String type = (String) tokenizer.nextElement();
         int num = Utils.stringToInt((String) tokenizer.nextElement());
         for (int i = 0; i < num; i++)
            list.add(type);
      }
      int size = list.size();
      boolean[] b = new boolean[size];
      for (int i = 0; i < size; i++) {
         String type = (String) list.get(i);
         if ( "B".equals(type) || "b".equals(type) )
            b[i] = true;
         else if ( "W".equals(type) || "w".equals(type) )
            b[i] = false;
         else {
            System.out.println("unknown type in ProteinProblem.stringToBoolean");
            System.exit(0);
         }
      }
      return b;
   }
   
   public Solution getInitSolution(){
      if ( initSolution == null ){
	 initSolution = (Solution) new ProteinSolution(this);
         ((ProteinSolution)initSolution).initializeSolution();
      }
      return initSolution;
   }
   
   public Solution randomSolution(){
      System.out.println("WARNING: should maybe implement randomSolution for ProteinProblem");
      return getInitSolution();
   }

   public Node[] getNodes () { return nodes;}
   public int size () { return nodes.length; }

   // extra
   public ProteinNode getNode (int i) { return nodes[i]; }
   public int getXSize() { return xSize;}
   public int getYSize() { return ySize;}

   public static void makeProb (int x, int y, String name, String s){
      boolean[] b = stringToBooleans(s);
      System.out.println("String: " + s);
      System.out.print("booleans: ");
      for (int i = 0; i < b.length; i++)
         System.out.print(" " + b[i]);
      System.out.println();
      ProteinProblem p = new ProteinProblem(x,y,b);
      System.out.println("filename = " + name);
      Utils.writeObject(p,name);
   }
   public static void main (String[] args) {
      makeProb(30,30,"protein.l60.b36","W 2 B 3 W 1 B 8 W 3 B 10 W 1 B 1 W 3 B 12 W 4 B 6 W 1 B 2 W 1 B 1 W 1");
      makeProb(30,30,"protein.l64.b42","B 12 W 1 B 1 W 1 B 1 W 2 B 2 W 2 B 2 W 2 B 1 W 2 B 2 W 2 B 2 W 2 B 1 W 2 B 2 W 2 B 2 W 2 B 1 W 1 B 1 W 1 B 12");
      makeProb(30,30,"protein.l100.b47","W 6 B 1 W 1 B 2 W 5 B 3 W 1 B 5 W 1 B 2 W 4 B 2 W 2 B 2 W 1 B 5 W 1 B 10 W 1 B 2 W 1 B 7 W 11 B 7 W 2 B 1 W 1 B 3 W 6 B 1 W 1 B 2");
      makeProb(30,30,"protein.l100.b49","W 3 B 2 W 2 B 4 W 2 B 3 W 1 B 2 W 1 B 2 W 1 B 4 W 8 B 6 W 2 B 6 W 9 B 1 W 1 B 2 W 1 B 11 W 2 B 3 W 1 B 2 W 1 B 1 W 2 B 1 W 1 B 3 W 6 B 3");
   }
}



