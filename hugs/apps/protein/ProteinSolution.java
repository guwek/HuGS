package hugs.apps.protein;

import hugs.support.*;
import hugs.*;
import hugs.utils.*;
import java.util.*;
import java.awt.Point;
import java.io.*;


public class ProteinSolution implements Solution {

   // public static boolean useOdd = false;
   
   private Point[] locations;
   private ProteinProblem problem;
   private Node[] nodes;
   private int xSize;
   private int ySize;

   public ProteinSolution( ProteinProblem problem ){
      nodes = problem.getNodes();
      this.problem = problem;
      int size = problem.size();
      xSize = problem.getXSize();
      ySize = problem.getYSize();
      locations = new Point[size];
   }

   public ProteinSolution( Point[] locations ){
      this.locations = locations;
      this.problem = (ProteinProblem) Hugs.THIS.getProblem();
      int size = problem.size();
      xSize = problem.getXSize();
      ySize = problem.getYSize();
      nodes = problem.getNodes();
   }
   
  public Solution copy(){
    int size = locations.length;
    ProteinSolution s = new ProteinSolution(problem);
    for (int i = size; i-->0;)
	s.locations[i] = new Point(locations[i].x,locations[i].y);
    s.score = this.score;
    return s;
  }
  public void setLocation(int num, Point point){locations[num] = point;}
  public Point getLocation(int num){return locations[num];}
  public Point getLocation(Node node){return locations[node.getId()];}
  public Node getNode(Point p){ return getNode(p.x,p.y); }
  public Node getNode(int x, int y){
    for (int i = locations.length;i-->0;)
      if (locations[i].x == x && locations[i].y == y)
         return problem.getNodes()[i];
    return null;
  }

  public int size() { return locations.length; }
  Score score;

   private int /* 0 or 1 */ hit(int[][]board, int x, int y, int order){
      if ( x >= 0 && y >= 0 && x < xSize && y < ySize &&
           board[x][y] != -1 && board[x][y] != (order - 1) )
         return 1;
      return 0;
   }
   
   private int hits(int[][] board, int x, int y, int order){
      return hit(board,x-1,y,order) + hit(board,x+1,y,order) +
         hit(board,x,y-1,order) + hit(board,x,y+1,order);
   }

   public Score computeScore () {
     return computeScore(null,null);
  }

   public Score computeScore (SearchAdjuster adjuster, 
			       java.util.List marked)  {
       
       int energy = 0;
       int breaks = 0;
       int[][] board = new int[xSize][ySize];
       Node[] nodes = problem.getNodes();
       for (int x = xSize; x-->0;)
          for (int y = ySize; y-->0;)
             board[x][y] = -1;
       int size = nodes.length;
       ProteinNode from = (ProteinNode) nodes[0];
       if ( from.getValue() ) board[locations[0].x][locations[0].y] = 0;
       for (int i = 1; i < locations.length; i++) {
          ProteinNode to = (ProteinNode) nodes[i];
          if ( to.getValue() ) {
             board[locations[i].x][locations[i].y] = i;
             energy -= hits(board,locations[i].x,locations[i].y,i);
          }
          breaks += distance(i-1,i) -1;
          from = to;
       }
       if ( adjuster == null || !((ProteinSearchAdjuster)adjuster).secondary ) {
          score = new ProteinScore(energy,breaks);
          return score;
       }
       int sum = 0;
       List others = new ArrayList();
       if ( marked != null )
          for (Iterator i = marked.iterator(); i.hasNext();) {
             Node n1 = (Node) i.next();
             for (Iterator j = others.iterator(); j.hasNext();)
                sum+= mDistance(n1,(Node)j.next());
             others.add(n1);
          }

       score = new ProteinScore(energy,breaks,sum);
       // System.out.println("@@mDi " + energy + " " + breaks + " " + sum + " " + score);
       return score;
    }

   

   private double distance (int one, int two){
      double dx = locations[one].x - locations[two].x;
      double dy = locations[one].y - locations[two].y;
      return Math.sqrt(dx*dx + dy*dy);
   }

