package com.elieder.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.elieder.main.Game;

public class UI {
	
	private int rectW = Game.WIDTH - 20;
	private int rectH = Game.HEIGHT - 20;
	private int rectX = (Game.WIDTH/2) - rectW/2;
	private int rectY = (Game.HEIGHT/2) - rectH/2;

	public void render(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		switch (Game.gameState) {		
		
		case Game.GAME_OVER:
			
			g2.setColor(new Color(20, 20, 20, 200));
			g2.fillRect(rectX, rectY, rectW, rectH);
			
			g2.setColor(Color.black);
			g2.drawRect(rectX, rectY, rectW, rectH);
			
			renderText("arial", Font.BOLD, 35, Game.gameResult, "center", rectX + rectW/2, rectY + 50, Color.white, g);
			renderText("arial", Font.BOLD, 25, "Victories: " + Game.victories, "right", rectX + 15, rectY + 100, Color.white, g);
			renderText("arial", Font.BOLD, 25, "Loses: " + Game.loses, "right", rectX + 15, rectY + 150, Color.white, g);
			renderText("arial", Font.BOLD, 25, "Draws: " + Game.draws, "right", rectX + 15, rectY + 200, Color.white, g);
			renderText("arial", Font.BOLD, 18, "<Press Enter to Restart>", "center", rectX + rectW/2, rectY + 250, Color.white, g);
			break;
		}
		
	}
	
	private void renderText(String font, int style, int size, String text, String align, int x, int y, Color color, Graphics g) {
		int textAlignment = 0;
		g.setColor(color);
		g.setFont(new Font(font, style, size));
		
		Rectangle2D stringBound = g.getFontMetrics().getStringBounds(text, g);
		
		switch (align) {
		case "center":
			textAlignment = (int) stringBound.getCenterX();
			break;
		case "right":
			textAlignment = 0;
			break;
		case "left":
			textAlignment = (int) stringBound.getWidth();
		}
		
		g.drawString(text, x - textAlignment, y);
	}
	
	
	
}
