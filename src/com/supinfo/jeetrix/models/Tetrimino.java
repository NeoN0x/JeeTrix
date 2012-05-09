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
    
package com.supinfo.jeetrix.models;

import com.supinfo.jeetrix.controller.GameEngine;

/**
 * Modèle Tetrimino.
 * 
 * 0 : I
 * 1 : L symétrie
 * 2 : L
 * 3 : Carré
 * 4 : S symétrie
 * 5 : T
 * 6 : S
 */
public class Tetrimino implements Cloneable {
	private int width;
	private int height;
	private boolean[][] blocks;
	private int color;
	private int positionX;
	private int positionY;
	private GameBoard gameBoard;
	private boolean touchFloor = false;

	// Le nombre de tetriminos implémentés
	public final static int NUMBERS = 7;
	public static final int NUMBERS_EXTENDED = 18;

	/**
	 * Construit un Tetrimino
	 * @param nb Le tetrimino choisit (entre 0 et 6)
	 * @param x Position, sur x
	 * @param y Position, sur y
	 */
	public Tetrimino(int nb, int x, int y, GameBoard gameBoard) {
		switch(nb) {
			case 0:
				width = 4;
				height = 1;
				blocks = new boolean[][] {{true, true, true, true}};
				break;
			case 1:
				width = 3;
				height = 2;
				blocks = new boolean[][] {{true, false, false}, {true, true, true}};
				break;
			case 2:
				width = 3;
				height = 2;
				blocks = new boolean[][] {{false, false, true}, {true, true, true}};
				break;
			case 3:
				width = 2;
				height = 2;
				blocks = new boolean[][] {{true, true}, {true, true}};
				break;
			case 4:
				width = 3;
				height = 2;
				blocks = new boolean[][] {{false, true, true}, {true,true, false}};
				break;
			case 5:
				width = 3;
				height = 2;
				blocks = new boolean[][] {{false, true, false}, {true, true, true}};
				break;
			case 6:
				width = 3;
				height = 2;
				blocks = new boolean[][] {{true, true, false}, {false, true, true}};
				break;
			case 7:
				width = 3;
				height = 3;
				blocks = new boolean[][] {{false, true, false}, {true, true, true}, {false, true, false}};
				break;
			case 8:
				width = 3;
				height =2;
				blocks = new boolean[][] {{true, false, true}, {true, true, true}};
				break;
			case 9:
				width = 2;
				height = 3;
				blocks = new boolean[][] {{true, true}, {true, false}, {true, true}};
				break;
			case 10:
				width = 2;
				height = 2;
				blocks = new boolean[][] {{true, false}, {true, true}};
				break;
			case 11:
				width = 3;
				height = 1;
				blocks = new boolean[][] {{true, true, true}, {true, true, true}};
				break;
			case 12:
				width = 4;
				height = 2;
				blocks = new boolean[][] {{false, true, false, false}, {true, true, true, true}};
				break;
			case 13:
				width = 2;
				height = 3;
				blocks = new boolean[][] {{true, true}, {true, true}, {true, false}};
				break;
			case 14:
				width = 2;
				height = 3;
				blocks = new boolean[][] {{true, true}, {true, true}, {false, true}};
				break;
			case 15:
				width = 2;
				height = 1;
				blocks = new boolean[][] {{true, true}};
				break;
			case 16:
				width = 4;
				height = 2;
				blocks = new boolean[][] {{false, false, true, false}, {true, true, true, true}};
				break;
			case 17:
				width = 1;
				height = 1;
				blocks = new boolean[][] {{true}};
				break;
		}
		
		this.gameBoard = gameBoard;
		positionX = x;
		positionY = y;
		setColor(nb);
	}
	
	/**
	 * Tourne le bloc dans le sens trigonométrique
	 * @param level 1 = 90°, 2 = 180, etcaetera.
	 */
	public void rotate(int level) {
		while(level > 0) {
			int oldWidth = width;
			int oldHeight = height;
			width = oldHeight;
			height = oldWidth;
			
			boolean[][] newBlocks = new boolean[height][width];

			// On inverse ligne et colonne
			for(int i = 0; i < width; ++i) {
				for(int j = 0; j < height; ++j) {
					newBlocks[j][i] = blocks[i][j];
				}
			}

			blocks = newBlocks;
			newBlocks = new boolean[height][width];
			
			// Puis on fait une symétrie verticale
			for(int i = 0; i < height; ++i) {
				for(int j = 0; j < width; ++j) {
					newBlocks[height-i-1][j] = blocks[i][j];
				}
			}
			
			blocks = newBlocks;
			level--;	
		}
	}
	
