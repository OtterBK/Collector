package Collect;

import java.awt.Component.BaselineResizeBehavior;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import BCollector.BCollector;
import MyEnum.MyErrorType;
import MyEnum.MyInventoryType;
import Utility.MyUtility;

public abstract class CollectSystem {

	//데이터 관련
	protected HashMap<UUID, CollectData> dataMap = new HashMap<UUID, CollectData>();
	
	protected List<Inventory> matterSetter = new ArrayList<Inventory>();
	
	//클래스 개별 필드 관련
	private String systemName = "시스템명";
	private String systemCmd = "텟";
	protected String mainMS = "§f[ §a§l테스트 §f§l] ";
	
	private int slot_Stat = 4;
	private int slot_Cooldown = 10;
	private int slot_Bag = 16;

	//시스템UI 관련
	private HashMap<ItemStack, Integer> slotMap = new HashMap<ItemStack, Integer>();
	
	private Inventory systemSettingUI;
	private ItemStack settingUI_setMatter;
	private ItemStack settingUI_setCollectorItem;
	private ItemStack settingUI_setRecipe;
	
	private ItemStack deco;
	private ItemStack deco2;
	
	private Inventory selectMaterialUI;
	private List<ItemStack> materialInv = new ArrayList<ItemStack>();
	private ItemStack lowItem;
	private ItemStack mediumItem;
	private ItemStack highItem;
	private ItemStack veryHighItem;
	
	//조합 관련
	private List<Inventory> recipeUI_List = new ArrayList<Inventory>();
	private ItemStack recipeUI_add;
	private ItemStack recipeUI_next;
	private ItemStack recipeUI_prev;
	private HashMap<String, Integer> pageMap = new HashMap<String, Integer>();
	private String adminWorkbenchName;
	private String playerWorkbenchName;
	private ItemStack workbenchUI_create;
	private ItemStack workbenchUI_delete;
	private int workbenchUI_recipeSlot[][] = {{0, 1, 2},
													{ 9, 10, 11},
													{18, 19, 20}};
	private int workbenchUI_resultSlot = 16;
	private ItemStack workbenchUI_check;
	private ItemStack workbenchUI_makeIt;
	private ItemStack itemStack_air = new ItemStack(Material.AIR, 1);
	
	protected HashMap<ItemStack, MyRecipe> recipeMap= new HashMap<ItemStack, MyRecipe>();
	
	private String dirPath = BCollector.plugin.getDataFolder().getPath() + systemName;
	
	public CollectSystem(String systemName) {
		
		systemInit();
		
		Bukkit.getServer().getPluginManager().registerEvents(new CollectFormatEvent(), BCollector.plugin);	
		
		matterSetter.add(Bukkit.createInventory(null, 54, "§0§l하급 재료 목록"));
		matterSetter.add(Bukkit.createInventory(null, 54, "§0§l중급 재료 목록"));
		matterSetter.add(Bukkit.createInventory(null, 54, "§0§l상급 재료 목록"));
		matterSetter.add(Bukkit.createInventory(null, 54, "§0§l최상급 재료 목록"));
		
		systemSettingUI = Bukkit.createInventory(null, 27, "§0§l"+systemName+" 설정");
		selectMaterialUI = Bukkit.createInventory(null, 27, "§0§l"+systemName+" 소재 설정");
		for(int i = 0; i < 10; i++)
			recipeUI_List.add(Bukkit.createInventory(null, 27, "§0§l"+systemName+" 조합 설정"));
		
		adminWorkbenchName = "§0§l"+ systemName+" 관리자용 작업대";
		playerWorkbenchName = " §0§l"+ systemName+" 작업대";
			
		loadMatterInv();
		
		setSystemUI();
	}
	
