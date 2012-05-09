/*
 * JeeTrix - A nice Tetris clone, with new blocks, in Java.
 * Copyright (C) 2012 Yannick COPIN - COPIN.Yannick@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * See LICENCE.txt
 */
    
package com.supinfo.jeetrix.view;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import com.supinfo.jeetrix.controller.GameEngine;
import com.supinfo.jeetrix.models.GameBoard;
import com.supinfo.jeetrix.models.Tetrimino;

/**
 * Permet de gérer la couche graphique.
 * Chargement des images, affichage du jeu.
 */
public class GraphicLayer {
	public final static int GAMEBOARD_X_OFFSET = 94;
	public final static int GAMEBOARD_Y_OFFSET = 50;
	public final static int CACHE_TETRI_X_OFFSET = 642;
	public final static int CACHE_TETRI_Y_OFFSET = 484;
	public final static int TEXT_INFO_X_OFFSET = 557;
	public final static int TEXT_INFO_Y_OFFSET = 195;
	public static final int TEXT_VERTICAL_SPACE = 59;
	public final static int TEXT_END_X_OFFSET = 206;
	public final static int TEXT_END_Y_OFFSET = 295;
	public static final int TEXT_END_VERTICAL_SPACE = 63;
	private Image background;
	private Image whiteLayer;
	private Image gameOver;
	private Image menuBkg;
	private Image menu;
	private Image[] blocks;
	private SpriteSheet blockList;
	private GameBoard gameBoard;
	private UnicodeFont defaultFont;
	private UnicodeFont defaultFontGray;
	private GameEngine gameEngine;
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color GHOST_COLOR = new Color(64, 64, 64, 128);
	

	// correspond aux tetriminos
	private Tetrimino[] tetriminos;
	
	/**
	 * Crée le graphics manager. Et charge toutes les images
	 */
	@SuppressWarnings("unchecked")
	public GraphicLayer() throws SlickException {
		background = new Image("data/imgs/bkg.png");
		whiteLayer = new Image("data/imgs/whitelayer.png");
		gameOver = new Image("data/imgs/gameover.png");
		blockList = new SpriteSheet("data/imgs/blocks.png", 25,25); 
		menuBkg = new Image("data/imgs/menubkg.png");
		menu = new Image("data/imgs/menu.png");
		
		blocks = new Image[Tetrimino.NUMBERS_EXTENDED];
		
		// Crée la map des blocks
		for (int i = 0; i < Tetrimino.NUMBERS_EXTENDED; i++) {
			blocks[i] = blockList.getSprite(i ,0);
		}		

		defaultFontGray = new UnicodeFont("data/fonts/04B_25__.TTF", 24+12, false, false);
		defaultFontGray.addAsciiGlyphs();
		defaultFontGray.getEffects().add(new ColorEffect(java.awt.Color.decode("#afafaf")));
		defaultFontGray.loadGlyphs();
		defaultFont = new UnicodeFont("data/fonts/04B_25__.TTF", 24+12, false, false);
		defaultFont.addAsciiGlyphs();
		defaultFont.getEffects().add(new ColorEffect(java.awt.Color.white));
		defaultFont.loadGlyphs();
	}

	/**
	 * Retourne l'image d'une couleur
	 * @param nb Le numéro du bloc (= de la couleur
	 * @return
	 */
	public Image getBlockImage(int nb) {
		return blocks[nb];
	}
	
