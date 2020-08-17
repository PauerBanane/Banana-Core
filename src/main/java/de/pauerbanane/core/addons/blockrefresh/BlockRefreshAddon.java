package de.pauerbanane.core.addons.blockrefresh;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.addons.blockrefresh.commands.BlockRefreshCommand;
import de.pauerbanane.core.addons.blockrefresh.listener.BlockRefreshListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockRefreshAddon extends Addon {

    private static BlockRefreshAddon instance;

    private HashMap<Material, RefreshChain> chains;

    private HashMap<Block, BlockRefreshCountdown> countdowns;
    private FileLoader config;

    @Override
    public void onEnable() {
        instance = this;
        this.countdowns = Maps.newHashMap();
        this.chains = Maps.newHashMap();
        ConfigurationSerialization.registerClass(RefreshChain.class, "refreshchain");
        load();
        commandSetup();

        registerListener(new BlockRefreshListener(this));
    }

    public void addCountdown(Block block, Material current) {
        if (countdowns.containsKey(block)) {
            countdowns.get(block).restart(current);
        } else if (getChain(block.getType()).isEnabled()) {
            RefreshChain chain = getChain(block.getType());
            countdowns.put(block, new BlockRefreshCountdown(block, chain, current));
        }
    }

    public void removeCountdown(Block block) {
        if (!countdowns.containsKey(block)) return;
        if (countdowns.get(block).isRunning()) countdowns.get(block).forceFinish();
        countdowns.remove(block);
    }

    public RefreshChain getChain(Material material) {
        return chains.get(material);
    }

    public boolean isInCountdown(Block block) {
        return countdowns.containsKey(block);
    }

    public boolean isInChain(Material material) {
        return chains.containsKey(material);
    }

    private void commandSetup() {
        commandManager.getCommandCompletions().registerCompletion("refreshchain", c -> {
            ArrayList<String> chains = Lists.newArrayList();
            this.chains.keySet().forEach(chain -> chains.add(chain.toString()));

            return ImmutableList.copyOf(chains);
        });

        commandManager.getCommandContexts().registerContext(RefreshChain.class, c -> {
            final String tag = c.popFirstArg();
            Material material = Material.getMaterial(tag);
            if (material != null && chains.containsKey(material)) {
                return chains.get(material);
            } else
                throw new InvalidCommandArgument("Diese RefreshChain existiert nicht.");
        });

        registerCommand(new BlockRefreshCommand(this));
    }

    private void load() {
        int amount = 0;
        this.config = new FileLoader(getAddonFolder(), "BlockRefresh.yml");

        if (config.isSet("chains")) {
            ConfigurationSection section = config.getConfigurationSection("chains");
            for (String entry : section.getKeys(false)) {
                RefreshChain chain = section.getSerializable(entry, RefreshChain.class);
                if (chain == null) continue;
                if (registerChain(chain)) {
                    amount++;
                } else
                    plugin.getLogger().warning("Failed to load RefreshChain: " + chain);
            }
        }
    }

    public void forceFinishAndRemove(RefreshChain chain) {
        ArrayList<Block> toRemove = Lists.newArrayList();
        for (int i = 0; i < countdowns.size(); i++) {
            Block block = (Block) countdowns.keySet().toArray()[i];
            BlockRefreshCountdown countdown = countdowns.get(block);
            if (chain == null || countdown.getChain() == chain) {
                toRemove.add(block);
                countdown.forceFinish();
            }
        }

        toRemove.forEach(b -> countdowns.remove(b));
    }

    private void save() {
        config.set("chains", null);
        for (int i = 0; i < chains.size(); i++) {
            config.set("chains." + i, chains.values().toArray()[i]);
        }

        config.save();
    }

    public boolean registerChain(RefreshChain chain) {
        System.out.println("Registerchain: " + chain);
        if (chains.containsKey(chain.getFirst())) return false;
        System.out.println("Registered: " + chain.getFirst().toString());
        chains.put(chain.getFirst(), chain);
        return true;
    }

    public void unregisterChain(RefreshChain chain) {
        chains.remove(chain.getFirst());
    }

    @Override
    public void onDisable() {
        forceFinishAndRemove(null);
        save();
    }

    @Override
    public void onReload() {

    }

    public HashMap<Material, RefreshChain> getChains() {
        return chains;
    }

    public static BlockRefreshAddon getInstance() {
        return instance;
    }
}