   private double distance (Point one, Point two){
      double dx = one.x - two.x;
      double dy = one.y - two.y;
      return Math.sqrt(dx*dx + dy*dy);
   }


   private double mDistance (Node one, Node two){
      return mDistance(getLocation(one),getLocation(two));
   }
   private double mDistance (int one, int two){
      return Math.abs(locations[one].x - locations[two].x) +
         Math.abs(locations[one].y - locations[two].y);
   }

   private double mDistance (Point one, Point two){
      return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
   }

   
   public Score getScore(){ return score; }

   public void print (){
      System.out.println("solution: " + score);
      for (int y = 0; y < ySize; y++){
         for (int x = 0; x < xSize; x++)
            System.out.print(" " + getNode(x,y).getId());
         System.out.println();
      }
   }

   public void initializeSolution () {
      int size = locations.length;
      int width = xSize/2;
      int height = size/width;
      int posY = ySize/2 - height/2;
      int posX = width/2;
      int startX = posX;
      int stopX = posX + width;
      boolean goingRight = true;
      for (int i = 0; i < size; i++){
         locations[i] = new Point(posX,posY);
         if ( goingRight ){
            posX++;
            if ( posX > stopX ){
               posX--;
               posY++;
               goingRight = false;
            }
         }
         else{
            posX--;
            if ( posX < startX ){
               posX++;
               posY++;
               goingRight = true;
            }
         }
         // System.out.println(i + ": " + locations[i]);
      }
   }

   public String toString () {
      String s = "[solution: ";
      for (int i = 0; i < locations.length; i++) {
         if ( i > 0 ) s += " -> ";
         s += locations[i].x + "," + locations[i].y;
      }
      s += "]";
      return s;
   }

   // The assumption is that no code that calls getBoard will
   // change its contents.  Is meant to be read-only.
   private int[][] board = null; 
   public int[][] getBoard () {
      if ( board == null )
         board = makeBoard();
      return board;
   }
   
   // puts ids in locations where nodes are
   private int[][] makeBoard () {
      int[][] board = new int[xSize][ySize];
      Node[] nodes = problem.getNodes();
      for (int x = xSize; x-->0;)
         for (int y = ySize; y-->0;)
            board[x][y] = -1;
      for (int i = 0; i < locations.length; i++) 
         board[locations[i].x][locations[i].y] = nodes[i].getId();
      return board;
   }

   private boolean isLegal (int x, int y ){
      return x >= 0 && y >= 0 && x < xSize && y < ySize;
   }
   private void fBNaddPoint (int x,int y,int[][] board, List points){
      if ( isLegal(x,y) && board[x][y] == -1 )
         points.add(new Point(x,y));
   }

   private List fBPoints = new ArrayList(4);

   private Point testPoint (int x,int y,int[][] board) {
      if ( isLegal(x,y) && board[x][y] == -1 )
         return new Point(x,y);
      return null;
   }

   private Point findBestNeighbor (Point target, Point current, int[][] board){
      // if ( useOdd )
      // return oddFindBestNeighbor(target,current,board);
      return goodFindBestNeighbor(target,current,board);
   }

   private Point oddFindBestNeighbor (Point target, Point current, int[][] board){
      int deltaX = Math.abs(target.x - current.x);
      int deltaY = Math.abs(target.y - current.y);
      int dX = ( target.x < current.x ) ? 1 : -1;
      int dY = ( target.y < current.y ) ? 1 : -1;
      Point p = null;
      if ( deltaX > deltaY ) {
         if ( (p = testPoint(target.x+dX,target.y,board)) != null ) return p;
         if ( (p = testPoint(target.x-dX,target.y,board)) != null ) return p;
         if ( (p = testPoint(target.x,target.y+dY,board)) != null ) return p;
         if ( (p = testPoint(target.x,target.y-dY,board)) != null ) return p;
      }
      else {
         if ( (p = testPoint(target.x,target.y+dY,board)) != null ) return p;
         if ( (p = testPoint(target.x,target.y-dY,board)) != null ) return p;
         if ( (p = testPoint(target.x+dX,target.y,board)) != null ) return p;
         if ( (p = testPoint(target.x-dX,target.y,board)) != null ) return p;
      }
      return null;
   }


