import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

class MinesweeperController implements MinesweeperAIController {
	MinesweeperView view;
	MinesweeperModel model;

	int[][] moves = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	//map data
	static final int EMPTY = 0;
	static final int MINE = 10;
	static final int CLICKED_MINE = 11;

	//reveal data
	static final int HIDDEN = 0;
	static final int REVEALED = 1;
	static final int QUESTION = 2;
	static final int FLAG = 3;
	
	int numX = 15, numY = 15;
	int numMines = 10;

	javax.swing.Timer timer;

	

	boolean endGame = false;
	boolean firstClick = true;

	int flags = 0;
	int currMines = numMines;
	
	boolean cheat = false;
	
	MinesweeperAI ai = null;

	public MinesweeperController(MinesweeperView view,
			MinesweeperModel model, MinesweeperAI a) {
		a = new MinesweeperAI(this);
		ai = a;
		this.view = new MinesweeperView(this, a);
		this.model = model;

	}
	public void setGameStatus(boolean isEnded) {
		endGame = isEnded;
	}

	public void makeTimer() {
		timer = new javax.swing.Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.incTime();
				view.setTimerText(model.getTime());
			}
		});
	}

	public void initiate() {
		model = new MinesweeperModel(numY, numX, numMines);
		ai.reset(numY, numX);
		
		flags = 0;
		timer.stop();
		model.setTime(0);
		currMines = numMines;
		endGame = false;
		firstClick = true;
		cheat = false;
	}

	public boolean initiateNewMines() {
		int newMines = 0;
		boolean sucess = true;
		do {
			try {
				int minMines = 10;
				String in = view.showJOption("Enter Number of mines (min 10)");
				if (in == null) {
					return false;
				} else {
					newMines = Integer.parseInt(in);
					if (newMines < minMines)
						newMines = minMines;
					int v = numX * numY;
					newMines = Math.min(newMines, v - 1);

					numMines = newMines;

				}
				sucess = true;
				return true;
			} catch (NumberFormatException nfe) {
				sucess = false;

			}
		} while (!sucess);
		return false;
	}

	public boolean initiateNewSize() {
		int newRow = 0;
		int newCol = 0;
		int condition1 = 0;
		int condition2 = 0;

		int minSize = 5;
		do {
			try {
				String in = view.showJOption("Enter Grid Width (min 5):");
				if (in == null) {
					condition1 = 1;
					return false;
				} else {
					newCol = Math.max(Integer.parseInt(in), minSize);
					condition1 = 2;
				}
			} catch (NumberFormatException nfe) {
				condition1 = 0;

			}
		} while (condition1 == 0);

		if (condition1 == 2) {
			do {
				try {
					String in = view.showJOption("Enter Grid Height (min 5):");
					if (in == null) {
						condition2 = 1;
						return false;
					} else {
						newRow = Math.max(Integer.parseInt(in), minSize);
						condition2 = 2;
					}
				} catch (NumberFormatException nfe) {
					condition2 = 0;
				}
			} while (condition2 == 0);

			if (condition2 == 2) {
				//make changes

				numX = newCol;
				numY = newRow;

				int v = numX * numY;
				numMines = Math.min(numMines, v - 1);
			}
		}
		return true;
	}

	//used by view
	public int getNumX() {
		return numX;
	}

	public int getNumY() {
		return numY;
	}

	public int getNumMines() {
		return numMines;
	}

	public int getTime() {
		return model.getTime();
	}
	public void mouseRelease(MouseEvent e) {

		if (!endGame) {
			int r = e.getY() / 32;
			int c = e.getX() / 32;
			if (c >= numX)
				c = numX - 1;
			if (r >= numY)
				r = numY - 1;
			if (c < 0)
				c = 0;
			if (r < 0)
				r = 0;
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (firstClick) {
					model.generate(r, c);
					timer.start();
					firstClick = false;
					ai.setMode(1);
				}
				clickReveal(r, c);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				toggleBlock(r, c);

			}
		}
	}

	public void clickReveal(int r, int c) {
		int reveal = model.getReveal(r, c);
		int map = model.getMap(r, c);
		if (map == MINE) {
			model.setReveal(r, c, REVEALED);
			model.setMap(r, c, CLICKED_MINE);
			
			timer.stop();

			model.revealOnlyMines();
			endGame = true;
			view.showEndGame(0);

		} else {
			if (reveal != REVEALED) {
				model.reveal(r, c);
				flags = model.getFlags();
				currMines = numMines - flags;
				if (currMines < 0)
					currMines = 0;
				view.setMinesText(currMines);

				if (model.checkWin()) {
					timer.stop();

					model.revealAll();
					view.showEndGame(cheat ? 2 : 1);
					endGame = true;

				}
			}

		}

	}

	private void toggleBlock(int r, int c) {
		int reveal = model.getReveal(r, c);
		switch (reveal) {
		case HIDDEN:
			model.setReveal(r, c, FLAG);
			flags++;
			break;

		case FLAG:
			model.setReveal(r, c, QUESTION);
			flags--;
			if (flags < 0)
				flags = 0;
			break;
		case QUESTION:
			model.setReveal(r, c, HIDDEN);
			break;
		}
		//give this to view
		currMines = numMines - flags;
		if (currMines < 0)
			currMines = 0;
		view.setMinesText(currMines);
	}

	public String getImageString(int r, int c) {
		String ret = "";
		int reveal = model.getReveal(r, c);
		int map = model.getMap(r, c);
		switch (reveal) {
		case HIDDEN:
			ret += "blank";
			break;

		case REVEALED:
			if (reveal == EMPTY) {
				ret += "num_0";
			} else if (map == CLICKED_MINE) {
				ret += "bomb_death";
			} else if (map != MINE) {
				ret += "" + "num_" + Integer.toString(map).charAt(0);
			} else {
				ret += "bomb_revealed";
			}
			break;
		case FLAG:
			ret += "bomb_flagged";
			break;
		case QUESTION:
			ret += "bomb_question";
			break;
		}
		return ret;
	}

	//interface implementations for ai interaction
	@Override
	public boolean endGame() {
		return endGame;
	}

	@Override
	public boolean inBounds(int r, int c) {
		return model.inBounds(r, c);
	}

	@Override
	public int getRevealedStatus(int r, int c) {
		return model.getReveal(r, c);
	}
	

	@Override
	public int getNumber(int r, int c) {
		if (model.getReveal(r, c) == REVEALED) {
			return model.getMap(r, c);
		} else {
			return -1;
		}
	}

	@Override
	public int numUnflaggedNeighbors(int r, int c) {
		int unFlagged = 0;

		for (int i = 0; i < moves.length; i++) {
			int newRo = moves[i][0] + r;
			int newCo = moves[i][1] + c;
			if (inBounds(newRo, newCo) && model.getReveal(newRo, newCo) != FLAG && model.getReveal(newRo, newCo) != REVEALED) {
				unFlagged++;
			}
		}
		return unFlagged;
	}
	
	@Override
	public int numFlaggedNeighbors(int r, int c) {
		int flagged = 0;

		for (int i = 0; i < moves.length; i++) {
			int newRo = moves[i][0] + r;
			int newCo = moves[i][1] + c;
			if (inBounds(newRo, newCo) && model.getReveal(newRo, newCo) == FLAG)  {
				flagged++;
			}
		}
		return flagged;
	}

	@Override
	public void revealUnflaggedNeighbors(int r, int c) {
		for (int i = 0; i < moves.length; i++) {
			int newRo = moves[i][0] + r;
			int newCo = moves[i][1] + c;
			if (inBounds(newRo, newCo) && model.getReveal(newRo, newCo) != FLAG && model.getReveal(newRo, newCo) != REVEALED) {
				clickReveal(newRo, newCo);
			}
		}

	}

	@Override
	public void flag(int r, int c) {
		int reveal = model.getReveal(r, c);
		if(reveal == HIDDEN || reveal == QUESTION){
			model.setReveal(r, c, FLAG);
			flags++;
		}
		currMines = numMines - flags;
		if (currMines < 0)
			currMines = 0;
		view.setMinesText(currMines);
		view.repaint();
	}
	
	@Override
	public void aiReveal(int r, int c){
		if (firstClick) {
			model.generate(r, c);
			timer.start();
			firstClick = false;
		}
		clickReveal(r, c);
		view.repaint();
	}
	
	public void usedCheat(){
		cheat = true;
	}
	
	@Override
	public boolean firstClicked() {
		return firstClick;
	}
	
	@Override
	public void setFirstClick(boolean firstClick) {
		this.firstClick = firstClick;
	}

}