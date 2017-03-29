package hugs.apps.crossing;

import hugs.*;
import hugs.utils.*;
import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.math.*;
import java.io.*;
import java.text.*;
import hugs.utils.*;

public class CrossingProblem implements Problem {
   public static boolean debug = false;
   public static boolean lagunaMethod = false;
   public static boolean fakeAgo = false;
   public static boolean initOptimize = true;
   public static int numOptimizeIts = 1;
   public static double TARGET_DENSITY = .065 * 2;
   public static int MIN_X_NUM_NODES = 5;
   
   private int xSize = 15; // 12
   private int ySize = 6; // 8
   private int numNodes = xSize * ySize;

   private int numEdges;
   private CrossingSolution initSolution;
   private Vector allEdges = new Vector(200);
   private Vector[] allEdgesForNodes;
   private Vector[] sourceEdges;
   private Node[] nodes;

   private void addEdge (Edge newEdge,int from, int to ) {
      if ( newEdge.from.getId() != from || newEdge.to.getId() != to ){
         System.out.println("WARNING: strangeness in CrossingProblem.addEdge");
      }
      allEdges.addElement(newEdge);
      allEdgesForNodes[from].addElement(newEdge);	    
      allEdgesForNodes[to].addElement(newEdge);
      sourceEdges[from].addElement(newEdge);
   }

   public int getXSize() { return xSize; }
   public int getYSize() { return ySize; }
   
   public Node[] getNodes () {
      return nodes;
   }
   /*
   // this is all very clunky
   public static void resetSize (Problem p) {
      CrossingProblem problem = (CrossingProblem) p;
      resetSize(problem.getNumX(), problem.getNumY());
   }
   public static void resetSize (int x, int y) {
      System.out.println("resizing: " + x + ", " + y);
      xSize = x;
      ySize = y;
      numNodes = x*y;
   }
   */
   
    
  public Solution getInitSolution(){
      if ( initOptimize && !optimizedInitSolution )
	  optimizeInitSolution(numOptimizeIts);
      return initSolution;
  }
  public int getNumEdges(){return numEdges;}
  public Vector getEdges(){return allEdges;}
  public Vector[] getEdgesForNodes(){return allEdgesForNodes;}
   public int getNumEdgesForNode(int id){return allEdgesForNodes[id].size();}
  public Vector getEdgesForNodes(int num){return allEdgesForNodes[num];}
  public Vector[] getSourceEdges(){return sourceEdges;}
  public Vector getSourceEdges(int num){return sourceEdges[num];}
  public int getNumSourceEdges(){return sourceEdges.length;}
  public int size(){return numNodes;}

   private static Node /* or null */ getNode(Vector nodes, String name){
      for (int i = nodes.size(); i-->0;)
         if (((Node)nodes.elementAt(i)).getName().compareTo(name) == 0)
            return (Node) nodes.elementAt(i);
      Node n = new Node(name);
      System.out.println("made node " + n.getId() + " with name " + name);
      nodes.addElement(n);
      return n;
   }
   

