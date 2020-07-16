package de.pauerbanane.core.addons.votifier;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.votifier.crypto.RSAIO;
import de.pauerbanane.core.addons.votifier.crypto.RSAKeygen;
import de.pauerbanane.core.addons.vote.VoteAddon;
import de.pauerbanane.core.addons.votifier.listener.VoteListener;
import de.pauerbanane.core.addons.votifier.thread.VoteReceiver;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.security.KeyPair;
import java.util.logging.Logger;

public class Votifier extends Addon {

    public static Logger LOG;

    private static Votifier instance;

    private VoteReceiver voteReceiver;

    private KeyPair keyPair;

    private BananaCore plugin;

    @Override
    public void onEnable() {
        plugin = BananaCore.getInstance();
        LOG = plugin.getLogger();
        instance = this;
        File rsaDirectory = new File(plugin.getDataFolder() + File.separator + "rsa");
        try {
            if (!rsaDirectory.exists()) {
                if (rsaDirectory.mkdirs()) {
                    this.keyPair = RSAKeygen.generate(2048);
                    RSAIO.save(rsaDirectory, this.keyPair);
                }
            } else {
                this.keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        File config = new File(getAddonFolder(), "votifier.yml");
        if (!config.exists())
            try {
                LOG.info("Configuring Votifier for the first time...");
                config.createNewFile();
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(config);
                yamlConfiguration.set("host", "0.0.0.0");
                yamlConfiguration.set("port", Integer.valueOf(8193));
                yamlConfiguration.set("debug", Boolean.valueOf(false));
                yamlConfiguration.save(config);
                LOG.info("------------------------------------------------------------------------------");
                LOG.info("Assigning Votifier to listen on port 8192. If you are hosting Craftbukkit on a");
                LOG.info("shared server please check with your hosting provider to verify that this port");
                LOG.info("is available for your use. Chances are that your hosting provider will assign");
                LOG.info("a different port, which you need to specify in config.yml");
                LOG.info("------------------------------------------------------------------------------");
            } catch (Exception ex) {
                LOG.severe("Error creating configuration file");
                gracefulExit();
                return;
            }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(config);
        try {
            this.voteReceiver = new VoteReceiver(this, configuration.getString("host"), configuration.getInt("port"));
            this.voteReceiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerListener(new VoteListener(BananaCore.getInstance()));
    }

    @Override
    public void onDisable() {
        if (this.voteReceiver != null)
            this.voteReceiver.shutdown();
        LOG.info("VoteReceiver disabled");
    }

    @Override
    public void onReload() {

    }

    private void gracefulExit() {
        LOG.warning("Votifier did not initialize properly!");
    }

    public static Votifier getInstance() {
        return instance;
    }

    public String getVersion() {
        return "1.15.2";
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public VoteReceiver getVoteReceiver() {
        return this.voteReceiver;
    }

    public static Logger getLogger() {
        return LOG;
    }


}
