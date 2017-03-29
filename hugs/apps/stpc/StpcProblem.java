
package hugs.apps.stpc;

import hugs.*;
import hugs.utils.*;
import java.util.*;
import java.io.*;

public class StpcProblem implements Problem
{
   public static int MAX_ORDER = 7;
   public static int MIN_ORDER = 3;
   private StpcNode[] nodes;
   private StpcEdge[] edges;
   private Vector [] inzEdges;
   private int xSize;
   private int ySize;
   private double xAdjustment;
   private StpcSolution initSolution = null;
   private int depots;
   private int maxDeliveries;
   private double maxDistance;

   public StpcProblem (int xSize, int ySize, Node [] n, StpcEdge [] e, int depots)
	{
		this.xSize = xSize;
		this.ySize = ySize;
		this.xAdjustment = StpcVisualization.aspectRatio * (double) ySize/ (double) xSize;
		this.depots = depots;
		
		maxDeliveries = 0;
		maxDistance = 0;
		nodes = (StpcNode [] ) n;
		edges = e;
		int size = nodes.length;
		
		inzEdges = new Vector[size];
		for (int i=0; i<size; i++)
			inzEdges[i] = new Vector();

		for (int i=0; i<edges.length; i++)
		{
			maxDistance += edges[i].distance;
			inzEdges[edges[i].to  ].addElement(new Integer(i));
			inzEdges[edges[i].from].addElement(new Integer(i));
		}		
		
		int maxOrder = 0;
		int minOrder = Integer.MIN_VALUE;
		
		for (int i=0; i<nodes.length; i++)
		{
			maxDeliveries += nodes[i].getOrders();
			if (nodes[i].getOrders() > maxOrder)
				maxOrder = nodes[i].getOrders();
			else if (nodes[i].getOrders() < minOrder)
				minOrder = nodes[i].getOrders();
		}
		
		MIN_ORDER = minOrder;
		MAX_ORDER = maxOrder;
	}
	
   public StpcProblem (int xSize, int ySize, int size, int depots)
   {
		this.xSize = xSize;
		this.ySize = ySize;
		this.xAdjustment = StpcVisualization.aspectRatio * (double) ySize/ (double) xSize;
		this.depots = depots;
		
		boolean[][] taken = new boolean[xSize][ySize];
		maxDeliveries = 0;
		
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				taken[x][y] = false;
		
		nodes = new StpcNode[size];
		inzEdges = new Vector[size];
		for (int i=0; i<size; i++)
			inzEdges[i] = new Vector();
								
		for (int i = 0; i < size; i++)
		{
			boolean ok = false;
			int x = Utils.randomInt(xSize);
			int y = Utils.randomInt(ySize);
		
			while ( taken[x][y] )
			{
				x = Utils.randomInt(xSize);
				y = Utils.randomInt(ySize);
			}
	
			taken[x][y] = true;
			int order = MIN_ORDER + Utils.randomInt((MAX_ORDER-MIN_ORDER)+1);
			maxDeliveries += order;
			nodes[i] = new StpcNode(i, x,y,order);
		}

		// Add Edges //
		
		edges = new StpcEdge[2*size];
		double best;
		int from = 0;
		int to = 0;
				
		for (int u=0; u<2; u++)
		for (int i=0; i<size; i++)
		{
			best = Double.MAX_VALUE;
			
			for (int j=0; j<size; j++)
			{
				if (j!=i)
				{
					double dx = (nodes[i].x - nodes[j].x) * xAdjustment;
					double dy = nodes[i].y - nodes[j].y;
					double dist = Math.sqrt(dx*dx + dy*dy);
					
					if (dist < best)
					{
						boolean alreadyExists = false;
						for (Enumeration l = inzEdges[j].elements(); l.hasMoreElements(); )
						{
							int e = ((Integer) l.nextElement()).intValue();
							if (edges[e].to   == i ||
								 edges[e].from == i)
								alreadyExists = true;
						}
						
						if (!alreadyExists)
						{		
							best = dist;
							from = i;
							to   = j;
						}
					}
				}
			}

			edges[u*size+i] = new StpcEdge(from, to, best, true);
			inzEdges[from].add(new Integer(u*size+i));
			inzEdges[to].add(new Integer(u*size+i));
		}
		/*
		// Random Edges
		for (int i=0; i<5; i++)
		{
			int n1 = Utils.randomInt(nodes.length);
			int n2 = Utils.randomInt(nodes.length);
			while (n2 == n1)
				n2 = Utils.randomInt(nodes.length);
				
			double dx = (nodes[n1].x - nodes[n2].x) * xAdjustment;
			double dy = nodes[n1].y - nodes[n2].y;
			double dist = Math.sqrt(dx*dx + dy*dy);
	
			edges[i+2*size] = new StpcEdge(nodes[n1], nodes[n2], dist);
			neighbors[n2].add(nodes[n1]);
			neighbors[n1].add(nodes[n2]);
		}*/

	}

   
   public Solution getInitSolution(){
      if ( initSolution == null )
         initSolution = new StpcSolution(this);
      return (Solution) initSolution;
   }
   
   public Solution randomSolution(){
      System.out.println("WARNING: should maybe implement randomSolution for StpcProblem");
      return getInitSolution();
   }

