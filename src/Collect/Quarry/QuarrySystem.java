package Collect.Quarry;

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

public class QuarrySystem extends CollectSystem{

	private List<ItemStack> pickers = new ArrayList<ItemStack>();
	private HashMap<ItemStack, PercentageMap> pickersMap = new HashMap<ItemStack, PercentageMap>();
	
	private ItemStack picker_veryLow;
	private ItemStack picker_Low;
	private ItemStack picker_Medium;
	private ItemStack picker_High;
	private ItemStack picker_veryHigh;
	
	public QuarrySystem() {
		super("채광");
		systemInit();
		
		
		picker_veryLow = new ItemStack(Material.WOOD_PICKAXE, 1);
		ItemMeta pickerMeta = picker_veryLow.getItemMeta();
		pickerMeta.setDisplayName("§f[ §7최하급 §e곡괭이 §f]");
		
		List<String> loreList = new ArrayList<String>();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6300초");
		loreList.add("");
		
		pickerMeta.setLore(loreList);
		picker_veryLow.setItemMeta(pickerMeta);
		picker_veryLow.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		picker_Low = new ItemStack(Material.STONE_PICKAXE, 1);
		pickerMeta = picker_veryLow.getItemMeta();
		pickerMeta.setDisplayName("§f[ §2하급 §e곡괭이 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6240초");
		loreList.add("");
		
		pickerMeta.setLore(loreList);
		picker_Low.setItemMeta(pickerMeta);
		picker_Low.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		picker_Medium = new ItemStack(Material.GOLD_PICKAXE, 1);
		pickerMeta = picker_Medium.getItemMeta();
		pickerMeta.setDisplayName("§f[ §1중급 §e곡괭이 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6180초");
		loreList.add("");
		
		pickerMeta.setLore(loreList);
		picker_Medium.setItemMeta(pickerMeta);
		picker_Medium.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		picker_High = new ItemStack(Material.IRON_PICKAXE, 1);
		pickerMeta = picker_High.getItemMeta();
		pickerMeta.setDisplayName("§f[ §b상급 §e곡괭이 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §6120초");
		loreList.add("");
		
		pickerMeta.setLore(loreList);
		picker_High.setItemMeta(pickerMeta);
		picker_High.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		picker_veryHigh = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		pickerMeta = picker_veryHigh.getItemMeta();
		pickerMeta.setDisplayName("§f[ §4최상급 §e곡괭이 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§7사용 시 적용되는");
		loreList.add("§7재사용 대기시간: §660초");
		loreList.add("");
		
		pickerMeta.setLore(loreList);
		picker_veryHigh.setItemMeta(pickerMeta);
		picker_veryHigh.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		pickers.add(picker_veryLow);
		pickers.add(picker_Low);
		pickers.add(picker_Medium);
		pickers.add(picker_High);
		pickers.add(picker_veryHigh);
		
		pickersMap.put(picker_veryLow, new PercentageMap(1, 10, 20));
		pickersMap.put(picker_Low, new PercentageMap(3, 12, 25));
		pickersMap.put(picker_Medium, new PercentageMap(5, 15, 30));
		pickersMap.put(picker_High, new PercentageMap(10, 30 ,35));
		pickersMap.put(picker_veryHigh, new PercentageMap(20, 35, 40));
		
	}

	@Override
	public void systemInit() {
		setSystemCommand("/채광");
		setSystemName("채광");
		setSystemMainMS("§f[ §a채광 §f] ");
		setDirPath(BCollector.plugin.getDataFolder().getPath() + "/"+getSystemName());
	}
	
	@Override
	public void loadPlayerData(UUID uuid) {
		CollectData data = new QuarryData(uuid);
		data.load();
		dataMap.put(uuid, data);
	}

	@Override
	public boolean checkCollectorItem(Player p, ItemStack item) {
		
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return false;
		String itemName = meta.getDisplayName();
		if(itemName == null) return false;
		for(ItemStack picker : pickers) {
			if(itemName.equals(picker.getItemMeta().getDisplayName())) {
				CollectData pData = dataMap.get(p.getUniqueId());
				if(pData == null) {
					sendError(p, MyErrorType.NoDataError);
					return false;
				}
				
				int lowLevel = 0;
						
				if(picker.equals(picker_Low)) {
					lowLevel = 10;
				} else if(picker.equals(picker_veryLow)) {
					lowLevel = 20;
				} else if(picker.equals(picker_Medium)) {
					lowLevel = 30;
				} else if(picker.equals(picker_veryHigh)) {
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
		return pickersMap.get(item);
	}

	@Override
	public boolean isCollectorBlock(Block block) {
		if(block.getType() == Material.DIAMOND_ORE) return true;
		else return false;
	}
	
	@Override
	public void applyCooldown(Player p, ItemStack item) {
		String itemName = item.getItemMeta().getDisplayName();
		if(itemName == null) return;
		for(int i = 0; i < pickers.size(); i++) {
			ItemStack picker = pickers.get(i);
			if(itemName.equals(picker.getItemMeta().getDisplayName())) {
				CollectData pData = dataMap.get(p.getUniqueId());
				if(pData == null) sendError(p, MyErrorType.NoDataError);
				else {
					pData.setLeftCooldown((pickers.size()-i)*60); //기본값 *60
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
				p.getInventory().addItem(pickers.get(rank-1));
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
