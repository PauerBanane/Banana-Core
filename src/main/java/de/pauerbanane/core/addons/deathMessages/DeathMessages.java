package de.pauerbanane.core.addons.deathMessages;

import com.google.common.collect.Maps;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.util.HashMap;

public class DeathMessages extends Addon implements Listener {

    private FileLoader config;

    private String defaultMessage;

    private HashMap<EntityType, String> entityType;

    private HashMap<EntityDamageEvent.DamageCause, String> damageCause;

    @Override
    public void onEnable() {
        this.entityType = Maps.newHashMap();
        this.damageCause = Maps.newHashMap();
        UtilFile.copyResource(plugin.getResource("DeathMessages.yml"), new File(getAddonFolder(), "DeathMessages.yml"));
        this.config = new FileLoader(getAddonFolder(), "DeathMessages.yml");

        initConfig();
        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    private void initConfig() {

        this.defaultMessage = config.getString("default");

        ConfigurationSection section = config.getConfigurationSection("entity");
        for(String entity : section.getKeys(false)) {
            EntityType type = EntityType.valueOf(entity);
            if(type != null) {
                entityType.put(type, section.getString(entity));
            } else
                Bukkit.getLogger().warning("Failed to init EntityType: " + entity);
        }

        section = config.getConfigurationSection("damagecause");
        for(String cause : section.getKeys(false)) {
            EntityDamageEvent.DamageCause dmgCause = EntityDamageEvent.DamageCause.valueOf(cause);
            if(dmgCause != null) {
                damageCause.put(dmgCause, section.getString(cause));
            } else
                Bukkit.getLogger().warning("Failed to init DamageCause: " + cause);
        }
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent e) {
        EntityDamageEvent damageEvent = e.getEntity().getLastDamageCause();
        e.setDeathMessage(formatMessage(defaultMessage, e.getEntity().getName()));

        if(damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) damageEvent;
            if (event.getDamager().getType() != null && entityType.get(event.getDamager().getType()) != null) {
                e.setDeathMessage(formatMessage(entityType.get(event.getDamager().getType()), e.getEntity().getName()));
            } else if(event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if(projectile.getShooter() instanceof Entity) {
                    Entity en = (Entity) ((Projectile) event.getDamager()).getShooter();
                    e.setDeathMessage(formatMessage(entityType.get(en.getType()), e.getEntity().getName()));
                }
            }

        } else if(damageCause.containsKey(damageEvent.getCause())) {
            e.setDeathMessage(formatMessage(damageCause.get(damageEvent.getCause()), e.getEntity().getName()));
        }
    }

    private String formatMessage(String message, String playerName) {
        if(message.contains("%player%")) {
            String replaced = String.valueOf(ChatColor.GRAY + message.replace("%player%", ChatColor.YELLOW + playerName + ChatColor.GRAY));
            return replaced;
        } else return ChatColor.GRAY + message;
    }
}
