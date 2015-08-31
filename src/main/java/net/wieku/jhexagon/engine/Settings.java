package net.wieku.jhexagon.engine;

import java.io.Serializable;

/**
 * @author Sebastian Krajewski on 07.04.15.
 */
public class Settings implements Serializable{

	public static Settings instance;

	public boolean vSync = false;
	public boolean fullscreen = false;
	public int msaa = 4;

	public int masterVolume = 100;
	public int effectVolume = 100;
	public int musicVolume = 100;

}
