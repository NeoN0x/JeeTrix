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

/**
 * Permet de g�rer le temps par �tape
 */
public class StepControl extends Thread {
	GameEngine gameEngine;
	
	/**
	 * Cr�e le timer.
	 * @param gameEngine Le GE qui sera utilis� pour mettre � jour le temps
	 */
	public StepControl(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
		
		// S'il est cr��, c'est pour �tre lanc�, autant le faire l�
		this.start();
	}
	
	
	@Override
	/**
	 * Le run, lanc� en thread.
	 */
	public void run() {
		while(true){
			try {
				Thread.sleep(gameEngine.getStepTime());
				gameEngine.stepDown();
			} catch (InterruptedException e) {
				break;
			}
			
		}
	}
}
