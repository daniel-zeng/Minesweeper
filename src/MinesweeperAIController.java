
public interface MinesweeperAIController {
	//passive data
	public int getNumX();
	public int getNumY();
	public boolean endGame();
	public boolean inBounds(int r, int c);
	
	//active data
	public int getRevealedStatus(int r, int c);
	public int getNumber(int r, int c); //under condition that isRevealed
	public int numUnflaggedNeighbors(int r, int c);
	public int numFlaggedNeighbors(int r, int c);
	
	//actions
	public void aiReveal(int r, int c);//reveals the location
	public void revealUnflaggedNeighbors(int r, int c);
	public void flag(int r, int c);
	
	public boolean firstClicked();
	public void setFirstClick(boolean firstClick);
}
