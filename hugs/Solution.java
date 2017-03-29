package hugs;

import java.util.*;
import java.io.*;

public interface Solution extends Serializable{

    public Solution copy() ; 
    public int size (); 
    public Score computeScore (SearchAdjuster adjuster, java.util.List marked);
    public Score computeScore () ;
    public Score getScore () ; 
    public void precompute ();  // for efficiency only, see note below
    
    /* Design note: precompute
       
    The precompute() function is a way for a search algorithm to indicate that many
    moves will be considered and/or applied to a solution.  For example, before
    Tabu evaluates all moves to a given solution, it calls precompute on it.  

    It is perfectly reasonable to ignore this information, i.e. to have a null 
    definition for precompute.  But it can be used to increase efficiency by precomputing
    some information that will be used by each of the calls to operateOn() or
    evaluate.

    */

}

