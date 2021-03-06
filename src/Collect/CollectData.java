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
	
	private long leftCooldown = 0; //���� �� ���� ��Ÿ��
	private int level = 1; //����
	private int exp = 0; //���� ����ġ
	protected int nextExp = 50; //���� ������ �Ǳ� ���� ��ƾ��ϴ� ����ġ
	private int maxCooldown = 300; //���� ������ ��Ÿ��
	private int maxLevel = 5;
	
	private String dirPath = BCollector.plugin.getDataFolder().getPath() + "/playerData";
	
	private UUID uuid;
	
	private ItemStack decoItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5);
	
	private ItemStack main_statItem;
	private ItemStack main_cooldownItem;
	private ItemStack main_workbench;
	//private ItemStack main_bagItem;
	
	private Inventory workbench;
	
	private int slot_Stat = 4; //���� 4
	private int slot_Cooldown = 10; //���� 10
	private int slot_Workbench = 16;
	//private int slot_Bag = 16;

	private List<String> statLore = new ArrayList<String>();
	private List<String> cooldownLore = new ArrayList<String>();
	private List<String> workbenchLore = new ArrayList<String>();
	//private List<String> bagLore = new ArrayList<String>();
	
	private String dataSysName;
	
	private int timerId;
	
	public CollectData(UUID uuid, String  dataSysName) {
		if(uuid == null) MyUtility.printLog("UUID�� null���̹Ƿ� ������ ���� ����");
		this.uuid = uuid;
		this.dataSysName = dataSysName;
		
		dirPath = BCollector.plugin.getDataFolder().getPath() + "/" +dataSysName +"/playerData";
		
		//UI �ʱ�ȭ
		personalMenu = Bukkit.getServer().createInventory(null, 27, "��0��l"+dataSysName+" �޴�");
		
		//personalBag = Bukkit.getServer().createInventory(null, 1, "��0��l"+dataSysName+" �賶"); //���� ����
		
		//���� �޴�
		main_statItem = new ItemStack(Material.BOOK_AND_QUILL, 1); //���� ������
		//main_statItem.addUnsafeEnchantment(Enchantment.LUCK, 1);
		
		main_cooldownItem = new ItemStack(Material.ANVIL, 1); //��Ÿ�� ������
		
		main_workbench = new ItemStack(Material.WORKBENCH, 1); //�۾��� ������
		
		workbench = Bukkit.getServer().createInventory(null, 27, "��0��l"+dataSysName+" �۾���");
		
		/*main_bagItem = new ItemStack(Material.CHEST, 1);
		ItemMeta bagItemMeta = main_bagItem.getItemMeta();
		bagItemMeta.setDisplayName("��f[ ��d�賶 ��f]");
		
		bagLore.add("");
		bagLore.add("��7�⺻���� �����Ǵ� 18ĭ �賶�Դϴ�.");
		bagLore.add("");
		
		main_bagItem.setItemMeta(bagItemMeta);
		mainMenu.setItem(slot_Bag, main_bagItem);*/
		
		Timer();
		
	}
	
	public void updateGUI() {
		
		//personalBag = Bukkit.getServer().createInventory(null, 1, "��0��l"+dataSysName+" �賶"); //���� ����
		
		//���� �޴�
		ItemMeta statItemMeta = main_statItem.getItemMeta();
		statItemMeta.setDisplayName("��f[ ��d���� ��f]");
		
		statLore.clear();
		statLore.add("");
		statLore.add("��7- ����: ��6"+level+".Lv");
		statLore.add("��7- ���� ����ġ: ��6"+exp+".Exp");
		statLore.add("��7- ���� ��������: ��6"+(nextExp - exp)+".Exp");
		cooldownLore.add("");
		
		statItemMeta.setLore(statLore);
		
		
		main_statItem.setItemMeta(statItemMeta);
		personalMenu.setItem(slot_Stat, main_statItem);
		
		ItemMeta cooldownItemMeta = main_cooldownItem.getItemMeta();
		cooldownItemMeta.setDisplayName("��f[ ��d����� ��Ȳ ��f]");
		
		cooldownLore.clear();
		cooldownLore.add("");
		cooldownLore.add("��7- ���� �Ϸ����: ��6"+leftCooldown+"��");
		cooldownLore.add("");
		
		cooldownItemMeta.setLore(cooldownLore);
		main_cooldownItem.setItemMeta(cooldownItemMeta);
		personalMenu.setItem(slot_Cooldown, main_cooldownItem);
		
		ItemMeta workbenchMeta = main_workbench.getItemMeta();
		workbenchMeta.setDisplayName("��f[ ��d�۾��� ��f]");
		
		workbenchLore.clear();
		workbenchLore.add("");
		workbenchLore.add("��7- ���縦 ����Ͽ� �������� �����մϴ�.");
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
					cooldownLore.set(1, "��7- ���� �Ϸ����: ��e"+leftCooldown+"��");
					cooldownMeta.setLore(cooldownLore);
					main_cooldownItem.setItemMeta(cooldownMeta);
					main_cooldownItem.removeEnchantment(Enchantment.DURABILITY);
					main_cooldownItem.setDurability(
							(short)(main_cooldownItem.getType().getMaxDurability()*(double)(maxCooldown-leftCooldown/maxCooldown)));
					personalMenu.setItem(slot_Cooldown, main_cooldownItem);
				} else {
					leftCooldown = 0;
					ItemMeta cooldownMeta = main_cooldownItem.getItemMeta();
					cooldownLore.set(1, "��7- ��� ����");
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
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"���� �����͸� �����ϴ� �� ���� �߻�");
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
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"���� �����͸� �ҷ����� �� ���� �߻�");
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean load() {
		File file = getDataFile();
		if(!file.exists()) {
			MyUtility.printLog(uuid+", "+Bukkit.getPlayer(uuid).getPlayer().getName()+"���� ������ ������");
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
