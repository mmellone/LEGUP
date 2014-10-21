package edu.rpi.phil.legup.puzzles.skyscrapers;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JDialog;

import edu.rpi.phil.legup.AI;
import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleGeneration;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public class Skyscrapers extends PuzzleModule
{
  public static int CELL_UNKNOWN = 0; //rewritten for clarity
  private int boardSize = 0;
  
  public int numAcceptableStates() { return boardSize + 1; }
  
  public Skyscrapers()
  {
    name = "Skyscrapers";
  }
  
  /**
  * Creates a BoardState from an input file.
  * @param filename the location of the file containing the boardstate
  * @return the BoardState described by the file.
  **/
  public BoardState importPuzzle(String filename)
  {
    BoardState ret = super.importPuzzle(filename);
    if(ret != null)
    {
      boardSize = ret.getWidth();
    }
    else
    {
      boardSize = 0;
    }
    return ret;
  }
}
