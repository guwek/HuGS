package hugs;

import java.io.*;

public class Parameter implements Serializable {

    protected String name;
    public Parameter (String name) {
	this.name = name;
    }
    public String getName() { return name;}
    public Object getValue() { return null;}
    public Parameter copy() {
       System.out.println("need to implement copy for Parameter" + this);
       System.exit(0);
       return null;
    }
    public String toString () {
	return name + "= " + getValue();
    }
}

