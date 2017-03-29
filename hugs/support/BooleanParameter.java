package hugs.support;

import hugs.*;

public class BooleanParameter extends Parameter {
    public boolean value;
    public BooleanParameter (String name, boolean value) {
	super(name);
        this.value = value;
    }
   public BooleanParameter (String name) {
      this(name,false);
   }
    public Object getValue () {
       if ( value ) return Boolean.TRUE;
       return Boolean.FALSE;
    }

   public Parameter copy () {
      return (Parameter) new BooleanParameter(name,value);
   }
}

	    
