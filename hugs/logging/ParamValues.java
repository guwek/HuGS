package hugs.logging;

import java.io.*;
import java.util.*;

public class ParamValues implements Serializable { 

    public String name;
    public List values = new ArrayList();
    public ParamValues (String name){
	this.name = name;
    }
    public String toString () {
	return name + ": " + values;
    }
	
}


