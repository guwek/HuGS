package hugs.apps.delivery;

import hugs.*;
import hugs.support.*;
import hugs.utils.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;  
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;


public class DeliveryVisualization extends NodeVisualization {

   public static boolean showMoveTypes = false;
   public static double aspectRatio =  2.0;
   public static int X_MARGIN = 3;
   public static int Y_MARGIN = 2;

   
   {
      NodeVisualization.selectStroke = NodeVisualization.twoStroke;
      NodeVisualization.selectColor = new Color( 255, 0, 255); // purple
   }   

   
   /***********************************/
   /* overrides for NodeVisualization */
   /***********************************/
   protected Node[] getNodes() {
      if ( deliveryProblem != null ) return deliveryProblem.getNodes();
      return new Node[0];
   }
   protected Point getNodeLocation (Node n) {
      DeliveryNode node = (DeliveryNode) n;
      return new Point (node.x, node.y);
   }
   protected void  processDragNode(Node from, Node to) {
      System.out.println("processDragNode : " + from + " to " + to);
      if ( from != null && to != null &&
           deliverySolution.onRoute(to.getId()) ) {
         int position = deliverySolution.getPosition(to.getId());
         if ( deliverySolution.onRoute(from.getId()) ) {
            int fromPosition = deliverySolution.getPosition(from.getId());
            // System.out.println("reinsert " + from.getId() + " " + position);
            hugs.manualMove(new DeliveryReinsertMove(deliverySolution,fromPosition,position));
         }
         else 
            hugs.manualMove(new DeliveryInsertMove(deliverySolution,from.getId(),position));
      }
      if ( from != null &&
           deliverySolution.onRoute(from.getId()) && to == null ){
         int position = deliverySolution.getPosition(from.getId());
         if ( position >= 0 ){
            Move m = new DeliveryRemoveMove(deliverySolution,position);
            hugs.manualMove(m);
         }
      }
   }

   protected boolean drawGrid () { return false; }

   JPanel controlPane;
   protected JPanel getControlPane () {
      setupButtons();
      if ( controlPane == null ) {
         controlPane = new JFramedPanel();
         controlPane.add(buttonClear);
         controlPane.add(buttonInsert);
         controlPane.add(buttonRemove);
         controlPane.add(buttonSelect);
         controlPane.add(buttonUnselect);
         if ( showMoveTypes ) {
            controlPane.add(boxInsert);
            controlPane.add(boxRemove);
            controlPane.add(boxSwap);
            controlPane.add(boxSplice);
         }
      }
      return controlPane;
   }

   protected boolean useAspectRatio () { return true; }
   public double aspectRatio () { return aspectRatio; }

   /***********************************/

   protected JCheckBox boxRemove;
   protected JCheckBox boxInsert;
   protected JCheckBox boxSwap;
   protected JCheckBox boxSplice;

   protected JButton buttonRemove;
   protected JButton buttonInsert;
   protected JButton buttonSelect;
   protected JButton buttonUnselect;
   protected JButton buttonClear;
   
   
   public boolean doRemoves() { return boxRemove.isSelected();}
   public boolean doInserts() { return boxInsert.isSelected();}
   public boolean doSwaps() { return boxSwap.isSelected();}
   public boolean doSplices() { return boxSplice.isSelected();}


   protected void getNeighbors (int pos, DeliverySolution s, java.util.List l){
      l.clear();
      if ( pos > 0 ) l.add(s.getNode(pos-1));
      if ( pos < (s.size()-1) ) l.add(s.getNode(pos+1));
   }

   // change if _both_ neighbors change
   protected boolean neighborsChange (java.util.List one,
                                     java.util.List two ){ // max size is 2
      if ( one.size() != two.size() ) return true;
      int size = one.size();
      if ( size == 0 ) return false;
      if ( size == 1 ) return one.get(0) != two.get(0);
      return !( (one.get(0) == two.get(0) || one.get(1) == two.get(1) ) ||
               (one.get(0) == two.get(1) || one.get(1) == two.get(0) ) );
   }
   
   protected java.util.List oldNeighbors = new ArrayList();
   protected java.util.List newNeighbors = new ArrayList();
   
