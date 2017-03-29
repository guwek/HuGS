package hugs.apps.crossing;

import hugs.*;
import hugs.utils.*;
import hugs.search.*;
import hugs.support.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.*; // for linux
import javax.swing.filechooser.*; // for linux
import javax.swing.border.*; // for linux

public class CrossingVisualization extends NodeVisualization {

   public static boolean set_loners_low = false;
   public static boolean aviSwitch = true;
   public static Color DEFAULT_COLOR = Color.blue;
   public static double MIN_NODE_FACTOR = 0.4;
   public static double MAX_NODE_FACTOR = 1.0;
   public static boolean varyNodeSize = true;
   {
      if ( aviSwitch && false)
         NodeVisualization.MAX_NODE_SIZE = 40;
      else
         NodeVisualization.MAX_NODE_SIZE = 35;
   }
   
   /***********************************/
   /* overrides for NodeVisualization */
   /***********************************/
   protected Node[] getNodes() {
      return problem.getNodes();
   }
   protected Point getNodeLocation (Node node) {
      return crossingSolution.getLocation(node.getId());
   }
   protected void  processDragNode(Node from, Node to) {
      if ( moveSelected() && anySelected() )
         moveSelected(from,to);
      else if ( getInsert() )
         insert(from,to);
      else swap(to,from);
   }
   protected boolean drawGrid () { return false;}

   protected void paintSelected (Graphics g, Node n ){
      // is handled below
   }
   
   /***********************************/

   /*
   protected Node getNode (int x, int y){
      Node[] nodes = getNodes();
      int xPos = x / xFactor;
      int yPos = y / yFactor;
      for (int i = nodes.length; i -->0;){
         Point pt = getNodeLocation(nodes[i]);
         if ( pt.x == xPos && pt.y == yPos) {
            Point p = getCenter(xPos,yPos);
            double dist = Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
            if ( dist < nodeRadius ) return nodes[i];
            return null;
         }
      }
      return null;
   }
   */
   
   private CrossingSolution crossingSolution = null;
   private CrossingProblem crossingProblem = null;

   
   public void setSolution (Solution solution){
      super.setSolution(solution);
      this.crossingSolution = (CrossingSolution) solution;
      /*
      if ( solution != null ){
	   CrossingSolution tSolution = (CrossingSolution) solution;
           updateFruitLoops(crossingProblem,this.crossingSolution,tSolution);
	   this.crossingSolution = tSolution;
       }
      */
   }

   public void setProblem (Problem problem ){
      super.setProblem(problem);
      crossingProblem = (CrossingProblem) problem;
      System.out.println("CrossingVisualizaiton: setting problem ith size " + crossingProblem.getXSize() + "," + crossingProblem.getYSize() + ", numNodes = " + crossingProblem.getNodes().length);
      if ( crossingProblem.getXSize() > 14 ){
	  System.out.println("*******  setting AAAI_C_MODE *******");
	  TabuThread.AAAI_C_MODE = true;
      }
      this.xSize = crossingProblem.getXSize();
      this.ySize = crossingProblem.getYSize();
      int size = crossingProblem.size();
      Mobilities mobilities = Hugs.THIS.getMobilities();
      if ( set_loners_low )
         for (int i = 0; i < size; i++)
            if ( crossingProblem.getNumEdgesForNode(i) == 0 )
               mobilities.setMobility(i,Mobilities.LOW);
      
      /*
      this.problem = (CrossingProblem) problem;
      selected = new boolean[CrossingProblem.NUM_NODES];
      clearSelected();
      */
   }

   
 public static Color[] colors = {Color.blue,Color.green,Color.pink,Color.red,
                                 Color.orange,Color.gray,Color.magenta};

   private static int fruitLoopSize = 3;
   protected static final Color TEXT_1          = Color.black; //time zones

   protected JCheckBox boxInsert;//  = new JCheckBox("Insert");
   protected JCheckBox boxMobility; // = new JCheckBox("Show priorities");
   protected JCheckBox boxMoveSelected; // = new JCheckBox("Show priorities");
   protected JButton buttonClear;
   
   private boolean getInsert (){return boxInsert.isSelected();}
   private boolean showMobilities (){return boxMobility.isSelected();}
   private boolean moveSelected (){return boxMoveSelected.isSelected();}

