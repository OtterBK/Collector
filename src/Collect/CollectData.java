package Collect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import BCollector.BCollector;
import MyEnum.MyInventoryType;
import Utility.MyUtility;

public class CollectData {

	private Inventory personalBag;
	private Inventory personalMenu;
	
	private long leftCooldown = 0; //수집 후 남은 쿨타임
	private int level = 1; //레벨
	private int exp = 0; //현재 경험치
	protected int nextExp = 50; //다음 레벨이 되기 위해 모아야하는 경험치
	private int maxCooldown = 300; //현재 레벨의 쿨타임
	private int maxLevel = 5;
	
	private String dirPath = BCollector.plugin.getDataFolder().getPath() + "/playerData";
	
	private UUID uuid;
	
	private ItemStack decoItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5);
	
	private ItemStack main_statItem;
	private ItemStack main_cooldownItem;
	private ItemStack main_workbench;
	//private ItemStack main_bagItem;
	
	private Inventory workbench;
	
	private int slot_Stat = 4; //원래 4
	private int slot_Cooldown = 10; //원래 10
	private int slot_Workbench = 16;
	//private int slot_Bag = 16;

	private List<String> statLore = new ArrayList<String>();
	private List<String> cooldownLore = new ArrayList<String>();
	private List<String> workbenchLore = new ArrayList<String>();
	//private List<String> bagLore = new ArrayList<String>();
	
	private String dataSysName;
	
	private int timerId;
	
	public CollectData(UUID uuid, String  dataSysName) {
		if(uuid == null) MyUtility.printLog("UUID가 null값이므로 데이터 생성 실패");
		this.uuid = uuid;
		this.dataSysName = dataSysName;
		
		dirPath = BCollector.plugin.getDataFolder().getPath() + "/" +dataSysName +"/playerData";
		
		//UI 초기화
		personalMenu = Bukkit.getServer().createInventory(null, 27, "§0§l"+dataSysName+" 메뉴");
		
		//personalBag = Bukkit.getServer().createInventory(null, 1, "§0§l"+dataSysName+" 배낭"); //가방 생성
		
		//메인 메뉴
		main_statItem = new ItemStack(Material.BOOK_AND_QUILL, 1); //스탯 아이콘
		//main_statItem.addUnsafeEnchantment(Enchantment.LUCK, 1);
		
		main_cooldownItem = new ItemStack(Material.ANVIL, 1); //쿨타임 아이콘
		
		main_workbench = new ItemStack(Material.WORKBENCH, 1); //작업대 아이콘
		
		workbench = Bukkit.getServer().createInventory(null, 27, "§0§l"+dataSysName+" 작업대");
		
		/*main_bagItem = new ItemStack(Material.CHEST, 1);
		ItemMeta bagItemMeta = main_bagItem.getItemMeta();
		bagItemMeta.setDisplayName("§f[ §d배낭 §f]");
		
		bagLore.add("");
		bagLore.add("§7기본으로 제공되는 18칸 배낭입니다.");
		bagLore.add("");
		
		main_bagItem.setItemMeta(bagItemMeta);
		mainMenu.setItem(slot_Bag, main_bagItem);*/
		
		Timer();
		
	}
	
	public void updateGUI() {
		
		//personalBag = Bukkit.getServer().createInventory(null, 1, "§0§l"+dataSysName+" 배낭"); //가방 생성
		
		//메인 메뉴
		ItemMeta statItemMeta = main_statItem.getItemMeta();
		statItemMeta.setDisplayName("§f[ §d스탯 §f]");
		
		statLore.clear();
		statLore.add("");
		statLore.add("§7- 레벨: §6"+level+".Lv");
		statLore.add("§7- 현재 경험치: §6"+exp+".Exp");
		statLore.add("§7- 다음 레벨까지: §6"+(nextExp - exp)+".Exp");
		cooldownLore.add("");
		
		statItemMeta.setLore(statLore);
		
		
		main_statItem.setItemMeta(statItemMeta);
		personalMenu.setItem(slot_Stat, main_statItem);
		
		ItemMeta cooldownItemMeta = main_cooldownItem.getItemMeta();
		cooldownItemMeta.setDisplayName("§f[ §d재수집 현황 §f]");
		
		cooldownLore.clear();
		cooldownLore.add("");
		cooldownLore.add("§7- 수리 완료까지: §6"+leftCooldown+"초");
		cooldownLore.add("");
		
		cooldownItemMeta.setLore(cooldownLore);
		main_cooldownItem.setItemMeta(cooldownItemMeta);
		personalMenu.setItem(slot_Cooldown, main_cooldownItem);
		
		ItemMeta workbenchMeta = main_workbench.getItemMeta();
		workbenchMeta.setDisplayName("§f[ §d작업대 §f]");
		
		workbenchLore.clear();
		workbenchLore.add("");
		workbenchLore.add("§7- 소재를 사용하여 아이템을 생성합니다.");
		workbenchLore.add("");
		
		workbenchMeta.setLore(workbenchLore);
		main_workbench.setItemMeta(workbenchMeta);
		main_workbench.addUnsafeEnchantment(Enchantment.LUCK, 1);
		personalMenu.setItem(slot_Workbench, main_workbench); 
	}
	
	private void Timer() {
		timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BCollector.plugin, new Runnable() {
			public void run() {
				if(leftCooldown > 0) {
					leftCooldown--;
					ItemMeta cooldownMeta = main_cooldownItem.getItemMeta();
					cooldownLore.set(1, "§7- 수리 완료까지: §e"+leftCooldown+"초");
					cooldownMeta.setLore(cooldownLore);
					main_cooldownItem.setItemMeta(cooldownMeta);
					main_cooldownItem.removeEnchantment(Enchantment.DURABILITY);
					main_cooldownItem.setDurability(
							(short)(main_cooldownItem.getType().getMaxDurability()*(double)(maxCooldown-leftCooldown/maxCooldown)));
					personalMenu.setItem(slot_Cooldown, main_cooldownItem);
				} else {
					leftCooldown = 0;
					ItemMeta cooldownMeta = main_cooldownItem.getItemMeta();
					cooldownLore.set(1, "§7- 사용 가능");
					cooldownMeta.setLore(cooldownLore);
					main_cooldownItem.setItemMeta(cooldownMeta);
					main_cooldownItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					personalMenu.setItem(slot_Cooldown, main_cooldownItem);
				}
			}
		}, 0l, 20l);
	}
	
	public long getLeftCooldown() {
		return this.leftCooldown;
	}
	
	public int getWorkbenchSlot() {
		return this.slot_Workbench;
	}
	
	public void setLeftCooldown(long cooldown) {
		this.leftCooldown = cooldown;
	}
	
	public int getLevel() {
		return this.level;
	}	
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public int getNextExp() {
		return this.nextExp;
	}
	
	public void setNextExp(int nextExp) {
		this.nextExp = nextExp;
	}
	
	public int getMaxLevel() {
		return this.maxLevel;
	}
	
	public Inventory getInventory(MyInventoryType type) {
		switch(type) {
		case bag: return this.personalBag;
		case statusMenu: return this.personalMenu;
		case personalWorkbench: return this.workbench;
		
		
		default: return null;
		}
	}
	
	public boolean save() {
		File file = getDataFile();
		try {
			FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			
			fileConfig.set("leftCooldown", leftCooldown);
			fileConfig.set("level", level);
			fileConfig.set("exp", exp);
			fileConfig.set("nextExp", nextExp);
			
			fileConfig.save(file);
		}catch(Exception e) {
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"님의 데이터를 저장하는 중 오류 발생");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private File getDataFile() {
		try {
			File file = new File(dirPath, uuid+".pData");
			return file;
		} catch(Exception e ) {
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"님의 데이터를 불러오는 중 오류 발생");
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean load() {
		File file = getDataFile();
		if(!file.exists()) {
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"님의 데이터 생성됨");
			save(); 
		} 
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
		leftCooldown = fileConfig.getInt("leftCooldown");
		level = fileConfig.getInt("level");
		exp = fileConfig.getInt("exp");
		nextExp = fileConfig.getInt("nextExp");
		
		updateGUI();
		
		return true;
	}
	
	public void delete() {
		File file = getDataFile();
		file.delete();
	}
	
}
