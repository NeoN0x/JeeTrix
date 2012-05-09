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
import java.util.Vector;

import com.supinfo.jeetrix.models.Particle;
import com.supinfo.jeetrix.models.Tetrimino;
import com.supinfo.jeetrix.view.GraphicLayer;

/**
 * Petit moteur de particule créé pour l'occasion.
 */
public class ParticleEngine {
	// Vector : on a besoin de threadsafe !
	private Vector<Particle> particles = new Vector<Particle>();
	private GraphicLayer graphicLayer;
	
	public ParticleEngine() {
		
	}
	
	/**
	 * Rattache le GraphicLayer
	 * @param GraphicLayer Le graphicLayer
	 */
	public void attachGraphicLayer(GraphicLayer graphicLayer) {
		this.graphicLayer = graphicLayer;
	}

	/**
	 * Met à jour les particules
	 */
	public void update() {
		ArrayList<Particle> deleteMe = new ArrayList<Particle>();
		
		for(Particle p : particles) {
			p.x += p.vx * Game.FRAME_TIME / 1000;
			p.y += p.vy * Game.FRAME_TIME / 1000;
			p.vy += 8.5f;
			
			if(p.start + p.life < System.currentTimeMillis())
				deleteMe.add(p);
		}
		
		for(Particle p : deleteMe) {
			particles.remove(p);
		}
	}
	
	/**
	 * Dessine les particules
	 */
	public void draw() {
		synchronized (particles) {
			for(Particle p : particles) {
				p.image.draw(p.x, p.y);
			}
		}
	}

	/**
	 * Ajoute au pif une particule, en bas de la scène, vers le haut
	 */
	public void pushBlocks() {
		Particle particle = new Particle();
		particle.x = (float) (400 + (Math.random()-0.5) * 400);
		particle.y = (float) (350 + (Math.random()) * 400);
		particle.vx = (Math.random()-0.5) * 120;
		particle.vy = -500 + (Math.random()-0.5) * 100;
		particle.life = 5000;
		
		particle.image = graphicLayer.getBlockImage((int) ((Math.random()) * Tetrimino.NUMBERS_EXTENDED));
		particles.add(particle);
	}
	
	/**
	 * Ajoute une particule, à l'endroit indiqué, de la couleur indiquée
	 * @param x
	 * @param y
	 * @param color
	 */
	public void pushOneBlock(float x, float y, int color) {
		Particle particle = new Particle();
		particle.x = x;
		particle.y = y;
		particle.vx = (Math.random()-0.5) * 60;
		particle.vy = -190 + (Math.random()-0.5) * 28;
		particle.life = 3000;
		
		particle.image = graphicLayer.getBlockImage(color);
		particles.add(particle);
	}
}
