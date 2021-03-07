package Collect.Plant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import BCollector.BCollector;
import Collect.CollectData;
import Collect.CollectSystem;
import MyEnum.MyErrorType;

public class PlantSystem extends CollectSystem{

	private List<ItemStack> sickles = new ArrayList<ItemStack>();
	private HashMap<ItemStack, PercentageMap> sicklesMap = new HashMap<ItemStack, PercentageMap>();
	private ItemStack sickle_veryLow;
	private ItemStack sickle_Low;
	private ItemStack sickle_Medium;
	private ItemStack sickle_High;
	private ItemStack sickle_veryHigh;
	
	public PlantSystem() {
		super("채집");
		systemInit();
		
		sickle_veryLow = new ItemStack(Material.WOOD_HOE, 1);
		ItemMeta sickleMeta = sickle_veryLow.getItemMeta();
		sickleMeta.setDisplayName("§f[ §7최하급 §e낫 §f]");
		
		List<String> loreList = new ArrayList<String>();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6300초");
		loreList.add("");
		
		sickleMeta.setLore(loreList);
		sickle_veryLow.setItemMeta(sickleMeta);
		sickle_veryLow.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		sickle_Low = new ItemStack(Material.STONE_HOE, 1);
		sickleMeta = sickle_veryLow.getItemMeta();
		sickleMeta.setDisplayName("§f[ §2하급 §e낫 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6240초");
		loreList.add("");
		
		sickleMeta.setLore(loreList);
		sickle_Low.setItemMeta(sickleMeta);
		sickle_Low.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		sickle_Medium = new ItemStack(Material.GOLD_HOE, 1);
		sickleMeta = sickle_Medium.getItemMeta();
		sickleMeta.setDisplayName("§f[ §1중급 §e낫 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6180초");
		loreList.add("");
		
		sickleMeta.setLore(loreList);
		sickle_Medium.setItemMeta(sickleMeta);
		sickle_Medium.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		sickle_High = new ItemStack(Material.IRON_HOE, 1);
		sickleMeta = sickle_High.getItemMeta();
		sickleMeta.setDisplayName("§f[ §b상급 §e낫 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6120초");
		loreList.add("");
		
		sickleMeta.setLore(loreList);
		sickle_High.setItemMeta(sickleMeta);
		sickle_High.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		sickle_veryHigh = new ItemStack(Material.DIAMOND_HOE, 1);
		sickleMeta = sickle_veryHigh.getItemMeta();
		sickleMeta.setDisplayName("§f[ §4최상급 §e낫 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §660초");
		loreList.add("");
		
		sickleMeta.setLore(loreList);
		sickle_veryHigh.setItemMeta(sickleMeta);
		sickle_veryHigh.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		sickles.add(sickle_veryLow);
		sickles.add(sickle_Low);
		sickles.add(sickle_Medium);
		sickles.add(sickle_High);
		sickles.add(sickle_veryHigh);
		
		sicklesMap.put(sickle_veryLow, new PercentageMap(1, 10, 20));
		sicklesMap.put(sickle_Low, new PercentageMap(3, 12, 25));
		sicklesMap.put(sickle_Medium, new PercentageMap(5, 15, 30));
		sicklesMap.put(sickle_High, new PercentageMap(10, 30 ,35));
		sicklesMap.put(sickle_veryHigh, new PercentageMap(20, 35, 40));
		
	}

	@Override
	public void systemInit() {
		setSystemCommand("/채집");
		setSystemName("채집");
		setSystemMainMS("§f[ §a채집 §f] ");
		setDirPath(BCollector.plugin.getDataFolder().getPath() + "/"+getSystemName());
	}
	
	@Override
	public void loadPlayerData(UUID uuid) {
		CollectData data = new PlantData(uuid);
		data.load();
		dataMap.put(uuid, data);
	}

	@Override
	public boolean checkCollectorItem(Player p, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return false;
		String itemName = meta.getDisplayName();
		if(itemName == null) return false;
		for(ItemStack sickle : sickles) {
			if(itemName.equals(sickle.getItemMeta().getDisplayName())) {
				CollectData pData = dataMap.get(p.getUniqueId());
				if(pData == null) {
					sendError(p, MyErrorType.NoDataError);
					return false;
				}
				
				int lowLevel = 0;
						
				if(sickle.equals(sickle_Low)) {
					lowLevel = 10;
				} else if(sickle.equals(sickle_Medium)) {
					lowLevel = 20;
				} else if(sickle.equals(sickle_High)) {
					lowLevel = 30;
				} else if(sickle.equals(sickle_veryHigh)) {
					lowLevel = 50;
				}
						
				if(pData.getLevel() >= lowLevel) {
					return true;
				} else {
					p.sendMessage(mainMS + " 레벨 "+lowLevel + "부터 사용 가능합니다.");
					return false;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public PercentageMap getPercentageMap(ItemStack item) {
		return sicklesMap.get(item);
	}

	@Override
	public boolean isCollectorBlock(Block block) {
		if(block.getType() == Material.RED_ROSE) return true;
		else return false;
	}
	
	@Override
	public void applyCooldown(Player p, ItemStack item) {
		String itemName = item.getItemMeta().getDisplayName();
		if(itemName == null) return;
		for(int i = 0; i < sickles.size(); i++) {
			ItemStack sickle = sickles.get(i);
			if(itemName.equals(sickle.getItemMeta().getDisplayName())) {
				CollectData pData = dataMap.get(p.getUniqueId());
				if(pData == null) sendError(p, MyErrorType.NoDataError);
				else {
					pData.setLeftCooldown((sickles.size()-i)*60); //기본값 *60
				}
			}
		}
	}
	
	@Override
	public void giveOwnItem(Player p, String type) {
		try {
			int rank = Integer.parseInt(type);
			if(rank > 5 || rank < 1) p.sendMessage(getSystemMainMS()+getSystemCommand()+" 아이템 <1~5>");
			else {
				p.getInventory().addItem(sickles.get(rank-1));
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
			}
		}catch(NumberFormatException e) {
			p.sendMessage(getSystemMainMS()+getSystemCommand()+" 아이템 <1~5>");
		}	
	}
	
	public void saveAllData() {
		for(CollectData pData : dataMap.values()) {
			pData.save();
		}
	}
	
}
