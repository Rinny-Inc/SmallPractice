package us.noks.smallpractice.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.util.com.google.common.collect.Maps;
import us.noks.smallpractice.objects.Duel;
import us.noks.smallpractice.objects.managers.DuelManager;
import us.noks.smallpractice.objects.managers.PlayerManager;

public class InvView implements Listener {

	static InvView instance = new InvView();
	public static InvView getInstance() {
		return instance;
	}
	  
    private Map<UUID, Inventory> inventorymap = Maps.newHashMap();
    
	public void saveInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + p.getName() + "'s Inventory");
		
		for (int i = 0; i < 9; i++) {
			inv.setItem(i + 27, p.getInventory().getItem(i));
		}
		for (int i = 0; i < p.getInventory().getSize() - 9; i++) {
			inv.setItem(i, p.getInventory().getItem(i + 9));
		}
		
		inv.setItem(36, p.getInventory().getHelmet());
		inv.setItem(37, p.getInventory().getChestplate());
		inv.setItem(38, p.getInventory().getLeggings());
		inv.setItem(39, p.getInventory().getBoots());
		
		if (p.getHealth() > 0) {
			ItemStack vie = new ItemStack(Material.SPECKLED_MELON, Integer.valueOf((int) p.getHealth()).intValue());
			ItemMeta v = vie.getItemMeta();
			v.setDisplayName(ChatColor.DARK_AQUA + "Hearts: " + ChatColor.RESET + Math.ceil(p.getHealth() / 2.0D) + ChatColor.RED + " hp");
			vie.setItemMeta(v);
			
			inv.setItem(48, vie);
		} else {
			ItemStack vie = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal());
			ItemMeta v = vie.getItemMeta();
			v.setDisplayName(ChatColor.DARK_AQUA + "Player Died");
			vie.setItemMeta(v);
			
			inv.setItem(48, vie);
		}
		
		ItemStack bouffe = new ItemStack(Material.COOKED_BEEF, p.getFoodLevel());
		ItemMeta b = bouffe.getItemMeta();
		b.setDisplayName(ChatColor.DARK_AQUA + "Food points: " + ChatColor.RESET + p.getFoodLevel());
		bouffe.setItemMeta(b);
		inv.setItem(49, bouffe);
      
		ItemStack item2 = new ItemStack(Material.BREWING_STAND_ITEM, p.getActivePotionEffects().size());
		ItemMeta itemm2 = item2.getItemMeta();
		itemm2.setDisplayName(ChatColor.DARK_AQUA + "Potion Effects:");
		List<String> lore = Lists.newArrayList();
		if (p.getActivePotionEffects().size() == 0) {
			lore.add(ChatColor.RED + "No potion effects");
		} else {
			for (PotionEffect pe : p.getActivePotionEffects()) {
				int realtime = pe.getDuration() / 20;
				String emp = convertToRoman(pe.getAmplifier() + 1);
          
				lore.add(ChatColor.GRAY + "-> " + ChatColor.RED + WordUtils.capitalizeFully(pe.getType().getName().replaceAll("_", " ")) + " " + emp + " for " + ChatColor.RESET + convertToPotionFormat(realtime));
			}
		}
		itemm2.setLore(lore);
		item2.setItemMeta(itemm2);
		inv.setItem(50, item2);
      
		int amount = (p.getInventory().contains(new ItemStack(Material.POTION, 1, (short)16421)) ? Integer.valueOf(p.getInventory().all(new ItemStack(Material.POTION, 1, (short)16421)).size()).intValue() : 0);
      
		PlayerManager pm = PlayerManager.get(p);
		pm.setLastFailedPotions(pm.getFailedPotions());
		pm.setFailedPotions(0);
		if(pm.getCombo() > pm.getLongestCombo()) {
    		pm.setLongestCombo(pm.getCombo());
    	}
		pm.setCombo(0);
		
		ItemStack pots = new ItemStack(Material.POTION, amount > 64 ? 64 : amount, (short)16421);
		ItemMeta po = pots.getItemMeta();
		po.setDisplayName(ChatColor.YELLOW.toString() + amount + ChatColor.DARK_AQUA + " health pot(s) left");
		po.setLore(Arrays.asList(ChatColor.GRAY + "-> " + ChatColor.DARK_AQUA + "Missed potions: " + ChatColor.YELLOW + pm.getLastFailedPotions()));
		pots.setItemMeta(po);
		inv.setItem(45, pots);
		
		ItemStack stats = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta sm = stats.getItemMeta();
		sm.setDisplayName(ChatColor.GOLD + "Statistics");
		sm.setLore(Arrays.asList(ChatColor.GRAY + "-> " + ChatColor.DARK_AQUA + "Total hit: " + ChatColor.YELLOW + pm.getHit(), ChatColor.GRAY + "-> " + ChatColor.DARK_AQUA + "Longest combo: " + ChatColor.YELLOW + pm.getLongestCombo()));
		stats.setItemMeta(sm);
		inv.setItem(46, stats);
		
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		ItemMeta arr = arrow.getItemMeta();
		arr.setDisplayName(ChatColor.YELLOW + pm.getOldOpponent().getName() + ChatColor.DARK_AQUA + "'s Inventory" + ChatColor.RED + "(DOESNT WORK)");
		arrow.setItemMeta(arr);
		inv.setItem(53, arrow);
      
		this.inventorymap.put(p.getUniqueId(), inv);
	}
    
	public void openInv(Player p, UUID t) {
		if (this.inventorymap.containsKey(t)) {
			p.openInventory(this.inventorymap.get(t));
		}
	}
    
	public void deathMsg(Player winner, Player looser) {
		String winnerMessage = ChatColor.DARK_AQUA + "Winner: " + ChatColor.YELLOW + winner.getName();
		List<Player> spectators = Lists.newArrayList();
		Duel duel = DuelManager.getInstance().getDuelByPlayer(winner);
		
		spectators.addAll(duel.getAllSpectators());
		
	    TextComponent l1 = new TextComponent();
	    l1.setText("Inventories (Click): ");
	    l1.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
	    
	    TextComponent l1a = new TextComponent();
	    l1a.setText(winner.getName());
	    l1a.setColor(net.md_5.bungee.api.ChatColor.GREEN);
	    l1a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + winner.getName() + "'s inventory").create()));
	    l1a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + winner.getUniqueId()));
	    
	    TextComponent l1b = new TextComponent();
	    l1b.setText(", ");
	    l1b.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
	    
	    TextComponent l1c = new TextComponent();
	    l1c.setText(looser.getName());
	    l1c.setColor(net.md_5.bungee.api.ChatColor.RED);
	    l1c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + looser.getName() + "'s inventory").create()));
	    l1c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + looser.getUniqueId()));
	    
	    TextComponent l1d = new TextComponent();
	    l1d.setText(".");
	    l1d.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
	    
	    l1.addExtra(l1a);
	    l1.addExtra(l1b);
	    l1.addExtra(l1c);
	    l1.addExtra(l1d);
	    
	    StringJoiner spect = new StringJoiner(ChatColor.DARK_AQUA + ", ");
	    if (duel.hasSpectator()) {
	    	for (Player spec : spectators) {
	    		spect.add(ChatColor.YELLOW + spec.getName());
	    	}
	    }
	    String spectatorMessage = ChatColor.DARK_AQUA + "Spectator" + (spectators.size() > 1 ? "s: " : ": ") + spect.toString();
	    
	    winner.sendMessage(winnerMessage);
	    winner.spigot().sendMessage(l1);
	    if (duel.hasSpectator()) winner.sendMessage(spectatorMessage);
	    if (looser != null) {
	    	looser.sendMessage(winnerMessage);
	    	looser.spigot().sendMessage(l1);
	    	if (duel.hasSpectator()) looser.sendMessage(spectatorMessage);
	    }
	    
	    Iterator<Player> its = spectators.iterator();
	    while (its.hasNext()) {
			Player spectator = its.next();
			
			spectator.sendMessage(winnerMessage);
			spectator.spigot().sendMessage(l1);
			if (duel.hasSpectator()) spectator.sendMessage(spectatorMessage);
			its.remove();
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onInvsClick(InventoryClickEvent e) {
		if (e.getInventory().getName().endsWith("'s Inventory")) {
			e.setCancelled(true);
			
			/*if (e.getCurrentItem() != null && e.getCurrentItem().getType() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null) {
				if (e.getCurrentItem().getItemMeta().getDisplayName().endsWith("'s Inventory")) {
					String oldOpponentItemName = e.getCurrentItem().getItemMeta().getDisplayName().split("'")[0];
					String semiRealOldOpponentItemName = oldOpponentItemName.substring(2, oldOpponentItemName.length());
					String realOldOpponentItemName = semiRealOldOpponentItemName.substring(0, semiRealOldOpponentItemName.length() - 2);
					UUID realOldOpponentItemUUID = Bukkit.getOfflinePlayer(realOldOpponentItemName).getUniqueId();
					
					openInv((Player) e.getWhoClicked(), realOldOpponentItemUUID);
				}
			}*/
		}
	}
    
	private String convertToPotionFormat(long paramLong) {
		if (paramLong < 0L) {
			return null;
		}
		return String.format("%01dm %02ds", new Object[] { Long.valueOf(paramLong / 60L), Long.valueOf(paramLong % 60L) });
	}
	
	private String convertToRoman(int N) {
		String roman = "";
		for (int i = 0; i < numbers.length; i++) {
			while (N >= numbers[i]) {
				roman = roman + letters[i];
				N -= numbers[i];
			}
		}
		return roman;
	}
	
	private int[] numbers = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
	private String[] letters = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
}