	/**
	 * Tourne le bloc de 90° dans le sens trigo
	 * return Si la rotation a eue lieue
	 */
	public boolean rotate() {
		this.rotate(1);

		// Si on dépasse à droite
		if(positionX > GameEngine.GAME_WIDTH - this.width)
			this.move(-1, 0);

		// Si on peut pas tourner, on tourne de 3/4 (donc un tour complet, retour au point de départ
		if(!gameBoard.isTetriminoPlaceable(this)) {
			this.rotate(3);
			
			// On tente quand même de tourner en déplaçant d'un cran
			if(this.move(0, -1)) {
				this.rotate(1);
				if(!gameBoard.isTetriminoPlaceable(this))
					this.rotate(3);
				touchFloor = true;
			}	
			
			// On tente quand même de tourner en déplaçant d'un cran
			else if(this.move(-1, 0)) {
				this.rotate(1);
				if(!gameBoard.isTetriminoPlaceable(this))
					this.rotate(3);
			}	
			
			// On tente quand même de tourner en déplaçant d'un cran
			else if(this.move(1, 0)) {
				this.rotate(1);
				if(!gameBoard.isTetriminoPlaceable(this))
					this.rotate(3);
			}
		}
		
		if(touchFloor) {
			for (int i = 0; i < this.height; i++) {
				this.move(0, 1);
			}
		}
		return true;
	}

	/**
	 * Récupère la couleur
	 * @return la couleur, entre 0 et Tetrimino.NUMBERS-1.
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Met à jour la couleur
	 * @param la couleur, entre 0 et Tetrimino.NUMBERS-1.
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * Retourne la matrice interne
	 * @return La matrice. Comme en maths, mat[ligne][colonne]
	 */
	public boolean[][] getMatrix() {
		return blocks;
	}	
	
	/**
	 * Change la matrice interne
	 * @param La matrice. Comme en maths, mat[ligne][colonne]
	 */
	public void setMatrix(boolean[][] matrix) {
		blocks = matrix;
	}
	
	/**
	 * Déplace le tétrimino.
	 * @param horizontalOffset Déplacement relatif sur x
	 * @param verticalOffset Déplacement relatif sur y
	 * @return Si le déplacement a eu lieu
	 */
	public boolean move(int horizontalOffset, int verticalOffset) {
		positionX += horizontalOffset;
		positionY += verticalOffset;
		
		if(!gameBoard.isTetriminoPlaceable(this)) {
			positionX -= horizontalOffset;
			positionY -= verticalOffset;
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Crée le tetrimino qui représente le fantome du tetrimino courrant.
	 * @return Le tetrimino fantôme
	 */
	public Tetrimino makeGhost() {
		Tetrimino t = new Tetrimino(color, positionX, positionY, gameBoard);
		t.setWidth(width);
		t.setHeight(height);
		t.setBlocks(this.getBlocks());
		while(t.move(0, 1));
		return t;
	}

	/**
	 * Récupère la hauteur du tetrimino
	 * @return la hauteur
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Récupère la largeur du tetrimino
	 * @return la largeur
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Récupère la position horizontale sur le plateau
	 * @return la position horizontale
	 */
	public int getX() {
		return positionX;
	}

	/**
	 * Met à jour la position horizontale sur le plateau
	 * @param x Position x
	 */
	public void setX(int x) {
		positionX = x;
	}
	
	/**
	 * Récupère la position verticale sur le plateau
	 * @return la position verticale
	 */
	public int getY() {
		return positionY;
	}

	/**
	 * Met à jour la position verticale sur le plateau
	 * @param y Position y
	 */
	public void setY(int y) {
		positionY = y;
	}

	/**
	 * Récupère la matrice. (usage interne)
	 * @return La matrice.
	 */
	private boolean[][] getBlocks() {
		return blocks;
	}

	/**
	 * Set la matrice. (usage interne)
	 * @param La matrice.
	 */
	private void setBlocks(boolean[][] blocks) {
		this.blocks = blocks;
	}

	/**
	 * Set la largeur. (usage interne)
	 * @param La largeur.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Set la hauteur. (usage interne)
	 * @param La hauteur.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public Tetrimino clone() throws CloneNotSupportedException {
		return (Tetrimino) super.clone();
	}
}
