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

package com.supinfo.jeetrix.controller;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Input;

import com.supinfo.jeetrix.models.GameBoard;
import com.supinfo.jeetrix.models.GameMode;
import com.supinfo.jeetrix.models.GameStatus;
import com.supinfo.jeetrix.models.Tetrimino;
import com.supinfo.jeetrix.view.SoundManager;

/**
 * Classe Event Listener
 * Permet d'écouter tous les évènements
 */
public class GameEngine  {
	public final static int GAME_WIDTH = 10;
	public final static int GAME_HEIGHT = 20;
	private GameBoard gameBoard;
	private ParticleEngine particleEngine;
	private Random random;
	private int stepTime = 1000;
	private StepControl stepControl;
	private int score = 0;
	private int lines = 0;
	private int level = 1;
	private int combo = 0;
	private long startTime;
	private long endTime;
	private boolean tetriminoCacheable = true;
	private GameStatus gameStatus = GameStatus.MENU;
	private GameMode gameMode = GameMode.NORMAL;
	
	// 0 - Ghost
	// 1 - Actuel
	// 2 - Cache
	// 3 - Anticipe 1
	// 4 - Anticipe 2
	private Tetrimino[] tetriminos;
	
	/**
	 * Initialise le GameEngine (avec le plateau de jeu)
	 */
	public GameEngine() {
		gameBoard = new GameBoard(GameEngine.GAME_WIDTH, GameEngine.GAME_HEIGHT);
		random = new Random();
		tetriminos = new Tetrimino[6];
		this.initGame(true);
	}
	
	/**
	 * Permet de lier le moteur de particule
	 */
	public void attachParticleEngine(ParticleEngine particleEngine) {
		this.particleEngine = particleEngine;
		gameBoard.attachParticleEngine(particleEngine);
	}

	/**
	 * Permet de réininitialiser le jeu de façon normale
	 * Pas de surcharge :(
	 */
	private void initGame() {
		this.initGame(false);
	}
	
