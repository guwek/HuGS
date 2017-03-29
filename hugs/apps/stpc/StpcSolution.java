package hugs.apps.stpc;

import hugs.*;
import hugs.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.util.*;

public class StpcSolution implements Solution
{
    public static boolean trace = false;

    private int[] degrees;
    private boolean[] nodeTaken;
    private boolean[] edgeTaken;
    private StpcProblem problem;

    private double cost;
    private double distance; // computed
    private int components;
   
    private boolean absValues = false;

    private StpcScore score;
        
    public StpcSolution (StpcProblem problem) 
    {
      
	distance = 0;
	this.problem = problem;

	degrees = new int[problem.size()];
	for (int i=0; i<problem.size(); i++)
	    degrees[i] = 0;
      
	nodeTaken = new boolean[problem.size()];
	for (int i=0; i<problem.size(); i++)
	    nodeTaken[i] = i < problem.getDepots() ? true : false;
      
	edgeTaken = new boolean[problem.getEdges().length];
	for (int i=0; i<problem.getEdges().length; i++)
	    edgeTaken[i] = false;
					
	initializeSolution();
    }
   
    public void addEdge(int nEdge)
    {
	if (edgeTaken[nEdge]) return;

	StpcEdge [] edges = problem.getEdges();
	
	edgeTaken[nEdge] = true;
	degrees[edges[nEdge].to]++;
	degrees[edges[nEdge].from]++;
    }
	
    public void removeEdge(int nEdge)
    {
	if (!edgeTaken[nEdge]) return;

	StpcEdge [] edges = problem.getEdges();
	
	edgeTaken[nEdge] = false;
	if (degrees[edges[nEdge].to] == 0 || degrees[edges[nEdge].from] == 0)
	    System.out.println("Remove Edge Error!");
		
	degrees[edges[nEdge].to]--;
	degrees[edges[nEdge].from]--;
    }

    public void addNode(int nNode)
    {
	nodeTaken[nNode] = true;
	construct();
    }
	
    public void removeNode(int nNode)
    {
	if (nNode > problem.getDepots())
	{
	    nodeTaken[nNode] = false;
	    construct();
	}
    }
	
    public boolean equals (Solution s)
    {
   	boolean [] sNodeTaken = ((StpcSolution) s).getNodeTaken();
   	if (nodeTaken.length != sNodeTaken.length) return false;
   	for (int i=0; i<nodeTaken.length; i++)
	    if (nodeTaken[i] != sNodeTaken[i]) return false;
   	return true;
    }
   
    public Solution copy ()
    {
	StpcSolution s = new StpcSolution(problem);
	
	s.edgeTaken = new boolean [this.edgeTaken.length];
	for (int i=0; i<this.edgeTaken.length; i++)
	    s.edgeTaken[i] = this.edgeTaken[i];
			
	s.nodeTaken = new boolean [this.nodeTaken.length];
	for (int i=0; i<this.nodeTaken.length; i++)
	    s.nodeTaken[i] = this.nodeTaken[i];

	s.degrees = new int [this.degrees.length];
	for (int i=0; i<this.degrees.length; i++)
	    s.degrees[i] = this.degrees[i];
		
	s.absValues = this.absValues;

	s.components = this.components;
	
	s.computeCost();
		
	return (Solution) s;
    }

    public void construct()
    {
	Mst();		
    }
	
    private int Mst()
    {
	Mobilities mobilities = Hugs.THIS.getMobilities();
		
	StpcUnionFind uf = new StpcUnionFind(problem.size());

	for (int i=0; i<problem.size(); i++)
	    degrees[i] = 0;
            
	for (int i=0; i<problem.getEdges().length; i++)
	    edgeTaken[i] = false;

	int treeSize = 0;
	int aktSize = 0;
				
	for (int i=0; i<nodeTaken.length; i++)
	    if (nodeTaken[i])
		treeSize++;
				
	StpcEdge [] edges = problem.getEdges();
		
	for (int i=0; (i<edges.length) && (aktSize+1<treeSize); i++)
	    if (nodeTaken[edges[i].to] &&
		nodeTaken[edges[i].from])
		if (uf.find(edges[i].to, edges[i].from, true))
		{
		    addEdge(i);
		    aktSize++;
		}

	peel();

	components = uf.getComponents(nodeTaken);
	return components;
    }

