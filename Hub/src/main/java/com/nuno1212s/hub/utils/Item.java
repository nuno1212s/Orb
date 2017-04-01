package com.nuno1212s.hub.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Item {
	
	public static ItemStack create(Material material, int amount, int data, Enchantment enchantment, int power, String displayname) {
	    ItemStack item = new ItemStack(material, amount, (byte)data);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(displayname);
	    meta.addEnchant(enchantment, power, true);
	    
	    item.setItemMeta(meta);
	    
	    return item;
	}
  
	public static ItemStack create(Material material, int amount, int data, String displayname, String... lore) {
	    ItemStack item = new ItemStack(material, amount, (byte)data);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(displayname);
	    ArrayList<String> loreList = new ArrayList<String>();
	    for (String s : lore) {
	      loreList.add(s);
	    }
	    meta.setLore(loreList);
	    
	    item.setItemMeta(meta);
	    
	    return item;
	}
  
	public static ItemStack create(Material material, int amount, String displayname, String... lore) {
	    ItemStack item = new ItemStack(material, amount);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(displayname);
	    ArrayList<String> loreList = new ArrayList<String>();
	    for (String s : lore) {
	      loreList.add(s);
	    }
	    meta.setLore(loreList);
	    
	    item.setItemMeta(meta);
	    
	    return item;
	}
  
	public static ItemStack create(Material material, int amount, String displayname) {
	    ItemStack item = new ItemStack(material, amount);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(displayname);
	    
	    item.setItemMeta(meta);
	    
	    return item;
	}
	
	public static ItemStack create(Material material, int amount, int data) {
	    ItemStack item = new ItemStack(material, amount, (byte)data);
	    
	    return item;
	}
  
	public static ItemStack create(Material Material, int amount) {
	    ItemStack item = new ItemStack(Material, amount);
	    
	    return item;
	}

}
