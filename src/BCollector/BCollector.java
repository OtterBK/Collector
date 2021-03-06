package BCollector;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import Collect.Plant.PlantSystem;
import Collect.Quarry.QuarrySystem;

public class BCollector extends JavaPlugin implements Listener{
	//////////////////////전역  변수, 함수
	
	public static String ms = "§f[ §aBC §f] ";
	public static Plugin plugin;
	
	
	/////////////////////////////////////////////
	
	private PlantSystem plantSystem;
	private QuarrySystem quarrySystem;
	
	public void onEnable() {
		plugin = this;
		
		Bukkit.getLogger().info("■■■■■■■■■■■■■■■■■■■■■");
		Bukkit.getLogger().info("■□□□□□□■■■■■■□□□□□□■■");
		Bukkit.getLogger().info("■□■■■■■□■■■■□■■■■■■□■");
		Bukkit.getLogger().info("■□■■■■■□■■■□■■■■■■■■■");
		Bukkit.getLogger().info("■□■■■■■□■■■□■■■■■■■■■");
		Bukkit.getLogger().info("■□□□□□□■■■■□■■■■■■■■■");
		Bukkit.getLogger().info("■□■■■■■□■■■□■■■■■■■■■");
		Bukkit.getLogger().info("■□■■■■■□■■■□■■■■■■■■■");
		Bukkit.getLogger().info("■□■■■■■□■■■■□■■■■■■□■");
		Bukkit.getLogger().info("■□□□□□□■■■■■■□□□□□□■■");
		Bukkit.getLogger().info("■■■■■■■■■■■■■■■■■■■■■");
		
		Bukkit.getLogger().info("[BCollector] BCollector 플러그인 로드됨");
		
		plantSystem = new PlantSystem(); //채집 시스템
		quarrySystem = new QuarrySystem(); //채광 시스템
	}
	
	public void onDisable() {
		Bukkit.getLogger().info("[BCollector] BCollector 플러그인 언로드됨");
		plantSystem.saveAllData();
	}
	
	/*public boolean onCommand(CommandSender sender, Command command, String string, String args[]) {
		byte denyCode = 0; //0-거부 안됨, 1-콘솔입력거부, 2-권한부족
		
		if(string.equalsIgnoreCase("채집")) {
			if(!MyUtility.isInGame(sender)) denyCode = 1;
			
			
		} else if(string.equalsIgnoreCase("광물")) {
			
		}
		
		if(denyCode == 1) {
			sender.sendMessage(ms+"해당 명령어는 콘솔에서 사용할 수 없습니다.");
			return false;
		}
		
		return true;
	}*/
	
}