   protected boolean hasChanged (Node n, Solution old, Solution s){
      DeliverySolution oldS = (DeliverySolution) old;
      int oldPos = oldS.getPosition(n.getId());
      DeliverySolution newS = (DeliverySolution) s;
      int newPos = newS.getPosition(n.getId());
      if ( (oldPos == -1) != (newPos == -1) )
         return true;
      if ( oldPos == -1 ) return false;
      getNeighbors(oldPos,oldS,oldNeighbors);
      getNeighbors(newPos,newS,newNeighbors);
      if ( neighborsChange(oldNeighbors,newNeighbors) ) return true;
                                                          
      /*
      // check before
      if ( !(oldPos == 0 && newPos == 0) ){
         if ( oldPos == 0 || newPos == 0 ) return true;
         if ( oldS.getId(oldPos-1) != newS.getId(newPos-1) ) return true;
      }
      int oldSize = oldS.size()-1;
      int newSize = newS.size()-1;
      if ( !(oldPos == oldSize  && newPos == newSize) ){
         if (oldPos == oldSize || newPos == newSize) return true;
         if ( oldS.getId(oldPos+1) != newS.getId(newPos+1) ) return true;
      }
      */
      return false;
   }
   
   private static int WIDTH = 10;
   private static int HEIGHT = 10;
   
   private int xSize = 700;
   private int ySize = 250;
   
