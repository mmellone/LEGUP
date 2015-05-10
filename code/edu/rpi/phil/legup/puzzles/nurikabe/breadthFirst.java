package edu.rpi.phil.legup.puzzles.nurikabe;
import edu.rpi.phil.legup.Legup;

public class breadthFirst {
  public static void main(String[] args) {
    Legup.main(null);
    Legup thisLegup = Legup.getInstance();
    thisLegup.getGui().promptPuzzle();
    long startTime = System.nanoTime();
    NurikabeAI.breadthFirstSearch(Legup.getCurrentState());
    long stopTime = System.nanoTime();
    System.out.println("breadth First time = " + (stopTime - startTime)/1000000);
  }
}
