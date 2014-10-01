/**
 *  CustomSavedRule.java
 *  @author L. McCall Saltzman
 *  
 * Purpose of this class is to be able to save proofs for puzzle rules
 * 
**/
package edu.rpi.phil.legup.subproof;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.ClassNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class CustomSavedRule implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String name = "Rule Name Default";
	public String description = "Rule Description Default";
	public ImageIcon image = null;
	//different ways the rule can be applied
	private boolean allowHorizontalReflection;
	private boolean allowVerticalReflection;
	private boolean allowRotations;

	//representation of the proof
	private int height;
	private int width;
	//values required for certain cells
	private int requiredCells[][];
	//boolean arrays are initialized as false by default
	//true represents a cell that can hold any value
	private boolean arbitraryCells[][];
	//puzzle specific data;
	protected ArrayList <Object> extraData = new ArrayList <Object>();
	
	/*
	 * Constructors etc.
	 */
	public CustomSavedRule(BoardState finalState){
		
	}
	
	
	
	
	/*
	 * Getters and Setters
	 */
	public int getHeight{
		return height;
	}
	public int getWidth{
		return width;
	}
	public int[][] getRequiredCells{
		return requiredCells;
	}
	public int[][] getArbitraryCells{
		return arbitraryCells;
	}
	public ArrayList<Object> getExtraData{
		return extraData;
	}
	public boolean getAllowHorizontalReflections{
		return allowHorizontalReflections;
	}
	public boolean getVerticalReflections{
		return allowVerticalReflections;
	}
	public boolean getAllowRotations{
		return allowRotations;
	}
	public void setHeight(int h){
		height = h;
	}
	public void setHeight(int w){
		width = w;
	}
	public void setRequiredCells(int reqCells[][]){
		requiredCells = reqCells;
	}
	public void setArbitraryCells(int arbCells[][]){
		arbitraryCells = arbCells;
	}
	public void setExtraData(ArrayList<Object> extra){
		extraData = extra;
	}
	public void setVerticalReflections(boolean vReflect){
		allowVerticalReflections = vReflect;
	}
	public void setHorizontalReflections(boolean hReflect){
		allowHorizontalReflections = hReflect;
	}
	public void setRotations(boolean rotate){
		allowRotations = rotate;
	}

}