	private void setSystemUI() {
		settingUI_setMatter = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = settingUI_setMatter.getItemMeta();
		meta.setDisplayName("§f[ §c소재 설정 §f]");
		
		List<String> loreList = new ArrayList<String>();
		loreList.add("");
		loreList.add("§d- 소재 아이템 목록을 설정할 수 있습니다.");
		loreList.add("");
		meta.setLore(loreList);
		settingUI_setMatter.setItemMeta(meta);
		slotMap.put(settingUI_setMatter, 2);
		systemSettingUI.setItem(slotMap.get(settingUI_setMatter), settingUI_setMatter);
		
		settingUI_setCollectorItem = new ItemStack(Material.GOLD_BLOCK, 1);
		meta = settingUI_setCollectorItem.getItemMeta();
		meta.setDisplayName("§f[ §c수집 아이템 확률 설정 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§d- 아이템별 소재 드랍 확률을 설정할 수 있습니다.");
		loreList.add("§d- 아직 미구현... 설정 필요시 말씀해주세요");
		loreList.add("");
		meta.setLore(loreList);
		settingUI_setCollectorItem.setItemMeta(meta);
		slotMap.put(settingUI_setCollectorItem, 6);
		systemSettingUI.setItem(slotMap.get(settingUI_setCollectorItem), settingUI_setCollectorItem);
		
		settingUI_setRecipe = new ItemStack(Material.WORKBENCH, 1);
		meta = settingUI_setRecipe.getItemMeta();
		meta.setDisplayName("§f[ §c조합 설정 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§d- 조합 레시피를 관리합니다.");
		loreList.add("");
		meta.setLore(loreList);
		settingUI_setRecipe.setItemMeta(meta);
		slotMap.put(settingUI_setRecipe, 22);
		systemSettingUI.setItem(slotMap.get(settingUI_setRecipe), settingUI_setRecipe);
		
		recipeUI_add = new ItemStack(Material.ANVIL, 1);
		meta = recipeUI_add.getItemMeta();
		meta.setDisplayName("§f[ §c조합법 추가 §f]");
		
		loreList.clear();
		loreList.add("");
		loreList.add("§d- 새로운 레시피를 작성합니다.");
		loreList.add("");
		meta.setLore(loreList);
		recipeUI_add.setItemMeta(meta);
		slotMap.put(recipeUI_add, 22);
		/*for(Inventory inv : recipeUI_List) {
			inv.setItem(slotMap.get(recipeUI_add), recipeUI_add);
		}*/
		
		recipeUI_prev = new ItemStack(Material.PAPER, 1);
		meta = recipeUI_prev.getItemMeta();
		meta.setDisplayName("§f[ §c이전 페이지 §f]");

		recipeUI_prev.setItemMeta(meta);
		slotMap.put(recipeUI_prev, 18);
		/*for(Inventory inv : recipeUI_List) {
			inv.setItem(slotMap.get(recipeUI_prev), recipeUI_prev);
		}
		recipeUI_List.get(0).setItem(slotMap.get(recipeUI_prev), null); //첫 페이지 이전버튼 삭제*/
		
		recipeUI_next = new ItemStack(Material.PAPER, 1);
		meta = recipeUI_next.getItemMeta();
		meta.setDisplayName("§f[ §c다음 페이지 §f]");

		recipeUI_next.setItemMeta(meta);
		slotMap.put(recipeUI_next, 26);
		/*for(Inventory inv : recipeUI_List) {
			inv.setItem(slotMap.get(recipeUI_next), recipeUI_next);
		}	
		
		recipeUI_List.get(recipeUI_List.size()-1).setItem(slotMap.get(recipeUI_next), null); //마지막 페이지 다음버튼 삭제*/
		
		updateRecipeUI();
		
		workbenchUI_create = new ItemStack(Material.BREWING_STAND_ITEM, 1);
		meta = workbenchUI_create.getItemMeta();
		meta.setDisplayName("§f[ §e조합법 저장 §f]");
		workbenchUI_create.setItemMeta(meta);
		slotMap.put(workbenchUI_create, 22);
		
		workbenchUI_delete = new ItemStack(Material.BARRIER, 1);
		meta = workbenchUI_delete.getItemMeta();
		meta.setDisplayName("§f[ §e조합법 삭제 §f]");
		workbenchUI_delete.setItemMeta(meta);
		slotMap.put(workbenchUI_delete, 4);
		
		deco = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)12);
		meta = deco.getItemMeta();
		meta.setDisplayName("§f[ §e작업창 §f]");
		deco.setItemMeta(meta);
		deco2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15);
		meta = deco2.getItemMeta();
		meta.setDisplayName("§f[ §e작업창 §f]");
		deco2.setItemMeta(meta);
		
		workbenchUI_check = new ItemStack(Material.ENCHANTED_BOOK, 1);
		meta = workbenchUI_check.getItemMeta();
		meta.setDisplayName("§f[ §e조합 시도 §f]");
		workbenchUI_check.setItemMeta(meta);
		slotMap.put(workbenchUI_check, 4);
		
		workbenchUI_makeIt = new ItemStack(Material.ANVIL, 1);
		meta = workbenchUI_makeIt.getItemMeta();
		meta.setDisplayName("§f[ §e생성 §f]");
		workbenchUI_makeIt.setItemMeta(meta);
		workbenchUI_makeIt.addUnsafeEnchantment(Enchantment.LUCK, 1);
		slotMap.put(workbenchUI_makeIt, 22);
		
		lowItem = new ItemStack(Material.GOLD_INGOT, 1);
		meta = lowItem.getItemMeta();
		meta.setDisplayName("§f[ §2하급 §6소재 §f]");
		lowItem.setItemMeta(meta);
		slotMap.put(lowItem, 1);
		selectMaterialUI.setItem(slotMap.get(lowItem), lowItem);
		
		mediumItem = new ItemStack(Material.IRON_INGOT, 1);
		meta = mediumItem.getItemMeta();
		meta.setDisplayName("§f[ §1중급 §6소재 §f]");
		mediumItem.setItemMeta(meta);
		slotMap.put(mediumItem, 3);
		selectMaterialUI.setItem(slotMap.get(mediumItem), mediumItem);
		
		highItem = new ItemStack(Material.DIAMOND, 1);
		meta = highItem.getItemMeta();
		meta.setDisplayName("§f[ §b상급 §6소재 §f]");
		highItem.setItemMeta(meta);
		slotMap.put(highItem, 5);
		selectMaterialUI.setItem(slotMap.get(highItem), highItem);
		
		veryHighItem = new ItemStack(Material.EMERALD, 1);
		meta = veryHighItem.getItemMeta();
		meta.setDisplayName("§f[ §4최상급 §6소재 §f]");
		veryHighItem.setItemMeta(meta);
		slotMap.put(veryHighItem, 7);
		selectMaterialUI.setItem(slotMap.get(veryHighItem), veryHighItem);
		
		loadRecipes();
	}
	
	public String getDirPath() {
		return this.dirPath;
	}
	
	public void setDirPath(String path) {
		this.dirPath = path;
	}
	
	public void loadPlayerData(UUID uuid) {
		CollectData data = new CollectData(uuid, "미지정");
		data.load();
		dataMap.put(uuid, data);
	}
	
	public void loadMatterInv() {
		for(int i = 0; i < matterSetter.size(); i++) {
			Inventory inv = matterSetter.get(i);
			Inventory tmpInv = MyUtility.getInventoryFromFile(new File(dirPath+"/matterInv",ChatColor.stripColor(inv.getTitle()+".invsave")));
			if(tmpInv != null) {
				matterSetter.set(i, tmpInv);
			}
		}
	}
	
	public void loadRecipes() {
		int cnt = 0;
		
		File dir = new File(dirPath+"/recipes/");
		if(dir.isDirectory()) {
			File[] fileList = dir.listFiles();
			for(File file : fileList) {
				if(file.getName().equals("static_RecipeID")) {
					try {
						FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);						
						int id = fileConfig.getInt("recipeID");
						MyRecipe.recipeID = id;
					}catch(NumberFormatException e) {
						MyUtility.printLog(file.getName()+" 레시피가 잘못됐습니다.");
						continue;
					}
				} else {
					String recipeID = file.getName().replace(".recipe", "");
					try {
						int id = Integer.parseInt(recipeID);
						MyRecipe recipe = new MyRecipe(id);
						if(!recipe.load(dirPath)) {
							MyUtility.printLog(file.getName()+" 레시피 불러오기 실패");
						} else {
							recipeMap.put(recipe.getResultItem(), recipe);
							cnt++;
						}
					}catch(NumberFormatException e) {
						MyUtility.printLog(file.getName()+" 레시피가 잘못됐습니다.");
						continue;
					}
				}			
			}
		}
		
		MyUtility.printLog(mainMS+cnt+"개의 조합법 불러오기 완료");
		
		updateRecipeUI();
	}
	
	public void setSystemCommand(String cmd) {
		this.systemCmd = cmd;
	}
	
	public String getSystemCommand() {
		return this.systemCmd;
	}
	
	public void setSystemName(String sysName) {
		this.systemName = sysName;
	}
	
	public String getSystemName() {
		return this.systemName;
	}
	
	public void setSystemMainMS(String ms) {
		this.mainMS = ms;
	}
	
	public String getSystemMainMS() {
		return this.mainMS;
	}
	
	public boolean isExistData(CollectData data) {
		if(data == null) return false;
		else return true;
	}
	
	public void sendError(Player p, MyErrorType type) {
		switch(type) {
			case NoDataError: p.sendMessage(mainMS + "데이터가 존재하지 않습니다. 재접속해보세요."); break;
			case NoInventoryTypeError: p.sendMessage(mainMS + "존재하지 않은 UI 타입입니다"); break;
			case NoPermissionError: p.sendMessage(mainMS + "권한이 부족합니다."); break;
			case NoPercentageMap: p.sendMessage(mainMS + "채집 확률이 설정되지 않은 아이템입니다."); break;
			case NoMatterInv:  p.sendMessage(mainMS + "존재하지 않는 재료 인벤토리"); break;
			case NoRecipePage: p.sendMessage(mainMS + "존재하지 않는 조합UI 페이지"); break;
			
			default: p.sendMessage(mainMS+"정의되지 않은 오류");
		}
	}
	
	public void openMenu(Player p, MyInventoryType menuType) { //스테이터스 메뉴
		CollectData pData = dataMap.get(p.getUniqueId());
		if(!isExistData(pData)) sendError(p, MyErrorType.NoDataError); //데이터 없을시 에러
		else {
			switch(menuType) {
			
			case statusMenu: 
				Inventory statusMenu = pData.getInventory(MyInventoryType.statusMenu);
				p.openInventory(statusMenu);
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;

			case selectMatter:
				p.openInventory(selectMaterialUI);
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case setRecipe:
				p.openInventory(recipeUI_List.get(0));
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				pageMap.put(p.getName(), 0);
				break;

			case systemSetting:
				p.openInventory(systemSettingUI);
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case settingMatter_Low:
				p.openInventory(matterSetter.get(0));
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case settingMatter_Medium:
				p.openInventory(matterSetter.get(1));
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case settingMatter_High:
				p.openInventory(matterSetter.get(2));
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case settingMatter_VeriHigh:
				p.openInventory(matterSetter.get(3));
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			case personalWorkbench:
				Inventory inv = pData.getInventory(MyInventoryType.personalWorkbench);
				for(int i = 3; i < 27; i += 9) inv.setItem(i, deco); //장식 설정
				for(int i = 5; i < 27; i += 9) inv.setItem(i, deco);
				inv.setItem(13, deco);
				for(int i = 6; i < 27; i += 9) inv.setItem(i, deco2);
				for(int i = 7; i < 27; i += 9) inv.setItem(i, deco2);
				for(int i = 8; i < 27; i += 9) inv.setItem(i, deco2);
				
				inv.setItem(16, null);
				
				inv.setItem(slotMap.get(workbenchUI_check), workbenchUI_check);
				inv.setItem(slotMap.get(workbenchUI_makeIt), workbenchUI_makeIt);
				p.openInventory(inv);
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
				break;
				
			default: sendError(p, MyErrorType.NoInventoryTypeError);
		
		
					
				
			}
		}
	} 
	
	public void gainExp(Player p, int exp) {
		CollectData pData = dataMap.get(p.getUniqueId());
		if(pData != null) {
			int nowExp = pData.getExp();
			int nextExp = pData.getNextExp();
			int lv = pData.getLevel();
			nowExp += exp;
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.0f);
			if(lv < pData.getMaxLevel()) { //레벨 한계선 아니면
				p.sendMessage(mainMS+systemName+" 경험치를 얻었습니다. §e현재 경험치 : "+(nowExp));
				pData.setExp(nowExp);
			}
			if(nowExp >= nextExp) { //레벨 업

				if(lv < pData.getMaxLevel()) { //최대치 이하일때
					exp = 0;
					if(lv >= pData.getMaxLevel()) { //이미 최대치라면
						lv = pData.getMaxLevel();
						nextExp = 0;
					} else { //최대치 아니라면
						pData.setLevel(lv+1); //렙업
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
						p.sendMessage(mainMS+"레벨 업!!!\t§e" + lv +".Lv -> " + (lv + 1)+".Lv");
					}
				}
				nextExp *= 1.5; //필요경험치 1.5배
				pData.setNextExp(nextExp);
			}
			pData.updateGUI();
			pData.save();
		} else sendError(p, MyErrorType.NoDataError);
	}
	
	public void giveMaterial(Player p, ItemStack handItem) {
		PercentageMap percent = getPercentageMap(handItem);
		if(percent == null) sendError(p, MyErrorType.NoPercentageMap);;
		
		int maxPercent = percent.getMaxPercentage();
		int rd = MyUtility.getRandom(1, maxPercent);
		Inventory inv = null;
		if((maxPercent -= percent.veryHigh) < rd) { //최상급
			inv = matterSetter.get(3);
			//Bukkit.getLogger().info("inv3");
		} else if((maxPercent -= percent.high) < rd) { //상급
			inv = matterSetter.get(2);
			//Bukkit.getLogger().info("inv2");
		} else if((maxPercent -= percent.medium) < rd) { //중급
			inv = matterSetter.get(1);
			//Bukkit.getLogger().info("inv1");
		} else if((maxPercent -= percent.low) < rd) { //하급
			inv = matterSetter.get(0);
			//Bukkit.getLogger().info("inv0");
		}
		if(inv == null) sendError(p, MyErrorType.NoMatterInv);
		else {
			int maxIndex = 0;
			for(ItemStack item : inv.getContents()) {
				if(item == null) continue;
				maxIndex += item.getAmount();
			}
			if(maxIndex == 0) sendError(p, MyErrorType.NoMatterInv);
			else {
				int rdIndex = MyUtility.getRandom(1, maxIndex);
				for(ItemStack item : inv.getContents()) {
					if(item == null) continue;
					if(rdIndex - item.getAmount() <= 0) {
						ItemStack rewardItem = item;
						p.getInventory().addItem(rewardItem);
						ItemMeta mt = item.getItemMeta();
						if(mt != null) {
							if(mt.getDisplayName() != null)
								p.sendMessage(mainMS+mt.getDisplayName()+"을 획득했습니다.");	
							else 
								p.sendMessage(mainMS+"채집 완료!");
							
							p.playSound(p.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.1f, 2.0f);
							return;
						}
					} else {
						rdIndex -= item.getAmount();
					}
				}
			}
		}
	}
	
	public boolean isCooldown(Player p) {
		CollectData pData = dataMap.get(p.getUniqueId());
		if(pData != null) {
			long leftCooldown = pData.getLeftCooldown();
			if(leftCooldown <= 0) {
				return false;
			} else {
				p.sendMessage(mainMS+"아직 재사용 대기시간이 §e"+leftCooldown+"§f초 남았습니다.");
				return true;
			}
		}else sendError(p, MyErrorType.NoDataError);
		return false;
	}
	
	private void recipeUIClick(Player p, int slot) {
		int nowPage = pageMap.get(p.getName());
		Inventory inv = recipeUI_List.get(nowPage);
		if(inv == null) sendError(p, MyErrorType.NoRecipePage);
		if(slot < 0 || slot >= inv.getSize()) return;//인벤 밖 클릭 방지
		ItemStack clickItem = inv.getItem(slot);
		if(clickItem == null) return; //빈 칸 클릭시 리턴
		
		if(slot == slotMap.get(recipeUI_prev)) {
			p.openInventory(recipeUI_List.get(nowPage - 1 < 0 ? 0 : nowPage - 1));
			pageMap.put(p.getName(), nowPage - 1 < 0 ? 0 : nowPage - 1);
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		} else if(slot == slotMap.get(recipeUI_next)) {
			p.openInventory(recipeUI_List.get(nowPage + 1 >= recipeUI_List.size() ? nowPage : nowPage + 1));
			pageMap.put(p.getName(), nowPage + 1 >= recipeUI_List.size() ? nowPage : nowPage + 1);
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		} else if(slot == slotMap.get(recipeUI_add)) {
			Inventory adminWorkBench = createAdminWorkbench();
			p.openInventory(adminWorkBench);
			p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		} else {
			for(ItemStack resultItem : recipeMap.keySet()) {
				if(resultItem.equals(clickItem)) { //클릭한 템이 조합맵에 있다면
					MyRecipe recipe = recipeMap.get(resultItem);
					Inventory adminWorkBench = createAdminWorkbench();
					adminWorkBench.setItem(16, resultItem);
					ItemStack recipeArray[][] = recipe.getRecipe();
					adminWorkBench.setItem(0, recipeArray[0][0]);
					adminWorkBench.setItem(1, recipeArray[0][1]);
					adminWorkBench.setItem(2, recipeArray[0][2]);
					adminWorkBench.setItem(9, recipeArray[1][0]);
					adminWorkBench.setItem(10, recipeArray[1][1]);
					adminWorkBench.setItem(11, recipeArray[1][2]);
					adminWorkBench.setItem(18, recipeArray[2][0]);
					adminWorkBench.setItem(19, recipeArray[2][1]);
					adminWorkBench.setItem(20, recipeArray[2][2]);
					
					p.openInventory(adminWorkBench);
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
					break;
				}
			}
		}
	}
	
	private Inventory createAdminWorkbench() {
		Inventory inv = Bukkit.createInventory(null, 27, adminWorkbenchName);
		for(int i = 3; i < 27; i += 9) inv.setItem(i, deco); //장식 설정
		for(int i = 5; i < 27; i += 9) inv.setItem(i, deco);
		for(int i = 6; i < 27; i += 9) inv.setItem(i, deco2);
		for(int i = 7; i < 27; i += 9) inv.setItem(i, deco2);
		for(int i = 8; i < 27; i += 9) inv.setItem(i, deco2);
		
		inv.setItem(16, null);
		
		inv.setItem(slotMap.get(workbenchUI_create), workbenchUI_create);
		inv.setItem(slotMap.get(workbenchUI_delete), workbenchUI_delete);
		
		return inv;
	}
	
	private void adminWorkbenchUIClick(Player p, InventoryClickEvent e) {
		InventoryView invView = p.getOpenInventory();
		//Bukkit.getLogger().info(e.getRawSlot() + " / "+e.getSlot());
		if(invView.getTitle().equals(adminWorkbenchName) && e.getRawSlot() == e.getSlot()) { //상자인지 아닌지 확인 RawSlot은 상자에만 있음
			/*LoopColumn:
			for(int[] column : workbenchUI_recipeSlot) {
				for(int tmpSlot : column) {
					if(tmpSlot == slot) { //레시피 창을 클릭했다면
						isCancel = false;
						break LoopColumn;
					}
				}
			}
			if(workbenchUI_resultSlot == slot) isCancel = false;
			e.setCancelled(isCancel);*/
			Inventory topInv = invView.getTopInventory();
			if(topInv == null) return;
			if(e.getSlot() < 0 || e.getSlot() >= topInv.getSize()) return;
			ItemStack clickItem = topInv.getItem(e.getSlot());
			if(clickItem != null) {
				if(clickItem.equals(deco) || clickItem.equals(deco2)) {
					e.setCancelled(true);
				} else if(clickItem.equals(workbenchUI_create)) {
					e.setCancelled(true);
					ItemStack result = topInv.getItem(workbenchUI_resultSlot);
					if(result == null) {
						p.sendMessage(mainMS+"조합 결과물칸이 비어있습니다.");
						return;
					}
					
					boolean isEmpty = true;
					ItemStack newRecipe[][] = {{null, null, null},
												{null, null, null},
												{null, null, null}}; //3x3
					for(int i = 0; i < workbenchUI_recipeSlot.length; i++) {
						for(int j = 0; j < workbenchUI_recipeSlot[i].length; j++) {
							int checkSlot = workbenchUI_recipeSlot[i][j];
							ItemStack tmpItem = topInv.getItem(checkSlot);
							if(tmpItem != null) { 
								isEmpty = false;
								newRecipe[i][j] = tmpItem;
							}
						}
					}
					if(isEmpty) {
						p.sendMessage(mainMS+"조합 레시피칸에는 적어도 1개 이상의 아이템이 존재해야합니다.");
						return;
					}
					
					if(recipeMap.containsKey(result)) {
						p.sendMessage(mainMS+"이미 해당 아이템에 대한 조합법이 존재합니다.");
						return;
					}
					
					MyRecipe recipe = new MyRecipe();
					recipe.setRecipe(newRecipe);
					
					ItemStack checkAlready = getResultFromRecipe(recipe);
					if(checkAlready != null) {
						p.sendMessage(mainMS+"이미 해당 조합법에 대한 아이템"+checkAlready+"이 존재합니다.");
						return;
					}
					registerNewRecipe(result, recipe);
					p.sendMessage(mainMS+"레시피 등록을 완료했습니다.");
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
					p.closeInventory();
				} else if(clickItem.equals(workbenchUI_delete)) {
					e.setCancelled(true);
					ItemStack result = topInv.getItem(workbenchUI_resultSlot);
					if(result == null) {
						p.sendMessage(mainMS+"조합 결과물칸에 삭제할 아이템을 놓아주세요.");
						return;
					}
					if(getRecipe(result) == null) p.sendMessage(mainMS+"레시피에 존재하지 않는 아이템입니다.");
					else {
						removeRecipe(result);
						p.sendMessage(mainMS+"레시피 삭제를 완료했습니다.");
						p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
						p.closeInventory();
					}			
				}
			}
		}
	}
	
	private void personalWorkbenchUIClick(Player p, InventoryClickEvent e) {
		InventoryView invView = p.getOpenInventory();
		if(e.getRawSlot() == e.getSlot()) { //상자인지 아닌지 확인 RawSlot은 상자에만 있음
			Inventory topInv = invView.getTopInventory();
			if(topInv == null) return;
			if(!(e.getAction() == InventoryAction.PICKUP_SOME
					|| e.getAction() == InventoryAction.PICKUP_ALL
					|| e.getAction() == InventoryAction.PICKUP_HALF
					|| e.getAction() == InventoryAction.PICKUP_ONE
					|| e.getAction() == InventoryAction.PLACE_SOME 
					|| e.getAction() == InventoryAction.PLACE_ALL 
					|| e.getAction() == InventoryAction.PLACE_ONE )) {
				e.setCancelled(true);
				return;
			}
			if(e.getSlot() < 0 || e.getSlot() >= topInv.getSize()) return;
			
			ItemStack clickItem = topInv.getItem(e.getSlot());
			
			if(e.getSlot() == 16) {
				e.setCancelled(true);
			}
			
			if(clickItem != null) {
				if(clickItem.equals(deco) || clickItem.equals(deco2)) {
					e.setCancelled(true);
				} else if(clickItem.equals(workbenchUI_check)) {
					e.setCancelled(true);
					
					boolean isEmpty = true;
					ItemStack newRecipe[][] = {{null, null, null},
												{null, null, null},
												{null, null, null}}; //3x3
					for(int i = 0; i < workbenchUI_recipeSlot.length; i++) {
						for(int j = 0; j < workbenchUI_recipeSlot[i].length; j++) {
							int checkSlot = workbenchUI_recipeSlot[i][j];
							ItemStack tmpItem = topInv.getItem(checkSlot);
							if(tmpItem != null) { 
								isEmpty = false;
								newRecipe[i][j] = tmpItem;
							}
						}
					}
					
					if(isEmpty) {
						return;
					}
					
					ItemStack resultItem = getResultFromRecipe(MyUtility.copyRecipeArray(newRecipe));
					if(resultItem != null) {
						topInv.setItem(16, resultItem);
						p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2.0f, 1.0f);
					} else {
						p.sendMessage(mainMS+"해당 조합법에 대한 결과가 없습니다.");
					}
					
				} else if(clickItem.equals(workbenchUI_makeIt) || e.getSlot() == 16) {
					e.setCancelled(true);
					
					ItemStack resultItem = topInv.getItem(16);
					if(resultItem == null) p.sendMessage(mainMS+"먼저 조합 시도를 해주세요.");
					else {
						boolean isEmpty = true;
						ItemStack newRecipe[][] = {{null, null, null},
													{null, null, null},
													{null, null, null}}; //3x3
						for(int i = 0; i < workbenchUI_recipeSlot.length; i++) {
							for(int j = 0; j < workbenchUI_recipeSlot[i].length; j++) {
								int checkSlot = workbenchUI_recipeSlot[i][j];
								ItemStack tmpItem = topInv.getItem(checkSlot);
								if(tmpItem != null) { 
									isEmpty = false;
									newRecipe[i][j] = tmpItem;
								}
							}
						}
						
						if(isEmpty) {
							p.sendMessage(mainMS+"부적절한 접근");
							return;
						}
						
						ItemStack tmpCheck = getResultFromRecipe(MyUtility.copyRecipeArray(newRecipe));
						if(tmpCheck != null) {
							if(tmpCheck.equals(resultItem)) {
								for(int i = 0; i < workbenchUI_recipeSlot.length; i++) {
									for(int j = 0; j < workbenchUI_recipeSlot[i].length; j++) {
										int checkSlot = workbenchUI_recipeSlot[i][j];
										ItemStack tmpItem = topInv.getItem(checkSlot);
										if(tmpItem != null) {
											tmpItem.setAmount(tmpItem.getAmount()-1);
										}
									}
								}
								p.getInventory().addItem(resultItem);
								p.sendMessage(mainMS+"조합 완료!");
								topInv.setItem(16, itemStack_air);
								p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.5f, 1.0f);
							} else {
								p.sendMessage(mainMS+"부적절한 접근 er3");
							}						
						} else {
							p.sendMessage(mainMS+"부적절한 접근 er2");
						}
					}
					
				} else {
					topInv.setItem(16, itemStack_air);
					//p.updateInventory();
					p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_STEP, 1.0f, 1.0f);
				}
			}
		}
	}
	
	private void personalWorkbenchUIClose(Player p, InventoryCloseEvent e) {
		InventoryView invView = e.getView();
		Inventory topInv = invView.getTopInventory();
		if(topInv == null) return;
		
		for(int i = 0; i < workbenchUI_recipeSlot.length; i++) {
			for(int j = 0; j < workbenchUI_recipeSlot[i].length; j++) {
				int checkSlot = workbenchUI_recipeSlot[i][j];
				ItemStack item = topInv.getItem(checkSlot);
				if(item == null) continue;
				topInv.setItem(checkSlot, null);
				p.getInventory().addItem(item);
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
			}
		}
	}
	
	
	private void updateRecipeUI() {
		int page = 0;
		Inventory inv = recipeUI_List.get(page);
		inv.clear();
		inv.setItem(slotMap.get(recipeUI_add), recipeUI_add);
		int slotIndex = 0;
		for(ItemStack resultItem : recipeMap.keySet()) {
			inv.setItem(slotIndex, resultItem);	
			slotIndex += 1;
			if(slotIndex >= 18) {
				//페이지 넘기기 전에 할 일
				inv.setItem(slotMap.get(recipeUI_next), recipeUI_next);
			
				//페이지 넘기기
				slotIndex = 0;
				page += 1; 
				inv = recipeUI_List.get(page);
				inv.clear();
				inv.setItem(slotMap.get(recipeUI_prev), recipeUI_prev);
				inv.setItem(slotMap.get(recipeUI_add), recipeUI_add);
			}
		}
	}
	
	private void registerNewRecipe(ItemStack result, MyRecipe recipe) {
		recipeMap.put(result, recipe); //등록 완료
		
		recipe.setResultItem(result);
		recipe.save(dirPath);
		
		updateRecipeUI();
	}
	
	private void removeRecipe(ItemStack result) {
		MyRecipe recipe = recipeMap.get(result);
		recipeMap.remove(result); //삭제 완료
		
		if(recipe != null) {
			recipe.delete(dirPath);
			updateRecipeUI();
		}

	}
	
	public MyRecipe getRecipe(ItemStack result) {
		return recipeMap.get(result);
	}
	
	public ItemStack getResultFromRecipe(MyRecipe recipe) {
		//Bukkit.getLogger().info("cnt: "+recipeMap.size());
		ItemStack items[][] = recipe.getRecipe();

		int newSourceCnt = recipe.getNeedItemAmout();
		if(newSourceCnt <= 6) {
			//미리 잘라내기
			COLUMNCHECK:
			for(int i = 0; i < items.length-1; i++) { //2번반복
				
				for(int j = 0; j < items[0].length; j++) {
					if(items[0][j] != null) {
						break COLUMNCHECK;
					}
				}
				for(int j = 0; j < items[0].length; j++) {
					items[0][j] = items[1][j]; //끌어올리기
					items[1][j] = items[2][j];
					items[2][j] = null;
				}
			}
		
			ROWCHECK:
			for (int i = 0; i < items.length - 1; i++) { // 2번반복

				for (int j = 0; j < items[0].length; j++) {
					if (items[j][0] != null) {
						break ROWCHECK;
					}
				}
				for (int j = 0; j < items[0].length; j++) {
					items[j][0] = items[j][1]; // 끌어당기기
					items[j][1] = items[j][2];
					items[j][2] = null;
					
				}
			}
		}
		
		
		/*for(int i = 0; i < items.length; i++) {
			for(int j = 0; j < items[i].length; j++) {
				Bukkit.getLogger().info(items[i][j]+"\t");
			}
		}*/
			
		////////비교부분
		CompareRecipe:
		for(ItemStack resultItem : recipeMap.keySet()) {
			MyRecipe baseRecipe = recipeMap.get(resultItem);
			if(baseRecipe == null) {
				continue;
			}
			ItemStack baseItems[][] = baseRecipe.getRecipe(); //레시피 비교...
			int baseSourceCnt = baseRecipe.getNeedItemAmout();
			if(baseSourceCnt != newSourceCnt) {
				continue; //우선 갯수 비교
			}
			if(baseSourceCnt > 6) { //재료 개수가 7개 이상이면 잘라내기할 필요없음
				for(int i = 0; i < items.length; i++) { //아이템 비교 시작
					for(int j = 0; j < items[i].length; j++) {
						if(items[i][j] == null || baseItems[i][j] == null) {
							if(items[i][j] == null && baseItems[i][j] == null)continue; //둘다 null이면 ㅇㅋ
							else continue CompareRecipe; //둘중 1개라도 null 아니면 같은게 아니니 패스
						} else if(!items[i][j].equals(baseItems[i][j])) { //둘 다 null 아니면 비교
							continue CompareRecipe; //하나라도 다르면 패스
						}
					}
				}
				return resultItem;
			} else { //막대기 처럼 위치 달라도 조합 가능하도록 잘라내기 기법 적용
				//baseItems 잘라내기
				COLUMNCHECK: 
				for (int i = 0; i < baseItems.length - 1; i++) { // 2번반복

					for (int j = 0; j < baseItems[0].length; j++) {
						if (baseItems[0][j] != null) {
							break COLUMNCHECK;
						}
					}
					for (int j = 0; j < baseItems[0].length; j++) {
						baseItems[0][j] = baseItems[1][j]; // 끌어올리기
						baseItems[1][j] = baseItems[2][j];
						baseItems[2][j] = null;
					}
				}

				ROWCHECK: for (int i = 0; i < baseItems.length - 1; i++) { // 2번반복

					for (int j = 0; j < baseItems[0].length; j++) {
						if (baseItems[j][0] != null) {
							break ROWCHECK;
						}
					}
					for (int j = 0; j < baseItems[0].length; j++) {
						baseItems[j][0] = baseItems[j][1]; // 끌어당기기
						baseItems[j][1] = baseItems[j][2];
						baseItems[j][2] = null;
					}
				}
				
				/*for(int i = 0; i < baseItems.length; i++) {
					for(int j = 0; j < baseItems[i].length; j++) {
						Bukkit.getLogger().info(baseItems[i][j]+"\t");
					}
				}*/
				
				//그 후 비교
				for(int i = 0; i < items.length; i++) { //아이템 비교 시작
					for(int j = 0; j < items[i].length; j++) {
						if(items[i][j] == null || baseItems[i][j] == null) {
							if(items[i][j] == null && baseItems[i][j] == null)continue; //둘다 null이면 ㅇㅋ
							else continue CompareRecipe; //둘중 1개라도 null 아니면 같은게 아니니 패스
						} else if(!items[i][j].equals(baseItems[i][j])) { //둘 다 null 아니면 비교
							continue CompareRecipe; //하나라도 다르면 패스
						}
					}
				}
				return resultItem;
			}
		}
		return null;
	}
	
	public ItemStack getResultFromRecipe(ItemStack items[][]) {
		int newSourceCnt = 0;
		for(int i = 0; i < items.length; i++) {
			for(int j = 0; j < items.length; j++) {
				ItemStack tmpItem = items[i][j];
				if(tmpItem != null) {
					if(tmpItem.getAmount() > 1) {
						tmpItem.setAmount(1);
					}
					newSourceCnt++;
				}
			}
		}

		if(newSourceCnt <= 6) {
			//미리 잘라내기
			COLUMNCHECK:
			for(int i = 0; i < items.length-1; i++) { //2번반복
				
				for(int j = 0; j < items[0].length; j++) {
					if(items[0][j] != null) {
						break COLUMNCHECK;
					}
				}
				for(int j = 0; j < items[0].length; j++) {
					items[0][j] = items[1][j]; //끌어올리기
					items[1][j] = items[2][j];
					items[2][j] = null;
				}
			}
		
			ROWCHECK:
			for (int i = 0; i < items.length - 1; i++) { // 2번반복

				for (int j = 0; j < items[0].length; j++) {
					if (items[j][0] != null) {
						break ROWCHECK;
					}
				}
				for (int j = 0; j < items[0].length; j++) {
					items[j][0] = items[j][1]; // 끌어당기기
					items[j][1] = items[j][2];
					items[j][2] = null;
					
				}
			}
		}
		
		
		/*for(int i = 0; i < items.length; i++) {
			for(int j = 0; j < items[i].length; j++) {
				Bukkit.getLogger().info(items[i][j]+"\t");
			}
		}*/
			
		////////비교부분
		CompareRecipe:
		for(ItemStack resultItem : recipeMap.keySet()) {
			MyRecipe baseRecipe = recipeMap.get(resultItem);
			if(baseRecipe == null) {
				continue;
			}
			ItemStack baseItems[][] = baseRecipe.getRecipe(); //레시피 비교...
			int baseSourceCnt = baseRecipe.getNeedItemAmout();
			if(baseSourceCnt != newSourceCnt) {
				continue; //우선 갯수 비교
			}
			if(baseSourceCnt > 6) { //재료 개수가 7개 이상이면 잘라내기할 필요없음
				for(int i = 0; i < items.length; i++) { //아이템 비교 시작
					for(int j = 0; j < items[i].length; j++) {
						if(items[i][j] == null || baseItems[i][j] == null) {
							if(items[i][j] == null && baseItems[i][j] == null)continue; //둘다 null이면 ㅇㅋ
							else continue CompareRecipe; //둘중 1개라도 null 아니면 같은게 아니니 패스
						} else if(!items[i][j].equals(baseItems[i][j])) { //둘 다 null 아니면 비교
							continue CompareRecipe; //하나라도 다르면 패스
						}
					}
				}
				return resultItem;
			} else { //막대기 처럼 위치 달라도 조합 가능하도록 잘라내기 기법 적용
				//baseItems 잘라내기
				COLUMNCHECK: 
				for (int i = 0; i < baseItems.length - 1; i++) { // 2번반복

					for (int j = 0; j < baseItems[0].length; j++) {
						if (baseItems[0][j] != null) {
							break COLUMNCHECK;
						}
					}
					for (int j = 0; j < baseItems[0].length; j++) {
						baseItems[0][j] = baseItems[1][j]; // 끌어올리기
						baseItems[1][j] = baseItems[2][j];
						baseItems[2][j] = null;
					}
				}

				ROWCHECK: for (int i = 0; i < baseItems.length - 1; i++) { // 2번반복

					for (int j = 0; j < baseItems[0].length; j++) {
						if (baseItems[j][0] != null) {
							break ROWCHECK;
						}
					}
					for (int j = 0; j < baseItems[0].length; j++) {
						baseItems[j][0] = baseItems[j][1]; // 끌어당기기
						baseItems[j][1] = baseItems[j][2];
						baseItems[j][2] = null;
					}
				}
				
				/*for(int i = 0; i < baseItems.length; i++) {
					for(int j = 0; j < baseItems[i].length; j++) {
						Bukkit.getLogger().info(baseItems[i][j]+"\t");
					}
				}*/
				
				//그 후 비교
				for(int i = 0; i < items.length; i++) { //아이템 비교 시작
					for(int j = 0; j < items[i].length; j++) {
						if(items[i][j] == null || baseItems[i][j] == null) {
							if(items[i][j] == null && baseItems[i][j] == null)continue; //둘다 null이면 ㅇㅋ
							else continue CompareRecipe; //둘중 1개라도 null 아니면 같은게 아니니 패스
						} else if(!items[i][j].equals(baseItems[i][j])) { //둘 다 null 아니면 비교
							continue CompareRecipe; //하나라도 다르면 패스
						}
					}
				}
				return resultItem;
			}
		}
		return null;
	}
	
	public abstract void systemInit();
	public abstract boolean checkCollectorItem(Player p, ItemStack item);
	public abstract boolean isCollectorBlock(Block block);
	public abstract void applyCooldown(Player p, ItemStack item);
	public abstract PercentageMap getPercentageMap(ItemStack item);
	public abstract void giveOwnItem(Player p, String type);
	
	////////이벤트
	private class CollectFormatEvent implements Listener{
		
		@EventHandler
		public void onJoin(PlayerJoinEvent e) {
			Player p = e.getPlayer();
			if(dataMap.get(p.getUniqueId()) == null) { //접속한 플레이어 데이터가 로드되어 있지 않으면
				loadPlayerData(p.getUniqueId());
			}
		}
		
		@EventHandler
		public void onCommandInput(PlayerCommandPreprocessEvent e) {
			Player p = e.getPlayer();
			if(e.getMessage() == null) return;
			String cmdArgs[] = e.getMessage().split(" ");
			String cmdMain = cmdArgs[0];
			if(cmdMain == null) return;
			if(cmdMain.equals(systemCmd)) {
				if(cmdArgs.length == 1) openMenu(p, MyInventoryType.statusMenu); 
				else if(cmdArgs[1].equals("설정") && p.isOp()) {
					if(p.isOp()) {
						openMenu(p, MyInventoryType.systemSetting);
					} else sendError(p, MyErrorType.NoPermissionError);
				} else if(cmdArgs[1].equals("아이템") && p.isOp()) {
					giveOwnItem(p, cmdArgs.length > 2 ? cmdArgs[2] : "");
				} /*else if(cmdArgs[1].equals("작업대")) { //사용안함
					if(cmdArgs.length < 3) { 
						p.sendMessage(mainMS+"작업대를 표시할 대상의 이름을 입력해주세요.");
					} else {
						String targetName = cmdArgs[2];
						Player t = Bukkit.getPlayer(targetName);
						if(t == null) {
							p.sendMessage(mainMS+"존재하지 않는 플레이어입니다.");
						} else {
							openMenu(t, MyInventoryType.personalWorkbench);
						}
					}				
				}*/
			} 
		}
		
		@EventHandler
		public void onClickInventory(InventoryClickEvent e) {
			if(e.getWhoClicked() instanceof Player){
				Player p = (Player) e.getWhoClicked();
				Inventory clickedInv = e.getInventory();
				String invTitle = clickedInv.getTitle();
				CollectData pData = dataMap.get(p.getUniqueId());
				
				if(pData != null) { //이제 작업대는 커스텀 npc에서만 가능 
					if(pData.getInventory(MyInventoryType.statusMenu).getTitle().equals((invTitle))) {
						if(pData.getWorkbenchSlot() == e.getSlot()) {
							openMenu(p, MyInventoryType.personalWorkbench);
						}
						e.setCancelled(true);
					} else if(pData.getInventory(MyInventoryType.personalWorkbench).getTitle().equals(invTitle)) {
						personalWorkbenchUIClick(p, e);
					}
				} 
				if(invTitle.equals(systemSettingUI.getTitle())) {
					if(e.getSlot() == slotMap.get(settingUI_setMatter)) {
						openMenu(p, MyInventoryType.selectMatter);
					} else if(e.getSlot() == slotMap.get(settingUI_setRecipe)) {
						openMenu(p, MyInventoryType.setRecipe);
					}
					e.setCancelled(true);
				} else if(invTitle.equals(selectMaterialUI.getTitle())){	
					if(e.getSlot() == slotMap.get(lowItem)) {
						openMenu(p, MyInventoryType.settingMatter_Low);
					} else if(e.getSlot() == slotMap.get(mediumItem)) {
						openMenu(p, MyInventoryType.settingMatter_Medium);
					} else if(e.getSlot() == slotMap.get(highItem)) {
						openMenu(p, MyInventoryType.settingMatter_High);
					} else if(e.getSlot() == slotMap.get(veryHighItem)) {
						openMenu(p, MyInventoryType.settingMatter_VeriHigh);
					} 
					e.setCancelled(true);
				} else if(invTitle.equals(recipeUI_List.get(0).getTitle())) { //조합창 제목
					recipeUIClick(p, e.getSlot());
					e.setCancelled(true);
				} else if(invTitle.equals(adminWorkbenchName)) {
					adminWorkbenchUIClick(p, e);
				}
			}
		}
			
		@EventHandler
		public void onCloseInventory(InventoryCloseEvent e) {
			if(e.getPlayer() instanceof Player) {
				Player p = (Player) e.getPlayer();
				Inventory clickedInv = e.getInventory();
				String invTitle = clickedInv.getTitle();
				CollectData pData = dataMap.get(p.getUniqueId());
				
				for(int i = 0; i < matterSetter.size(); i++) {
					Inventory matterInv = matterSetter.get(i);
					if(invTitle.equals(matterInv.getTitle())) {
						MyUtility.saveInventoryToFile(dirPath+"/matterInv", matterInv, ChatColor.stripColor(matterInv.getTitle()));
					}
				}
							
				if(pData != null) {
					if(pData.getInventory(MyInventoryType.personalWorkbench).getTitle().equals(invTitle)) {
						personalWorkbenchUIClose(p, e);
					}
				} 
			}
		}
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent e) {
			Player p = e.getPlayer();
			ItemStack handItem = p.getInventory().getItemInMainHand();
			if (isCollectorBlock(e.getBlock())) {
				if (!isCooldown(p)) {
					if (checkCollectorItem(p, handItem)) {
						handItem.setDurability((short) 0); // 내구도 무한
						applyCooldown(p, handItem);
						e.setCancelled(true);
						e.getBlock().setType(Material.AIR);
						gainExp(p, 1);
						giveMaterial(p, handItem);
					} else
						e.setCancelled(true);
				} else 
					e.setCancelled(true);
			}
		}
		
	}
	
	protected class PercentageMap{
		
		final private int maxPercentage = 100;
		
		private int veryHigh = 1;
		private int high = 1;
		private int medium = 10;
		private int low = maxPercentage - veryHigh - high - medium;
		
		public PercentageMap(int veryHigh, int High, int medium) {
			this.veryHigh = veryHigh;
			this.high = High;
			this.medium = medium;
			this.low = maxPercentage - veryHigh - high - medium;
		}
		
		public int getMaxPercentage() {
			return this.maxPercentage;
		}
		
	}
	
	
}
