package hugs;

import java.io.*;
import java.util.*;

public interface Move extends Serializable {
    public Node[] getMoved ();
    public int getPly();

    // executes the moveon the input solution
    public void operateOn (Solution current); // actually changes current

    // does not modify the input solution, and returns the Score that results
    // from executing the move on the input soution, or can return null 
    // if this score would not be better than the input score. see note below.
    // NOTE: if using MultiMove.java, evaluate is only effective for 1-ply moves
    public Score evaluate (Solution solution, Score score, SearchAdjuster searchAdjuster, List marked);

    /* note: evaluate
       
    The evaluate function is designed to allow for improved efficiency, but a simple
    if inefficient definition is:

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }
    */    




}
