package de.pauerbanane.core.addons.carriages;

import com.google.common.collect.ImmutableList;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.carriages.commands.CarriageCommand;
import de.pauerbanane.core.addons.carriages.listener.CarriageListener;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Collection;
import java.util.stream.Collectors;

public class Carriages extends Addon {

    private static Carriages instance;

    private CarriageManager manager;

    @Override
    public void onEnable() {
        instance = this;
        ConfigurationSerialization.registerClass(Carriage.class, "Carriage");
        ConfigurationSerialization.registerClass(CarriageLine.class, "CarriageLine");
        this.manager = new CarriageManager(this);

        registerCommandContext();

        registerCommand(new CarriageCommand(this));

        registerListener(new CarriageListener(manager));
    }

    @Override
    public void onDisable() {
        manager.saveAll();
    }

    @Override
    public void onReload() {

    }

    private void registerCommandContext() {
        commandManager.getCommandContexts().registerContext(CarriageLine.class, c -> {
            final String tag = c.popFirstArg();
            if(manager.getCarriageLine(tag) != null) {
                return manager.getCarriageLine(tag);
            } else
                throw new InvalidCommandArgument("Invalid CarriageLine specified");
        });

        commandManager.getCommandCompletions().registerCompletion("carriageline", c -> {
            return ImmutableList.copyOf(manager.getCarriageLineNames());
        });
    }

    public CarriageManager getManager() {
        return manager;
    }

    public static Carriages getInstance() {
        return instance;
    }
}
