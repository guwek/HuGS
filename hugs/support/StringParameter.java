package hugs.support;

import hugs.*;

public class StringParameter extends Parameter {
    public String value;
   
    public StringParameter (String name) {
       this(name,"");
    }
   public StringParameter (String name,String value) {
	super(name);
        this.value = value;
    }
   public Object getValue () { return value; }

   public Parameter copy () {
      return (Parameter) new StringParameter(name,value);
   }
}

	    