   JPanel controlPane;
   protected JPanel getControlPane () {
      boxInsert = new JCheckBox("Insert",true);
      boxMobility = new JCheckBox("Show mobilities",true);
      boxMoveSelected = new JCheckBox("Move selected");
      buttonClear = new JButton("Clear");
      buttonClear.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            clearJustChanged();
         }
      });

      if ( controlPane == null ) {
         controlPane = new JFramedPanel();
         controlPane.add(buttonClear);
         controlPane.add(boxInsert);
         controlPane.add(boxMobility);
         controlPane.add(boxMoveSelected);
      }
      return controlPane;
   }
   
   public CrossingVisualization( int xSize, int ySize ) {
      super(xSize,ySize);
      boxMobility.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) { repaint(); }
      });
      
      // add(controlPane);
      setNodeRadius();
   }

   protected void paintJustChanged (Graphics g, Node node ) {
      if ( aviSwitch && false) return;
      Point nodePoint = getNodeLocation(node);
      Point pt = getCenter(nodePoint.x,nodePoint.y);
      int cx = pt.x;
      int cy = pt.y;
      g.setColor(NodeVisualization.justChangedColor);
      g.fillOval(cx-(nodeRadius/2)-fruitLoopSize, cy-(nodeRadius/2)-fruitLoopSize, nodeRadius+(2*fruitLoopSize), nodeRadius+(2*fruitLoopSize));
   }
   
   protected void paintOtherBeforeNodes(Graphics g) {
      if ( aviSwitch && false) {
         Node node = getNodes()[1];
         Point nodePoint = getNodeLocation(node);
         Point pt = getCenter(nodePoint.x,nodePoint.y);
         int cy = pt.y;
         g.setColor(Color.black);
         g.drawLine(0,cy,2300,cy);
      }
   }



   protected void paintNode (Graphics g, Node node, Color mobilityColor, boolean marked) {
      paintNode (g, node, mobilityColor, marked, 1.0);
   }

   private double testCount = 0;
   protected void paintNode (Graphics g, Node node, Color mobilityColor, boolean marked, double fraction) {
      Point nodePoint = getNodeLocation(node);
      Point pt = getCenter(nodePoint.x,nodePoint.y);
      int cx = pt.x;
      int cy = pt.y;
      int radius = (int) ((double) nodeRadius * fraction * nodeFactors[node.getId()]);
      if ( hugs != null ) {
         Color color = showMobilities() ?  mobilityColor : DEFAULT_COLOR;
         float[] vals = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null);
         // System.out.println(vals[0]);
         double v = nodeFactors[node.getId()];
         Color newColor = Color.getHSBColor(vals[0],(float)(v*vals[1]),vals[2]);
         if ( aviSwitch )
            newColor = color;
         g.setColor(newColor);
         if ( marked ) 
            g.fillRect(cx-(radius/2), cy-(radius/2), radius, radius);
         else {
            g.fillOval(cx-(radius/2), cy-(radius/2), radius, radius);
            if ( aviSwitch && fraction >= 1 ) {
               g.setColor(Color.black);
               g.drawOval(cx-(radius/2), cy-(radius/2), radius, radius);
               g.setColor(newColor);
            }


         }
         g.setColor(Color.black);
         if ( aviSwitch && false){
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String text = "" + node.getId();
            g.drawString(text,cx-3,cy+3);
         }
      }
      /* shows funny shapes
      else{
          int half = radius/2;
          int third = radius/3;
          int numShapes = 6;

          // NOTE: number of colors and shapes must be different and neither divisble
          // by the other
          int id = node.getId();
          int colorType = id % colors.length;
          int shapeType = id % numShapes;
          g.setColor(colors[colorType]);
          
          if (shapeType == 0){ // triangle
             int[] xP = {cx-half,cx,cx+half};
             int[] yP = {cy+half,cy-half,cy+half};
             Polygon polygon = new Polygon(xP,yP,3);
             g.fillPolygon(polygon);
          } //upside down triangle
          else if (shapeType == 1){
             int[] xP = {cx-half,cx,cx+half};
             int[] yP = {cy-half,cy+half,cy-half};
             Polygon polygon = new Polygon(xP,yP,3);
             g.fillPolygon(polygon);
          }
          else if (shapeType == 2){
             g.fillOval(cx-half, cy-half, radius, radius);
          }
          else if (shapeType == 3){
             g.fillRect(cx-half, cy-half, radius, radius);
          }
          else if (shapeType == 4){
             int[] xP = {cx-half,cx,cx+half,cx};
             int[] yP = {cy,cy+half,cy,cy-half};
             Polygon polygon = new Polygon(xP,yP,4);
             g.fillPolygon(polygon);
             
          }
          else if (shapeType == 5){
             int[] xP = {cx-half,cx+half,cx-half,cx+half};
             int[] yP = {cy-half,cy-half,cy+half,cy+half};
             Polygon polygon = new Polygon(xP,yP,4);
             
             g.fillPolygon(polygon);
             
          }
          }
      */

      if (selected[node.getId()] && fraction >= 1){
         g.setColor(Color.red);
         int margin = 2;
         g.drawRect(cx-(radius/2)-margin, cy-(radius/2)-margin, radius+2*margin, radius+2*margin);
      }

   }

   
   /*
   private void PaintCell( Graphics g, Reccrossing b, Node node) {
      Point pt = crossingSolution.getLocation(node.getId());
      int x_total = b.width - 1;
      int y_total = b.height - 1;
      int left, right, top, bottom, width, height;
      
      left = pt.x * x_total / xSize;
      right = (pt.x+1) * x_total / xSize;
      width = right - left;
      top = pt.y * y_total / ySize;
      bottom = (pt.y+1) * y_total / ySize;
      height = bottom - top;
      int cx = left + (width / 2);
      int cy = top + (height /2);

      if ( node.getJustMoved() ){
         g.setColor(fruitLoopColor);
         g.fillOval(cx-(nodeRadius/2)-fruitLoopSize, cy-(nodeRadius/2)-fruitLoopSize, nodeRadius+(2*fruitLoopSize), nodeRadius+(2*fruitLoopSize));
      }

      if ( hugs != null && showMobilities() ){
         g.setColor(node.getMobilityColor());
         g.fillOval(cx-(nodeRadius/2), cy-(nodeRadius/2), nodeRadius, nodeRadius);
      }
      else{
          int half = nodeRadius/2;
          int third = nodeRadius/3;
          
          int numShapes = 6;

          // NOTE: number of colors and shapes must be different and neither divisble
          // by the other
          int id = node.getId();
          int colorType = id % colors.length;
          int shapeType = id % numShapes;
          g.setColor(colors[colorType]);
          
          if (shapeType == 0){ // triangle
             int[] xP = {cx-half,cx,cx+half};
             int[] yP = {cy+half,cy-half,cy+half};
             Polygon polygon = new Polygon(xP,yP,3);
             g.fillPolygon(polygon);
          } //upside down triangle
          else if (shapeType == 1){
             int[] xP = {cx-half,cx,cx+half};
             int[] yP = {cy-half,cy+half,cy-half};
             Polygon polygon = new Polygon(xP,yP,3);
             g.fillPolygon(polygon);
          }
          else if (shapeType == 2){
             g.fillOval(cx-half, cy-half, nodeRadius, nodeRadius);
          }
          else if (shapeType == 3){
             g.fillRect(cx-half, cy-half, nodeRadius, nodeRadius);
          }
          else if (shapeType == 4){
             int[] xP = {cx-half,cx,cx+half,cx};
             int[] yP = {cy,cy+half,cy,cy-half};
             Polygon polygon = new Polygon(xP,yP,4);
             g.fillPolygon(polygon);
             
          }
          else if (shapeType == 5){
             int[] xP = {cx-half,cx+half,cx-half,cx+half};
             int[] yP = {cy-half,cy-half,cy+half,cy+half};
             Polygon polygon = new Polygon(xP,yP,4);
             
             g.fillPolygon(polygon);
             
          }
      }

      if (selected[node.getId()]){
         g.setColor(Color.red);
         int margin = 2;
         g.drawRect(cx-(nodeRadius/2)-margin, cy-(nodeRadius/2)-margin, nodeRadius+2*margin, nodeRadius+2*margin);
      }

   }
   */

   private void drawEdge(Graphics g, int x1, int y1, int x2, int y2){
      if ( aviSwitch && false ) return;
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(twoStroke);
      Point one = getCenter(x1,y1);
      Point two = getCenter(x2,y2);
      g.drawLine(one.x,one.y,two.x,two.y);
      g2.setStroke(oneStroke);
   }


   protected void paintOtherAfterNodes(Graphics g) {
      if ( crossingProblem == null || crossingSolution == null ) return;
      Node[] nodes = crossingProblem.getNodes();
      if (Hugs.ready()){
         Vector[] sourceEdges = crossingProblem.getSourceEdges();
         int numSourceEdges = sourceEdges.length;
         for (int fromNum = numSourceEdges; fromNum-->0;){
            for (int toNum = sourceEdges[fromNum].size(); toNum-->0;){
               Point from,to;
               Edge edge = (Edge) (sourceEdges[fromNum].elementAt(toNum));
               from = crossingSolution.getLocation(nodes[fromNum]);
               to = crossingSolution.getLocation(edge.to.getId());
               int delta = edge.getDelta();
               if (delta > 0)
                  g.setColor(Color.red);
               else  if (delta < 0)
                  g.setColor(Color.black);
               else  g.setColor(Color.blue);
               int x1, x2, y1, y2;
               drawEdge(g,from.x, from.y,to.x,to.y);
            }
         }
      }
   }


   /*
   public void paintComponent( Graphics g ) {
      if ( crossingProblem == null || crossingSolution == null ) return;
      Node[] nodes = crossingProblem.getNodes();
      super.paintComponent(g); //paint background
      if (Hugs.ready()){
         Vector[] sourceEdges = crossingProblem.getSourceEdges();
         int numSourceEdges = sourceEdges.length;
         for (int fromNum = numSourceEdges; fromNum-->0;){
            for (int toNum = sourceEdges[fromNum].size(); toNum-->0;){
               Point from,to;
               Edge edge = (Edge) (sourceEdges[fromNum].elementAt(toNum));
               from = crossingSolution.getLocation(nodes[fromNum]);
               to = crossingSolution.getLocation(edge.to.getId());
               int delta = edge.getDelta();
               if (delta > 0)
                  g.setColor(Color.red);
               else  if (delta < 0)
                  g.setColor(Color.black);
               else  g.setColor(Color.blue);
               int x1, x2, y1, y2;
               drawEdge(g,from.x, from.y,to.x,to.y);
            }
         }
      }
   }
   */

   private double[] nodeFactors = new double[0];
   private void computeNodeFactors () {
      crossingSolution.computeScore();
      Node[] nodes = getNodes();
      if ( nodes.length != nodeFactors.length )
         nodeFactors = new double[nodes.length];
      double max = 0;
      if ( !varyNodeSize) {
         for (int i = nodes.length; i-->0;) 
            nodeFactors[i] = 1.0;
         return;
      }
      for (int i = nodes.length; i-->0;) {
         int crosses = crossingSolution.getCrosses(i);
         nodeFactors[i] = crosses;
         if ( crosses > max ) max = crosses;
      }
      double delta = MAX_NODE_FACTOR - MIN_NODE_FACTOR;
      for (int i = nodes.length; i-->0;) 
         nodeFactors[i]  = MIN_NODE_FACTOR + (delta *  nodeFactors[i]/max);
   }

   protected Node getNode (int x, int y) {
      Node n = super.getNode(x,y);
      if ( n == null ) return null;
      Point nodePoint = getNodeLocation(n);
      Point pt = getCenter(nodePoint.x,nodePoint.y);
      double radius = ((double) nodeRadius * nodeFactors[n.getId()]);
      double dist = Utils.distance(pt,new Point(x,y));
      if ( dist > radius ) return null;
      return n;
   }
   
   public void paintComponent( Graphics g ) {

      if ( crossingProblem == null || crossingSolution == null ) return;
      computeNodeFactors();
      super.paintComponent(g);
      Node[] nodes = getNodes();
      if ( nodes == null ) return;
      boolean[] marked = Hugs.THIS.getMarked();
      for (int i = nodes.length; i-->0;)
         paintNode(g,nodes[i],getMobilityColor(nodes[i]),marked[i],.5);
   }

   private boolean insert (Node n1, Node n2){
      if ( n1 == null || n2 == null ) return false;
      Point p1 = crossingSolution.getLocation(n1.getId());
      Point p2 = crossingSolution.getLocation(n2.getId());
      if ( p1.y != p2.y ) return false;
      int length = Math.abs(p1.x - p2.x);
      Node[] froms = new Node[length+1];
      Point[] tos = new Point[length+1];
      CrossingSolution current = crossingSolution;
      int d1,d2;
      if (p1.x < p2.x)
         for (int i = 0; i < length; i++){
            int n = i + p1.x;
            froms[i] = current.getNode(n+1,p1.y);
            tos[i] = new Point(n,p1.y);
         }
      else
         for (int i = 0; i < length; i++){
            int n = i + p2.x;
            froms[i] = current.getNode(n,p1.y);
            tos[i] = new Point(n+1,p1.y);
         }
      froms[length] = n1;
      tos[length] = crossingSolution.getLocation(n2.getId());
      Move m = new CrossingMove(froms,tos);
      hugs.manualMove(m);
      return true;
   }

   protected void paintDraggedNode (Graphics g, Node node, int xLoc, int y) {
      if ( moveSelected() && anySelected() ) {
         Point nodePoint = getNodeLocation(node);
         Point pt = getCenter(nodePoint.x,nodePoint.y);
         int delta = xLoc - pt.x;
         Node[] nodes = getNodes();
         for (int i = nodes.length; i-->0;){
            Node n = nodes[i];
            if ( selected[n.getId()] ){
               Point nPoint = getNodeLocation(n);
               Point p = getCenter(nPoint.x,nPoint.y);
               super.paintDraggedNode(g,n,p.x+delta,p.y);
            }
         }
      }
      else super.paintDraggedNode(g,node,xLoc,y);
   }
   private boolean moveSelected (Node n1, Node n2){
      if ( n1 == null || n2 == null ) return false;
      Point p1 = crossingSolution.getLocation(n1.getId());
      Point p2 = crossingSolution.getLocation(n2.getId());
      if ( p1.y != p2.y ) return false;
      // make lists
      java.util.List[] board = new ArrayList[ySize];
      for (int y = 0; y < ySize; y++){
         board[y] = new ArrayList();
         for (int x = 0; x < xSize; x++)
            board[y].add(crossingSolution.getNode(x,y));
      }
      int delta = p2.x - p1.x;
      if ( delta > 0 ){
         for (int y = 0; y < ySize; y++) {
            int maxX = xSize-1;
            for (int x = xSize; x-->0;){
               Node n = crossingSolution.getNode(x,y);
               if ( selected[n.getId()] ){
                  int newPos = x + delta;
                  if ( newPos > maxX ) {
                     newPos = maxX;
                     maxX--;
                  }
                  board[y].remove(n);
                  board[y].add(newPos,n);
               }
            }
         }
      }
      else { // ( delta < 0 ){
         delta = -1 * delta;
         for (int y = 0; y < ySize; y++) {
            int minX = 0;
            for (int x = 0; x < xSize;x++){
               Node n = crossingSolution.getNode(x,y);
               if ( selected[n.getId()] ){
                  int newPos = x - delta;
                  if ( newPos <= minX ) {
                     newPos = minX;
                     minX++;
                  }
                  board[y].remove(n);
                  board[y].add(newPos,n);
               }
            }
         }
      }
      ArrayList froms = new ArrayList();
      ArrayList tos = new ArrayList();
      for (int y = 0; y < ySize; y++) 
         for (int x = 0; x < xSize; x++){
            Node n = (Node) board[y].get(x);
            Point l = crossingSolution.getLocation(n);
            if ( l.x != x ) {
               froms.add(n);
               tos.add(new Point(x,y));
               // System.out.println("moving " + n + " to " + x + "," + y);
            }
         }
      
      Move m = new CrossingMove(froms,tos);
      hugs.manualMove(m);
      /*
      for (int y = 0; y < ySize; y++){
         for (int x = 0; x < xSize; x++)
            System.out.print(board[y].get(x) + " ");
         System.out.println();
      }
      */
      
      return true;
   }


   private boolean swap(Node n1, Node n2){
      // if ( n1.getLocation().y != n2.getLocation().y )
      if ( crossingSolution.getLocation(n1.getId()).y != crossingSolution.getLocation(n2.getId()).y )
         return false;
      Node[] froms = {n1,n2};
      Point[] tos = {crossingSolution.getLocation(n2.getId()),crossingSolution.getLocation(n1.getId())};
      Move m = new CrossingMove(froms,tos);
      hugs.manualMove(m); 
      return true;
   }


   
   // don't use addElement(s) because want to update
   // fruit loops
   protected void updateJustChanged (Problem problem, Solution oldS, Solution newS){
      CrossingProblem crossingProblem = (CrossingProblem) problem;
         CrossingSolution old = (CrossingSolution) oldS;
         CrossingSolution s = (CrossingSolution) newS;
         if (old != null && s != null){
            // compute fruit loops
            Node[] nodes = crossingProblem.getNodes();
            for (int i = 0; i < nodes.length; i++){
               Node n = nodes[i];
               boolean b = old.getLocation(n).equals(s.getLocation(n));
               n.setJustMoved(!b);
            }
            // compute edge changes (might be slow)
            Vector edges = crossingProblem.getEdges();
            int size = edges.size();
            int[] edgeCut = new int[size];
            Edge.computeAllCrosses(crossingProblem,old);
            for (int i = size; i-->0;)
               edgeCut[i] = ((Edge)edges.elementAt(i)).getNumCrossed();
            Edge.computeAllCrosses(crossingProblem,s);
            for (int i = size; i-->0;){
               Edge e = ((Edge)edges.elementAt(i));
               e.setDelta(e.getNumCrossed() - edgeCut[i]);     
            }
         }
      }

   private boolean anySelected () {
      for (int i = selected.length; i-->0;)
         if ( selected[i] ) return true;
      return false;
   }
                               
}
