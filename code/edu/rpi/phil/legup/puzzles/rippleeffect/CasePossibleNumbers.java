package edu.rpi.phil.legup.puzzles.rippleeffect;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;

public class CasePossibleNumbers extends CaseRule {
	
	public CasePossibleNumbers() {
		setName("Possible Numbers");
		description = "Each cell contains a number from 1 to n, n being the size of the region";
		image = new ImageIcon("images/rippleeffect/CasePossibleValues.png");
	}

	@Override
	protected String checkCaseRuleRaw(BoardState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageName() {
		return "images/rippleeffect/CasePossibleValues.png";
	}

}
