package tehtros.bukkit.TCastAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import tehtros.bukkit.Exceptions.FailedConfigLoad;
import tehtros.bukkit.Exceptions.FailedConfigSave;
import tehtros.bukkit.Exceptions.NoNameSupplied;
import tehtros.bukkit.Exceptions.NotValidColor;

/**
 * 
 * Finally, a TCast API! Documentation soon to come.
 * 
 * @author tehtros
 * 
 */
public class TCastAPI extends JavaPlugin {
	public final Plugin plugin;

	private String name;
	private String chatcolor;
	private String trackip;

	private FileConfiguration config = null;
	private File configFile = null;

	public TCastAPI(final Plugin plugin) {
		if(trackip.equals("true")) {
			plugin.getLogger().info("NOTICE: The \"logip\" option in the config.ylm is set to TRUE! We will collect the server's IP address.");
		}
		this.plugin = plugin;
		try {
			tcastreload();
		} catch(FailedConfigLoad e) {
			e.printStackTrace();
		}
	}

	private String config(String input) throws FileNotFoundException, IOException, InvalidConfigurationException, FailedConfigLoad {
		// Check for config. + Other crap.
		if(configFile == null) {
			configFile = new File(new File("plugins", "TCastAPI"), "TCastAPI.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// Reload the config.
		config.load(configFile);

		// Reload the individual thingys.
		String tname = config.getString("name");
		if(tname == null) {
			config.set("name", "&a[&bTCaster&a]");
		}
		tname = config.getString("name", "&4[&cERROR&4]");

		String tcolor = config.getString("chatcolor");
		if(tcolor == null) {
			config.set("chatcolor", "&a");
		}
		tcolor = config.getString("chatcolor", "&a");

		String tip = config.getString("logip");
		if(tip == null) {
			config.set("logip", "&a");
		}
		tcolor = config.getString("logip", "true");

		// Save teh config.
		try {
			config.save(configFile);
		} catch(IOException e) {
			e.printStackTrace();
		}

		// Get the individual thingys.
		if(!input.isEmpty()) {
			if(input.equalsIgnoreCase("name")) {
				return tname;
			} else if(input.equalsIgnoreCase("chatcolor")) {
				return tcolor;
			} else if(input.equalsIgnoreCase("trackip")) {
				return tip;
			} else {
				return "";
			}
		}
		return "";
	}

	/**
	 * TCasts a message.
	 * 
	 * @param says
	 */
	public void tcast(String says) {
		if(says != "") {
			plugin.getServer().broadcastMessage(colors(name + ": " + chatcolor + says));
		}
	}

	/**
	 * Changes the name of the TCaster.
	 * 
	 * @param newname
	 * @return true if the name was successfully changed.
	 * @throws NoNameSupplied
	 */
	public boolean tcastname(String newname) throws NoNameSupplied {
		if(!newname.isEmpty()) {
			config.set("name", newname);
			try {
				config.save(configFile);
			} catch(IOException e) {
				e.printStackTrace();
			}
			name = config.getString("name");
			return true;
		} else {
			throw new NoNameSupplied();
		}
	}

	/**
	 * Changes the default color of the chat text. Parameter "newcolor" MUST be
	 * in the form of a color code. (ex: &a)
	 * 
	 * @param newcolor
	 * @return true if the color was successfully changed.
	 * @throws NotValidColor
	 * @throws FailedConfigSave
	 */
	public boolean tcastcolor(String newcolor) throws NotValidColor, FailedConfigSave {
		if(newcolor.length() != 2) {
			throw new NotValidColor();
		} else {
			chatcolor = newcolor;
		}
		config.set("color", newcolor);
		try {
			config.save(configFile);
		} catch(IOException e) {
			throw new FailedConfigSave();
		}
		chatcolor = config.getString("color");
		return true;
	}

	/**
	 * Reloads the TCast API configuration.
	 * 
	 * @return true if the configuration was successfully reloaded.
	 * @throws FailedConfigLoad
	 */
	public boolean tcastreload() throws FailedConfigLoad {
		try {
			name = config("name");
			chatcolor = config("chatcolor");
		} catch(FileNotFoundException e) {
			throw new FailedConfigLoad();
		} catch(IOException e) {
			throw new FailedConfigLoad();
		} catch(InvalidConfigurationException e) {
			throw new FailedConfigLoad();
		} catch(FailedConfigLoad e) {
			throw new FailedConfigLoad();
		}
		return true;
	}

	public String getTCastName() {
		return name;
	}

	public String getChatColor() {
		return chatcolor;
	}

	public String colors(String color) {
		return color.replaceAll("&(?=[0-9a-fA-FkKmMoOlLnNrR])", "\u00a7");
	}
}
