package com.elieder.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.elieder.graficos.UI;

public class Game extends Listener implements Runnable {
	
	private static final long serialVersionUID = 1L;

	public static JFrame frame;
	
	public static final int PLAYING = 1, GAME_OVER = 2;
	
	public static int gameState = PLAYING;
	
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 300;
	public static final int HEIGHT = 300;
	public static final int SCALE = 2;
	public static final int spriteSize = 70;
	
	private BufferedImage PLAYER_SPRITE, OPONENT_SPRITE;
	
	
	
	public static int draws = 0, victories = 0, loses = 0;
	public static String gameResult = "Draw!";
	
	public final int PLAYER = 1, OPONENT = -1, EMPTY = 0;
	public int CURRENT = OPONENT;
	
	public static int[][] BOARD = new int[3][3];
	
	public static int initialX, initialY;
	public static boolean firstMove = true;
	
	public UI ui;
	
	public Game() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		initFrame();
		
//		Carregar sprite do Player e Oponent
		try {
			PLAYER_SPRITE = ImageIO.read(getClass().getResource("/Player.png"));
			OPONENT_SPRITE = ImageIO.read(getClass().getResource("/Oponent.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		Inicializando objetos
		ui = new UI();
		
		resetBoard();
	
	}
	
	public static void resetBoard() {
		for (int xx = 0; xx < BOARD.length; xx ++) {
			for (int yy = 0; yy < BOARD.length; yy ++) {
				BOARD[xx][yy] = 0;
			}
		}
		
		initialX = new Random().nextInt(3);
		initialY = new Random().nextInt(3);
		
		firstMove = true;
	}
	
	public void initFrame() {
		frame = new JFrame("Tic Tac Toe");
		frame.add(this);
		
		frame.setResizable(false);
		frame.pack();					
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}
		
	public static void main(String[] args) {
	 Game game = new Game();
	 game.start();
	 
	}
	
	@Override
	public void run() {
		double fps = 60.0;
		while(isRunning) {
			update();
			render();
			
			requestFocus();
			
//			Define o FPS. 1000 milessegundos = 1 segundo. 1000 milessegundos / 60 = 60 Frames Por Segundos
			try {
				Thread.sleep((int)(1000/fps));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
		
		switch (gameState) {
		case PLAYING:
			playingUpdate();
			break;
		}
		
	}

	public void render () {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
//		Render Background
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH , HEIGHT);	
		
//		Render Board
		renderBoard(g);
				
//		Render UI
		ui.render(g);					
		
		g.dispose();
		bs.show();
	}

	private void playingUpdate() {
		if (CURRENT == PLAYER) {
			if (mousePressed) {
				mousePressed = false;
				
				if (firstMove) {
					firstMove = false;
				}
				
				mx /= 100;
				my /= 100;
				if (BOARD[mx][my] == EMPTY) {
					BOARD[mx][my] = PLAYER;
					CURRENT = OPONENT;
				}
			}
			
		} 
		else if (CURRENT == OPONENT) {
			
			if (firstMove) {
				firstMove = false;
				BOARD[initialX][initialY] = OPONENT;
				CURRENT = PLAYER;
			}			
			else oponentAI();
		}
		
		if (checkVictory() == PLAYER) {
			gameState = GAME_OVER;
			gameResult = "Player Wins!";
			victories++;
		}
		else if (checkVictory() == OPONENT ) {
			gameState = GAME_OVER;
			gameResult = "CPU Wins!";
			loses++;
		}
		else if (checkVictory() == EMPTY) {
			gameState = GAME_OVER;
			gameResult = "Draw!";
			draws++;
		}
		
	}

	private void oponentAI() {
		for (int xx = 0; xx < BOARD.length; xx ++) {
			for (int yy = 0; yy < BOARD.length; yy++) {
				if (BOARD[xx][yy] == EMPTY) {
					
					Node bestMove = getBestMove(xx, yy, 0, OPONENT);
					
					BOARD[bestMove.x][bestMove.y] = OPONENT;
					
					CURRENT = PLAYER;
					
					return;
				}
			}
		}
		
	}

	private Node getBestMove(int x, int y, int depth, int turn) {
		
//		Verifica condição de vitória durante as jogadas
		if (checkVictory() == PLAYER) {
			return new Node (x, y, 10-depth, depth); // Alterado para funcionar melhor. Original seria depth -10
		}
		else if (checkVictory() == OPONENT) {
			return new Node (x, y, 10-depth, depth); // Alterado para funcionar melhor. Original seria 10 -depth
		}
		else if (checkVictory() == EMPTY) {
			return new Node(x, y, 0, depth);
		}
		
		List<Node> nodes = new ArrayList<Node>();
		List<Node> biggestNodes = new ArrayList<Node>();
		
//		Simula as jogadas possíveis
		for (int xx = 0; xx < BOARD.length; xx ++) {
			for (int yy = 0; yy < BOARD.length; yy++) {
				if (BOARD[xx][yy] == EMPTY) {
					Node node;
					if (turn == PLAYER) {
						BOARD[xx][yy] = PLAYER;
						node = getBestMove(xx, yy, depth+1, OPONENT);
						BOARD[xx][yy] = EMPTY;
					}
					else {
						BOARD[xx][yy] = OPONENT;
						node = getBestMove(xx, yy, depth+1, PLAYER);
						BOARD[xx][yy] = EMPTY;
					}
					nodes.add(node);
				}
			}
		}
		
//		Ordenar a lista de nodes
		Node finalNode = nodes.get(0);
				
		for (int i = 0; i < nodes.size(); i ++) {
			Node n = nodes.get(i);
			if (turn == PLAYER) {
				if (n.score > finalNode.score) {
					finalNode = n;
				}
			}
			
			else {
				if (n.score > finalNode.score) { // Alterado. Original seria < finalNode.score
					finalNode = n;
				}
			}
		}
		
		for (int i = 0; i < nodes.size(); i ++) {
			Node n = nodes.get(i);
			if (n.score == finalNode.score) {
				biggestNodes.add(n);
			}
		}
		
		finalNode = biggestNodes.get(new Random().nextInt(biggestNodes.size()));
		
		return finalNode;
	}
	
	private void renderBoard(Graphics g) {
		
		for (int xx = 0; xx < BOARD.length; xx ++) {
			for (int yy = 0; yy < BOARD.length; yy++) {
				g.setColor(Color.black);
				g.drawRect(xx * 100, yy * 100, 100, 100);
				
//				renderiza Player ou Oponente
				if (BOARD[xx][yy] == PLAYER) {
					g.drawImage(PLAYER_SPRITE, (xx * 100) + 50 - spriteSize/2, (yy * 100) + 50 - spriteSize/2, spriteSize, spriteSize, null);
				} else if (BOARD[xx][yy] == OPONENT) {
					g.drawImage(OPONENT_SPRITE, (xx * 100) + 50 - spriteSize/2, (yy * 100) + 50 - spriteSize/2, spriteSize, spriteSize, null);
				}
			}
		}
	}

	private int checkVictory () {
		if (checkPlayerWin() == PLAYER) return PLAYER;
		if (checkOponentWin() == OPONENT) return OPONENT;
		if (checkDraw() == EMPTY) return EMPTY;		
		
//		Ninguém ganhou
		return -10;
	}

	private int checkDraw() {
		int curScore = 0;
		for (int xx = 0; xx < BOARD.length; xx ++) {
			for (int yy = 0; yy < BOARD.length; yy++) {
				
				if (BOARD[xx][yy] != EMPTY) {
					curScore++;
				}
			}
		}
		
		if (curScore == BOARD.length * BOARD[0].length) {
			return 0;
		}
		
		return -10;
		
}

	private int checkPlayerWin() {
//		Check Player win
		// Horizontal
		if (
			BOARD[0][0] == PLAYER &&
			BOARD[1][0] == PLAYER &&
			BOARD[2][0] == PLAYER	
			) return PLAYER;
		
		if (
			BOARD[0][1] == PLAYER &&
			BOARD[1][1] == PLAYER &&
			BOARD[2][1] == PLAYER	
			) return PLAYER;
		
		if (
			BOARD[0][2] == PLAYER &&
			BOARD[1][2] == PLAYER &&
			BOARD[2][2] == PLAYER	
			) return PLAYER;
		
		// Vertical
		if (
			BOARD[0][0] == PLAYER &&
			BOARD[0][1] == PLAYER &&
			BOARD[0][2] == PLAYER	
			) return PLAYER;
		
		if (
			BOARD[1][0] == PLAYER &&
			BOARD[1][1] == PLAYER &&
			BOARD[1][2] == PLAYER	
			) return PLAYER;
		
		if (
			BOARD[2][0] == PLAYER &&
			BOARD[2][1] == PLAYER &&
			BOARD[2][2] == PLAYER	
			) return PLAYER;
		
		// Diagonal
		if (
			BOARD[0][0] == PLAYER &&
			BOARD[1][1] == PLAYER &&
			BOARD[2][2] == PLAYER	
			) return PLAYER;
		
		if (
			BOARD[2][0] == PLAYER &&
			BOARD[1][1] == PLAYER &&
			BOARD[0][2] == PLAYER	
			) return PLAYER;
		
		return -10;
	}

	private int checkOponentWin() {
//		Check Oponent win
		// Horizontal
		if (
			BOARD[0][0] == OPONENT &&
			BOARD[1][0] == OPONENT &&
			BOARD[2][0] == OPONENT	
			) return OPONENT;
		
		if (
			BOARD[0][1] == OPONENT &&
			BOARD[1][1] == OPONENT &&
			BOARD[2][1] == OPONENT	
			) return OPONENT;
		
		if (
			BOARD[0][2] == OPONENT &&
			BOARD[1][2] == OPONENT &&
			BOARD[2][2] == OPONENT	
			) return OPONENT;
		
		// Vertical
		if (
			BOARD[0][0] == OPONENT &&
			BOARD[0][1] == OPONENT &&
			BOARD[0][2] == OPONENT	
			) return OPONENT;
		
		if (
			BOARD[1][0] == OPONENT &&
			BOARD[1][1] == OPONENT &&
			BOARD[1][2] == OPONENT	
			) return OPONENT;
		
		if (
			BOARD[2][0] == OPONENT &&
			BOARD[2][1] == OPONENT &&
			BOARD[2][2] == OPONENT	
			) return OPONENT;
		
		// Diagonal
		if (
			BOARD[0][0] == OPONENT &&
			BOARD[1][1] == OPONENT &&
			BOARD[2][2] == OPONENT	
			) return OPONENT;
		
		if (
			BOARD[2][0] == OPONENT &&
			BOARD[1][1] == OPONENT &&
			BOARD[0][2] == OPONENT	
			) return OPONENT;
		
		return -10;
	}
	
}
