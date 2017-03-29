# HuGS
Human-Guided Search (HuGS) is a framework for helping the computer solve computational problems by human interaction.

## Authors

HuGS was developed by Neal Lesh, Gunnar W. Klau, Michael Mitzenmacher, and Joe Marks.

## Citation

> Klau, G. W., Lesh, N., Marks, J., and Mitzenmacher, M. 2002. Human-guided tabu search. In Proceedings of the 18th National Conference on Artificial Intelligence (AAAI'02]. AAAI Press. 41–47.

## Usage

To run the programs, try

    java hugs.apps.crossing.Crossing -size 10 5
    java hugs.apps.delivery.Delivery
    java hugs.apps.protein.Protein -size 50
    java hugs.apps.jobshop.Jobshop -loadProblem apps/jobshop/problems/la16.jsp

The last assumes you are in the hugs directory.  You can, of course,
run the programs without any arguments, but the above give samples of
how to specify the problem.

Here are a few of the less obvious features of the system:

    - Double-click on the background to unselect all elements

    - The red, yellow, and green squares near the score are shortcuts
      for setting mobilities.  Click on them once to set the mobilities
      of the selected elements.

    - Double-click on the mobility icons to set the mobility of _all_
      elements.

    - The Selections menu allows you to mark (and unmark) elements.
      Some of the applications allow you to specify secondary
      objective functions on marked elements, by controlling the
      inputs to the search algorithm (press the "Inputs" button to get
      the controls) For example, in Crossing you can tell the search
      algorithm to move marked elements to the left or right.