   public CrossingProblem (String filename, int xSize, int ySize) {
      this.xSize = xSize;
      this.ySize = ySize;
      this.numNodes = xSize * ySize;

      System.out.println("CrossingProblem reading problem from " + filename);
      allEdgesForNodes = new Vector[numNodes]; // from and to
      sourceEdges = new Vector[numNodes]; // only from
      nodes = new Node[numNodes];
      
      int count = 0;
      initSolution = new CrossingSolution(numNodes);
      int[] level = new int[numNodes];
      for (int y = 0; y < ySize; y++)
         for (int x = 0; x < xSize; x++)
            {
               initSolution.setLocation(count,new Point(x,y));
               String name = "c"+count;
               nodes[count] = new Node(name);
               // nodes[count].setLocation(new Point(x,y));
               level[count] = y;
               count ++;
            }
      
      int size = ySize * xSize;
      for (int i = 0; i < size; i++)
         allEdgesForNodes[i] = new Vector();
      
      for (int i = 0; i < size; i++)
         sourceEdges[i] = new Vector();
      
      BufferedReader r =  Utils.myOpenFile(filename);
      Utils.readLineIf(r); // Leda.Graph
      Utils.readLineIf(r); // void
      Utils.readLineIf(r); // void
      String s1 = Utils.readLineIf(r);
      int num = Utils.stringToInt(s1);
      if ( num != numNodes ) {
         System.out.println("file " + filename + " contains " + num + " nodes, was expecting " + numNodes);
         System.exit(0);
      }
      for (int i = 0; i < num; i++)
         Utils.readLineIf(r); // graph nodes
      s1 = Utils.readLineIf(r);
      int numEdges = Utils.stringToInt(s1);
      for (int i = 0; i < numEdges; i++) {
         s1 = Utils.readLineIf(r);
         StringTokenizer tokenizer = new StringTokenizer(s1);
         int from = Utils.stringToInt((String)tokenizer.nextElement()) -1;
         int to = Utils.stringToInt((String)tokenizer.nextElement()) -1;
         System.out.println(i + ": " + from + "->" + to);
         Edge newEdge = new Edge(nodes[from],nodes[to]);
         addEdge(newEdge,from,to);
      }
   }
   
   /*
   public CrossingProblem (String name){
      String fileName = "/homes/lesh/optable/crossings/" + name;
      BufferedReader r =  Utils.myOpenFile(fileName);
      if (r == null) {
         System.out.println("Error: no file named " + fileName);
      }
      MyTokenizer tokenizer = new MyTokenizer(r,fileName);
      // skip beginning nonsense
      for (int i = 0; i < 15; i++) 
         tokenizer.myNextToken();
      int maxFrom = 0;
      int maxTo = 0;
      Vector nodeVector = new Vector();
      Vector edgeVector = new Vector();
      while (tokenizer.ttype != StreamTokenizer.TT_EOF){
         tokenizer.myNextToken();
         String from = tokenizer.sval;
         System.out.println("from = " + from);
         if (from == null) break;
         int fromNum = Integer.valueOf(from.substring(1,from.length())).intValue();
         tokenizer.myNextToken();
         tokenizer.myNextToken();
         tokenizer.myNextToken();
         String to = tokenizer.sval;
         int toNum = Integer.valueOf(to.substring(1,to.length())).intValue();
         tokenizer.myNextToken();
         if ( fromNum > maxFrom ) maxFrom = fromNum;
         if ( toNum > maxTo ) maxTo = toNum;
         Node fromNode = getNode(nodeVector,from);
         Node toNode = getNode(nodeVector,to);
         edgeVector.addElement(new Edge(fromNode,toNode));
         System.out.println(from + " " + fromNum + " -> " + to);
      }
      Utils.myCloseFile(r);
      int max = maxFrom > maxTo ? maxFrom : maxTo; 
      xSize = max;
      ySize = 2;
      numNodes = xSize * ySize;
      System.out.println("numNodes = " + numNodes);
      initSolution = new CrossingSolution(numNodes);
      for (int i = 1; i <= max; i++){
         String AName = "a" + i;
         String BName = "b" + i;
         Node ANode = getNode(nodeVector,AName);
         Node BNode = getNode(nodeVector,BName);
         initSolution.setLocation(ANode.getId(),new Point(i-1,0));
         initSolution.setLocation(BNode.getId(),new Point(i-1,1));
         ///ANode.setLocation(new Point(i,0));
         ///BNode.setLocation(new Point(i,1));
      }
      allEdgesForNodes = new Vector[numNodes]; // from and to
      sourceEdges = new Vector[numNodes]; // only from
      nodes = new Node[numNodes];
      
      for (int i = 0; i < numNodes; i++){
         nodes[i] = (Node) nodeVector.elementAt(i);
         allEdgesForNodes[i] = new Vector();
      }
      for (int i = 0; i < numNodes; i++)
         sourceEdges[i] = new Vector();
      for (int i = edgeVector.size(); i-->0;){
         Edge edge = (Edge) edgeVector.elementAt(i);
         edge.update(initSolution);
         allEdges.addElement(edge);
         int from = edge.from.getId();
         int to = edge.from.getId();
         allEdgesForNodes[from].addElement(edge);	    
         allEdgesForNodes[to].addElement(edge);
         sourceEdges[from].addElement(edge);
      }
      numEdges = allEdges.size();
   }
   */
   
