package edu.rpi.phil.legup.puzzles.rippleeffect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JDialog;

import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.AI;
import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.puzzles.rippleeffect.RippleEffect;

public class RippleEffect extends PuzzleModule {
	private int width, height;
	private int[][] regions;
	private Map<Integer, Integer> regionSizes;
	
	Vector <PuzzleRule> ruleList = new Vector <PuzzleRule>();
	Vector <Contradiction> contraList = new Vector <Contradiction>();
	Vector <CaseRule> caseList = new Vector <CaseRule>();
	
	public RippleEffect() {
		regionSizes = new LinkedHashMap<Integer, Integer>();
		width = 0;
		height = 0;
	}
	
	public Integer getRegion(int row, int col) {
		return regions[row][col];
	}
	
	public Integer getRegionSize(Integer region) {
		return regionSizes.get(region);
	}

	@Override
	public Map<String, Integer> getSelectableCells() {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        for (int i=0; i<10; i++) {
        	tmp.put(Integer.toString(i), i);
        }
        return tmp;
	}

	@Override
	public Map<String, Integer> getUnselectableCells() {
		return null;
	}

	@Override
	public Vector<PuzzleRule> getRules() {
		return ruleList;
	}

	@Override
	public Vector<Contradiction> getContradictions() {
		return contraList;
	}

	@Override
	public Vector<CaseRule> getCaseRules() {
		return caseList;
	}
	
	
	public void drawGrid(Graphics gr, Rectangle bounds, int w, int h)
	{
		Graphics2D g = (Graphics2D)gr;
				g.setColor(Color.black);

		double dx = bounds.width / (double)w;
		double dy = bounds.height / (double)h;
		Stroke thin = new BasicStroke(1);
		Stroke thick = new BasicStroke(2);
		
		for (int i=0; i<=w; i++) {
			for (int j=0; j<=h; j++) {
				// vertical line strip
				int drawX1 = bounds.x + (int)dx*j;
				int drawY1 = bounds.y + (int)dy*i;
				int drawY2 = bounds.y + (int)dy*(i+1);
				
				if (i<h) {
					if (j==0 || regions[i][j-1]!=regions[i][j]) {
						g.setStroke(thick);
					}
					else {
						g.setStroke(thin);
					}
					g.drawLine(drawX1, drawY1, drawX1, drawY2);
				}
				
				// horizontal line strip
				drawY1 = bounds.y + (int)dy*(i+1);
				int drawX2 = bounds.x + (int)dx*(j+1);
				
				if (j<w) {
					if (i==h || regions[i][j]!=regions[i+1][j]) {
						g.setStroke(thick);
					}
					else {
						g.setStroke(thin);
					}
					g.drawLine(drawX1, drawY1, drawX2, drawY1);
				}
				
			}
		}
		
		g.setStroke(thick);
		g.drawLine(bounds.x, bounds.y, bounds.x + w, bounds.y);
		g.drawLine(bounds.x + w, bounds.y, bounds.x + w, bounds.y + h);

	}
	
	public BoardState importPuzzle (String filename) {
		System.out.println(filename);
		try
		{
			//Loop through file and get size and data

			Vector<Vector<Integer>> cells = new Vector<Vector<Integer>>();

			FileReader file = new FileReader(filename);
			BufferedReader bf = new BufferedReader(file);

			String line;
			while((line = bf.readLine()) != null)
			{
				line = line.trim( );
				if(line.length( ) == 0)
					continue;
				String[] row = line.split( "," );

				width = row.length;
				Vector<Integer> rowCells = new Vector<Integer>();
				for(int i = 0; i < width; ++ i )
				{
					Integer val = Integer.parseInt(row[i]);
					
					if (regionSizes.containsKey(val)) {
						Integer count = regionSizes.get(val);
						regionSizes.put(val, count+1);
					}
					else {
						regionSizes.put(val, 1);
					}
					
					rowCells.add(val);
				}
				cells.add( rowCells );
				height++;
			}
			bf.close( );
			file.close( );

			if(cells.size( ) == 0 || (cells.size() != height && height != 0))
				return null;
			
			regions = new int[height][width];

			BoardState state = new BoardState(cells.size(), width);
			for(int y = 0; y < cells.size(); ++y)
			{
				for(int x = 0; x < cells.get( y ).size( ); ++x)
				{
					regions[y][x] = cells.get(y).get(x);
					state.setCellContents( x, y, CELL_UNKNOWN );
				}
			}


			return state;
		}
		catch(Exception e)
		{
			return null;
		}
	}

}