	/**
	 * Permet de réininitialiser le jeue façon passive (ou normale)
	 * @param passiveMode false pour jouer les sons adéquats
	 */
	private void initGame(boolean passiveMode) {
		for (int i = 1; i < 6; i++) {
			tetriminos[i] = this.generateOneTetrimino();
		}
		
		// Pour démarrer le jeu de façon soft
		if(passiveMode) {
			SoundManager.playMusic("menu");
			tetriminos[1].setY(-10);
		}
		else {
			SoundManager.playMusic("music", 0.6f);
			SoundManager.play("startgame", 0.8f);
			tetriminos[1].setX(4);
			this.resume();
		}
		
		gameBoard.reset();
		
		score = 0;
		lines = 0;
		level = 1;
		combo = 0;
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Met le jeu en pause
	 */
	public void pause() {
		gameStatus = GameStatus.PAUSED;
		
		this.stopStepControl();
	}
	
	/**
	 * Remet le jeu en marche
	 */
	public void resume() {
		gameStatus = GameStatus.STARTED;		
		
		this.stopStepControl();
		stepControl = new StepControl(this);
	}

	/**
	 * Permet de gérer les touches pendant le jeu
	 * @param key La touche enfoncée
	 */
	public void manageKeyboard(int key) {
		if(gameStatus == GameStatus.STARTED) {
			if(key == Input.KEY_LEFT) {
				if(tetriminos[1].move(-1, 0))
					SoundManager.play("beeplight");
			}
			if(key == Input.KEY_RIGHT) {
				if(tetriminos[1].move(1, 0))
					SoundManager.play("beeplight");
			}
			if(key == Input.KEY_UP) {
				if(tetriminos[1].rotate())
					SoundManager.play("beeplight");
			}
			if(key == Input.KEY_DOWN) {
				this.stepDown();
			}
			if(key == Input.KEY_SPACE) {
				while(tetriminos[1].move(0, 1));
				
				this.putCurrentTetrimino(true);
			}
			if(key == Input.KEY_C) {
				if(tetriminoCacheable) {
					Tetrimino tmp = tetriminos[1];
					tetriminos[1] = tetriminos[2];
					tetriminos[2] = tmp;
		
					tetriminos[1].setX(4);
					tetriminos[1].setY(0);
					tetriminos[2].setX(0);
					tetriminos[2].setY(0);
					tetriminoCacheable = false;
				}
			}
		}
		
		if(gameStatus == GameStatus.MENU) {
			if(key == Input.KEY_ENTER) {
				this.initGame();
			}

			if(key == Input.KEY_NUMPAD1 || key == Input.KEY_1) {
				this.gameMode = GameMode.NORMAL;
				this.initGame();
			}
			
			if(key == Input.KEY_NUMPAD2|| key == Input.KEY_2) {
				this.gameMode = GameMode.EXTENDED;
				this.initGame();
			}
			
			if(key == Input.KEY_NUMPAD3 || key == Input.KEY_3) {
				this.gameMode = GameMode.COMPLETE;
				this.initGame();
			}
		}

		if(key == Input.KEY_ENTER && gameStatus == GameStatus.ENDED) {
			this.gameStatus = GameStatus.MENU;
		}
		
		/* Seulement en mode développeur :)
		if(key == Input.KEY_M) {
			this.initGame();
		}
		if(key == Input.KEY_O) {
			this.pause();
		}
		if(key == Input.KEY_P) {
			this.resume();
		}
		if(key == Input.KEY_I) {
			particleEngine.pushBlocks();
		}*/
	}
	
	/**
	 * Descend le tetrimino. Si impossible, le place
	 */
	public void stepDown() {
		if(!tetriminos[1].move(0, 1))
			this.putCurrentTetrimino();
		else
			SoundManager.play("beep");
		
		this.stopStepControl();
		stepControl = new StepControl(this);
	}

	/**
	 * On s'assure que le timer est bien arrêté dans tous les cas
	 */
	private void stopStepControl() {
		// Ceci faisant on s'assure que le timer est réinitialisé
		if(stepControl != null)
			if(stepControl.isAlive())
				stepControl.interrupt();
	}

	/**
	 * Récupère le plateu de jeu (pour l'affichage)
	 * @return Le plateau de jeu
	 */
	public GameBoard getGameBoard() {
		return gameBoard;
	}
	
	/**
	 * Récupère le plateu de jeu (pour l'affichage)
	 * @return Le plateau de jeu
	 */
	public Tetrimino getCurrentTetrimino() {
		return tetriminos[1];
	}

	/**
	 * Surcharge. Parce que y a pas de paramètres par défaut en java :'(
	 */
	public void putCurrentTetrimino() {
		this.putCurrentTetrimino(false);
	}
	
	/**
	 * Ajoute le tetrimino en cours, et en recrée un.
	 * @param force S'il arrive avec 'force' pour jouer le son plus ou moins fort
	 */
	public void putCurrentTetrimino(boolean force) {
		// On simule le placment du tetri qui va venir. S'il ne peut pas être placé
		// Game Over !
		if(!gameBoard.isTetriminoPlaceable(tetriminos[1]) && gameStatus == GameStatus.STARTED) {
			gameStatus = GameStatus.ENDED;
			this.stopStepControl();
			SoundManager.playMusic("menu");
			SoundManager.play("lol", 0.4f);
			SoundManager.play("holyshit", 0.8f);
			endTime = System.currentTimeMillis();
			
			for (int i = 0; i < 200; i++) {
				particleEngine.pushBlocks();
			}
			
		}
		else if(gameStatus == GameStatus.STARTED) {
			if(force)
				SoundManager.play("paf", 0.7f);
			else
				SoundManager.play("paf", 0.15f);
			gameBoard.placeTetrimino(tetriminos[1]);
	
			// Mise en place de la file d'attente
			try {
				tetriminos[1] = tetriminos[3].clone();
				tetriminos[3] = tetriminos[4].clone();
				tetriminos[4] = tetriminos[5].clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
	
			tetriminos[1].setX(4);
			tetriminos[1].setY(0);
			
			tetriminos[5] = this.generateOneTetrimino();
			
			this.stopStepControl();
			stepControl = new StepControl(this);
			
			ArrayList<Integer> lines = gameBoard.checkLines();
			
			switch(lines.size())
			{
				case 1:
					setScore((int) (getScore() + 10 * (1+(0.2*(this.level-1)))));
					break;
				case 2:
					setScore((int) (getScore() + 30 * (1+(0.2*(this.level-1)))));
					break;
				case 3:
					setScore((int) (getScore() + 50 * (1+(0.2*(this.level-1)))));
					break;
				case 4:
					SoundManager.play("applauselight");
					setScore((int) (getScore() + 100 * (1+(0.2*(this.level-1)))));
					break;
			}
			
			for (Integer i : lines) {
				gameBoard.removeLine(i);
				this.lines++;
				
				// toutes les 10 lignes, on monte d'un niveau !
				if(this.lines % 10 == 0) {
					level++;
					this.stepTime = (int) (this.stepTime * 0.9f);
					SoundManager.play("levelup");
				}
			}
			
			// Gestion des combos
			if(lines.size() > 0)
				combo++;
			else
				combo = 0;
			
			if(combo == 1) {
				SoundManager.play("line1");
			}
			else if(combo == 2) {
				SoundManager.play("combo1"); 
				score += 20 * (1+(0.3*(this.level-1)));
			}				
			else if(combo == 3) {
				SoundManager.play("combo2");
				score += 50 * (1+(0.3*(this.level-1)));
			}			
			else if(combo > 3) {
				SoundManager.play("combo3");
				score += 100 * (1+(0.3*(this.level-1)));
			}
			
			tetriminoCacheable = true;
		}
	}

	private Tetrimino generateOneTetrimino() {
		int nb = 0;
		
		if(gameMode == GameMode.NORMAL)
			nb = random.nextInt(Tetrimino.NUMBERS);
		else if(gameMode == GameMode.EXTENDED)
			nb = random.nextInt(Tetrimino.NUMBERS_EXTENDED - Tetrimino.NUMBERS) + Tetrimino.NUMBERS;
		else if(gameMode == GameMode.COMPLETE)
			nb = random.nextInt(Tetrimino.NUMBERS_EXTENDED);
		
		return new Tetrimino(nb, 0, 0, gameBoard);
	}

	/**
	 * Renvoie le temps entre 2 étapes
	 * @return Le temps en ms.
	 */
	public int getStepTime() {
		return stepTime;
	}

	/**
	 * @return le score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score Le score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the lines
	 */
	public int getLines() {
		return lines;
	}

	/**
	 * @param lines Le nombre de ligne
	 */
	public void setLines(int lines) {
		this.lines = lines;
	}

	/**
	 * @return Le niveau
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the tetriminos
	 */
	public Tetrimino[] getTetriminos() {
		tetriminos[0] = tetriminos[1].makeGhost();
		return tetriminos;
	}

	/**
	 * @return si le tetri peut être caché
	 */
	public boolean isTetriminoCacheable() {
		return tetriminoCacheable;
	}

	/**
	 * Permet de connaître l'état du jeu
	 * @return L'état du jeu
	 */
	public GameStatus getStatus() {
		return gameStatus;
	}
	
	/**
	 * Permet de renvoyer le temps de jeu
	 * @return Le temps de jeu
	 */
	public long getGameTime() {
		return endTime - startTime;
	}

}