   //      from march 9, 2002
   private Point goodFindBestNeighbor (Point target, Point current, int[][] board){
      // List fBPoints = new ArrayList(4);
      fBPoints.clear();
      fBNaddPoint(target.x+1,target.y,board,fBPoints);
      fBNaddPoint(target.x-1,target.y,board,fBPoints);
      fBNaddPoint(target.x,target.y+1,board,fBPoints);
      fBNaddPoint(target.x,target.y-1,board,fBPoints);
      Point best = null;
      double bestD = 0;
      for (Iterator i = fBPoints.iterator();i.hasNext();){
         Point p = (Point) i.next();
         double dist = distance(current,p);
         if (best == null || dist < bestD ) {
            best = p;
            bestD = dist;
         }
      }
      return best;
   }
   
   private boolean followMove (int id, Point target, int inc, 
                               List moves, List destinations, int[][] board){
      id += inc;
      while ( id < locations.length &&
              id >= 0 && 
              distance(target,locations[id]) != 1 ){
         Point p = findBestNeighbor(target,locations[id],board);
         if ( p == null ) return false;
         moves.add(nodes[id]);
         destinations.add(p);
         board[locations[id].x][locations[id].y] = -1;
         board[p.x][p.y] = id;
         target = p;
         id += inc;
      }
      return true;
   }
   
   public Move followMove (int id, Point target) {
      List moves = new ArrayList();
      List destinations = new ArrayList();
      moves.add(nodes[id]);
      destinations.add(target);
      int[][] board = makeBoard();
      if ( board[target.x][target.y] != -1 )
         return null;
      board[locations[id].x][locations[id].y] = -1;
      board[target.x][target.y] = id;
      if (followMove(id,target,1,moves,destinations,board) &&
          followMove(id,target,-1,moves,destinations,board) ){
         int size = moves.size();
         Node[] moveArray = new Node[size];
         Point[] pointArray = new Point[size];
         for (int i = 0; i < size; i++){
            moveArray[i] = (Node) moves.get(i);
            pointArray[i] = (Point) destinations.get(i);
         }
         return (Move) new ProteinRepositionMove(this,moveArray,pointArray);
      }
      return null;
   }

   // for debuggin
   public boolean hasOverlap() {
      for (int i = 0; i < locations.length; i++)
         for (int j = i+1; j < locations.length; j++)
            if ( locations[i].x == locations[j].x &&
                 locations[i].y == locations[j].y )
               return true;
      return false;
   }

   private boolean canMoveAll (int deltaX, int deltaY){
      for (int i = 0; i < locations.length; i++)
         if ( !isLegal(locations[i].x+deltaX,locations[i].y+deltaY) ){
            System.out.println("illegal " + (locations[i].x+deltaX) + "," + (locations[i].y+deltaY) + " from " + locations[i]);
            return false;
         }
      return true;
   }
   public Move moveAll (int deltaX, int deltaY ){
      if ( !canMoveAll(deltaX,deltaY) ) {
         System.out.println("can't move all");
         return null;
      }
      int size = locations.length;
      Node[] moveArray = new Node[size];
      Point[] pointArray = new Point[size];
      for (int i = 0; i < size; i++){
         moveArray[i] = (Node) nodes[i];
         pointArray[i] = new Point(locations[i].x+deltaX,locations[i].y+deltaY);
      }
      return (Move) new ProteinRepositionMove(this,moveArray,pointArray);      
   }

   // carry moves

   private Point[] carryDeltas = null;

   private void computeCarryDeltas () {
      int size = locations.length;
      carryDeltas = new Point[size];
      carryDeltas[0] = new Point(0,0);
      for (int i = 1; i < size; i++)
         carryDeltas[i] = new Point(locations[i].x-locations[i-1].x,
                                    locations[i].y-locations[i-1].y);
   }
   