   public DeliveryVisualization(int xSize, int ySize){
      super(xSize,ySize);
      this.xSize = xSize;
      this.ySize = ySize;
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(xSize+50, ySize+50));
      // setMaximumSize(new Dimension(xSize+50, ySize+50));
   }

   private DeliveryProblem deliveryProblem = null;
   public void setProblem (Problem problem) {
      super.setProblem(problem);
      this.deliveryProblem = (DeliveryProblem) problem;
   }

   /*
   private DeliveryMobilities mobilities = null;
   public void setMobilities (DeliveryMobilities mobilities) {
      this.mobilities = mobilities;
   }
   */
   

   private DeliverySolution deliverySolution = null;
   public void setSolution (Solution solution) {
      super.setSolution(solution);
      this.deliverySolution = (DeliverySolution) solution;
   }


   protected void paintNode (Graphics g, Node n, Color mobilityColor, boolean marked) {
      DeliveryNode node = (DeliveryNode) n;
      if ( DeliveryProblem.TIME_WINDOWS ){
         g.setColor(Color.black);
         Point topLeft = getTopLeft(node.x,node.y);
         int yCenter = topLeft.y + yFactor/2;
         g.drawLine(topLeft.x, yCenter,topLeft.x+xFactor,yCenter);
         g.drawLine(topLeft.x, topLeft.y, topLeft.x,topLeft.y+yFactor);
         g.drawLine(topLeft.x+xFactor, topLeft.y,
                    topLeft.x+xFactor,topLeft.y+yFactor);
         double startX = topLeft.x + node.getStart()*xFactor;
         double widthX = (node.getEnd() -node.getStart()) * xFactor;
         if ( node == deliveryProblem.getDepot() )
            g.setColor(Color.black);
         else g.setColor(mobilityColor);
         g.fillRect((int)startX,topLeft.y,(int)widthX,yFactor);
         g.setColor(Color.black);
         g.drawRect((int)startX,topLeft.y,(int)widthX,yFactor);
      }
      else {
         Point center = getCenter(node.x,node.y);
         if ( node == deliveryProblem.getDepot() ){
            g.setColor(Color.black);
            int size = Math.min(xFactor,yFactor);
            g.fillRect(center.x-(size/2),center.y-(size/2),size,size);
         }
         else {
            g.setColor(mobilityColor);
            int width = (int) ((double)(xFactor-X_MARGIN) * ((double)node.getOrders() / (double)DeliveryProblem.MAX_ORDER ));
            int height = yFactor - Y_MARGIN;
            if ( marked ) {
               g.fillRect(center.x-(width/2),center.y-(height/2),width,height);
               g.setColor(Color.black);
               g.drawRect(center.x-(width/2),center.y-(height/2),width,height);
            }
            else {
               g.fillOval(center.x-(width/2),center.y-(height/2),width,height);
               g.setColor(Color.black);
               g.drawOval(center.x-(width/2),center.y-(height/2),width,height);
            }

         }
      }
   }

   
   BasicStroke thickStroke = new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   BasicStroke thinStroke = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);

   protected void paintOtherBeforeNodes (Graphics g){
      if ( !DeliveryProblem.TIME_WINDOWS ){
         g.setColor(Color.black);
         if ( deliverySolution != null ) {
            int size = deliverySolution.size();
            // DeliveryNode from = deliverySolution.getNode(0);
            DeliveryNode from = deliveryProblem.getDepot();
            for (int i = 0; i <= size; i++) {
               DeliveryNode to = ( i < size ) ? deliverySolution.getNode(i) : deliveryProblem.getDepot();
               Point fromPoint = getCenter(from.x,from.y);
               Point toPoint = getCenter(to.x,to.y);
               Graphics2D g2 = (Graphics2D) g;
               // g2.setColor(Color.green);
               g2.setStroke(thickStroke);

               // System.out.println("drawing line from " + from + " to " + to);
               g2.drawLine(fromPoint.x,fromPoint.y,toPoint.x,toPoint.y);
               g2.setStroke(thinStroke);
               from = to;
         }
      }
      }
   }
   
   protected void paintOtherAfterNodes (Graphics g){
      if ( DeliveryProblem.TIME_WINDOWS ){
         g.setColor(Color.black);
         if ( deliverySolution != null ) {
            int size = deliveryProblem.size();
            DeliveryNode from = deliveryProblem.getDepot();
            for (int i = 0; i < size; i++) {
               DeliveryNode to = deliverySolution.getNode(i);
               Point fromCenter = getCenter(from.x,from.y);
               int fromX = getTopLeft(from.x,from.y).x +
                  (int)(deliverySolution.getDeparture(i)*(double)xFactor);
               Point toCenter = getCenter(to.x,to.y);
            int toX = getTopLeft(to.x,to.y).x +
               (int)(deliverySolution.getArrival(i)*(double)xFactor);
            g.drawLine(fromX,fromCenter.y,toX,toCenter.y);
            from = to;
            }
         }
      }
   }

   private void setupButtons (){
      boxRemove = new JCheckBox("Removes",true);
      boxInsert = new JCheckBox("Inserts",true);
      boxSwap = new JCheckBox("Swaps",true);
      boxSplice = new JCheckBox("Splices",true);
      
      buttonRemove = new JButton("Remove selected");
      buttonInsert = new JButton("Insert selected");
      buttonSelect = new JButton("Select route");
      buttonUnselect = new JButton("Unselect route");
      buttonClear = new JButton("Clear");

      buttonClear.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            clearJustChanged();
         }
      });

      buttonSelect.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            for (int i = deliverySolution.size(); i-->0;)
               selected[deliverySolution.getId(i)] = true;
            repaint();
         }
      });

      buttonUnselect.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            for (int i = deliverySolution.size(); i-->0;)
               selected[deliverySolution.getId(i)] = false;
            repaint();
         }
      });
      
      buttonRemove.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            DeliverySolution sol = (DeliverySolution) deliverySolution.copy();
            java.util.List moves = new ArrayList();
            for (int i = selected.length; i-->0;)
               if ( selected[i] ) {
                  int pos = sol.getPosition(i);
                  if ( pos > 0) {
                     Move m = new DeliveryRemoveMove(deliverySolution,pos);
                     moves.add(m);
                     sol = (DeliverySolution) Utils.doMove(m,sol);
                  }
               }
            MultiMove m = makeMultiMove(moves);
            if ( m != null )
               hugs.manualMove(m);
            clearSelected();
            repaint();
         }
      });


      buttonInsert.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            DeliverySolution sol = (DeliverySolution) deliverySolution.copy();
            java.util.List moves = new ArrayList();
            for (int i = selected.length; i-->0;)
               if ( selected[i] ) {
                  int pos = deliverySolution.getPosition(i);
                  if ( pos < 0){
                     int insert = sol.closestInsert(i);
                     DeliveryInsertMove m = new DeliveryInsertMove(deliverySolution,i,insert);
                     moves.add(m);
                     sol = (DeliverySolution) m.doMove(sol);
                  }
               }
            MultiMove m = makeMultiMove(moves);
            if ( m != null )
               hugs.manualMove(m);
            clearSelected();            
            repaint();

         }
      });
   }  

   private MultiMove makeMultiMove (java.util.List moves) {
      if ( moves == null || moves.isEmpty() ) return null;
      int size = moves.size();
      Move[] array = new Move[size];
      for (int i = 0; i < size; i++)
         array[i] = (Move) moves.get(i);
      return new MultiMove(array);
   }
  
}

