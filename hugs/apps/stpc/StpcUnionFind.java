package hugs.apps.stpc;

public class StpcUnionFind
{
    private int [] tree;
   
    public StpcUnionFind(int size)
    {
	tree = new int [size];
	for (int i=0; i<size; i++)
	    tree[i] = -1;
    }
	
    public boolean find(int a, int b, boolean update)
    {
	int t=0;
	int i=a;
	int j=b;
	while (tree[i] >= 0) i=tree[i];	   
	while (tree[j] >= 0) j=tree[j];
	while (tree[a] >= 0)
	{
	    t = a;
	    a = tree[a];
	    tree[t] = i;
	}
	while (tree[b] >= 0)
	{
	    t = b;
	    b = tree[b];
	    tree[t] = j;
	}
		
	if (update && i != j)
	{
	    if (tree[j] < tree[i])
	    {
		tree[j] += tree[i] - 1;
		tree[i] = j;
	    }
	    else
	    {
		tree[i] += tree[j] - 1;
		tree[j] = i;
	    }
	}
	return (i != j);
    }
	
    int getComponents(boolean [] nodeTaken)
    {
	int components = 0;

	for (int i=0; i<tree.length; i++)
	    if (tree[i] <= -1)
		if (nodeTaken[i])
		    components++;

	return components;
    }
}
