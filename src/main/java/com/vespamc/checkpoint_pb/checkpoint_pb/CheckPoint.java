package com.vespamc.checkpoint_pb.checkpoint_pb;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CheckPoint extends JavaPlugin {

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {

		if (!(s instanceof Player)) {
			s.sendMessage(msg("&cOnly ingame players can use this command!"));
			return false;
		}

		Player p = (Player) s;

		if (cmd.getName().equalsIgnoreCase("checkpoint") || cmd.getName().equalsIgnoreCase("cp")) {
			if (args.length == 0) {
				p.sendMessage(msg("&cUsage:\n&c /cp set | Sets your checkpoint\n &c/cp go | Goes to your checkpoint"));
				return false;
			}

			if (args[0].equalsIgnoreCase("set")) {
				if (args.length == 1) {
					if (!p.hasPermission("checkpoint.set")) {
						p.sendMessage(msg("&cYou do not have enough permissions to do this!"));
						return false;
					}
					setCP(p, p.getLocation());

					p.sendMessage(msg("&aSuccessfully set your checkpoint!"));
				} else {
					if (args.length == 2) {
						if (!p.hasPermission("checkpoint.set.others")) {
							p.sendMessage(msg("&cYou do not have permission to do this!"));
							return false;
						}
						setCP(getServer().getOfflinePlayer(args[1]), p.getLocation());
						p.sendMessage(
								msg("&aSuccessfully set the checkpoint of " + args[1] + " to your current location"));

					}
				}
			} else if (args[0].equalsIgnoreCase("go")) {

				if (!p.hasPermission("checkpoint.go")) {
					p.sendMessage(msg("&cYou do not have enough permissions to do this!"));
					return false;
				}
				if (getCP(p) == null) {
					p.sendMessage(msg("&cYou do not have a checkpoint!"));
					return false;
				}
				p.teleport(getCP(p));
				p.sendMessage(msg("&aTaking you to your last checkpoint!"));
			} else if (args[0].equalsIgnoreCase("tp")) {
				if (!p.hasPermission("checkpoint.tp")) {
					p.sendMessage(msg("&cYou do not have enough permissions to do this!"));
					return false;
				}
				if (args.length != 3) {
					p.sendMessage(msg("&cUsage: /cp tp <player> <target>"));
					return false;
				}

				OfflinePlayer op = getServer().getOfflinePlayer(args[1]);
				OfflinePlayer otarget = getServer().getOfflinePlayer(args[2]);

				if (!op.isOnline()) {
					p.sendMessage(msg("&cThat player isn't online!"));
					return false;
				}

				Player player = getServer().getPlayer(args[1]);

				if (getCP(otarget) == null) {
					p.sendMessage(msg("&cThat player doesn't have a checkpoint!"));
					return false;
				}

				player.teleport(getCP(otarget));

				p.sendMessage(msg("&aTeleported " + args[1] + " to the checkpoint of " + args[2]));
			}
		}

		return false;
	}

	public void setCP(Player p, Location loc) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		float pitch = loc.getPitch(), yaw = loc.getYaw();
		String world = loc.getWorld().getName();

		getConfig().set(p.getUniqueId().toString() + ".x", x);
		getConfig().set(p.getUniqueId().toString() + ".y", y);
		getConfig().set(p.getUniqueId().toString() + ".z", z);
		getConfig().set(p.getUniqueId().toString() + ".world", world);
		getConfig().set(p.getUniqueId().toString() + ".pitch", pitch);
		getConfig().set(p.getUniqueId().toString() + ".yaw", yaw);

		saveConfig();

	}

	public void setCP(OfflinePlayer p, Location loc) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		float pitch = loc.getPitch(), yaw = loc.getYaw();
		String world = loc.getWorld().getName();

		getConfig().set(p.getUniqueId().toString() + ".x", x);
		getConfig().set(p.getUniqueId().toString() + ".y", y);
		getConfig().set(p.getUniqueId().toString() + ".z", z);
		getConfig().set(p.getUniqueId().toString() + ".world", world);
		getConfig().set(p.getUniqueId().toString() + ".pitch", pitch);
		getConfig().set(p.getUniqueId().toString() + ".yaw", yaw);

		saveConfig();

	}

	public Location getCP(Player p) {
		if (!getConfig().contains(p.getUniqueId().toString())) {
			return null;
		}

		return new Location(getServer().getWorld(getConfig().getString(p.getUniqueId().toString() + ".world")),
				getConfig().getDouble(p.getUniqueId().toString() + ".x"),
				getConfig().getDouble(p.getUniqueId().toString() + ".y"),
				getConfig().getDouble(p.getUniqueId().toString() + ".z"),
				(float) getConfig().getDouble(p.getUniqueId().toString() + ".yaw"),
				(float) getConfig().getDouble(p.getUniqueId().toString() + ".pitch"));
	}

	public Location getCP(OfflinePlayer p) {
		if (!getConfig().contains(p.getUniqueId().toString())) {
			return null;
		}

		return new Location(getServer().getWorld(getConfig().getString(p.getUniqueId().toString() + ".world")),
				getConfig().getDouble(p.getUniqueId().toString() + ".x"),
				getConfig().getDouble(p.getUniqueId().toString() + ".y"),
				getConfig().getDouble(p.getUniqueId().toString() + ".z"),
				(float) getConfig().getDouble(p.getUniqueId().toString() + ".yaw"),
				(float) getConfig().getDouble(p.getUniqueId().toString() + ".pitch"));
	}

	private String msg(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
