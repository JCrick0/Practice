package net.frozenorb.potpvp.extras.ability;

import lombok.Setter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.extras.ability.items.Dome;
import net.frozenorb.potpvp.extras.ability.profile.Profile;
import net.frozenorb.potpvp.extras.ability.profile.task.TaskType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.cooldown.Cooldowns;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 30/06/2021 / 12:51 PM
 * HCTeams / net.frozenorb.potpvp.ability
 */
public abstract class Ability implements Listener {

	public Ability() {
		Bukkit.getPluginManager().registerEvents(this, PotPvPSI.getInstance());
	}

	public abstract Cooldowns cooldown();
	public abstract String name();
	public abstract String displayName();
	public abstract int data();
	public abstract Material mat();
	public abstract boolean glow();
	public abstract List<String> lore();
	public abstract List<String> foundInfo();

	@Setter private ItemStack stack = new ItemBuilder(mat()).lore(lore()).data(data()).name(CC.chat(displayName())).build();

	public ItemStack getStack() {
		if (glow()) {
			stack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		}
		return stack;
	}

	public boolean canAttack(Player attacker, Player damaged) {
		Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(attacker);
		if (Dome.antiAbility.onCooldown(attacker)) {
			attacker.sendMessage(CC.translate("&cYou cannot do this for &l" + Dome.antiAbility.getRemaining(attacker)));
			return false;
		}
		if (match != null) {
			if (match.getSpectators().contains(attacker.getUniqueId()))
				return false;
			for (MatchTeam team : match.getTeams()) {
				if (team.getAllMembers().contains(damaged.getUniqueId()) && team.getAllMembers().contains(attacker.getUniqueId())) {
					return true;
				}
			}
		}
		return true;
	}

	public boolean isClick(PlayerInteractEvent event, String click) {
		return event.getAction().name().contains(click);
	}

	public boolean canUse(Player player) {
		if (PotPvPSI.getInstance().getAbilityHandler().getAbilityCD().onCooldown(player)) {
			player.sendMessage(CC.chat("&cYou are currently on &eAbility Item" + "&c cooldown for &l" + PotPvPSI.getInstance().getAbilityHandler().getAbilityCD().getRemaining(player)));
			return false;
		}
		if (Dome.antiAbility.onCooldown(player)) {
			player.sendMessage(CC.translate("&cYou cannot do this for &l" + Dome.antiAbility.getRemaining(player)));
			return false;
		}
		if (cooldown().onCooldown(player)) {
			player.sendMessage(CC.chat("&cYou are currently on " + displayName() + "&c cooldown for &l" + cooldown().getRemaining(player)));
			return false;
		}

		return true;
	}

	public boolean isSimilar(PlayerInteractEvent event) {
		if (event.getItem() == null) return false;

		return event.getItem().isSimilar(getStack());

	}

	public boolean isSimilar(ItemStack item) {
		if (item == null) return false;

		return item.isSimilar(getStack());

	}

	public boolean isSimilar(PlayerInteractEvent event, ItemStack stack) {
		if (event.getItem() == null) return false;

		return event.getItem().isSimilar(stack);

	}

	public void addCooldown(Player player, int seconds) {
		cooldown().applyCooldown(player, seconds);
		PotPvPSI.getInstance().getAbilityHandler().getAbilityCD().applyCooldown(player, 10);
	}

	public boolean checkInstanceSnowball(Object instance) {
		return instance instanceof Snowball;
	}

	public boolean checkInstancePlayer(Object instance) {
		return instance instanceof Player;
	}

	public boolean checkInstanceEgg(Object instance) {
		return instance instanceof Egg;
	}

	public boolean isInDistance(Player player, Player target, int distance) {
		return player.getLocation().distance(target.getLocation()) < distance;
	}

	public boolean isInDistance(Location player, Location target, int distance) {
		return player.distance(target) < distance;
	}

	public void takeItem(Player player) {
		if (player.getItemInHand().getAmount() > 1) {
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		} else {
			player.setItemInHand(null);
		}
	}

	public void activateRunnable(TaskType type, Player player, Map<UUID, Integer> hits) {
		Profile profile = Profile.byUUID(player.getUniqueId());

		CompletableFuture.runAsync(() -> {
			hits.putIfAbsent(player.getUniqueId(), 1);
			hits.put(player.getUniqueId(), hits.get(player.getUniqueId()) + 1);

			// Check if their profile has any active antibuild hits
			if (profile.getTasks().get(type) != null) {
				profile.getTasks().get(type).cancel();
				profile.getTasks().remove(type);
			}

			// Add the task to the profile
			BukkitTask task = new BukkitRunnable() {
				@Override
				public void run() {
					hits.remove(player.getUniqueId());
				}
			}.runTaskLaterAsynchronously(PotPvPSI.getInstance(), 20 * 15);
			profile.getTasks().put(type, task);
		});
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getItem() == null)
			return;

		if (isSimilar(event.getItem())) {
			if (cooldown().onCooldown(event.getPlayer())) {
				event.getPlayer().sendMessage(CC.chat("&cYou are currently on " + displayName() + "&c cooldown for &l" + cooldown().getRemaining(event.getPlayer())));

			}
		}
	}
}
