package de.pauerbanane.core.addons.essentials;

import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.essentials.commands.*;
import de.pauerbanane.core.addons.essentials.commands.teleport.TeleportAllCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.TeleportCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.TeleportHereCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.TeleportPositionCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.home.DelhomeCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.home.HomeCommand;
import de.pauerbanane.core.addons.essentials.commands.teleport.home.SethomeCommand;
import de.pauerbanane.core.addons.essentials.commands.tpa.TeleportRequestManager;

public class Essentials extends Addon {



    @Override
    public void onEnable() {
        new TeleportRequestManager(this);

        registerCommand(new HomeCommand());
        registerCommand(new SethomeCommand());
        registerCommand(new DelhomeCommand());

        registerCommand(new TeleportAllCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new TeleportHereCommand());
        registerCommand(new TeleportPositionCommand());

        registerCommand(new BackCommand(this));
        registerCommand(new BonemealCommand());
        registerCommand(new ClearCommand());
        registerCommand(new EnderchestCommand());
        registerCommand(new FlyCommand());
        registerCommand(new GamemodeCommand());
        registerCommand(new HatCommand());
        registerCommand(new HealCommand());
        registerCommand(new MemoryCommand());
        registerCommand(new NightvisionCommand());
        registerCommand(new NoPickupCommand());
        registerCommand(new OpenInventoryCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new RepairCommand());
        registerCommand(new SeenCommand(this));
        registerCommand(new SpeedCommand());
        registerCommand(new SpielzeitCommand());
        registerCommand(new TimeCommand());
        registerCommand(new UuidCommand());
        registerCommand(new WeatherCommand());
        registerCommand(new WorkbenchCommand());

    }

    @Override
    public void onDisable() {

    }
}
