package Collect;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import Utility.MyUtility;

public class MyRecipe{
	
	public static int recipeID =  0;
	
	private int myRecipeId = 0;
	
	private ItemStack recipe[][] = {{null, null, null},
									{null, null, null},
									{null, null, null}};
	private ItemStack resultItem;
	private final int recipeLine = 3;
	private int needItemAmout = 0;
	
	public MyRecipe() {
		this.myRecipeId = recipeID;
		recipeID += 1;
	}
	
	public MyRecipe(int recipeID) {
		this.myRecipeId = recipeID;
	}
	
	public void setResultItem(ItemStack item) {
		this.resultItem = item;
	}
	
	public ItemStack getResultItem() {
		return this.resultItem;
	}
	
	public ItemStack[][] getRecipe(){
		ItemStack tmp[][] = new ItemStack[3][3];

		for(int i = 0; i < tmp.length; i++) {
			tmp[i] = recipe[i].clone();
		}
		return tmp;
	}
	
	public boolean setRecipe(ItemStack newRecipe[][]) {
		if(newRecipe.length != recipeLine) return false; //레시피는 3x3이여함
		for(ItemStack[] tmp : newRecipe) {
			if(tmp.length != recipeLine) return false; 
		}
		
		needItemAmout = 0;
		for(int i = 0; i < newRecipe.length; i++) {
			for(int j = 0; j < newRecipe[i].length; j++) {
				ItemStack newItem = newRecipe[i][j];
				if(newItem != null) {
					newItem.setAmount(1); //강제적 1개로
					needItemAmout++;
				}				
				recipe[i][j] = newItem;
			}
		}
		return true;
		
	}
	
	public int getNeedItemAmout() {
		return this.needItemAmout;
	}
	
	public boolean save(String dirPath) {
		
		File file = new File(dirPath+"/recipes/"+myRecipeId+".recipe");
		try {
			FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			
			if(resultItem != null) {
				fileConfig.set("resultItem", resultItem);
			}
			
			int index = 0;
			for (int i = 0; i < recipe.length; i++) {
				for(int j = 0; j < recipe[i].length; j++) {
					if(recipe[i][j] != null) {
						fileConfig.set("Slot " + index, recipe[i][j]);
					}
					index += 1;
				}
			}
			
			fileConfig.save(file);
			MyUtility.printLog(myRecipeId+" 번 레시피 저장 완료");
			
			File staticFile = new File(dirPath+"/recipes/static_RecipeID");
			FileConfiguration fileConfig2 = YamlConfiguration.loadConfiguration(file);
			fileConfig2.set("recipeID", recipeID);
			fileConfig2.save(staticFile);
			
		}catch(Exception e) {
			MyUtility.printLog("레시피를 저장하는 중 오류 발생");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void delete(String dirPath) {
		File file = new File(dirPath+"/recipes/"+myRecipeId+".recipe");
		if(file.exists()) file.delete();
	}
	
	public boolean load(String dirPath) {
		File file = new File(dirPath+"/recipes/"+myRecipeId+".recipe");
		if(!file.exists()) return false;
		try {
			FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			
			ItemStack tmp = fileConfig.getItemStack("resultItem");
			if(tmp == null) {
				return false;
			}
			else this.resultItem = tmp;
			
			int index = 0;
			needItemAmout = 0;
			for (int i = 0; i < recipe.length; i++) {
				for(int j = 0; j < recipe[i].length; j++) {
					recipe[i][j] = fileConfig.getItemStack("Slot " + index);
					index += 1;
					if(recipe[i][j] != null) needItemAmout++;
				}
			}
		}catch(Exception e) {
			MyUtility.printLog("레시피를 불러오는 중 오류 발생");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}