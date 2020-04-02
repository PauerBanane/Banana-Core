package de.pauerbanane.core.data;

import de.pauerbanane.core.BananaCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;
import java.util.UUID;

public class PermissionManager {

    public static PermissionManager instance;

    private static LuckPerms api;

    private BananaCore plugin;

    public PermissionManager(BananaCore plugin) {
        this.instance = this;
        this.plugin = plugin;

        hookLuckPerms();
    }

    private void hookLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(provider != null) {
            api = provider.getProvider();
        } else
            Bukkit.getLogger().warning("Failed to hook into LuckPerms - Plugin not found");
    }

    public static User getUser(UUID uuid) {
        return api.getUserManager().getUser(uuid);
    }

    public static void addPlayerPermission(Player player, String permission) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Node node = Node.builder(permission).build();
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    public static String getPlayerPrefix(Player player) {
        User user = api.getUserManager().getUser(player.getName());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getPrefix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getPrefix());
    }

    public static String getPlayerPrefix(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getPrefix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getPrefix());
    }

    public static String getPlayerSuffix(Player player) {
        User user = api.getUserManager().getUser(player.getName());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getPrefix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getSuffix());
    }

    public static String getPlayerSuffix(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getPrefix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getSuffix());
    }

    public static boolean hasMetaValue(Player player, String key) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Optional own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(key);
        }).findFirst();
        if(!own.isPresent()) return false;
        return true;
    }

    public static String getMetaValue(Player player, String key) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Optional own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(key);
        }).findFirst();
        if(!own.isPresent()) return null;

        MetaNode node = (MetaNode) own.get();
        return node.getMetaValue();
    }

    public static CachedMetaData getMetaData(Player player) {
        User user = api.getUserManager().getUser(player.getName());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return metaData;
    }

    public static CachedPermissionData getCachedPermissionData(Player player) {
        User user = api.getUserManager().getUser(player.getName());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedPermissionData permData = user.getCachedData().getPermissionData(queryOptions);

        return permData;
    }

    public static CachedMetaData getMetaData(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return metaData;
    }

    public static CachedDataManager getCachedData(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedDataManager data = user.getCachedData();

        return data;
    }

    public static Optional<ImmutableContextSet> getContext(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        Optional<ImmutableContextSet> context = api.getContextManager().getContext(user);

        return context;
    }

    public static PermissionManager getInstance() {
        return instance;
    }
}
