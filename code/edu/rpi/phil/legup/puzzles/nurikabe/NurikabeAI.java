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
  private static BoardState bruteForceSolve(BoardState origState, int seedX, int seedY) {

    int unknownCount = 0;

    // for (int y = 0; y < origState.getHeight(); y++) {
    //   for (int x = 0; x < origState.getWidth(); x++) {
    //     if (origState.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN) {
    //       unknownCount++;
    //     }
    //   }
    // }
    // if (unknownCount == 0) return origState;
    // if (contraIn(origState)) return propagateBack(origState);
    if (contraIn(origState)) return origState;

    BoardState tmp = origState.addTransitionFrom();
    assert(tmp.getParents().size() == 1);
    assert(tmp.getParents().get(0) == origState);

    for (int dy = 0; dy < tmp.getHeight(); dy++) {
      int y = (seedY + dy) % tmp.getHeight();
      for (int dx = 0; dx < tmp.getWidth(); dx++) {
        int x = (seedX + dx) % tmp.getWidth();

        // Skip any cells that are already filled in
        if (tmp.getCellContents(x, y) != Nurikabe.CELL_UNKNOWN) continue;

        // See if any rules work if the cell is black
        tmp.setCellContents(x, y, Nurikabe.CELL_BLACK);
        for (PuzzleRule r : rules) {
          if (r.checkRule(tmp) == null) {
            tmp.setJustification(r);
            tmp.endTransition();
            Legup.setCurrentState(tmp.getChildren().get(0));
            return bruteForceSolve(Legup.getCurrentState(), x, y);
          }
        }

        // See if any rules work if the cell is White
        tmp.setCellContents(x, y, Nurikabe.CELL_WHITE);
        for (PuzzleRule r : rules) {
          if (r.checkRule(tmp) == null) {
            tmp.setJustification(r);
            tmp.endTransition();
            Legup.setCurrentState(tmp.getChildren().get(0));
            return bruteForceSolve(Legup.getCurrentState(), x, y);
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
    Arrays.sort(borderCellsArr, numAround(cur));
    return borderCellsArr[0];
  }

  // Applies Case Rule for depth first search (using a stack)
  private static BoardState applyCaseRule(BoardState cur, Point p, Stack<BoardState> splitStates) {
    BoardState tmp = null;
    for (int c = Nurikabe.CELL_BLACK; c <= Nurikabe.CELL_WHITE; c++){
      tmp = cur.addTransitionFrom();
      tmp.setCaseSplitJustification(new CaseBlackOrWhite());
      tmp.setCellContents(p.x, p.y, c);
      tmp.endTransition();
    }
    Legup.setCurrentState(cur.getChildren().get(0).getChildren().get(0));
    splitStates.push(cur.getChildren().get(1).getChildren().get(0));
    return Legup.getCurrentState();
  }

  // Applies Case Rule for breadth first search (using a queue)
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

  private static BoardState propagateBack(BoardState state) {
    while (state.getCaseRuleJustification() == null) {
      state = state.getSingleParentState();
      Legup.setCurrentState(state);
      return propagateBack(state);
    }
    return state.getSingleParentState().getChildren().get(1).getChildren().get(0);
  }

  public static void depthFirstSearch(BoardState state) {
    Stack<BoardState> splitStates = new Stack<BoardState>();
    BoardState next = bruteForceSolve(state, 0, 0);
    Scanner in = new Scanner(System.in);
    while (!isSolved(next)) {
    // while (in.nextInt() == 1) {
      // if (contraIn(next)) {
      //   System.out.println("In Contra if");
      //   next = splitStates.remove();
      // }
      // next.getChildren().get(0).deleteState();

      Point posGuess = bestCasesGuess(next);
      next = applyCaseRule(next, posGuess, splitStates);
      next = bruteForceSolve(next, posGuess.x, posGuess.y);
      if (contraIn(next)) {
        do {
          next = splitStates.pop();
          Legup.setCurrentState(next);
        } while (contraIn(next));
        next = bruteForceSolve(next, posGuess.x, posGuess.y);
      }
    }
  }

  public static void breadthFirstSearch(BoardState state) {
    LinkedList<BoardState> splitStates = new LinkedList<BoardState>();
    BoardState next = bruteForceSolve(state, 0, 0);
    Scanner in = new Scanner(System.in);
    while (!isSolved(next)) {
    // while (in.nextInt() == 1) {
      // if (contraIn(next)) {
      //   System.out.println("In Contra if");
      //   next = splitStates.remove();
      // }
      // next.getChildren().get(0).deleteState();

      Point posGuess = bestCasesGuess(next);
      next = applyCaseRule(next, posGuess, splitStates);
      next = bruteForceSolve(next, posGuess.x, posGuess.y);
      if (contraIn(next)) {
        do {
          next = splitStates.remove();
          Legup.setCurrentState(next);
        } while (contraIn(next));
        next = bruteForceSolve(next, posGuess.x, posGuess.y);
      }
    }
  }

  public static void main(String[] args) {
    Legup.main(null);
    Legup thisLegup = Legup.getInstance();
    thisLegup.getGui().promptPuzzle();
    // bruteForceSolve(thisLegup.getCurrentState());
    // depthFirstSearch(Legup.getCurrentState());
    breadthFirstSearch(Legup.getCurrentState());
    System.out.println("nurikabeAI main done");
  }
}
