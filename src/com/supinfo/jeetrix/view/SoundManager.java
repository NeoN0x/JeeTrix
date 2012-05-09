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

import java.util.Hashtable;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Le gestionnaire de son. Gère tout ce qui est lié au son
 * Chargement, lecture.
 * @author Shade
 *
 */
public class SoundManager {
	private Hashtable<String, Sound> soundTable;
	private Hashtable<String, Music> musicTable;
	private static SoundManager instance = new SoundManager();
	
	/**
	 * Charge les sons
	 */
	private SoundManager() {
		soundTable = new Hashtable<String, Sound>();
		musicTable = new Hashtable<String, Music>();
		try {
			soundTable.put("beep", new Sound("data/sounds/beep.wav"));
			soundTable.put("beeplight", new Sound("data/sounds/beeplight.wav"));
			soundTable.put("paf", new Sound("data/sounds/paf.wav"));
			soundTable.put("line1", new Sound("data/sounds/line1.wav"));
			soundTable.put("line4", new Sound("data/sounds/line4.wav"));
			soundTable.put("applauselight", new Sound("data/sounds/applauselight.wav"));
			soundTable.put("levelup", new Sound("data/sounds/levelup.wav"));
			soundTable.put("combo1", new Sound("data/sounds/combo1.wav"));
			soundTable.put("combo2", new Sound("data/sounds/combo2.wav"));
			soundTable.put("combo3", new Sound("data/sounds/combo3.wav"));
			soundTable.put("lol", new Sound("data/sounds/lol.wav"));
			soundTable.put("holyshit", new Sound("data/sounds/holyshit.wav"));
			soundTable.put("startgame", new Sound("data/sounds/startgame.wav"));

			musicTable.put("music", new Music("data/musics/JeeTrix.ogg"));
			musicTable.put("menu", new Music("data/musics/menu.ogg"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Récupère l'instance
	 * @return L'instance
	 */
	public static SoundManager getInstance() {
		return instance;
	}

	/**
	 * Joue un son
	 * @param soundName Le nom du son
	 * @param volume Le volume
	 */
	public static void play(String soundName, float volume) {
		if(SoundManager.getInstance().soundTable.get(soundName).playing())
			SoundManager.getInstance().soundTable.get(soundName).stop();
		
		try {
			SoundManager.getInstance().soundTable.get(soundName).play(1, volume);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Joue un son
	 * @param soundName Le nom du son
	 */
	public static void play(String soundName) {
		SoundManager.play(soundName, 1);
	}

	/**
	 * Joue une musique
	 * @param soundName Le nom de la musique
	 * @param volume Le volume
	 */
	public static void playMusic(String musicName, float volume) {
		SoundManager.getInstance().musicTable.get(musicName).loop(1, 0);
		SoundManager.getInstance().musicTable.get(musicName).fade(2000, volume, false);
	}
	
	/**
	 * Joue une musique
	 * @param soundName Le nom de la musique
	 */
	public static void playMusic(String musicName) {
		SoundManager.playMusic(musicName, 1);
	}
}
