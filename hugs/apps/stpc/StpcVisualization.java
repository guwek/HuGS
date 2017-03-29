package hugs.apps.stpc;

import hugs.*;
import hugs.support.*;
import java.util.*;
import hugs.*;
import java.awt.*;
import java.awt.geom.*;  
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;


public class StpcVisualization extends NodeVisualization {

   public static boolean showMoveTypes = false;
   public static double aspectRatio =  2.0;
   public static int X_MARGIN = 2;
   public static int Y_MARGIN = 1;

   
   {
      NodeVisualization.selectStroke = NodeVisualization.twoStroke;
      NodeVisualization.selectColor = new Color( 255, 0, 255); // purple
   }   

   
   /***********************************/
   /* overrides for NodeVisualization */
   /***********************************/
   protected Node[] getNodes() {
      if ( stpcProblem != null ) return stpcProblem.getNodes();
      return new Node[0];
   }
   protected Point getNodeLocation (Node n) {
      StpcNode node = (StpcNode) n;
      return new Point (node.x, node.y);
   }
   protected void  processDragNode(Node from, Node to) {
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
/*         if ( showMoveTypes ) {
            controlPane.add(boxInsert);
            controlPane.add(boxRemove);
            controlPane.add(boxSwap);
            controlPane.add(boxSplice);
         }*/
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

   protected boolean hasChanged (Node n, Solution old, Solution s)
   {
		int nNode = n.getId();

		boolean [] takenOld = ((StpcSolution) old).getNodeTaken();
		boolean [] taken	  = ((StpcSolution) s).getNodeTaken();

		if (taken[nNode] != takenOld[nNode])
			return true;
		else
	      return false;
   }
   
   private static int WIDTH = 10;
   private static int HEIGHT = 10;
   
   private int xSize = 700;
   private int ySize = 250;
   
   public StpcVisualization(int xSize, int ySize){
      super(xSize,ySize);
      this.xSize = xSize;
      this.ySize = ySize;
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(xSize+50, ySize+50));
      // setMaximumSize(new Dimension(xSize+50, ySize+50));
   }

   private StpcProblem stpcProblem = null;
   public void setProblem (Problem problem) {
      super.setProblem(problem);
      this.stpcProblem = (StpcProblem) problem;
   }

   /*
   private StpcMobilities mobilities = null;
   public void setMobilities (StpcMobilities mobilities) {
      this.mobilities = mobilities;
   }
   */
   

   private StpcSolution stpcSolution = null;

   public void setSolution (Solution solution) {
      super.setSolution(solution);
      this.stpcSolution = (StpcSolution) solution;
   }


   protected void paintNode (Graphics g, Node n, Color mobilityColor, boolean marked) {
      StpcNode node = (StpcNode) n;
		Point center = getCenter(node.x,node.y);

		if ( n.getId() < stpcProblem.getDepots() )
		{
			g.setColor(Color.black);
			int size = Math.min(xFactor,yFactor);
			g.fillRect(center.x-(size/2),center.y-(size/2),size,size);
		}
		else
		{
			g.setColor(mobilityColor);
			int width = 0;
			int height = 0;
			
			if (node.getOrders() != 0)
			{
				width  = (int) ((double)(xFactor-X_MARGIN) * ((double)node.getOrders() / (double)StpcProblem.MAX_ORDER ));
				if (width < 5)
					width = 5;
				height = yFactor-Y_MARGIN;
			}
			else
			{
				width  = 3;
				height = 3;
			}
			
			if ( marked )
			{
				g.fillRect(center.x-(width/2),center.y-(height/2),width,height);
				g.setColor(Color.black);
				g.drawRect(center.x-(width/2),center.y-(height/2),width,height);
			}
			else
			{
				g.fillOval(center.x-(width/2),center.y-(height/2),width,height);
				g.setColor(Color.black);
				g.drawOval(center.x-(width/2),center.y-(height/2),width,height);
			}

		}
   }

   
   BasicStroke thickStroke = new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
   BasicStroke thinStroke = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);

   protected void paintOtherBeforeNodes (Graphics g)
   {
   	if (problem != null)
		{
			BasicStroke thickStroke = new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
			BasicStroke thinStroke = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);

			boolean [] taken = null;
			if (stpcSolution != null) taken = stpcSolution.getEdgeTaken();
		
			// Paint Edges
			((Graphics2D) g).setStroke(thinStroke);
			g.setColor(Color.red);
		
			StpcEdge [] e = stpcProblem.getEdges();
			for (int i=0; i<e.length; i++)
			{
				if (stpcSolution != null)
					if (taken[i])
					{
						if (e[i].orig)
							g.setColor(Color.black);
						else
							g.setColor(Color.blue);
						((Graphics2D) g).setStroke(thickStroke);
					}
					else
					{
						g.setColor(Color.red);
						((Graphics2D) g).setStroke(thinStroke);
					}
				
				if (e[i].orig || taken[i])
				{
					StpcNode [] nodes = (StpcNode []) problem.getNodes();
			
					Point center1 = getCenter(nodes[e[i].from].x,nodes[e[i].from].y);
					Point center2 = getCenter(nodes[e[i].to].x,nodes[e[i].to].y);
					g.drawLine(center1.x, center1.y, center2.x, center2.y);
				}
			} 
		}
		((Graphics2D) g).setStroke(thinStroke);
   }
   
   protected void paintOtherAfterNodes (Graphics g)
   {  	
   }

   private void setupButtons (){
      boxRemove = new JCheckBox("Removes",true);
      boxInsert = new JCheckBox("Inserts",true);
      boxSwap = new JCheckBox("Swaps",true);
      boxSplice = new JCheckBox("Splices",true);
      
      buttonRemove = new JButton("Remove Selected");
      buttonInsert = new JButton("Insert selected");
      buttonSelect = new JButton("Select tree");
      buttonUnselect = new JButton("Unselect tree");
      buttonClear = new JButton("Clear");

      buttonClear.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            clearJustChanged();
         }
      });

      buttonSelect.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            for (int i = 0; i < getNodes().length; i++)
					if (stpcSolution.getDegrees()[i] > 0)
						selected[i] = true;
            repaint();
         }
      });

      buttonUnselect.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            for (int i = 0; i < getNodes().length; i++)
					if (stpcSolution.getDegrees()[i] > 0)
						selected[i] = false;
            repaint();
         }
      });
      
      buttonRemove.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {

				java.util.List moves = new ArrayList();
				
				boolean [] nodeTaken = stpcSolution.getNodeTaken();

				for (int i=0; i < selected.length; i++)
					if (selected[i] && nodeTaken[i])
					{
			      	Move m = new StpcRemoveMove(stpcSolution, i);
					   moves.add(m);
					}
		

				MultiMove m = 	makeMultiMove(moves);
				if ( m != null )
					hugs.manualMove(m);

				clearSelected();  
            repaint();

         }
      });


      buttonInsert.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
				
				java.util.List moves = new ArrayList();
				
				boolean [] nodeTaken = stpcSolution.getNodeTaken();
				StpcNode [] nodes = (StpcNode []) getNodes();
				
				for (int i=0; i < selected.length; i++)
					if (selected[i] && !nodeTaken[i])
					{
			      	Move m = new StpcInsertMove(stpcSolution, i);
					   moves.add(m);
					}		

				MultiMove m = 	makeMultiMove(moves);
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

