package hugs.support;

import hugs.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.*; // for linux
import javax.swing.filechooser.*; // for linux
import javax.swing.border.*; // for linux

public abstract class NodeVisualization extends Visualization {

   public static Color[] colors = {Color.blue,Color.green,Color.pink,Color.red,
                                   Color.orange,Color.gray,Color.magenta};
   
   public static Color justChangedColor = Color.gray;
   public static Color selectColor = Color.red;

   public static BasicStroke oneStroke = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   public static BasicStroke twoStroke = new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   public static BasicStroke threeStroke = new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   public static BasicStroke fourStroke = new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   public static BasicStroke fiveStroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);

   public static BasicStroke selectStroke = threeStroke;

   /***********************************/
   /* must override these functions   */
   /***********************************/
   protected Node[] getNodes() { return null;}
   protected Point getNodeLocation (Node node) { return null;}
    protected int mouseX = 0, mouseY = 0; //current coordinates (gunnar)
   protected void  processDragNode(Node from, Node to) {};
   protected void  processDragNodeToBlank(Node from, Point to) {};
   protected boolean drawGrid () { return true;}
   // protected boolean squareCells () { return false;}

   protected void paintNode (Graphics g, Node n, Color mobilityColor, boolean marked) {};
   protected void paintSelected (Graphics g, Node n ){
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(selectStroke);
      Point pt = getNodeLocation(n);
      g.setColor(selectColor);
      g.drawRect(pt.x*xFactor,pt.y*yFactor,xFactor,yFactor);
      g2.setStroke(oneStroke);
   }
   protected void paintOtherBeforeNodes(Graphics g) {};
   protected void paintOtherAfterNodes(Graphics g) {};
   protected boolean hasChanged (Node n, Solution old, Solution s){
      return false;
   }
   private JPanel master;
   public JPanel getMasterPane () { return master; }
   protected JPanel getControlPane () { return null; }

   protected boolean useAspectRatio () { return false; }
   protected double aspectRatio () { return 0; }

    protected boolean useScrollBar () { return false; }
   /***********************************/

   private Node draggedNode = null;
   protected void paintDraggedNode (Graphics g, Node n, int x, int y) {
      g.setColor(Color.black);
      int half = nodeRadius / 2;
      if ( getNode(x,y) != null )
         g.fillOval(x-half,y-half,nodeRadius,nodeRadius);
      else
         g.drawOval(x-half,y-half,nodeRadius,nodeRadius);
   }

   //gunnar:
   /**********************************/
   /*        selection modes         */
   /**********************************/
   public static final int MODE_RECTANGLE = 0;
   public static final int MODE_BRUSH = 1;
   
   public static int selectionMode;
   private JComboBox selectionModes (){
      String[] selectionModes = new String[2];
      selectionModes[MODE_RECTANGLE] = "rectangle";
      selectionModes[MODE_BRUSH] = "brush";
      
      JComboBox selectionModeList = new JComboBox(selectionModes);
      selectionModeList.setSelectedIndex(MODE_RECTANGLE);
      selectionMode = MODE_RECTANGLE;
      selectionModeList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               JComboBox cb = (JComboBox)e.getSource();
               selectionMode = cb.getSelectedIndex();
            }  
         });
      return selectionModeList;
   }

   protected void paintJustChanged (Graphics g, Node n){
      g.setColor(justChangedColor);
      Point nodePoint = getNodeLocation(n);
      Point topLeft = getTopLeft(nodePoint.x,nodePoint.y);
      g.fillRect(topLeft.x,topLeft.y,xFactor,yFactor);
   }
   
   protected void updateJustChanged (Problem Problem, Solution old, Solution s){
      if ( !showChanges ) return;
      if ( old != null && s != null ) {
         // compute fruit loops
         Node[] nodes = problem.getNodes();
         for (int i = 0; i < nodes.length; i++){
            Node n = nodes[i];
            n.setJustMoved(hasChanged(n,old,s));
         }
      }
   }
         

   
   protected Hugs hugs = null;
   public void setHugs (Hugs hugs) {
      this.hugs = hugs;
   }

   protected boolean selected[];

   public boolean[] getSelected () { return selected;}
   public void clearSelected(){
      for (int i = 0; i < selected.length; i++)
         selected[i] = false;
   }

   public void clearJustChanged (){
      Node[] nodes = problem.getNodes();
      for (int i = 0; i < nodes.length; i++)
         nodes[i].setJustMoved(false);
      repaint();
   }

   protected Problem problem;
   protected Solution solution = null;

   public void setSolution (Solution solution) {
      if ( solution != null && this.solution != null ){
         updateJustChanged(problem,this.solution,solution);
      }
      this.solution = solution;
   }

   public void setProblem (Problem problem) {
      this.problem = problem;
      selected = new boolean[problem.getNodes().length];
      clearSelected();
   }
   
   public static  int MAX_NODE_SIZE = 20;
   protected int xSize, ySize;
   public int getXSize (){ return xSize;}
   public int getYSize (){ return ySize;}

    //gunnar: the coordinates of the selection rectangl
    protected int left = 0; 
    protected int right = 0; 
    protected int bottom = 0; 
    protected int top = 0;
    //gunnar. should we draw it?
    protected boolean drawRect = false;
    //gunnar: the brush
    protected int r = 0; 
    protected boolean drawBrush = false;

   public NodeVisualization( int x_max, int y_max) {
      setBackground(Color.white);
      addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
               mouseDown(ev);
            }
            public void mouseReleased(MouseEvent ev) {
               mouseUp(ev);
            }
            public void mouseMoved(MouseEvent ev) {
               mouseDrag(ev);
            }
         }
                       );
      addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent ev) {
               mouseDrag(ev);
            }
         }
                             );
      this.xSize = x_max;
      this.ySize = y_max;
      setNodeRadius();
      JPanel control = getControlPane();
      if ( control != null ) {
         master = new JFramedPanel();
         master.setLayout(new BorderLayout());
         this.setSize(xSize,ySize);
	 if ( useScrollBar() ) {
	     JScrollPane scroll = new JScrollPane(this);
	     master.add(scroll,"Center");
	 }
	 else
	     master.add(this,"Center");
         master.add(control,"North");

      }
      else master = this;
      
      //gunnar: add the selection modes in the control panel
      control.add(selectionModes());
   }
   
   
   protected Point getTopLeft(int x, int y){
      return new Point(x*xFactor,y*yFactor);
   }

   protected Point getCenter (Point p){
      return getCenter(p.x,p.y);
   }
   
   protected Point getCenter(int x, int y){
      return new Point(x*xFactor+(xFactor/2),
                       y*yFactor+(yFactor/2));
   }

   protected void setNodeRadius (){
       Rectangle bounds = getBounds();
      // System.out.println("NR: " + bounds  + "  x,y = " + xSize + "," + ySize);
      xFactor = bounds.width / xSize;
      yFactor = bounds.height / ySize;
      nodeRadius = Math.min(xFactor/2,yFactor/2);
      if ( nodeRadius > MAX_NODE_SIZE) nodeRadius = MAX_NODE_SIZE;
      // System.out.println("NodeRadius: " + xFactor + ", " + yFactor + " " + nodeRadius);
      if ( useAspectRatio() && xFactor > 0 && yFactor > 0){
         double ratio = (double) (xFactor * xSize ) / (double) (yFactor * ySize);
         double aR = aspectRatio();
         // System.out.println("@@ xF= " + xFactor + ",xS= " + xSize + ", yF="                            + yFactor + ",yS = " + ySize + ", ratio="                          + ratio + ", aR=" + aR);
         if ( ratio > aR ) {
            xFactor = (int) (aR * (double) ySize * (double) yFactor / (double) xSize);
            xOffset = (bounds.width - xSize*xFactor) / 2;
         }
         else if ( ratio < aR ) {
            yFactor = (int) (((double)(xSize * xFactor))
               / (double) (aR * (double) ySize));
            yOffset = (bounds.height - ySize*yFactor) / 2;
         }
      }
   }
   
   protected int nodeRadius;
   protected int xFactor;
   protected int yFactor;
   protected int xOffset;
   protected int yOffset;
   /*
   private void drawEdge(Graphics g,int x1, int y1, int x2, int y2){
      Point one = getCenter(x1,y1);
      Point two = getCenter(x2,y2);
      g.drawLine(one.x,one.y,two.x,two.y);
   }
   */
   
   protected Node mouseDownNode;
   protected Point mouseDownPoint;

   public void mouseDown(MouseEvent ev) {
      setNodeRadius(); // BUG: should be here, really.
      mouseX = ev.getX();//gets the number of clicks in short period (for double click, etc.)
      int clicks = ev.getClickCount();
      mouseY = ev.getY();
      Node selected = getNode(mouseX,mouseY);
      if (selected == null && selectionMode != MODE_RECTANGLE) 
	  drawBrush = true; // just looks better, i think (gunnar)
      mouseDownPoint = new Point(mouseX,mouseY);
      mouseDownNode = selected;
      // System.out.println("Mouse down: " + mouseDownPoint + ", " + mouseDownNode);

      repaint(); //gunnar. too expensive? but i like it when the brush gets drawn even before the drag, ie, after just one mouseclick. of course only if the click is in empty space.

      //this clears the selected nodes
      //however, if you double click on a node
      //the rest clear, but that node becomes selected
      //even though the clear is after the possible set
      //not sure why -- eba
      if (clicks == 2) 
	  clearSelected();
   }

   private void selectAll(int x1, int y1, int x2, int y2){
       if (x1 > x2) {
        int temp = x1;
        x1 = x2;
        x2 = temp;
        }
       if (y1 > y2){
        int temp = y1;
        y1 = y2;
        y2 = temp;
       }
       // Node[] nodes = problem.getNodes();
       Node[] nodes = getNodes();
       for (int i = nodes.length; i -->0;){
          // Point pt = solution.getLocation(nodes[i].getId());
          Point pt = getNodeLocation(nodes[i]);
	  if ( pt != null ) {
	      Point p = getCenter(pt.x,pt.y);
	      if (x1 <= p.x && p.x <= x2 &&
		  y1 <= p.y && p.y <= y2){
		  /// selected[i] = true;
		  setSelected(nodes[i].getId(),true);
	      }
          } 
       }
       repaint();
   }

   public Color getMobilityColor(Node n) {
      return n.getMobilityColor();
   }
   public void paintComponent( Graphics g ) {
      super.paintComponent(g);
      setNodeRadius();
      if ( drawGrid() ) {
         g.setColor(Color.gray);
         int width = xSize * xFactor;
         int height = ySize * yFactor;
         for (int x = 0; x <= xSize; x++)
            g.drawLine(x*xFactor,0,x*xFactor,height);
         for (int y = 0; y <= ySize; y++)
            g.drawLine(0,y*yFactor,width,y*yFactor);
      }
      paintOtherBeforeNodes(g);
      Node[] nodes = getNodes();
      boolean[] marked = Hugs.THIS.getMarked();
      for (int i = nodes.length; i-->0;){
         if ( nodes[i].getJustMoved() )
            paintJustChanged(g,nodes[i]);
         paintNode(g,nodes[i],getMobilityColor(nodes[i]),marked[i]);
	 if ( getSelected(nodes[i].getId()) )
            paintSelected(g,nodes[i]);
      }

      
      //gunnar: draw the selection rectangle or brush here if necessary.
      if (drawRect) {
	  g.setColor(Color.black);
	  
// 	  System.out.println("drawRect(" + left + ", " + top + 
// 			     ", " + (right - left) + ", " + 
// 			     (bottom - top) + ");");
	  
	  g.drawRect(left, top, right - left, bottom - top);
      }
      if (drawBrush) {
	  g.setColor(Color.black);
	  //gunnar. a funny way to draw circles in java. i wonder how other people are doing it.
	  g.drawArc(mouseX-r, mouseY-r, 2*r, 2*r, 0, 360);
      }

      paintOtherAfterNodes(g);
      if ( draggedNode != null ) 
         paintDraggedNode(getGraphics(),draggedNode,mouseX, mouseY);
   }

   
    // the following two functions are over-ridden in some applications
    public boolean getSelected (int id ) { return selected[id]; }
    public void setSelected(int id, boolean value) {
	selected[id] = value;
    }
   
   public void mouseUp (MouseEvent ev) {
      draggedNode = null;
      mouseX = ev.getX();
      mouseY = ev.getY();
      Node selectedNode = getNode(mouseX,mouseY);
      System.out.println("mouseUp: " + selectedNode);
      if ( mouseDownNode == null && mouseDownPoint != null ) {
	  if ( selectionMode == MODE_RECTANGLE ) {
	      selectAll(mouseX,mouseY,mouseDownPoint.x,mouseDownPoint.y);
	  }
	  //if (selectionMode == MODE_BRUSH) drawBrush = false;
      }
      else if ( mouseDownNode == selectedNode && selectedNode != null){
         int selectedId = selectedNode.getId();
	 setSelected(selectedId,!getSelected(selectedId));
	 //// selected[selectedId] = !selected[selectedId];
         System.out.println("set " + selectedId + " to " + selected[selectedId]);
      }
      else{
         // System.out.println("drag: " + mouseDownNode + " " + selectedNode " + getPoint(x,y));
         processDragNode(mouseDownNode,selectedNode);
         if ( mouseDownNode != null && selectedNode == null )
	     processDragNodeToBlank(mouseDownNode,getPoint(mouseX,mouseY));
      }
      mouseDownNode = null;
      mouseDownPoint = null;
      drawRect = drawBrush = false; // don't draw the selection stuff anymore 
      repaint();
   }
   
   public void mouseDrag(MouseEvent ev) {
      mouseX = ev.getX();
      mouseY = ev.getY();

      if ( mouseDownNode != null){
         draggedNode = mouseDownNode;
         repaint();
      }
      if ( mouseDownPoint != null && mouseDownNode == null){
         
         switch ( selectionMode ) {
             case MODE_RECTANGLE: 
		 
		 //gunnar: rectangle was only displayed when openend to the bottom and right
		 //fix:
		 drawRect = true;
		 left = Math.min(mouseDownPoint.x, mouseX);
		 right = Math.max(mouseDownPoint.x, mouseX);
		 top = Math.min(mouseDownPoint.y, mouseY);
		 bottom = Math.max(mouseDownPoint.y, mouseY);
		 
		 // System.out.println("left: " + left + ", right: " + right + ", bottom: " + bottom +  ", top: " + top);   
		 
		 
		 //g.drawRect(left, top, right - left, bottom - top);
		 repaint();
		 break;
	 case MODE_BRUSH:
	     drawBrush = true;

	     Node[] nodes = getNodes();
	     for (int i = nodes.length; i-- > 0;) {
		 Point pt = getNodeLocation(nodes[i]);
		 Point p = getCenter(pt.x,pt.y);
		 r = 3 * nodeRadius; 
		 //does the node lie in the brush radius?
		 //first impl.: L1-metric
// 		 if (x - r <= p.x && p.x <= x + r &&
// 		     y - r <= p.y && p.y <= y + r)
// 		     //selected[i] = !selected[i];
// 		     //better: switch on
// 		     selected[i] = true;
		 //second impl.: L2-metric
		 final int dx = mouseX - p.x;
		 final int dy = mouseY - p.y;
		 final double distance = Math.sqrt(dx*dx + dy*dy);
		 /// if (distance <= r) selected[i] = true;
		 if (distance <= r) setSelected(nodes[i].getId(),true);
	     }
	     repaint();
	     
	     break;
         }
         
	 
      }
   }

   protected Point getPoint (int x, int y){
      return new Point (x / xFactor, y / yFactor );
   }
   
   protected Node getNode (int x, int y){
      Node[] nodes = getNodes();
      if ( nodes == null ) return null;
      int xPos = x / xFactor;
      int yPos = y / yFactor;
      for (int i = nodes.length; i -->0;){
         Point pt = getNodeLocation(nodes[i]);
	 if ( pt == null ) return null;
         if ( pt.x == xPos && pt.y == yPos) return nodes[i];
      }
      return null;
   }

}