   public Node[] getNodes () { return nodes;}
   public StpcEdge[] getEdges() { return edges; }
   public Vector[] getInzEdges() { return inzEdges; }
   public int size () { return nodes.length; }

   // extra
   public StpcNode getNode (int i) { return nodes[i]; }
   public double getXAdjustment() { return xAdjustment;}
   public int getXSize() { return xSize;}
   public int getYSize() { return ySize;}
   public int getDepots() {return depots;}
   public int getMaxDeliveries () {return maxDeliveries;}
   public double getMaxDistance () {return maxDistance;}

	public static StpcProblem read (String name)
	{
		Vector nodes = new Vector();
		Vector edges = new Vector();
		Vector translation = new Vector(100);
		
		int nNodes = 0;
		int nEdges = 0;
		int nDepots = 0;
		int xMax = 0;
		int yMax = 0;
		int mode = 0;
		String line = null;
		
		BufferedReader reader = Utils.myOpenFile(name);
		if ( reader == null ) return null;
		line = Utils.readLineIf(reader);
		while (line != null && line.startsWith("#"))
		    line = Utils.readLineIf(reader);
      
		while (line != null)
		{
			StringTokenizer s = new StringTokenizer(line);
			if (s.countTokens() != 0)
			{
				String identifier = s.nextToken();
				if (identifier.equals("depots"))
				{
					nDepots = Integer.parseInt(s.nextToken());
				}
				else if (identifier.equals("node"))
					mode = 1; // nodes
				else if (identifier.equals("link"))
					mode = 2; // links
				else if (identifier.equals("ring"))
					{} // ignore
				else if (mode == 1)
				{
					int id = Integer.parseInt(identifier);
					int x = Integer.parseInt(s.nextToken());
					int y = Integer.parseInt(s.nextToken());
					int o = Integer.parseInt(s.nextToken());

					if (x > xMax) xMax = x;
					if (y > yMax) yMax = y;
					
					if (translation.size() < id)
						translation.setSize(id);
						
					translation.insertElementAt(new Integer(nNodes), id);
										
					nodes.addElement(new StpcNode(nNodes, x, y, o));
					nNodes++;
				}
				else if (mode == 2)
				{
					// identifier unused
					int from = Integer.parseInt(s.nextToken());
					int to = Integer.parseInt(s.nextToken());
					double distance = Double.parseDouble(s.nextToken());
					
					from = ((Integer) translation.elementAt(from)).intValue();
					to   = ((Integer) translation.elementAt(to  )).intValue();
					
					edges.addElement(new StpcEdge(from, to, distance, true));
				}
			}
			
	      line = Utils.readLineIf(reader);			
	      while (line != null && line.startsWith("#"))
		      line = Utils.readLineIf(reader);
		}
		
		StpcNode [] n = new StpcNode[nodes.size()];
		nodes.copyInto(n);
		
//		makeCompleteGraph(edges, n);
		
		StpcEdge [] e = new StpcEdge[edges.size()];
		edges.copyInto(e);
		
		Arrays.sort(e);
				
		for (int i=0; i<n.length; i++)
		{
			n[i].x /= ((float) xMax / 100.0);
			n[i].y /= ((float) yMax / 50.0);
		}

		int d=0;
		for (int i=0; i<n.length; i++)
			for (int j=i+1; j<n.length; j++)
				if (n[i].x == n[j].x && n[i].y==n[j].y)
					d++;

		System.out.println(d + " duplicates!");
		
		return new StpcProblem(100, 50, n, e, nDepots);
	}

   public static void write (StpcProblem problem, String name)
   {
		List list = new ArrayList();
		StpcNode [] nodes = (StpcNode []) problem.getNodes();
		StpcEdge [] edges = problem.getEdges();
		String type;
		
		for (int i = 0; i < nodes.length; i++) 
		{
			if (i < problem.getDepots())
				type = new String("S");
			else
				type = new String("V");

			list.add(type + " " + nodes[i].getId() + " " + nodes[i].x + " " + nodes[i].y + " " + nodes[i].getOrders());
		}
		
		for (int i = 0; i < edges.length; i++)
		{
			list.add("E " + edges[i].from + " " + edges[i].to + " " + edges[i].distance);
		}
		
		Utils.makeFile(name,list);
   }

	protected static void makeCompleteGraph(Vector edges, StpcNode [] nodes)
	{
		boolean [][] edgeExists = new boolean [nodes.length][nodes.length];
		for (int i=0; i<nodes.length; i++)
			for (int j=0; j<nodes.length; j++)
				edgeExists[i][j] = (i==j);
					
		double sum = 0;	
		for (Enumeration e = edges.elements(); e.hasMoreElements(); )
		{
			StpcEdge n = (StpcEdge) e.nextElement();
			edgeExists[n.from][n.to] = true;
			edgeExists[n.to][n.from] = true;
			
			sum += n.distance;
		}

		for (int i=0; i<nodes.length; i++)
			for (int j=0; j<nodes.length; j++)
				if (!edgeExists[i][j])
				{
					edgeExists[i][j] = true;
					edgeExists[j][i] = true;
					double dx = nodes[i].x - nodes[j].x;
					double dy = nodes[i].y - nodes[j].y;
					double dist = Math.sqrt((dx*dx) + (dy*dy));
					dist += sum;
					edges.addElement(new StpcEdge(i,j,dist, false));
				}			
	}
}



