import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MinesweeperView {
	JFrame window;
	private MyDrawingPanel drawPanel;
	static final int IMG_L = 32;

	MinesweeperController control;

	int pad = 20;

	int downPad = 200;

	int numMines, numX, numY, drawL, drawH, winL, winH;

	javax.swing.Timer timer;

	JPanel config;
	JLabel gridSize;
	JLabel minesI;

	JPanel timePan;
	JLabel timeInfo;
	JLabel mineInfo;

	int infoSizeX = 150;

	JEditorPane aboutHTML = null;
	JScrollPane howPlay = null;

	MinesweeperAI ai = null;

	MinesweeperView(MinesweeperController c, MinesweeperAI a) {
		control = c;
		ai = a;

		numMines = control.getNumMines();
		numX = control.getNumX();
		numY = control.getNumY();

		drawL = numX * IMG_L;
		drawH = numY * IMG_L;

		winL = pad * 2 + drawL;
		winH = pad * 2 + drawH + downPad;

		//create GUI
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		// Create Java Window
		window = new JFrame("SimpleDraw");
		window.setBounds(100, 100, winL, winH);
		window.setResizable(false);
		window.setTitle("Minesweeper");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		drawPanel = new MyDrawingPanel(control, numY, numX);
		drawPanel.setBorder(BorderFactory.createEtchedBorder());
		drawPanel.setBounds(pad, pad, drawL, drawH);

		drawPanel.addMouseListener(new DownListen());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);

		//create menus
		JMenuBar menu = new JMenuBar();
		menu.setBounds(0, 0, winL, 20);

		JMenu gameMenu = new JMenu("Game");
		JMenu option = new JMenu("Options");
		JMenu help = new JMenu("Help");

		JMenuItem nGame = new JMenuItem("New Game");
		JMenuItem quit = new JMenuItem("Exit");

		JMenuItem editMines = new JMenuItem("Total Mines");
		JMenuItem editSize = new JMenuItem("Game Size");

		JMenuItem about = new JMenuItem("About");
		JMenuItem htp = new JMenuItem("How to Play");

		//---------------
		timePan = new JPanel();
		timePan.setBorder(BorderFactory.createTitledBorder("Info:"));

		timePan.setBounds(pad * 2, drawH + pad * 2, 150, 120);

		timeInfo = new JLabel("Time Elapsed: 0 (secs)");
		mineInfo = new JLabel("Mines Left: " + numMines);

		//---------------
		config = new JPanel();
		config.setBorder(BorderFactory.createTitledBorder("Settings:"));

		config.setBounds(pad * 4 + 150, drawH + pad * 2, 150, 120);

		gridSize = new JLabel("Grid Size: " + numX + " x " + numY);
		minesI = new JLabel("Total Mines: " + numMines);

		//help pane

		try {
			aboutHTML = new JEditorPane(new URL("file:html\\about.html"));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JEditorPane htPlay = null;
		try {
			htPlay = new JEditorPane(new URL("file:html\\howtoplay.html"));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		howPlay = new JScrollPane(htPlay);

		//action listeners
		nGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				makeNewGame();
			}
		});
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.dispose();
			}
		});
		editMines.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.initiateNewMines()) {
					//fetch data
					numMines = control.getNumMines();
					numX = control.getNumX();
					numY = control.getNumY();

					makeNewGame();
				}
			}
		});
		editSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.initiateNewSize()) {
					numMines = control.getNumMines();
					numX = control.getNumX();
					numY = control.getNumY();
					drawPanel.setRC(numY, numX);

					//redraw gui
					drawL = numX * IMG_L;
					drawH = numY * IMG_L;
					winL = Math.max(pad * 2 + drawL, pad * 6 + infoSizeX * 2);
					winH = pad * 2 + drawH + downPad;

					window.setSize(winL, winH);

					timePan.setBounds(pad * 2, drawH + pad * 2, infoSizeX, 120);

					config.setBounds(pad * 4 + 150, drawH + pad * 2, infoSizeX, 120);

					makeNewGame();
				}
			}
		});

		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, aboutHTML, "About", JOptionPane.PLAIN_MESSAGE, null);

			}
		});
		htp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(null, howPlay, "How to Play", JOptionPane.PLAIN_MESSAGE, null);
			}
		});
		window.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
//				}
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()  == KeyEvent.VK_A){
					control.usedCheat();
					ai.oneStep();
				}
				
			}
		});

		timePan.add(timeInfo);
		timePan.add(mineInfo);

		config.add(gridSize);
		config.add(minesI);

		menu.add(gameMenu);
		menu.add(option);
		menu.add(help);

		gameMenu.add(nGame);
		gameMenu.add(quit);

		option.add(editMines);
		option.add(editSize);

		help.add(about);
		help.add(htp);

		mainPanel.add(menu);
		mainPanel.add(drawPanel);
		mainPanel.add(timePan);
		mainPanel.add(config);

		window.add(mainPanel);

		window.setVisible(true);
	}

	public void makeNewGame() {
		control.initiate();

		drawPanel.setBounds(pad, pad, drawL, drawH);
		drawPanel.repaint();

		timeInfo.setText("Time Elapsed: 0 (secs)");
		mineInfo.setText("Mines Left: " + numMines);

		gridSize.setText("Grid Size: " + numX + " x " + numY);
		minesI.setText("Total Mines: " + numMines);
	}

	public void showEndGame(int kase) {
		repaint();
		String show = null;
		switch (kase) {
		case 0:
			show = "You Lost! :(";
			break;
		case 1:
			show = "You Won! :)";
			break;
		case 2:
			show = "You Won! ;) (with cheats)";
			break;

		}
		JOptionPane.showMessageDialog(null, show);
	}

	private class DownListen implements MouseListener {

		public void mouseClicked(MouseEvent mouseEvent) {
		}

		public void mouseEntered(MouseEvent mouseEvent) {
		}

		public void mousePressed(MouseEvent mouseEvent) {
		}

		public void mouseExited(MouseEvent mouseEvent) {
		}

		public void mouseReleased(MouseEvent e) {
			control.mouseRelease(e);


			drawPanel.repaint();
		}

	}

	public void setTimerText(int time) {
		timeInfo.setText("Time Elapsed: " + time + " (secs)");
	}

	public void setMinesText(int currMines) {
		mineInfo.setText("Mines Left: " + currMines);
	}

	public String showJOption(String txt) {
		return JOptionPane.showInputDialog(null, txt);
	}

	public void setAI(MinesweeperAI ai) {
		this.ai = ai;
	}
	public void repaint(){
		drawPanel.repaint();
	}
}

class MyDrawingPanel extends JPanel {
	static final int IMG_L = 32;

	int rows, cols;

	static String start = "images_minesweep\\";
	static String ext = ".gif";

	static final long serialVersionUID = 1234567890L;


	MinesweeperController control;

	public MyDrawingPanel(MinesweeperController c, int r, int co) {
		control = c;
		rows = r;
		cols = co;
	}

	public void setRC(int r, int c) {
		rows = r;
		cols = c;
	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.white);

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {

				String a = start;
				a += control.getImageString(r, c);
				a += ext;

				BufferedImage img = null;
				try {
					img = ImageIO.read(new File(a));
				} catch (IOException e) {
					e.printStackTrace();
				}
				g.drawImage(img, c * IMG_L, r * IMG_L, null);
			}
		}
	}
}