   private int randomTarget (int from, int max ){
      // 
      for (int i = 0; i < max; i++){
         if ( Math.random() < .3 && ((from + i) < max))
            return from+i;
         if ( Math.random() < .3 && ((from - i) >= 0) )
            return from-i;
      }
      return (int) Math.random() * xSize;
   }


   public CrossingProblem(int xSize, int ySize){
      if ( fakeAgo ) {
         xSize = 12;
         ySize = 8;
      }
      this.xSize = xSize;
      this.ySize = ySize;
      this.numNodes = xSize * ySize;
      
      allEdgesForNodes = new Vector[numNodes]; // from and to
      sourceEdges = new Vector[numNodes]; // only from
      nodes = new Node[numNodes];
      
      int count = 0;
      initSolution = new CrossingSolution(numNodes);
      int[] level = new int[numNodes];
      for (int y = 0; y < ySize; y++)
         for (int x = 0; x < xSize; x++)
            {
               initSolution.setLocation(count,new Point(x,y));
               String name = "c"+count;
               nodes[count] = new Node(name);
               // nodes[count].setLocation(new Point(x,y));
               level[count] = y;
               count ++;
            }
      
      int size = ySize * xSize;
      for (int i = 0; i < size; i++)
         allEdgesForNodes[i] = new Vector();
      
      for (int i = 0; i < size; i++)
         sourceEdges[i] = new Vector();
      
      if ( fakeAgo ) {
         boolean ok = false; 
         while ( !ok) {
            System.out.println("attemping to make a fake ago");
            // add one out of every node
            for (int y = 0; y < ySize-1; y++)
               for (int x = 0; x < xSize; x++) {
                  int from = (y * xSize) + x;
                  if ( allEdgesForNodes[from].isEmpty() ) {
                     int toX = Utils.randomInt(xSize);
                     int to = ((y+1) * xSize) + toX;
                     Edge newEdge = new Edge(nodes[from],nodes[to]);
                     addEdge(newEdge,from,to);
                  }
               }
            // add one to everybody at end.
         int lastLevel = ySize -1;
         for (int x = 0; x < xSize; x++) {
            int to = (lastLevel * xSize) + x;
            if ( allEdgesForNodes[to].isEmpty() ) {
               int fromX = Utils.randomInt(xSize);
               int from = ((lastLevel-1) * xSize) + fromX;
               Edge newEdge = new Edge(nodes[from],nodes[to]);
               if ( debug ) System.out.println("last row edge: " + newEdge);
               allEdges.addElement(newEdge);
               allEdgesForNodes[from].addElement(newEdge);	    
               allEdgesForNodes[to].addElement(newEdge);
               sourceEdges[from].addElement(newEdge);
            }
         }
         int numLeft = 110 - allEdges.size();
         if ( numLeft >= 0 ){ 
            System.out.println("numRandomEdges: " + numLeft);
            for (int i =0; i < numLeft; i++)
               addRandomEdge();
            ok = true;
         }
         }
      }
      else if ( lagunaMethod ) {
         int numNodes = 0;
         int[] num = new int[ySize];
         int span = xSize - MIN_X_NUM_NODES;
         for (int i = 0; i < ySize; i++) {
            num[i] = Utils.randomInt(span) + MIN_X_NUM_NODES;
            if ( debug ) System.out.println("num nodes on level " + i + " = " + num[i]);
            numNodes += num[i];
         }
         if ( debug ) System.out.println("total: " + numNodes);
         // add one out of every node
         for (int y = 0; y < ySize-1; y++)
            for (int x = 0; x < num[y]; x++) {
               int from = (y * xSize) + x;
               int toX = Utils.randomInt(num[y+1]);
               int to = ((y+1) * xSize) + toX;
               if ( debug ) System.out.println("reg: " + y + "," + x + "-> " + (y+1) + "," + toX);
               Edge newEdge = new Edge(nodes[from],nodes[to]);
               addEdge(newEdge,from,to);
            }
         // add one to everybody at end.
         int lastLevel = ySize -1;
         for (int x = 0; x < num[lastLevel]; x++) {
            int to = (lastLevel * xSize) + x;
            if ( allEdgesForNodes[to].isEmpty() ) {
               int fromX = Utils.randomInt(num[lastLevel-1]);               
               int from = ((lastLevel-1) * xSize) + fromX;
               Edge newEdge = new Edge(nodes[from],nodes[to]);
               if ( debug ) System.out.println("last row edge: " + newEdge);
               allEdges.addElement(newEdge);
               allEdgesForNodes[from].addElement(newEdge);	    
               allEdgesForNodes[to].addElement(newEdge);
               sourceEdges[from].addElement(newEdge);
            }
         }
         double maxEdges = 0;
         for (int i = 0; i < (ySize-1); i++)
            maxEdges += num[i] * num[i+1];
         double density = (double) allEdges.size() / maxEdges;
         double numNewEdges = 0;
         if ( TARGET_DENSITY > density )
            numNewEdges = (TARGET_DENSITY - density) * maxEdges;
         if ( debug ) {
            System.out.println("num edges = " + allEdges.size());
            System.out.println("maxEdges= " + maxEdges + ", density: " + density);
            System.out.println("num edges needed= " + numNewEdges);
         }
         for (int i = 0; i < numNewEdges; i++) {
            Edge newEdge = null;
            int from = 0;
            int to = 0;
            while ( newEdge == null || newEdge.equalsAny(allEdges) ) {
               int fromY = Utils.randomInt(ySize-1);
               int toY = fromY+1;
               int fromX = Utils.randomInt(num[fromY]);
               int toX = Utils.randomInt(num[toY]);
               from = (fromY * xSize) + fromX;
               to = (toY * xSize) + toX;
               newEdge = new Edge(nodes[from],nodes[to]);
            }
            if (debug) System.out.println("Extra edge= " + newEdge);
            addEdge(newEdge,from,to);
         }
      }
      else {
      java.util.List edges = new ArrayList();
      for (int i = 0; i < 3; i++)
         for (int y = 0; y < ySize-1; y++)
            for (int x = 0; x < xSize; x++)
               if ( i >= 2 ||
                    (Utils.randomInt(3) == 0 ) ) {
                  Edge newEdge = null;
                  int from = 0;
                  int to = 0;
                  while ( newEdge == null || newEdge.equalsAny(edges) ) {
                     from = (y * xSize) + x;
                     to = ((y+1) * xSize) + (int) (Math.random() * xSize);
                     // int to = ((y+1) * xSize) + (int) randomTarget(x,xSize);
                     // System.out.println(x + "," + y + ": " + from + " to " + to);
                     newEdge = new Edge(nodes[from],nodes[to]);
                  }
                  edges.add(newEdge);
                  if ( debug )
                     System.out.println("edge: " + newEdge);
                  allEdges.addElement(newEdge);
                  allEdgesForNodes[from].addElement(newEdge);	    
                  allEdgesForNodes[to].addElement(newEdge);
                  sourceEdges[from].addElement(newEdge);
               }
      if ( debug ) {
         System.out.println("num edges = " + allEdges.size());
         double maxEdges = xSize * xSize * (ySize-1);
         double density = (double) allEdges.size() / maxEdges;
         System.out.println("maxEdges= " + maxEdges + ", density: " + density);
      }
      }

      numEdges = allEdges.size();

  
  }

