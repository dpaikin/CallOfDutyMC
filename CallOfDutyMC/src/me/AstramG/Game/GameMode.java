package me.AstramG.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.CallOfDuty.GameState;
import me.AstramG.CallOfDutyMC.Gun;
import me.AstramG.MultiMap.MultiMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameMode implements Listener {

	CallOfDuty cod;

	protected final MultiMap<String, Integer, Integer> primaryAmmo = new MultiMap<String, Integer, Integer>();
	protected final MultiMap<String, Integer, Integer> secondaryAmmo = new MultiMap<String, Integer, Integer>();
	protected MultiMap<UUID, String, Double> bulletDamage = new MultiMap<UUID, String, Double>();
	protected MultiMap<String, Integer, Integer> kdr = new MultiMap<String, Integer, Integer>();

	protected final List<String> reloadPrimary = new ArrayList<String>();
	protected final List<String> reloadSecondary = new ArrayList<String>();

	protected List<String> fireRateP = new ArrayList<String>();
	protected List<String> fireRateS = new ArrayList<String>();

	protected List<String> invulnerable = new ArrayList<String>();

	protected int timeLeft;

	private GameType gameType;
	private static GameMode gameMode;
	protected GameRunnable gr;

	public GameMode(CallOfDuty cod, int timeLeftInSeconds, GameType gameType) {
		GameMode.gameMode = this;
		this.gameType = gameType;
		this.cod = cod;
		this.timeLeft = timeLeftInSeconds;
	}

	public static GameMode getGameMode() {
		return gameMode;
	}

	public abstract void start();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void shootEvent(final PlayerInteractEvent event) {
		if (CallOfDuty.instance.began == false)
			return;
		if (event.getItem() == null)
			return;
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (gameType == GameType.GRIND) {
				Grind grind = CallOfDuty.instance.grind;
				if (event.getClickedBlock().getType() == Material.HUGE_MUSHROOM_1) {
					if (player.getInventory().contains(Material.SKULL_ITEM)) {
						for (int i = 0; i < 5; i++)
							player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.SMOKE, 1);
						if (CallOfDuty.instance.blueTeam.contains(player.getName())) {
							grind.blueScore++;
						} else {
							grind.redScore++;
						}
						player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've deposited a head into the bank!");
						if (grind.redScore == 100) {
							endGame(false);
						}
						if (grind.blueScore == 100) {
							endGame(false);
						}
						player.getInventory().remove(Material.SKULL_ITEM);
						player.updateInventory();
					}
					event.setCancelled(true);
					return;
				}
			}
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (CallOfDuty.instance.gm.isGun(ChatColor.stripColor(event.getItem().getItemMeta().getLore().get(0)))) {
				final Gun gun = CallOfDuty.instance.gm.getGun(ChatColor.stripColor(event.getItem().getItemMeta().getLore().get(0)));
				if (gun.gunType.equalsIgnoreCase("shotgun")) {
					player.setVelocity(player.getVelocity().multiply(-.5));
				}
				if (gun.gunType.equalsIgnoreCase("secondary")) {
					shootSecondary(gun, player, event.getItem());
				} else {
					shootPrimary(gun, player, event.getItem());
				}
			}
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (CallOfDuty.instance.gm.isGun(ChatColor.stripColor(event.getItem().getItemMeta().getLore().get(0)))) {
				Gun gun = CallOfDuty.instance.gm.getGun(ChatColor.stripColor(event.getItem().getItemMeta().getLore().get(0)));
				if (!(player.hasPotionEffect(PotionEffectType.SLOW))) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, gun.zoom - 1, true));
				} else {
					player.removePotionEffect(PotionEffectType.SLOW);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void shootPrimary(final Gun gun, final Player player, final ItemStack item) {
		Random rand = new Random();
		if (primaryAmmo.getSecondValue(player.getName()) >= 0) {
			if (primaryAmmo.getFirstValue(player.getName()) > 0) { // Shoot
				if (!(reloadPrimary.contains(player.getName()))) {
					if (!(fireRateP.contains(player.getName()))) {
						for (int i = 0; i < gun.bullets; i++) {
							Snowball sb = player.throwSnowball();
							bulletDamage.put(sb.getUniqueId(), player.getName(), gun.damage);
							if (gameType.equals(GameType.CRANKED)) {
								if (((Cranked) this).invulnerable.contains(player.getName())) {
									bulletDamage.put(sb.getUniqueId(), player.getName(), gun.damage);
								}
							}
							if (gun.bullets > 1) {
								double number = rand.nextInt(26) / 100;
								sb.setVelocity(player.getLocation().getDirection().normalize().multiply(gun.range - number));
							} else {
								sb.setVelocity(player.getLocation().getDirection().normalize().multiply(gun.range));
							}
						}
						fireRateP.add(player.getName());
						player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1, 2);
						primaryAmmo.setFirstValue(player.getName(), primaryAmmo.getFirstValue(player.getName()) - 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + primaryAmmo.getFirstValue(player.getName()) + "> " + " <" + primaryAmmo.getSecondValue(player.getName()) + ">");
						item.setItemMeta(meta);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
							public void run() {
								fireRateP.remove(player.getName());
							}
						}, Math.round(20 * gun.fireRate) + 1);
					}
				}
			} else { // Reload
				if (primaryAmmo.getSecondValue(player.getName()) > 0) {
					primaryAmmo.setFirstValue(player.getName(), -1);
					reloadPrimary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadPrimary.remove(player.getName());
							ItemStack item2 = player.getInventory().getItem(0);
							ItemMeta meta2 = item2.getItemMeta();
							meta2.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
							item2.setItemMeta(meta2);
							primaryAmmo.setFirstValue(player.getName(), gun.clip);
							primaryAmmo.setSecondValue(player.getName(), primaryAmmo.getSecondValue(player.getName()) - gun.clip);
						}
					}, gun.reload);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void shootSecondary(final Gun gun, final Player player, final ItemStack item) {
		Random rand = new Random();
		if (secondaryAmmo.getSecondValue(player.getName()) >= 0) {
			if (secondaryAmmo.getFirstValue(player.getName()) > 0) { // Shoot
				if (!(reloadSecondary.contains(player.getName()))) {
					if (!(fireRateS.contains(player.getName()))) {
						for (int i = 0; i < gun.bullets; i++) {
							Snowball sb = player.throwSnowball();
							bulletDamage.put(sb.getUniqueId(), player.getName(), gun.damage);
							if (gameType.equals(GameType.CRANKED)) {
								if (((Cranked) this).invulnerable.contains(player.getName())) {
									bulletDamage.put(sb.getUniqueId(), player.getName(), gun.damage);
								}
							}
							if (gun.bullets > 1) {
								double number = rand.nextInt(26) / 100;
								sb.setVelocity(player.getLocation().getDirection().normalize().multiply(gun.range - number));
							} else {
								sb.setVelocity(player.getLocation().getDirection().normalize().multiply(gun.range));
							}
						}
						fireRateS.add(player.getName());
						player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1, 2);
						secondaryAmmo.setFirstValue(player.getName(), secondaryAmmo.getFirstValue(player.getName()) - 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + secondaryAmmo.getFirstValue(player.getName()) + "> " + " <" + secondaryAmmo.getSecondValue(player.getName()) + ">");
						item.setItemMeta(meta);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
							public void run() {
								fireRateS.remove(player.getName());
							}
						}, Math.round(20 * gun.fireRate) + 1);
					}
				}
			} else { // Reload
				if (secondaryAmmo.getSecondValue(player.getName()) > 0) {
					secondaryAmmo.setFirstValue(player.getName(), -1);
					reloadSecondary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadSecondary.remove(player.getName());
							ItemStack item2 = player.getInventory().getItem(1);
							if (item2 != null) {
								ItemMeta meta2 = item2.getItemMeta();
								meta2.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
								item2.setItemMeta(meta2);
							}
							secondaryAmmo.setFirstValue(player.getName(), gun.clip);
							secondaryAmmo.setSecondValue(player.getName(), secondaryAmmo.getSecondValue(player.getName()) - gun.clip);
						}
					}, gun.reload);
				}
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = event.getItemDrop().getItemStack();
		if (CallOfDuty.instance.gm.isGun(ChatColor.stripColor(item.getItemMeta().getLore().get(0)))) {
			final Gun gun = CallOfDuty.instance.gm.getGun(ChatColor.stripColor(item.getItemMeta().getLore().get(0)));
			if (gun.gunType.equalsIgnoreCase("secondary")) {
				if (secondaryAmmo.getSecondValue(player.getName()) - gun.clip > 0) {
					reloadSecondary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadSecondary.remove(player.getName());
							meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
							item.setItemMeta(meta);
							secondaryAmmo.setSecondValue(player.getName(), secondaryAmmo.getSecondValue(player.getName()) - (gun.clip - secondaryAmmo.getFirstValue(player.getName())));
							secondaryAmmo.setFirstValue(player.getName(), gun.clip);
						}
					}, gun.reload);
				} else {
					reloadSecondary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadSecondary.remove(player.getName());
							meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
							item.setItemMeta(meta);
							secondaryAmmo.setFirstValue(player.getName(), secondaryAmmo.getSecondValue(player.getName()));
							secondaryAmmo.setSecondValue(player.getName(), 0);
						}
					}, gun.reload);
				}
			} else {
				if (primaryAmmo.getSecondValue(player.getName()) - gun.clip > 0) {
					reloadPrimary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadPrimary.remove(player.getName());
							meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
							item.setItemMeta(meta);
							primaryAmmo.setSecondValue(player.getName(), primaryAmmo.getSecondValue(player.getName()) - (gun.clip - primaryAmmo.getFirstValue(player.getName())));
							primaryAmmo.setFirstValue(player.getName(), gun.clip);
						}
					}, gun.reload);
				} else {
					reloadPrimary.add(player.getName());
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reloading...");
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
						public void run() {
							reloadPrimary.remove(player.getName());
							meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
							item.setItemMeta(meta);
							primaryAmmo.setFirstValue(player.getName(), primaryAmmo.getSecondValue(player.getName()));
							primaryAmmo.setSecondValue(player.getName(), 0);
						}
					}, gun.reload);
				}
			}
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getCause() != DamageCause.PROJECTILE) {
				Player player = (Player) event.getEntity();
				if (player.getHealth() - event.getDamage() <= 0) {
					respawn(player);
					event.setCancelled(true);
				}
			}
		}
	}

	public abstract void respawn(Player player);

	@EventHandler
	public void respawnEvent(PlayerRespawnEvent event) {
		respawn(event.getPlayer());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void getHit(EntityDamageByEntityEvent event) {
		if (CallOfDuty.instance.began == false)
			return;
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getDamager();
			Player player = (Player) event.getEntity();
			if (invulnerable.contains(player.getName())) {
				event.setDamage(0D);
				event.setCancelled(true);
				return;
			}
			if (bulletDamage.containsKey(snowball.getUniqueId())) {
				String thrower = bulletDamage.getFirstValue(snowball.getUniqueId());
				if (CallOfDuty.instance.am.isOnSameTeam(player, Bukkit.getPlayer(thrower))) {
					event.setCancelled(true);
					return;
				}
				double damage = bulletDamage.getSecondValue(snowball.getUniqueId());
				if (player.getHealth() - damage <= 0) {
					Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " was killed by " + thrower + "!");
					player.playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
					for (int i = 0; i < 5; i++) {
						Random random = new Random();
						int dX = random.nextInt(3) - 1;
						int dZ = random.nextInt(3) - 1;
						final Item item = player.getWorld().dropItem(player.getLocation().add(dX, .75, dZ), new ItemStack(Material.INK_SACK, 1, (byte) 1));
						item.setVelocity(player.getLocation().toVector().add(item.getLocation().toVector()).normalize().multiply(.15));
						item.setPickupDelay(1000);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
							public void run() {
								item.remove();
							}
						}, 15L);
					}
					Player killer = Bukkit.getPlayer(thrower);
					onKill(killer, player);
					respawn(player);
					killer.sendMessage(ChatColor.YELLOW + "You received 5 exp for the kill!");
					if (!(CallOfDuty.instance.kills.containsKey(killer.getName()))) {
						CallOfDuty.instance.kills.put(killer.getName(), 1);
					} else {
						CallOfDuty.instance.kills.put(killer.getName(), CallOfDuty.instance.kills.get(killer.getName()) + 1);
					}
					if (!(kdr.containsKey(killer.getName()))) {
						kdr.put(killer.getName(), 1, 0);
					} else {
						kdr.put(killer.getName(), kdr.getFirstValue(killer.getName()) + 1, kdr.getSecondValue(killer.getName()));
					}
					CallOfDuty.instance.awardExp(killer, 5);
				} else {
					event.setDamage(damage);
				}
			}
		}
	}

	public abstract void onKill(Player killer, Player killed);

	@EventHandler
	public void explosionEvent(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	public abstract void endGame(boolean timeRanOut);

	public abstract void updateScoreboard(Player player);

	public class GameRunnable extends BukkitRunnable {

		@Override
		public void run() {
			if (CallOfDuty.instance.state == GameState.SESSION) {
				if (CallOfDuty.instance.began == false)
					return;
				if (timeLeft == 0) {
					Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Game Over!");
					endGame(true);
					timeLeft = -1;
					CallOfDuty.instance.state = GameState.RESTARTING;
					CallOfDuty.instance.reset();
					return;
				}
				if (10 > timeLeft && timeLeft > 0) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 2);
					}
				}
				if (timeLeft % 60 == 0) {
					Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.YELLOW + "You have " + timeLeft / 60 + " minute(s) left!");
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					updateScoreboard(p);
				}
				timeLeft--;
			} else {
				cancel();
			}
		}

	}

	public enum GameType {
		CRANKED, GRIND, GUN_GAME
	}

}