   private Move carryRight (int id, Point target, boolean flipRight, boolean flipLeft) {
      int x = target.x;
      int y = target.y;
      int value = board[x][y];
      if ( value >= 0 && value < id ) return null;
      int size = locations.length;
      int length = size - id;
      Node[] moveArray = new Node[length];
      Point[] pointArray = new Point[length];
      moveArray[0] = nodes[id];
      pointArray[0] = target;
      for (int i = 1; i < length; i++ ) {
         int index = id + i;
         if ( flipRight ) {
            x += carryDeltas[index].y * -1;
            y += carryDeltas[index].x;
         }
         else if ( flipLeft ) {
            x += carryDeltas[index].y ;
            y += carryDeltas[index].x * -1;
         }
         else {
            x += carryDeltas[index].x;
            y += carryDeltas[index].y;
         }
         if ( !isLegal(x,y) ) return null;
         value = board[x][y];
         if ( value >= 0 && value < id ) {
            return null;
         }
         moveArray[i] = nodes[index];
         pointArray[i] = new Point(x,y);
      }
      /*
      for (int i = 0; i < length; i++)
         System.out.println("@@ " + moveArray[i] + " " + locations[i] +
                            "-> " + pointArray[i]);
      */
      return (Move) new ProteinRepositionMove(this,moveArray,pointArray);
   }
      private Move carryLeft (int id, Point target, boolean flipRight, boolean flipLeft) {
      int x = target.x;
      int y = target.y;
      int value = board[x][y];
      if ( value > id ) return null;
      int size = locations.length;
      int length = id + 1;
      Node[] moveArray = new Node[length];
      Point[] pointArray = new Point[length];
      moveArray[0] = nodes[id];
      pointArray[0] = target;
      for (int i = 1; i < length; i++ ) {
         int index = id - i;
         int index1 = index + 1;
         if ( flipRight ) {
            x -= carryDeltas[index1].y * -1;
            y -= carryDeltas[index1].x;
         }
         else if ( flipLeft ) {
            x -= carryDeltas[index1].y ;
            y -= carryDeltas[index1].x * -1;
         }
         else {
            x -= carryDeltas[index1].x;
            y -= carryDeltas[index1].y;
         }
         if ( !isLegal(x,y) ) return null;
         value = board[x][y];
         if ( value > id ) {
            return null;
         }
         moveArray[i] = nodes[index];
         pointArray[i] = new Point(x,y);
      }
      /*
      for (int i = 0; i < length; i++)
         System.out.println("@@ " + moveArray[i] + " " + locations[i] +
                            "-> " + pointArray[i]);
      */
      return (Move) new ProteinRepositionMove(this,moveArray,pointArray);
   }
   

   public Move carryMove (int id, Point target, boolean flipRight, boolean flipLeft) {
      if ( carryDeltas == null ) computeCarryDeltas();
      if ( board == null ) getBoard();
      if ( id > (locations.length / 2) ) 
         return carryRight (id,target,flipRight,flipLeft);
      else 
         return carryLeft (id,target,flipRight,flipLeft);
   }

    public void precompute () {} 

   public static ProteinSolution read (String name) {
      BufferedReader reader = Utils.myOpenFile(name);
      if ( reader == null ) return null;
      String string = Utils.readLineIf(reader);
      int length = Utils.stringToInt(string);
      Point[] locations = new Point[length];
      for (int i = 0; i < length; i++) {
         string = Utils.readLineIf(reader);
         String[] point = Utils.stringToArray(string);
         locations[i] = new Point(Utils.stringToInt(point[0]),
                                  Utils.stringToInt(point[1]));
      }
      return new ProteinSolution(locations);
   }
   
   public static void write (ProteinSolution solution, String name) {
      List list = new ArrayList();
      list.add(""+solution.locations.length);
      for (int i = 0; i < solution.locations.length; i++)
         list.add("" + solution.locations[i].x + " " + solution.locations[i].y);
      Utils.makeFile(name,list);
   }
}