   private void addRandomEdge () {
      Edge newEdge = null;
      int from = 0;
      int to = 0;
      while ( newEdge == null || newEdge.equalsAny(allEdges) ) {
         int fromY = Utils.randomInt(ySize-1);
         int toY = fromY+1;
         int fromX = Utils.randomInt(xSize);
         int toX = Utils.randomInt(xSize);
         from = (fromY * xSize) + fromX;
         to = (toY * xSize) + toX;
         newEdge = new Edge(nodes[from],nodes[to]);
         System.out.println("edge:  " + newEdge);
      }
      if (debug) System.out.println("Extra edge= " + newEdge);
      addEdge(newEdge,from,to);
   }
   
   private CrossingSolution insert (CrossingSolution crossingSolution, Node n1, int loc){
      Point p1 = crossingSolution.getLocation(n1.getId());
      Point p2 = new Point(loc,p1.y);
      Node n2 = crossingSolution.getNode(nodes,p2.x,p2.y);
      int length = Math.abs(p1.x - p2.x);
      Node[] froms = new Node[length+1];
      Point[] tos = new Point[length+1];
      CrossingSolution current = crossingSolution;
      int d1,d2;
      if (p1.x < p2.x)
         for (int i = 0; i < length; i++){
            int n = i + p1.x;
            froms[i] = current.getNode(nodes,n+1,p1.y);
            tos[i] = new Point(n,p1.y);
         }
      else
         for (int i = 0; i < length; i++){
            int n = i + p2.x;
            froms[i] = current.getNode(nodes,n,p1.y);
            tos[i] = new Point(n+1,p1.y);
         }
      froms[length] = n1;
      tos[length] = crossingSolution.getLocation(n2.getId());
      Move m = new CrossingMove(froms,tos);
      return (CrossingSolution) Utils.doMove(m,crossingSolution);
   }


