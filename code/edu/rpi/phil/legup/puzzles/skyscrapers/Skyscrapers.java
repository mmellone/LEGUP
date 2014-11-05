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
	
	private Vector<PuzzleRule> puzzleRules = new Vector<PuzzleRule>();
	private Vector<Contradiction> contraRules = new Vector<Contradiction>();
	private Vector<CaseRule> caseRules = new Vector<CaseRule>();
	
	public int numAcceptableStates() { return boardSize + 1; }
	
	public Skyscrapers()
	{
		name = "Skyscrapers";
	}
	
	/**
	 * Creates a BoardState from an input file.
	 *
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
	
	/**
	 * Get the next call value of all of them (so if we're editing tree tent, for example, we can
	 * change to and from trees)
	 *
	 * @param x Column coordinate of the cell
	 * @param y Row coordinate of the cell
	 * @param boardState BoardState that the cell should be looked up in
	 * @return The next cell value
	 */
	public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
	{
		int ret = boardState.getCellContents(x,y);
		ret = (ret + 1) % numAcceptableStates();
		return ret;
	}
	
	
	/**
	 * Gets the next cell value for a specified cell in a boardstate
	 *
	 * @param x Column coordinate of the cell
	 * @param y Row coordinate of the cell
	 * @param boardState BoardState that the cell should be looked up in
	 * @return The next cell value
	 */
	public int getNextCellValue(int x, int y, BoardState boardState)
	{
		int ret = boardState.getCellContents(x,y);
		ret = (ret + 1) % numAcceptableStates();
		return ret;
	}
	
	/**
	 * Gets the name of a state from its number.
	 *
	 * @param state the state number
	 * @return the name of the state
	 **/
	public String getStateName(int state)
	{
		if(state != 0)
		{
			return state + "";
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * Gets the number of a state from its name.
	 *
	 * @param state the state name.
	 * @return the state number.
	 **/
	public int getStateNumber(String state)
	{
		if(state.equals(""))
		{
			return 0;
		}
		else
		{
			return Integer.parseInt(state);
		}
	}

	public String getImageLocation(int cellValue){
		return "images/treetent/unknown.gif";
	}

	public void initBoard(BoardState state)
	{
		int[] dir =
		{
			BoardState.LABEL_LEFT,
			BoardState.LABEL_RIGHT,
			BoardState.LABEL_TOP,
			BoardState.LABEL_BOTTOM
		};

		int[] sizes =
		{
			state.getHeight(),
			state.getHeight(),
			state.getWidth(),
			state.getWidth(),
		};

		for(int x = 0; x < dir.length; ++x)
		{
			for(int c = 0; c < sizes[x]; ++c)
			{
				state.setLabel(dir[x],c, 10);
			}
		}
	}

	public BoardImage[] getAllBorderImages()
	{
		BoardImage[] s = new BoardImage[20];
		int count = 0;

		for (int x = 0; x < 20; ++x)
		{
			s[count++] = new BoardImage("images/skyscrapers/" + (x)+ ".gif",10 + x);
		}

		return s;
	}

	/**
	 * Returns a list of the Basic Rules.
	 *
	 * @return a list of the basic rules.
	 **/
	public Vector<PuzzleRule> getRules()
	{
		return puzzleRules;
	}
	
	public Vector <Contradiction> getContradictions()
	{
		return contraRules;
	}
	
	public Vector<CaseRule> getCaseRules()
	{
		return caseRules;
	}
}
