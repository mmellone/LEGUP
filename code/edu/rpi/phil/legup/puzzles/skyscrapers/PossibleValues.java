package edu.rpi.phil.legup.puzzles.skyscrapers;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class PossibleValues extends CaseRule
{
	public PossibleValues()
	{
		setName("Possible Values");
		description = "Each cell has a value between 1 and N.";
	}
}