    private void peel()
    {
	LinkedList queue = new LinkedList();
	LinkedList nextQueue = new LinkedList();
		
	StpcEdge [] edges = problem.getEdges();
	StpcNode [] nodes = (StpcNode []) problem.getNodes();

	int ssize = size();
						
	for (int i=problem.getDepots(); i<problem.size(); i++)
	    if (degrees[i] == 1)
		queue.add(new Integer(i));

	while (queue.size() != 0)
	{
	    for (ListIterator e = queue.listIterator(0); e.hasNext(); )
	    {
		int nNode = ((Integer) e.next()).intValue();

		if (nodes[nNode].getOrders() == 0)
		{
		    Enumeration inz = problem.getInzEdges()[nNode].elements();
		    while (inz.hasMoreElements())
		    {
			int nEdge = ((Integer) inz.nextElement()).intValue();
					
			if (edgeTaken[nEdge] && ssize != 2)
			{
			    int next = 0;
			    if (edges[nEdge].to == nNode)
				next = edges[nEdge].from;
			    else
				next = edges[nEdge].to;
							
			    removeEdge(nEdge);
			    ssize--;
						
			    if (next >= problem.getDepots() && degrees[next] == 1)
				nextQueue.add(new Integer(next));
			}
		    }
		}			
	    }
	    queue = nextQueue;
	    nextQueue = new LinkedList();
	}		
    }

    public Score computeScore (SearchAdjuster adjuster, 
			       java.util.List marked)
    {
	if (adjuster != null)		// adjuster is now null when stpc is started
	    absValues = ((StpcSearchAdjuster) adjuster).absValues.value;
	return computeScore();
    }

    public Score computeScore (){
	cost = computeCost();
	return (Score) score;
    }
   
    public int [] getDegrees() { return degrees; }
    public boolean[] getNodeTaken() { return nodeTaken; }
    public boolean[] getEdgeTaken() { return edgeTaken; }
    public Score getScore (){ return (Score) score; }
    public StpcProblem getProblem() { return problem; }
    public void precompute () {};

    public double computeCost ()
    {
   	double distSum = 0;
	int deliveredSum = 0;

	StpcEdge[] e = problem.getEdges();
		
   	for (int i=0; i<edgeTaken.length; i++)
	    if (edgeTaken[i])
		distSum += e[i].distance;
   	
	StpcNode[] nodes = (StpcNode []) problem.getNodes();
		
	for (int i=0; i<degrees.length; i++)
	    if (degrees[i] > 0)
		deliveredSum += nodes[i].getOrders();

	if (components > 1)
	    distSum += problem.getMaxDistance() * (components - 1);
					
	score = new StpcScore(distSum, deliveredSum, absValues);
			
	return score.toDouble();
    }

   
    private double distance (Point one, Point two) {
	double dx = (one.x - two.x) * problem.getXAdjustment();
	double dy = one.y - two.y;
	return Math.sqrt(dx*dx + dy*dy);
    }
    private double distance (StpcNode one, StpcNode two) {
	double dx = (one.x - two.x) * problem.getXAdjustment();
	double dy = one.y - two.y;
	return Math.sqrt(dx*dx + dy*dy);
    }

    /*
      public String toString () {
      }
    */

    public void initializeSolution (){
	// greedyInit();
	cost = computeCost();
    }
   
    public int size ()
    {
	int s=0;
	
   	for (int i=0; i<nodeTaken.length; i++)
	    if (nodeTaken[i])
		s++;
		
	return s;
    }

    public void print () {
	/*      System.out.println("Solution:" + this);
		for (int i = 0; i < numVisited; i++)
		System.out.print(order[i] + " ");
		System.out.println();*/
    }

    public String toString() {
	return null; //"[Sol: unDel= " + percentUndelivered + ",dist= " + drivingTime + ", lateness = " + lateness;
    }

    ///////////////////////////////////

}
