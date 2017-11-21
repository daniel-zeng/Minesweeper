import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class MinesweeperAI {
	MinesweeperAIController ctrl = null;

	public MinesweeperAI(MinesweeperController c) {
		ctrl = c;
	}

	int[][] moves = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };

	static final int GUESS_MODE = 0;
	static final int FLAG_MODE = 1;
	static final int REVEAL_NEIGHBORS_MODE = 2;

	static final int HIDDEN = 0;
	static final int REVEALED = 1;
	static final int QUESTION = 2;
	static final int FLAG = 3;

	int state = 0;
	int rows, cols;
	Random random = new Random();

	int tempReveals = 0;

	public void reset(int r, int c) {
		state = 0;
		rows = r;
		cols = c;
		tempReveals = 0;
		System.out.println();
		System.out.println();
		System.out.println("NEW GAME -->    -");
	}
	public void setMode(int a) {
		state = a;
	}
	boolean consult = false;

	public void oneStep() {
		//use state machines
		if (state == GUESS_MODE) {
			System.out.print("GUESS MODE -->    ");
			if (ctrl.firstClicked()) {
				System.out.println("first click");
				
				ctrl.aiReveal(rows / 2, cols / 2);
				ctrl.setFirstClick(false);
			} else {
				ArrayList<Point> pts = new ArrayList<Point>();
				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						if (ctrl.getRevealedStatus(r, c) == HIDDEN) {
							pts.add(new Point(r, c));
						}

					}
				}
				if (pts.size() == 0) {
					System.out.println("No places left to guess");
				} else {
					int ran = random.nextInt(pts.size());
					int ro = (int) pts.get(ran).getX();
					int co = (int) pts.get(ran).getY();

					if(!consult){
						ctrl.aiReveal(ro, co);
						System.out.println("Taking random guess");
					}else{
						System.out.println("ai cannot determine, pls do it urself");
					}
				}

			}
			state = FLAG_MODE;
		} else if (state == FLAG_MODE) {
			System.out.print("FLAG MODE -->     ");
			boolean somethingFlagged = false;

			outerloop: for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					int num = ctrl.getNumber(r, c);
					if (num >= 1 && num <= 8) {
						int unflagged = ctrl.numUnflaggedNeighbors(r, c);
						int flagged = ctrl.numFlaggedNeighbors(r, c);
						if (num - flagged == unflagged && unflagged != 0) {
							for (int i = 0; i < moves.length; i++) {
								int newRo = moves[i][0] + r;
								int newCo = moves[i][1] + c;
								if (ctrl.inBounds(newRo, newCo)) {
									int revStat = ctrl.getRevealedStatus(newRo, newCo);
									if (revStat != FLAG && revStat != REVEALED) {
										ctrl.flag(newRo, newCo);
										somethingFlagged = true;
										System.out.println("flagged " + newRo + " " + newCo);
										break outerloop;
									}
								}

							}
						}
					}
				}
			}

			if (somethingFlagged) {
			} else {
				System.out.println("cant flag anything");
				state = REVEAL_NEIGHBORS_MODE;
				oneStep();
			}

		} else if (state == REVEAL_NEIGHBORS_MODE) {
			System.out.print("REVEAL MODE -->   ");
			boolean somethingRevealed = false;

			outerloop: for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					int num = ctrl.getNumber(r, c);
					//this is num of adj mines
					if (num >= 1 && num <= 8) {

						int unflagged = ctrl.numUnflaggedNeighbors(r, c);
						int flagged = ctrl.numFlaggedNeighbors(r, c);
						if (num == flagged && unflagged != 0) {
							for (int i = 0; i < moves.length; i++) {
								int newRo = moves[i][0] + r;
								int newCo = moves[i][1] + c;
								if (ctrl.inBounds(newRo, newCo)) {
									int revStat = ctrl.getRevealedStatus(newRo, newCo);

									if (revStat != FLAG && revStat != REVEALED) {
										ctrl.aiReveal(newRo, newCo);
										somethingRevealed = true;
										System.out.println("revealed " + newRo + " " + newCo);
										break outerloop;
									}
								}

							}
						}
						
					}
				}
			}

			if (somethingRevealed) {
				tempReveals++;
			} else {
				System.out.println("cant reveal nada");

				if (tempReveals != 0) {
					state = FLAG_MODE;
				} else {
					state = GUESS_MODE;
				}
				tempReveals = 0;
				oneStep();
			}
		}
	}
}
