import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class MinesweeperModel {

	int[][] map;
	int[][] reveal;

	//map data
	public static final int EMPTY = 0;
	public static final int MINE = 10;
	public static final int CLICKED_MINE = 11;

	//reveal data
	public static final int HIDDEN = 0;
	public static final int REVEALED = 1;
	public static final int QUESTION = 2;
	public static final int FLAG = 3;

	public int rows, cols, mines;
	
	int time = 0;

//	public int condition = 0;

	int[][] moves = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
	Random random = new Random();

	public MinesweeperModel(int row, int col, int mines) {
		if (row < 1)
			row = 1;
		if (col < 1)
			col = 1;
		int v = row * col;

		if (mines < 1)
			mines = 1;
		if (mines > v)
			mines = v;

		this.rows = row;
		this.cols = col;
		this.mines = mines;

		map = new int[row][col];
		reveal = new int[row][col];


	}

	public void reset() {
		map = new int[rows][cols];
		reveal = new int[rows][cols];
	}

	public void revealAll() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				reveal[r][c] = REVEALED;
			}
		}
	}

	public void revealOnlyMines() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (map[r][c] == MINE)
					reveal[r][c] = REVEALED;
			}
		}
	}

	public void generate(int ro, int co) {
		ArrayList<Point> pts = new ArrayList<Point>();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if(r != ro || c != co) pts.add(new Point(r, c));
			}
		}
		for (int i = 0; i < mines; i++) {
			int index = random.nextInt(pts.size());
			Point point = pts.remove(index);
			//			minesPts.add(point);
			map[(int) point.getX()][(int) point.getY()] = MINE;

		}
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (map[r][c] != MINE) {
					map[r][c] = numNeighbor(r, c);
				}
			}
		}

	}

	public boolean inBounds(int ro, int co) {
		if (ro < 0 || ro >= rows)
			return false;
		if (co < 0 || co >= cols)
			return false;
		return true;
	}

	public int numNeighbor(int ro, int co) {
		int num = 0;
		for (int i = 0; i < moves.length; i++) {
			int newRo = moves[i][0] + ro;
			int newCo = moves[i][1] + co;
			if (inBounds(newRo, newCo) && map[newRo][newCo] == MINE)
				num++;
		}
		return num;
	}

	public boolean checkWin() {
		int v = rows * cols;
		int numRevealed = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (reveal[r][c] == REVEALED)
					numRevealed++;
			}
		}
		return v - numRevealed - mines == 0;
	}

	public void reveal(int r, int c) {
		int neighbors = numNeighbor(r, c);
		reveal[r][c] = REVEALED;
		if (neighbors == 0) {

			for (int i = 0; i < moves.length; i++) {
				int newRo = moves[i][0] + r;
				int newCo = moves[i][1] + c;
				if (inBounds(newRo, newCo) && reveal[newRo][newCo] != REVEALED) {
					reveal(newRo, newCo);
				}
			}
		}

	}
	public void setMap(int r, int c, int a){
		map[r][c] = a;
	}
	
	public int getMap(int r, int c){
		return map[r][c];
	}
	public int[][] getMap() {
		return map;
	}

	public int[][] getReveal() {
		return reveal;
	}

	public void setReveal(int r, int c, int a){
		reveal[r][c] = a;
	}
	
	public int getReveal(int r, int c){
		return reveal[r][c];
	}
	
	public int getRows() {
		return rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public void setCols(int cols) {
		this.cols = cols;
	}
	
	public int getMines() {
		return mines;
	}
	
	public void setMines(int mines) {
		this.mines = mines;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int t) {
		this.time = t;
	}
	
	public void incTime() {
		time++;
	}
	

	public int[][] getMoves() {
		return moves;
	}
	
	public void setMoves(int[][] moves) {
		this.moves = moves;
	}
	
	public Random getRandom() {
		return random;
	}
	
	public void setRandom(Random random) {
		this.random = random;
	}

	public int getFlags() {
		int ret = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (reveal[r][c] == FLAG) ret++;
			}
		}
		return ret;
	}

}