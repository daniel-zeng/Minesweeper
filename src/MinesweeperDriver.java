public class MinesweeperDriver {
	public static void main(String[] args) {
		MinesweeperModel model = null;
		MinesweeperView view = null;
		MinesweeperAI ai = null;
		MinesweeperController control = new MinesweeperController(view, model, ai);
			
		
		control.makeTimer();
		control.initiate();
	}
}
