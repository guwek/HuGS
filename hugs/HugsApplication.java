package hugs;

public interface HugsApplication {
   
   public Visualization makeVisualization ();
   
   public MoveGenerator makeMoveGenerator (Mobilities mobilities,
                                           Solution solution);
   
   public Problem makeProblem (String name);

}
   
