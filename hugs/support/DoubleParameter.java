package hugs.support;

import hugs.*;
import hugs.utils.*;

public class DoubleParameter extends Parameter {
   public double value;
   public DoubleParameter (String name) { this(name,0);}
   public DoubleParameter (String name, double value) {
	super(name);
	this.value = value;
    }
   public String toString () {
	return name + "= " + Utils.doubleToString(value,2);
   }
   public Object getValue () { return new Double(value); }
   public Parameter copy () {
      return (Parameter) new DoubleParameter(name,value);
   }
}

	    
