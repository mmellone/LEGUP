package edu.rpi.phil.legup.puzzles.nurikabe;

import java.util.Vector;
import java.util.List;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.Arrays;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.ConnectedRegions;


public class NurikabeAI {
  // Static constants for use in the private functions
  static Nurikabe nur = new Nurikabe();
  static final Vector<PuzzleRule> rules = nur.getRules();
  static final Vector<Contradiction> contras = nur.getContradictions();

  /* solves the puzzle by simply trying looping through the board
     and applying as many basic rules as possible */
  private static BoardState bruteForceSolve(BoardState origState, Point seed) {

    // Check for any contraditction
    if (contraIn(origState)) return origState;

    // Add transition to check to modify board/check for rules
    BoardState tmp = origState.addTransitionFrom();

    // initialize priority queue to hold most likely points
    PriorityQueue<Point> points =
      new PriorityQueue<Point>(tmp.getWidth()*tmp.getHeight(), createPointDistanceComparator(seed));

    // Add points to priority Queue based on distance from seed
    // (closest distance has higher priority)
    for (int x = 0; x < tmp.getWidth(); x++) {
      for (int y = 0; y < tmp.getHeight(); y++) {
        if (tmp.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN) {
          points.add(new Point(x, y));
        }
      }
    }

    // Iterate through the priority queue of points to search for basic rules
    Point pTest = points.poll();
    while (pTest != null) {
      if (checkForBasicRule(tmp, pTest)) {
        // If a basic rule application is found, update the board
        // and search for another rule
        return bruteForceSolve(Legup.getCurrentState(), pTest);
      }
      pTest = points.poll();
    }

    // If no points are found, return the last state
    BoardState newCurrState = tmp.getSingleParentState();
    newCurrState.getChildren().get(0).deleteState();
    return newCurrState;
  }

  // Comparator that sorts points by closest proximity to the given point 'p'
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

  // Check if any basic rule works correctly for the given point p
  // returns true if there is a basic rule that can be applied, false otherwise
  private static boolean checkForBasicRule(BoardState tmp, Point p) {
    int x = p.x;
    int y = p.y;

    // See if any rules work if the cell is black
    tmp.setCellContents(x, y, Nurikabe.CELL_BLACK);
    for (PuzzleRule r : rules) {
      if (r.checkRule(tmp) == null) {
        tmp.setJustification(r);
        tmp.endTransition();
        Legup.setCurrentState(tmp.getChildren().get(0));
        return true;
      }
    }

    // See if any rules work if the cell is White
    tmp.setCellContents(x, y, Nurikabe.CELL_WHITE);
    for (PuzzleRule r : rules) {
      if (r.checkRule(tmp) == null) {
        tmp.setJustification(r);
        tmp.endTransition();
        Legup.setCurrentState(tmp.getChildren().get(0));
        return true;
      }
    }
    // Set the cell back to unknown if
    tmp.setCellContents(x, y, Nurikabe.CELL_UNKNOWN);
    return false;
  }

  // Check if the board is solved
  private static boolean isSolved(BoardState state) {
    for (int x = 0; x < state.getWidth(); x++) {
      for (int y = 0; y < state.getHeight(); y++) {
        if (state.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN)
          return false;
      }
    }
    return true;
  }

  // Comparator used to order sets from most number of elements to least
  // number of elements
  private static class SetSizeComparator implements Comparator<Set<?>> {
    @Override
    public int compare(Set<?> o1, Set<?> o2) {
      return Integer.valueOf(o2.size()).compareTo(o1.size());
    }
  }

  // Returns a comparator that sorts two points by whichever one has
  // more cells around it in the given boardstate
  private static Comparator<Point> numAround(BoardState state) {
    final BoardState finalState = state.copy();
    return new Comparator<Point>() {
      @Override
      public int compare(Point p0, Point p1) {
        int p0num = 0;
        int p1num = 0;
        for (int dx = -1; dx < 2; dx++) {
          for (int dy = -1; dy < 2; dy++) {
            if (dx == 0 && dy == 0) continue;
            if ((p0.x+dx >= 0 && p0.x+dx < finalState.getWidth()) &&
                (p0.y+dy >= 0 && p0.y+dy < finalState.getHeight()) &&
                finalState.getCellContents(p0.x+dx, p0.y+dy) != Nurikabe.CELL_UNKNOWN) {
              p0num++;
            }
            if ((p1.x+dx >= 0 && p1.x+dx < finalState.getWidth()) &&
                (p1.y+dy >= 0 && p1.y+dy < finalState.getHeight()) &&
                finalState.getCellContents(p1.x+dx, p1.y+dy) != Nurikabe.CELL_UNKNOWN) {
              p1num++;
            }
          }
        }
        return Integer.compare(p1num, p0num);
      }
    };
  }