    private boolean optimizedInitSolution = false;
   private void optimizeInitSolution () {
      optimizeInitSolution(-1); // go until no more improvements
   }
   private void optimizeInitSolution (int max) {
      if ( max == 0 ) return;
       System.out.println(max + ": initSolution score = " + initSolution.computeScore());

       CrossingSolution current = initSolution;
       CrossingSolution best = current;       
       for (int n = 0; n < nodes.length; n++) {
	   for (int x = 0; x < xSize; x++){
	       CrossingSolution s = insert(current,nodes[n],x);
	       if ( s.computeScore().isBetter(best.getScore()) )
		   best = s;
	   }
	   current = best;
       }
       System.out.println("newBest: " + best.getScore());
       if ( best.getScore().isBetter(initSolution.getScore()) ) {
	   initSolution = best;
           optimizeInitSolution(max-1);
       }
       optimizedInitSolution = true;
   }

   public Solution randomSolution(){
      CrossingSolution s = new CrossingSolution(nodes.length);
      int nodeCount = 0;
      for (int y = 0; y < ySize; y++){
         boolean taken[] = new boolean[xSize];
         for (int x = 0; x < xSize; x++)
            taken[x] = false;
         for (int x = 0; x < xSize; x++){
            int place = (int) (Math.random() * (xSize -x - 1));
            int spot = 0;
            while (taken[spot]) spot++;
            for (int j = 0; j < place; j++){
               spot++;
               while (taken[spot])  spot++;
            }
            s.setLocation(nodeCount,new Point(spot,y));
            taken[spot] = true;
            nodeCount++;
         }
      }
      System.out.println("random solution: ");
      for (int y = 0; y < ySize; y++){
         for (int x = 0; x < xSize; x++)
            System.out.print(" " + s.getNode(nodes,x,y).getId());
         System.out.println();
      }
      s.computeScore();
      return s;
   }

   public static void convert (String old, String newName) {
      Crossing.X_SIZE = 12;
      Crossing.Y_SIZE = 8;
      CrossingProblem p = new CrossingProblem(old,Crossing.X_SIZE,Crossing.Y_SIZE);
      Utils.writeObject(p,newName);
   }

   public static void main (String[] args) {
      if ( "-all".equals(args[0]) ) {
         for (int i = 0; i < 10; i++) {
            String old = "/homes/lesh/java/testgraphs/graph" + i + ".gw";
            String save = "/homes/lesh/java/crossing/problems/testtangle" + i;
            System.out.println("java crossing.crossing.CrossingProblem " + old + " " + save );
         }
         for (int i = 0; i < 10; i++) 
            System.out.println("cp -f  /homes/lesh/java/problems/testtangle" + i + " /homes/lesh/java/crossing/tests/mobilities/train.short.crossing" + i);
      }
      else {
         convert(args[0],args[1]);
         System.exit(0);
      }
   }
}