	/**
	 * Dessine le plateau de jeu
	 */
	public void drawGame() {
		background.draw();
		
		for (int i = 0; i < GameEngine.GAME_WIDTH; i++) {
			for (int j = 0; j <  GameEngine.GAME_HEIGHT; j++) {
				int cell = gameBoard.getCase(i, j);
				
				if(cell != GameBoard.CASE_FREE)
					blocks[cell].draw(94+25 * i, 50+25 * j);
			}
		}
		
		tetriminos = gameEngine.getTetriminos();

		for (int t = 0; t < 6; t++) {
			boolean[][] mat = tetriminos[t].getMatrix();
			int posX = tetriminos[t].getX();
			int posY = tetriminos[t].getY();
			
			for (int i = 0; i < tetriminos[t].getWidth(); i++) {
				for (int j = 0; j < tetriminos[t].getHeight(); j++) {
					if(mat[j][i]) {
						switch(t) {
							case 0: // Le normal
							case 1: // Le ghost
								blocks[tetriminos[t].getColor()].draw(
										GraphicLayer.GAMEBOARD_X_OFFSET + 25 * (posX + i), 
										GraphicLayer.GAMEBOARD_Y_OFFSET + 25 * (posY + j), 
										t == 0 ? GraphicLayer.GHOST_COLOR : GraphicLayer.WHITE);
								break;
								
							case 2: // le 'cache', qu'on dessine en bas à droite
								blocks[tetriminos[t].getColor()].draw(
										GraphicLayer.CACHE_TETRI_X_OFFSET + (25 * (i - (tetriminos[t].getWidth() / 2.f))), 
										GraphicLayer.CACHE_TETRI_Y_OFFSET + (25 * (j - (tetriminos[t].getHeight() / 2.f))),
										gameEngine.isTetriminoCacheable() ? GraphicLayer.WHITE : GraphicLayer.GHOST_COLOR);
								break;
								
							case 3: // les anticipations
								blocks[tetriminos[t].getColor()].draw(
										220 + 16 * (posX + (i - (tetriminos[t].getWidth() / 2.f))),
										24 + 16 * (posY + (j - (tetriminos[t].getHeight() / 2.f))), 0.64f);
								break;
								
							case 4: // les anticipations
								blocks[tetriminos[t].getColor()].draw(
										410 + 20 * (posX + (i - (tetriminos[t].getWidth() / 2.f))),
										162 + 20 * (posY + (j - (tetriminos[t].getHeight() / 2.f))), 0.8f);
								break;
								
							case 5: // les anticipations
								blocks[tetriminos[t].getColor()].draw(
										410 + 20 * (posX + (i - (tetriminos[t].getWidth() / 2.f))),
										325 + 20 * (posY + (j - (tetriminos[t].getHeight() / 2.f))), 0.8f);
								break;
						}
					}
				}
			}
		}
		
		this.write(GraphicLayer.TEXT_INFO_X_OFFSET, GraphicLayer.TEXT_INFO_Y_OFFSET, "Score : " + gameEngine.getScore());
		this.write(GraphicLayer.TEXT_INFO_X_OFFSET, GraphicLayer.TEXT_INFO_Y_OFFSET + GraphicLayer.TEXT_VERTICAL_SPACE, "Lines : " + gameEngine.getLines());
		this.write(GraphicLayer.TEXT_INFO_X_OFFSET, GraphicLayer.TEXT_INFO_Y_OFFSET + 2 *GraphicLayer.TEXT_VERTICAL_SPACE, "Level : " + gameEngine.getLevel());
	}

	/**
	 * Dessine la fin du jeu
	 */
	public void drawEndGame() {
		menuBkg.draw();
		gameOver.draw();

		this.write(GraphicLayer.TEXT_END_X_OFFSET, GraphicLayer.TEXT_END_Y_OFFSET, "Score : " + gameEngine.getScore(), 5);
		this.write(GraphicLayer.TEXT_END_X_OFFSET+270, GraphicLayer.TEXT_END_Y_OFFSET+30, "Time : " + gameEngine.getGameTime()/1000, 4);
		this.write(GraphicLayer.TEXT_END_X_OFFSET, GraphicLayer.TEXT_END_Y_OFFSET + GraphicLayer.TEXT_END_VERTICAL_SPACE, "Lines : " + gameEngine.getLines(), 3);
		this.write(GraphicLayer.TEXT_END_X_OFFSET+240, GraphicLayer.TEXT_END_Y_OFFSET + GraphicLayer.TEXT_END_VERTICAL_SPACE+30, "Press Enter", 2);
		this.write(GraphicLayer.TEXT_END_X_OFFSET, GraphicLayer.TEXT_END_Y_OFFSET + 2 * GraphicLayer.TEXT_END_VERTICAL_SPACE, "Level : " + gameEngine.getLevel(), 1);
	}
	
	/**
	 * Dessine le calque de 'pause'
	 */
	public void drawPauseLayout() {
		whiteLayer.draw(0, 0, new Color(0, 0, 0, 64));
	}

	/**
	 * Ecris du texte de façon stylisée
	 * @param x Le point de départ horitontal
	 * @param y Le point de départ vertical
	 * @param text Le texte
	 */
	public void write(int x, int y, String text) {
		defaultFont.drawString(x+1, y+1, text);
		defaultFontGray.drawString(x, y, text);
		defaultFontGray.drawString(x-1, y-1, text);
	}

	/**
	 * Ecris du texte de façon encore plus stylisée
	 * @param x Le point de départ horitontal
	 * @param y Le point de départ vertical
	 * @param text Le texte
	 * @param delta Le delta de la couleur
	 */
	public void write(int x, int y, String text, float delta) {
		double magicNumber = 155+100*Math.cos((double) System.currentTimeMillis()/600+delta);
		String hexa = Integer.toHexString((int) magicNumber / 2);
		
		defaultFont.drawString(x+1, y+1, text, Color.decode("#5500" + hexa));
		defaultFontGray.drawString(x, y, text);

		hexa = Integer.toHexString((int) magicNumber);
		
		defaultFontGray.drawString(x-1, y-1, text, Color.decode("#" +  hexa + "0000"));
	}
	
	/**
	 * Permet de lier le plateau de jeu pour l'affichage
	 * @param gameBoard Le plateau de jeu
	 */
	public void attachGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}	
	
	/**
	 * Permet de lier le gameEngine
	 * @param gameEngine
	 */
	public void attachGameEngine(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
	}

	public void drawMenu() {
		menuBkg.draw();
		menu.draw();
	}
}