  // Gets a set of the cells that boarder a region (cells)
  private static LinkedHashSet<Point> getBorderCells(Set<Point> cells, int width, int height) {
    LinkedHashSet<Point> border = new LinkedHashSet<Point>();
    // Add any cells that are not in the region, but adjacent to it in the set 'border'
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

  // Guess the best position to place the case rule
  private static Point bestCasesGuess(BoardState cur) {
    // Get all connected regions in the board
    List<Set<Point>> connectedRegions = ConnectedRegions.getConnectedRegions(Nurikabe.CELL_UNKNOWN, cur.getBoardCells(), cur.getWidth(), cur.getHeight());

    // Sort the connected Regions by size
    Collections.sort(connectedRegions, new SetSizeComparator());

    // Get the set of all cells that border the largest region
    Set<Point> borderCells = getBorderCells(connectedRegions.get(0), cur.getWidth(), cur.getHeight());
    Point[] borderCellsArr = borderCells.toArray(new Point[0]);

    // Sort the cells by whichever one is  surrounded by the most other cells
    Arrays.sort(borderCellsArr, numAround(cur));

    //return the cell that borders the largest region and is surrounded by the most cells
    return borderCellsArr[0];
  }

  // Applies Case Rule at point p for depth first search (using a stack)
  private static BoardState applyCaseRule(BoardState cur, Point p, Stack<BoardState> splitStates) {
    BoardState tmp = null;
    for (int c = Nurikabe.CELL_BLACK; c <= Nurikabe.CELL_WHITE; c++){
      tmp = cur.addTransitionFrom();
      tmp.setCaseSplitJustification(new CaseBlackOrWhite());
      tmp.setCellContents(p.x, p.y, c);
      tmp.endTransition();
    }
    Legup.setCurrentState(cur.getChildren().get(0).getChildren().get(0));
    // Add the alternate state the beginning of the splitStates stack
    splitStates.push(cur.getChildren().get(1).getChildren().get(0));
    return Legup.getCurrentState();
  }

  // Applies Case Rule at point p for breadth first search (using a queue)
  private static BoardState applyCaseRule(BoardState cur, Point p, LinkedList<BoardState> splitStates) {
    BoardState tmp = null;
    for (int c = Nurikabe.CELL_BLACK; c <= Nurikabe.CELL_WHITE; c++){
      tmp = cur.addTransitionFrom();
      tmp.setCaseSplitJustification(new CaseBlackOrWhite());
      tmp.setCellContents(p.x, p.y, c);
      tmp.endTransition();
    }
    Legup.setCurrentState(cur.getChildren().get(0).getChildren().get(0));
    // Add the alternate state to the end of the splitStates queue
    splitStates.add(cur.getChildren().get(1).getChildren().get(0));
    return Legup.getCurrentState();
  }

  // Check for a contradiction in state, returns true if there is one, false if not
  private static boolean contraIn(BoardState state) {
    for (Contradiction c : contras) {
      if (c.checkContradictionRaw(state) == null) {
        // propagate contradiction fills in red for all previous cases
        state.propagateContradiction(c);
        return true;
      }
    }
    return false;
  }

  // Performs a depthFirstSearch for a solution given a boardstate
  public static void depthFirstSearch(BoardState state) {
    // Uses a stack to hold alternate states
    Stack<BoardState> splitStates = new Stack<BoardState>();

    //initially tries to brute force solve the state
    BoardState next = bruteForceSolve(state, new Point(0, 0));

    // Goes until board is solved or a case rule is needed, then enters
    // this while loop
    while (!isSolved(next)) {
      // Get a guess where to place a case rule and apply that case rule
      Point posGuess = bestCasesGuess(next);
      next = applyCaseRule(next, posGuess, splitStates);
      // Continue to try to solve the puzzle down the first branch
      next = bruteForceSolve(next, new Point(posGuess.x, posGuess.y));
      if (contraIn(next)) {
        // If it encounters a contradiction, find the state farthest right
        // that is non-contradictory
        do {
          next = splitStates.pop();
          Legup.setCurrentState(next);
        } while (contraIn(next));

        // Try to solve that state, then repeat
        next = bruteForceSolve(next, new Point(posGuess.x, posGuess.y));
      }
      // If a contradiction is not found, than it will repeat the case rule process
    }
  }

  // Performs a depthFirstSearch for a solution given a boardstate
  // Functions exact same way as depth-first, except it uses a queue instead
  // of a stack to hold alternate branches
  public static void breadthFirstSearch(BoardState state) {
    LinkedList<BoardState> splitStates = new LinkedList<BoardState>();
    BoardState next = bruteForceSolve(state, new Point(0, 0));
    Scanner in = new Scanner(System.in);
    while (!isSolved(next)) {
      Point posGuess = bestCasesGuess(next);
      next = applyCaseRule(next, posGuess, splitStates);
      next = bruteForceSolve(next, new Point(posGuess.x, posGuess.y));
      if (contraIn(next)) {
        do {
          next = splitStates.remove();
          Legup.setCurrentState(next);
        } while (contraIn(next));
        next = bruteForceSolve(next, new Point(posGuess.x, posGuess.y));
      }
    }
  }
}
