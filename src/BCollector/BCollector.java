package BCollector;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import Collect.Plant.PlantSystem;
import Collect.Quarry.QuarrySystem;

public class BCollector extends JavaPlugin implements Listener{
	//////////////////////����  ����, �Լ�
	
	public static String ms = "��f[ ��aBC ��f] ";
	public static Plugin plugin;
	
	
	/////////////////////////////////////////////
	
	private PlantSystem plantSystem;
	private QuarrySystem quarrySystem;
	
	public void onEnable() {
		plugin = this;
		
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		Bukkit.getLogger().info("����������������������");
		
		Bukkit.getLogger().info("[BCollector] BCollector �÷����� �ε��");
		
		plantSystem = new PlantSystem(); //ä�� �ý���
		quarrySystem = new QuarrySystem(); //ä�� �ý���
	}
	
	public void onDisable() {
		Bukkit.getLogger().info("[BCollector] BCollector �÷����� ��ε��");
		plantSystem.saveAllData();
	}
	
	/*public boolean onCommand(CommandSender sender, Command command, String string, String args[]) {
		byte denyCode = 0; //0-�ź� �ȵ�, 1-�ܼ��Է°ź�, 2-���Ѻ���
		
		if(string.equalsIgnoreCase("ä��")) {
			if(!MyUtility.isInGame(sender)) denyCode = 1;
			
			
		} else if(string.equalsIgnoreCase("����")) {
			
		}
		
		if(denyCode == 1) {
			sender.sendMessage(ms+"�ش� ��ɾ�� �ֿܼ��� ����� �� �����ϴ�.");
			return false;
		}
		
		return true;
	}*/
	
}
