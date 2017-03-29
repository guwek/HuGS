package hugs.utils;

   
import java.util.Vector;
import java.math.*;
import java.io.*;

// Tokenize a file specifying a topology
public class MyTokenizer extends StreamTokenizer {
  private String fileName; // name of the file we're reading
  private int filePart;    // which part of the file we're reading
  private int linePart;    // which part of the current line we're reading
  private boolean err;     // true means error during reading
  
  // Constructor
  public MyTokenizer(java.io.Reader r, String fileName) {
    super(r);

    this.fileName = fileName;
    filePart = 0;
    linePart = 0;

    err = false;
    // Initialize read syntax.
    // Start by establishing default syntax table, with two exceptions:
    // (1) we disable parseNumbers(), and
    // (2) we don't set slash (/) as a comment char.

    slashSlashComments(false); //neal changed
    /*
    
    resetSyntax();

    wordChars('A', 'Z');
    wordChars('a', 'z');
    wordChars('\u00A0', '\u00FF');
    whitespaceChars('\u0000', '\u0020');
    // commentChar('*');
    quoteChar('\'');
    quoteChar('"');
        //parseNumbers();


    // Add new syntax
    wordChars('0', '9');      // make digits part of words
    slashSlashComments(false); //neal changed
    eolIsSignificant(true);
    */
  }

   public boolean isString(String string) {
      return sval != null && sval.compareTo(string) == 0;
   }

   public int nextInt () {
      myNextToken();
      return (int) nval;
   }
   public String nextString () {
      myNextToken();
      return sval;
   }
   
  // Read and return the next token.
  // Like nextToken() method of StreamTokenizer,
  // but complains and dies if there's an IO error.
  public int myNextToken() {
    int token = TT_EOF;

    try {
      token = this.nextToken();
    } catch (IOException e) {
       System.err.println(e.getMessage());
      System.exit(1);
    }
    //System.out.println(this.nval + ", " + this.sval);
    return token;
  }
}
