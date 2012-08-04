package tehtros.bukkit.TCaster;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import tehtros.bukkit.Exceptions.FailedConfigSave;
import tehtros.bukkit.Exceptions.NoNameSupplied;
import tehtros.bukkit.Exceptions.NotValidColor;
import tehtros.bukkit.TCastAPI.TCastAPI;
import tehtros.bukkit.TCaster.Metrics.Graph;

/**
 * 
 * This plugin lets you broadcast messages from the server. With style.
 * 
 * @author tehtros
 * 
 */
public class TCaster extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	private int sentMessages = 0;
	
	TCastAPI api = new TCastAPI(this);

	public void onEnable() {
		try {
			Metrics mets = new Metrics(this);
			
			Graph graph = mets.createGraph("Extra Stats");
			
			graph.addPlotter(new Metrics.Plotter("Messages Sent") {
		        @Override
		        public int getValue() {
		            return sentMessages;
		        }
		    });
			
			mets.start();
		} catch(IOException e) {
			log.warning("[TCaster] Metrics fails to start!");
		}
	}

	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("tcast")) {
			if(sender.hasPermission("tcaster.tcast")) {
				String text = "";
				for(String words : args) {
					if(text.length() > 0)
						text += " ";
					text += words;
				}
				api.tcast(text);
				sentMessages++;
			}
		}

		if(cmd.getName().equalsIgnoreCase("tcastname")) {
			if(sender.hasPermission("tcaster.name")) {
				if(args.length != 0) {
					String newname = "";
					for(String words : args) {
						if(!newname.isEmpty()) {
							newname += " ";
						}
						newname += words;
					}

					try {
						api.tcastname(newname);
					} catch(NoNameSupplied e) {
						sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "You must supply a name! /tcastname [name]");
					}
					sender.sendMessage(api.colors(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.DARK_PURPLE + api.getTCastName() + ChatColor.YELLOW + " is ready to chat!"));
				} else {
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "You must supply a name! /tcastname [name]");
				}
			}

		}

		if(cmd.getName().equalsIgnoreCase("tcastcolor")) {
			if(sender.hasPermission("tcaster.color")) {
				try {
					api.tcastcolor(args[0]);
				} catch(NotValidColor e) {
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "You must supply a valid colorcode! (Ex. &a)");
				} catch(FailedConfigSave e) {
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "The new config failed to save!");
				}
				
				sender.sendMessage(api.colors(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "The chat color has been changed to " + api.getChatColor() + "THIS" + ChatColor.YELLOW + "!"));
			}
		}

		if(cmd.getLabel().equals("tcastreload")) {
			if(sender.hasPermission("tcaster.reload")) {
				if(api.tcastreload()) {
					sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.YELLOW + "has been reloaded!");
					sender.sendMessage(api.colors(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "TCaster" + ChatColor.GOLD + "] " + ChatColor.DARK_PURPLE + api.getTCastName() + ChatColor.YELLOW + " is ready to chat!"));
				}
			}
		}
		return true;
	}
}
