package de.pauerbanane.core.data;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import de.pauerbanane.core.BananaCore;

public class FlagManager {

    private static FlagManager instance;

    private BananaCore plugin;

    private FlagRegistry registry;

    private StateFlag blockrefreshFlag;

    private StringFlag regionNameFlag;

    public FlagManager(BananaCore plugin) {
        instance = this;
        this.plugin = plugin;
        this.registry = WorldGuard.getInstance().getFlagRegistry();

        load();
    }

    private void load() {
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("blockrefresh", false);
            registry.register(flag);
            blockrefreshFlag = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("blockrefresh");
            if (existing instanceof StateFlag) {
                blockrefreshFlag = (StateFlag) existing;
            } else {
                plugin.getLogger().warning("Failed to load WorldGuard Flags - Shutting down");
                plugin.getServer().shutdown();
            }
        }

        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StringFlag flag = new StringFlag("Region-Name", "§6KnicksCraft");
            registry.register(flag);
            regionNameFlag = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("Region-Name");
            if (existing instanceof StringFlag) {
                regionNameFlag = (StringFlag) existing;
            } else {
                plugin.getLogger().warning("Failed to load WorldGuard Flags - Shutting down");
                plugin.getServer().shutdown();
            }
        }
    }

    public static FlagManager getInstance() {
        return instance;
    }

    public StateFlag getBlockrefreshFlag() {
        return blockrefreshFlag;
    }

    public StringFlag getRegionNameFlag() {
        return regionNameFlag;
    }
}
