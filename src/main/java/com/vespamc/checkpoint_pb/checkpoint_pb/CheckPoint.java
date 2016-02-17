package com.vespamc.checkpoint_pb.checkpoint_pb;

import java.util.UUID;

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
					setCP(p.getUniqueId(), p.getLocation());

					p.sendMessage(msg("&aSuccessfully set your checkpoint!"));
				} else {
					if (args.length == 2) {
						if (!p.hasPermission("checkpoint.set.others")) {
							p.sendMessage(msg("&cYou do not have permission to do this!"));
							return false;
						}
						setCP(getServer().getOfflinePlayer(args[1]).getUniqueId(), p.getLocation());
						p.sendMessage(
								msg("&aSuccessfully set the checkpoint of " + args[1] + " to your current location"));

					}
				}
			} else if (args[0].equalsIgnoreCase("go")) {

				if (!p.hasPermission("checkpoint.go")) {
					p.sendMessage(msg("&cYou do not have enough permissions to do this!"));
					return false;
				}
				if (getCP(p.getUniqueId()) == null) {
					p.sendMessage(msg("&cYou do not have a checkpoint!"));
					return false;
				}
				p.teleport(getCP(p.getUniqueId()));
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

				if (getCP(otarget.getUniqueId()) == null) {
					p.sendMessage(msg("&cThat player doesn't have a checkpoint!"));
					return false;
				}

				player.teleport(getCP(otarget.getUniqueId()));

				p.sendMessage(msg("&aTeleported " + args[1] + " to the checkpoint of " + args[2]));
			}
		}

		return false;
	}

	public void setCP(UUID id, Location loc) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		float pitch = loc.getPitch(), yaw = loc.getYaw();
		String world = loc.getWorld().getName();

		getConfig().set(id.toString() + ".x", x);
		getConfig().set(id.toString() + ".y", y);
		getConfig().set(id.toString() + ".z", z);
		getConfig().set(id.toString() + ".world", world);
		getConfig().set(id.toString() + ".pitch", pitch);
		getConfig().set(id.toString() + ".yaw", yaw);

		saveConfig();

	}

	public Location getCP(UUID id) {
		if (!getConfig().contains(id.toString())) {
			return null;
		}

		return new Location(getServer().getWorld(getConfig().getString(id.toString() + ".world")),
				getConfig().getDouble(id.toString() + ".x"), getConfig().getDouble(id.toString() + ".y"),
				getConfig().getDouble(id.toString() + ".z"), (float) getConfig().getDouble(id.toString() + ".yaw"),
				(float) getConfig().getDouble(id.toString() + ".pitch"));
	}

	private String msg(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}