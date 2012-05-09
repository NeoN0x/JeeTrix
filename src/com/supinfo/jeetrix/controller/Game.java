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

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

import com.supinfo.jeetrix.models.GameStatus;
import com.supinfo.jeetrix.view.GraphicLayer;
import com.supinfo.jeetrix.view.SoundManager;

public class Game extends BasicGame {
	public final static int FRAME_TIME = 20;
	private GraphicLayer graphicLayer;
	private GameEngine gameEngine;
	private ParticleEngine particleEngine;
	private Input input;
	private static boolean fullscreen = false;
	
	public Game() {
		super("JeeTrix");
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		// On récupère l'input
		input = container.getInput();
		
		// On charge les ressources du jeu
		graphicLayer = new GraphicLayer();
		
		// En récupérant l'instance on construit l'objet (et donc on charge les sons)
		SoundManager.getInstance();
		
		// On initialise le jeu
		gameEngine = new GameEngine();
		particleEngine = new ParticleEngine();
		particleEngine.attachGraphicLayer(graphicLayer);
		gameEngine.attachParticleEngine(particleEngine);
		
		graphicLayer.attachGameBoard(gameEngine.getGameBoard());
		graphicLayer.attachGameEngine(gameEngine);
	}
 
	@Override
	/**
	 * Mis à jour à chaque frame
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		particleEngine.update();
		
		// Un peu de fun
		if(gameEngine.getStatus() != GameStatus.STARTED)
			if(Math.random() < 0.04)
				particleEngine.pushBlocks();

		// On gère le fenêtrage
        if(input.isKeyDown(Input.KEY_F)) {
        	fullscreen = !fullscreen;
			((AppGameContainer) container).setDisplayMode(800, 600, fullscreen);
        }
        if(input.isKeyDown(Input.KEY_ESCAPE)) {
        	container.exit();
        }
        
		// On économise un peu de CPU ;)
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
 
	@Override
	/**
	 * Rendu de chaque frame
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		// Tout ce qui est propre au rendu graphique se trouvera ici
		graphicLayer.drawGame();
		
		particleEngine.draw();
		
		if(gameEngine.getStatus() != GameStatus.STARTED)
			graphicLayer.drawPauseLayout();
		
		if(gameEngine.getStatus() == GameStatus.ENDED) {
			graphicLayer.drawEndGame();
		}
		
		if(gameEngine.getStatus() == GameStatus.MENU) {
			graphicLayer.drawMenu();
		}
		
	}

	/**
	 * Création de la fenêtre
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game());
			app.setShowFPS(false);
			app.setMinimumLogicUpdateInterval(Game.FRAME_TIME);
			app.setMaximumLogicUpdateInterval(Game.FRAME_TIME);
			app.setDisplayMode(800, 600, fullscreen);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	/**
	 * Surcharge de AppGameContainer : on envoie les évènements au gameEngine
	 */
	public void keyPressed(int key, char c) {
		gameEngine.manageKeyboard(key);
	}
	
}