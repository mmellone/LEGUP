package edu.rpi.phil.legup.puzzles.nurikabe;

import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.Arrays;
import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.ConnectedRegions;


public class NurikabeAI {
  static Nurikabe nur = new Nurikabe();
  static final Vector<PuzzleRule> rules = nur.getRules();
  static final Vector<Contradiction> contras = nur.getContradictions();

  /* solves the puzzle by simply trying looping through the board
     and applying as many basic rules as possible */
  private static BoardState bruteForceSolve(BoardState origState) {

    int unknownCount = 0;

    for (int y = 0; y < origState.getHeight(); y++) {
      for (int x = 0; x < origState.getWidth(); x++) {
        if (origState.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN) {
          unknownCount++;
        }
      }
    }
    if (unknownCount == 0 || contraIn(origState)) return origState;

    BoardState tmp = origState.addTransitionFrom();
    assert(tmp.getParents().size() == 1);
    assert(tmp.getParents().get(0) == origState);

    for (int y = 0; y < tmp.getHeight(); y++) {
      for (int x = 0; x < tmp.getWidth(); x++) {
        // Skip any cells that are already filled in
        if (tmp.getCellContents(x, y) != Nurikabe.CELL_UNKNOWN) continue;

        // See if any rules work if the cell is black
        tmp.setCellContents(x, y, Nurikabe.CELL_BLACK);
        for (PuzzleRule r : rules) {
          if (r.checkRule(tmp) == null) {
            tmp.setJustification(r);
            tmp.endTransition();
            Legup.setCurrentState(tmp.getChildren().get(0));
            return bruteForceSolve(Legup.getCurrentState());
          }
        }

        // See if any rules work if the cell is White
        tmp.setCellContents(x, y, Nurikabe.CELL_WHITE);
        for (PuzzleRule r : rules) {
          if (r.checkRule(tmp) == null) {
            tmp.setJustification(r);
            tmp.endTransition();
            Legup.setCurrentState(tmp.getChildren().get(0));
            return bruteForceSolve(Legup.getCurrentState());
          }
        }
        tmp.setCellContents(x, y, Nurikabe.CELL_UNKNOWN);
      }
    }
    BoardState newCurrState = tmp.getSingleParentState();
    newCurrState.getChildren().get(0).deleteState();
    return newCurrState;
  }

  private static boolean isSolved(BoardState state) {
    for (int x = 0; x < state.getWidth(); x++) {
      for (int y = 0; y < state.getHeight(); y++) {
        if (state.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN)
          return false;
      }
    }
    return true;
  }

  private static Point randomPosition(BoardState cur) {
    Random rand = new Random();
    int x = rand.nextInt(cur.getWidth());
    int y = rand.nextInt(cur.getHeight());
    while (cur.getCellContents(x, y) != Nurikabe.CELL_UNKNOWN) {
      x = rand.nextInt(cur.getWidth());
      y = rand.nextInt(cur.getHeight());
    }
    return new Point(x, y);
  }

  // Comparator used to order sets from most number of elements to least
  // number of elements
  private static class SetSizeComparator implements Comparator<Set<?>> {
    @Override
    public int compare(Set<?> o1, Set<?> o2) {
      return Integer.valueOf(o2.size()).compareTo(o1.size());
    }
  }

  private static Comparator<Point> createPointDistanceComparator(Point p)
  {
    final Point finalP = new Point(p.x, p.y);
    return new Comparator<Point>()
    {
      @Override
      public int compare(Point p0, Point p1)
      {
        double ds0 = p0.distanceSq(finalP);
        double ds1 = p1.distanceSq(finalP);
        return Double.compare(ds0, ds1);
      }
    };
  }

  private static LinkedHashSet<Point> getBorderCells(Set<Point> cells, int width, int height) {
    LinkedHashSet<Point> border = new LinkedHashSet<Point>();
    for (Point p : cells) {
      for (int delta = -1; delta < 2; delta += 2) {
        if (p.x+delta >=0 && p.x+delta < width) {
          Point xChange = new Point(p.x+delta, p.y);
          if (!cells.contains(xChange)) {
            border.add(xChange);
          }
        }
        if (p.y+delta >= 0 && p.y+delta < height) {
          Point yChange = new Point(p.x, p.y+delta);
          if (!cells.contains(yChange)) {
            border.add(yChange);
          }
        }
      }
    }
    return border;
  }

  private static Point bestCasesGuess(BoardState cur) {
    // Get all connected regions in the board
    List<Set<Point>> connectedRegions = ConnectedRegions.getConnectedRegions(Nurikabe.CELL_UNKNOWN, cur.getBoardCells(), cur.getWidth(), cur.getHeight());

    //Sort the connected Regions by size
    Collections.sort(connectedRegions, new SetSizeComparator());
    Set<Point> borderCells = getBorderCells(connectedRegions.get(0), cur.getWidth(), cur.getHeight());
    Point[] borderCellsArr = borderCells.toArray(new Point[0]);
    Point center = new Point(cur.getWidth()/2, cur.getHeight()/2);
    Arrays.sort(borderCellsArr, createPointDistanceComparator(center));
    // System.out.println(borderCellsArr[0]);
    return borderCellsArr[0];
  }

  private static BoardState applyCaseRule(BoardState cur, Point p, LinkedList<BoardState> splitStates) {
    BoardState tmp = null;
    for (int c = Nurikabe.CELL_BLACK; c <= Nurikabe.CELL_WHITE; c++){
      tmp = cur.addTransitionFrom();
      tmp.setCaseSplitJustification(new CaseBlackOrWhite());
      tmp.setCellContents(p.x, p.y, c);
      tmp.endTransition();
    }
    Legup.setCurrentState(cur.getChildren().get(0).getChildren().get(0));
    splitStates.add(cur.getChildren().get(1).getChildren().get(0));
    return Legup.getCurrentState();
  }

  private static boolean contraIn(BoardState state) {
    for (Contradiction c : contras) {
      if (c.checkContradictionRaw(state) == null) {
        state.propagateContradiction(c);
        return true;
      }
    }
    return false;
  }

  public static void depthFirstSearch(BoardState state) {
    LinkedList<BoardState> splitStates = new LinkedList<BoardState>();
    BoardState next = bruteForceSolve(state);
    while (!isSolved(next)) {
      // if (contraIn(next)) {
      //   System.out.println("In Contra if");
      //   next = splitStates.remove();
      // }
      // next.getChildren().get(0).deleteState();



      next = applyCaseRule(next, bestCasesGuess(next), splitStates);
      next = bruteForceSolve(next);
      next = splitStates.remove();
      next = bruteForceSolve(next);
    }
  }

  public static void main(String[] args) {
    Legup.main(null);
    Legup thisLegup = Legup.getInstance();
    thisLegup.getGui().promptPuzzle();
    System.out.println("nurikabeAI main");
    // bruteForceSolve(thisLegup.getCurrentState());
    depthFirstSearch(Legup.getCurrentState());
  }
}
