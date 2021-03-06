package Utility;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MyUtility {

	public static boolean isInGame(CommandSender sender) {
		if(!(sender instanceof Player)) return false;
		else return true;
	}
	
	public static void printLog(String log) {
		Bukkit.getServer().getLogger().info("[로그] "+log);
	}
	
	
	public static boolean saveInventoryToFile(String path, Inventory inventory , String fileName) {
		if (inventory == null || path == null || fileName == null) return false;
		try {
			File invFile = new File(path, fileName + ".invsave");
			if (invFile.exists()) invFile.delete();
			FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

			invConfig.set("Title", inventory.getTitle());
			invConfig.set("Size", inventory.getSize());
			invConfig.set("Max stack size", inventory.getMaxStackSize());

			ItemStack[] invContents = inventory.getContents();
			for (int i = 0; i < invContents.length; i++) {
				ItemStack itemInInv = invContents[i];
				if (itemInInv != null) {
					if (itemInInv.getType() != Material.AIR) {
						invConfig.set("Slot " + i, itemInInv);
					}
				}
			}

			invConfig.save(invFile);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public static Inventory getInventoryFromFile(File file) {
		if (file == null) {
			return null;
		}
		if (!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) {
			return null;
		}
		try {
			FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
			Inventory inventory = null;
			String invTitle = invConfig.getString("Title", "Inventory");
			int invSize = invConfig.getInt("Size", 27);
			int invMaxStackSize = invConfig.getInt("Max stack size", 64);
			inventory = Bukkit.getServer().createInventory(null, invSize, ChatColor.translateAlternateColorCodes('§', invTitle));
			inventory.setMaxStackSize(invMaxStackSize);
			try {
				ItemStack[] invContents = new ItemStack[invSize];
				for (int i = 0; i < invSize; i++) {
					if (invConfig.contains("Slot " + i)) {
						invContents[i] = invConfig.getItemStack("Slot " + i);
					}
					else invContents[i] = new ItemStack(Material.AIR);
				}
				inventory.setContents(invContents);
			} catch (Exception ex) {
			}
			return inventory;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static ItemStack[][] copyRecipeArray(ItemStack itemArray[][]) {
		ItemStack copyArray[][] = new ItemStack[3][3];
		for(int i = 0; i < copyArray.length; i++) {
			for(int j = 0; j < copyArray[i].length; j++) {
				if(itemArray[i][j] != null)
					copyArray[i][j] = itemArray[i][j].clone();	
				else 
					copyArray[i][j] = null;
			}		
		}
		return copyArray;
	}
	
	//min ~ max 중 값 1개 반환
	public static int getRandom(int min, int max) {
		return (int)(Math.random() * (max - min + 1) + min);
	}
	
}
