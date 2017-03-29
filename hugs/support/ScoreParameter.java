package hugs.support;

import hugs.*;

public class ScoreParameter extends Parameter {
    public Score value;
   public ScoreParameter (String name) { this(name,null);}
    public ScoreParameter (String name, Score value) {
	super(name);
	this.value = value;
    }
   public Object getValue () {return value;}
   public Parameter copy () {
      return new ScoreParameter(name, (value == null ? null : value.copy()));
   }
}

	    
