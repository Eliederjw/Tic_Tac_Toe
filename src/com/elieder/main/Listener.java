package com.elieder.main;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Player.Player;


public class Listener extends Canvas implements KeyListener, MouseListener, MouseMotionListener{

//	public static Point direction;
	
	private static final long serialVersionUID = 1L;

	public int mx, my;
	public boolean mousePressed = false;
	
	public Listener () {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		switch (Game.gameState) {
		case Game.PLAYING:
			mousePressed = true;
			mx = e.getX();
			my = e.getY();
			break;
			
		case Game.GAME_OVER:
			Game.gameState = Game.PLAYING;
			Game.resetBoard();
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		switch (Game.gameState) {
		case Game.GAME_OVER:
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				Game.gameState = Game.PLAYING;
				Game.resetBoard();
				
			}
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
