package us.noks.smallpractice.objects.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.minecraft.util.com.google.common.collect.Maps;

public class EloManager {
	
	public static EloManager instance = new EloManager();
	public static EloManager getInstance() {
		return instance;
	}
	
	public Map<UUID, Integer> playersElo = Maps.newHashMap();
	private int DEFAULT_ELO = 1000;
	
	public void tranferElo(UUID winnerUUID, UUID loserUUID) {
		Player winner = Bukkit.getPlayer(winnerUUID);
		Player loser = Bukkit.getPlayer(loserUUID);
		
		int scoreChange = 0;
		double expectedp = 1.0D / (1.0D + Math.pow(10.0D, (PlayerManager.get(winner).getElo() - PlayerManager.get(loser).getElo()) / 400.0D));

		scoreChange = (int) (expectedp * 32.0D);
		scoreChange = scoreChange > 25 ? 25 : scoreChange;
		scoreChange = scoreChange < 4 ? 4 : scoreChange;
		
		PlayerManager.get(winner).addElo(scoreChange);
		PlayerManager.get(loser).removeElo(scoreChange);

		winner.sendMessage(ChatColor.GOLD + "Elo Changes: " + ChatColor.GREEN + winner.getName() + " (+" + scoreChange + ") " + ChatColor.RED + loser.getName() + " (-" + scoreChange + ")");
		loser.sendMessage(ChatColor.GOLD + "Elo Changes: " + ChatColor.GREEN + winner.getName() + " (+" + scoreChange + ") " + ChatColor.RED + loser.getName() + " (-" + scoreChange + ")");
		
		updatePlayerElo(winner);
		updatePlayerElo(loser);
	}
	
	public int getPlayerElo(UUID uuid) {
		if (!playersElo.containsKey(uuid)) {
			playersElo.put(uuid, DEFAULT_ELO);
		}
		return playersElo.get(uuid);
	}
	
	private void updatePlayerElo(Player player) {
		playersElo.put(player.getUniqueId(), PlayerManager.get(player).getElo());
	}
}
