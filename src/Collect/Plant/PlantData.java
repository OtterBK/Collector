package Collect.Plant;

import java.util.UUID;

import Collect.CollectData;

public class PlantData extends CollectData{

	public PlantData(UUID uuid) {
		super(uuid, "ä��");
		nextExp = 20;
		updateGUI();
	}

}
