package hugs.apps.protein;

import hugs.*;
import hugs.support.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProteinVisualization extends NodeVisualization {

   public static double MARGIN_PERCENTAGE = .25;
   public static Color COLOR_BOX = Color.blue;
   public static Color COLOR_CIRCLE = Color.orange;
   public static Color COLOR_MARK = Color.red; // new Color( 255, 0, 255); // purple

   {
      NodeVisualization.selectStroke = NodeVisualization.fourStroke;
      NodeVisualization.selectColor = new Color( 255, 0, 255); // purple
   }   
   
   
   /***********************************/
   /* overrides for NodeVisualization */
   /***********************************/
   protected Node[] getNodes() {
      if ( proteinProblem != null ) return proteinProblem.getNodes();
      return new Node[0];
   }
   protected Point getNodeLocation (Node node) {
      return proteinSolution.getLocation(node.getId());
   }
   protected void  processDragNodeToBlank (Node from, Point to) {
      System.out.println("processDragNode : " + from + " to " + to);
      if ( from != null && to != null ) {
         Move m = null;
         if ( isFancy() ) m =  proteinSolution.followMove(from.getId(),to);
         else if ( isCarry() ) m =  proteinSolution.carryMove(from.getId(),to,false,false);
         else if ( isRotateRight() )
            m =  proteinSolution.carryMove(from.getId(),to,true,false);
         else if ( isRotateLeft() )
            m =  proteinSolution.carryMove(from.getId(),to,false,true);
         if ( m == null ) {
            if ( boxMoveSelected.isSelected() ) {
               Point fromPoint = getNodeLocation(from);
               m = moveSelected(to.x - fromPoint.x, to.y - fromPoint.y);
            } else
               m = (Move)new ProteinRepositionMove(proteinSolution,from.getId(),to.x,to.y);
         }
         if ( m != null )
            hugs.manualMove(m);
      }
   }
   
   protected boolean hasChanged (Node n, Solution old, Solution s){
      ProteinSolution oldS = (ProteinSolution) old;
      ProteinSolution newS = (ProteinSolution) s;
      return !oldS.getLocation(n).equals(newS.getLocation(n));
   }
   protected boolean drawGrid () { return true; }

   JPanel controlPane;
   protected JPanel getControlPane () {
      setupButtons();
      if ( controlPane == null ) {
         controlPane = new JFramedPanel();
         controlPane.add(moveList);
         controlPane.add(boxMobilities);
         controlPane.add(boxMoveSelected);
         controlPane.add(buttonInit);
         controlPane.add(buttonUp);
         controlPane.add(buttonDown);
         controlPane.add(buttonLeft);
         controlPane.add(buttonRight);
         controlPane.add(buttonFruitLoops);
         controlPane.add(buttonSelectBox);
         controlPane.add(buttonSelectCircle);
      }
      return controlPane;
   }
   

   /***********************************/

   private boolean legalPoint (int x, int y) {
      return  x >= 0 && y >= 0 && x < xSize || y > ySize;
   }
   
   private Move moveSelected ( int dX, int dY ) {
      Node[] nodes = getNodes();
      java.util.List moves = new ArrayList();
      for (int i = 0; i < selected.length; i++ ) {
         if ( selected[i] ) {
            Point loc = getNodeLocation(nodes[i]);
            int tX = loc.x + dX;
            int tY = loc.y + dY;
            if ( legalPoint(tX,tY) ) {
               Node n = proteinSolution.getNode(tX,tY);
               if ( n != null && !selected[n.getId()] )
                  return null;
               else
                  moves.add(new ProteinRepositionMove(proteinSolution,i,tX,tY));
            }
         }
      }
      int size = moves.size();
      Move[] moveArray = new Move[size];
      for (int i = 0; i < size; i++)
         moveArray[i] = (Move) moves.get(i);
      return new MultiMove(moveArray);
   }

   private int xSize;
   private int ySize;

   protected JComboBox moveList;
   protected JCheckBox boxMobilities;
   protected JCheckBox boxMoveSelected;
   protected JButton buttonUp;
   protected JButton buttonDown;
   protected JButton buttonLeft;
   protected JButton buttonRight;
   protected JButton buttonFruitLoops;
   protected JButton buttonInit;
   protected JButton buttonSelectBox;
   protected JButton buttonSelectCircle;
   public ProteinVisualization(int xSize, int ySize){
      super(xSize,ySize);
      this.xSize = xSize;
      this.ySize = ySize;
   }

   private ProteinProblem proteinProblem = null;
   public void setProblem (Problem problem) {
      super.setProblem(problem);
      this.proteinProblem = (ProteinProblem) problem;
   }

   private ProteinSolution proteinSolution = null;
   public void setSolution (Solution solution) {
      super.setSolution(solution);
      this.proteinSolution = (ProteinSolution) solution;
   }

   private int margin = 2;
   private int doubleMargin = margin*2;

   public void paintComponent( Graphics g ) {
      margin = (int) (MARGIN_PERCENTAGE * (double) Math.min(xFactor,yFactor));
      doubleMargin = margin*2;
      super.paintComponent(g);
   }

   protected void paintNode (Graphics g, Node n, Color mobilityColor, boolean marked) {
      ProteinNode node = (ProteinNode) n;
      Point pt = getNodeLocation(node);
      Point topLeft = getTopLeft(pt.x,pt.y);
      if ( boxMobilities.isSelected() )
         g.setColor(mobilityColor);
      else if ( node.getValue() )
         g.setColor(COLOR_BOX);
      else
         g.setColor(COLOR_CIRCLE);
      
      int x = topLeft.x + margin;
      int y = topLeft.y + margin;
      int w = xFactor - doubleMargin;
      int h = yFactor - doubleMargin;
      Graphics2D g2 = (Graphics2D) g;
      if ( node.getValue() ){
         g.fillRect(x,y,w,h);
         if ( marked ) {
            g2.setStroke(threeStroke);
            g.setColor(COLOR_MARK);
            g.drawRect(x,y,w,h);
            g2.setStroke(oneStroke);
         }
         else {
            /*
            if ( boxMobilities.isSelected() ) {
               g.setColor(COLOR_BOX);
               g2.setStroke(fiveStroke);
               g.drawRect(x,y,w,h);
               g2.setStroke(oneStroke);
            }
            else {
            */
               g.setColor(Color.black);
               g.drawRect(x,y,w,h);
         }
      }
      else {
         g.fillOval(x,y,w,h);
         if ( marked ) {
            g2.setStroke(threeStroke);
            g.setColor(COLOR_MARK);
            g.drawOval(x,y,w,h);
            g2.setStroke(oneStroke);
         }
         else {
            /*
            if ( boxMobilities.isSelected() ) {
               g.setColor(COLOR_CIRCLE);
               g2.setStroke(fiveStroke);
               g.drawOval(x,y,w,h);
               g2.setStroke(oneStroke);
            }
            */
            g.setColor(Color.black);
            g.drawOval(x,y,w,h);
         }
      }
   }


   protected void paintOtherAfterNodes (Graphics g){
      if ( problem == null ) return;
      Node[] nodes = problem.getNodes();
      g.setColor(Color.black);
      Node from = nodes[nodes.length-1];
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(threeStroke);
      for (int i = nodes.length-1; i-->0;){
         Node to = nodes[i];
         Point fromPoint = getCenter(getNodeLocation(from));
         Point toPoint = getCenter(getNodeLocation(to));
         g.drawLine(fromPoint.x,fromPoint.y,toPoint.x,toPoint.y);
         from = to;
      }
      g2.setStroke(oneStroke);
   }

   private boolean isFancy () { return "fancy".equals(moveList.getSelectedItem()); }
   private boolean isRegular () { return "regular".equals(moveList.getSelectedItem()); }
   private boolean isCarry () { return "carry".equals(moveList.getSelectedItem()); }
   private boolean isRotateLeft () { return "rotate left".equals(moveList.getSelectedItem()); }
   private boolean isRotateRight () { return "rotate right".equals(moveList.getSelectedItem()); }

   private void setupButtons (){
      String[] moveTypes = {"regular","fancy","carry","rotate right", "rotate left"};
      moveList = new JComboBox(moveTypes);
      moveList.setSelectedIndex(2);
      boxMobilities = new JCheckBox("Mobilities",false);
      boxMoveSelected = new JCheckBox("Move selected",false);
      buttonUp = new JButton("Up");
      buttonDown = new JButton("Down");
      buttonLeft = new JButton("Left");
      buttonRight = new JButton("Right");
      buttonFruitLoops = new JButton("Clear");
      buttonInit = new JButton("Init");
      buttonSelectBox = new JButton("Select boxes");
      buttonSelectCircle = new JButton("Select circles");
      boxMobilities.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) { repaint(); }
         });

      buttonFruitLoops.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) { clearJustChanged();         }
         });
      buttonInit.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) { actionInit();         }
         });
      buttonSelectBox.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) { actionSelectBox();         }
         });
      buttonSelectCircle.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) { actionSelectCircle();         }
         });
      buttonDown.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) {
               Move m = proteinSolution.moveAll(0,1);
               if ( m != null )
                  hugs.manualMove(m);
               repaint();
            }
         });
      buttonUp.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) {
               Move m = proteinSolution.moveAll(0,-1);
               if ( m != null )
                  hugs.manualMove(m);
               repaint();
            }
         });
      buttonLeft.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) {
               Move m = proteinSolution.moveAll(-1,0);
               if ( m != null )
                  hugs.manualMove(m);
               repaint();
            }
         });
   buttonRight.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event) {
               Move m = proteinSolution.moveAll(1,0);
               if ( m != null )
                  hugs.manualMove(m);
               repaint();
            }
         });
   }

   private void actionSelectBox () {
      Node[] nodes = getNodes();
      for (int i = 0; i < nodes.length; i++)
         if ( ((ProteinNode) nodes[i]).getValue() )
            selected[i] = true;
      repaint();
   }

   private void actionSelectCircle () {
      Node[] nodes = getNodes();
      for (int i = 0; i < nodes.length; i++)
         if ( !((ProteinNode) nodes[i]).getValue() )
            selected[i] = true;
      repaint();
   }
   
   private void actionInit () {

      Node[] moveArray = getNodes();
      int length = moveArray.length;
      Point[] pointArray = new Point[length];
      boolean[][] board = new boolean[length][length];
      Point current = new Point(0,0);
      Point direction = new Point(1,0);
      for (int i = 0; i < length; i++) {
         pointArray[i] = current;
         board[current.x][current.y] = true;
         int x = current.x+direction.x;
         int y = current.y+direction.y;
         if ( x < 0 || y < 0 || x >= xSize || y >= ySize ||
              board[x][y] ) {
            // make right turn
            if ( direction.x == 1 ) direction = new Point(0,1);
            else if ( direction.x == -1 ) direction = new Point(0,-1);
            else if ( direction.y == 1 ) direction = new Point(-1,0);
            else direction = new Point(1,0);
            x = current.x+direction.x;
            y = current.y+direction.y;
         }
         board[x][y] = true;
         current = new Point(x,y);
      }

      Move m = new ProteinRepositionMove(proteinSolution,moveArray,pointArray);
      if ( m != null )
         hugs.manualMove(m);
      clearJustChanged();
      repaint();
   }
}

