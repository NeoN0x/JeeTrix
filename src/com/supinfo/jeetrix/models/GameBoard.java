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

import java.util.ArrayList;

import com.supinfo.jeetrix.controller.ParticleEngine;
import com.supinfo.jeetrix.view.GraphicLayer;

/**
 * Le plateau de jeu, contient les données.
 */
public class GameBoard {
	public final static int CASE_FREE = -1;
	private int width;
	private int height;
	private int[][] board;
	private ParticleEngine particleEngine;
	
	/**
	 * Crée un tableau de jeu en 2 dimentions.
	 * @param width Largeur
	 * @param height Hauteur
	 */
	public GameBoard(int width, int height) {
		this.width = width;
		this.height = height;
		
		board = new int[width][height];
		
		this.reset();
	}

	/**
	 * Permet de lier le moteur de particule
	 */
	public void attachParticleEngine(ParticleEngine particleEngine) {
		this.particleEngine = particleEngine;
	}
	
	/**
	 * Permet de réinitiliaser le tableau (= de le vider)
	 */
	public void reset() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				board[i][j] = GameBoard.CASE_FREE;
			}
		}
	}

	/**
	 * Test si une coordonnée est dans le tableau
	 * @param x La coordonnée x
	 * @param y La coordonnée y
	 * @return Vrai si la case est dans le plateau de jeu
	 */
	public boolean inBound(int x, int y) {
		if(x > -1 && x < width) {
			if(y > -1 && y < height)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Test si une case est libre
	 * @param x La coordonnée x
	 * @param y La coordonnée y
	 * @return Vrai si la case est libre, Faux sinon.
	 */
	public boolean isCaseFree(int x, int y) {
		if (this.inBound(x, y)) {
			return board[x][y] != GameBoard.CASE_FREE;
		}
		
		return false;
	}
	
	/**
	 * Renvoie l'état d'une case
	 * @param x La coordonnée x
	 * @param y La coordonnée y
	 * @return GameBoard.CASE_FREE si la case est libre, la couleur sinon
	 */
	public int getCase(int x, int y) {
		if (this.inBound(x, y)) {
			return board[x][y];
		}
		
		return GameBoard.CASE_FREE;
	}
	
	/**
	 * Renvoie si le tetrimino est plaçable.
	 * @param tetrimino Le tetrimino
	 * @return True si on peut le placer, false sinon.
	 */
	public boolean isTetriminoPlaceable(Tetrimino tetrimino) {
		boolean[][] matrix =  tetrimino.getMatrix();
		int width = tetrimino.getWidth();
		int height = tetrimino.getHeight();
		int posX = tetrimino.getX();
		int posY = tetrimino.getY();
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int absoluteX = i + posX;
				int absoluteY = j + posY;
				
				if(absoluteX > -1 && absoluteX < this.width && absoluteY > -1 && absoluteY < this.height) {
					if(matrix[j][i] && board[absoluteX][absoluteY] != GameBoard.CASE_FREE)
						return false;
				} 
				else if(matrix[j][i] && (absoluteX < 0 || absoluteX > this.width - 1 || absoluteY < 0 || absoluteY > this.height - 1)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Place (= incruste) le tetrimino dans la map. Il collisionnera donc avec les prochains tetriminos.
	 * @param tetrimino Le tetrimino à placer.
	 */
	public void placeTetrimino(Tetrimino tetrimino) {
		boolean[][] matrix =  tetrimino.getMatrix();
		int width = tetrimino.getWidth();
		int height = tetrimino.getHeight();
		int posX = tetrimino.getX();
		int posY = tetrimino.getY();
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int absoluteX = i + posX;
				int absoluteY = j + posY;
				
				if(matrix[j][i])
					board[absoluteX][absoluteY] = tetrimino.getColor();
			}
		}
	}

	/**
	 * Vérifie si il y a des lignes pleines.
	 * @return Un ArrayList des lignes complètes. 
	 */
	public ArrayList<Integer> checkLines() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(board[x][y] == GameBoard.CASE_FREE)
					break;
				else if(x == width - 1)
					result.add(y);
			}
		}
		
		return result;
	}

	/**
	 * Supprime la ligne d'indice i
	 * @param i La ligne à supprimer
	 */
	public void removeLine(Integer i) {
		// On remplace ladite ligne vide par des cases... vides.
		for (int x = 0; x < width; x++) {
			if(board[x][i] != GameBoard.CASE_FREE) {
				particleEngine.pushOneBlock(GraphicLayer.GAMEBOARD_X_OFFSET + 25 * (x), 
											GraphicLayer.GAMEBOARD_Y_OFFSET + 25 * (i), board[x][i]);
				board[x][i] = GameBoard.CASE_FREE;
			}
		}
		
		int[][] newBoard = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newBoard[x][y] = board[x][y];
			}
		}
		
		for (int y = 0; y < i+1; y++) {
			for (int x = 0; x < width; x++) {
				if(y == 0)
					newBoard[x][y] = GameBoard.CASE_FREE;
				else
					newBoard[x][y] = board[x][y-1];
			}
		}	
		
		board = newBoard;
	}
}
