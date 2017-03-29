package hugs.support;

import hugs.*;

public class IntParameter extends Parameter {
    public int value;
   
    public IntParameter (String name) {
       this(name,0);
    }
   public IntParameter (String name,int value) {
	super(name);
        this.value = value;
    }
    public Object getValue () { return new Integer(value); }

   public Parameter copy () {
      return (Parameter) new IntParameter(name,value);
   }
}

	    
