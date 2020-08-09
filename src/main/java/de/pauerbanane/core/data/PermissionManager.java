package de.pauerbanane.core.data;

import com.google.common.collect.Lists;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.*;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class PermissionManager {

    public static PermissionManager instance;

    private static LuckPerms api;

    private static BananaCore plugin;

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
            plugin.getLogger().warning("Failed to hook into LuckPerms - Plugin not found");
    }

    public static User getUser(UUID uuid) {
        return api.getUserManager().getUser(uuid);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Check Player Nodes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static boolean hasNode(UUID uuid, Node node) {
        if(node == null) return true;

        User user = api.getUserManager().getUser(uuid);

        if(node instanceof PermissionNode) {
            return hasPermissionNode(user, (PermissionNode) node);
        } else if(node instanceof MetaNode) {
            return hasMetaNode(user, (MetaNode) node);
        } else if(node instanceof PrefixNode) {
            return hasPrefixNode(user, (PrefixNode) node);
        } else if(node instanceof SuffixNode) {
            return hasSuffixNode(user, (SuffixNode) node);
        } else {
            plugin.getLogger().warning("Failed to lookup Player Node - There's no defined method for this NodeType");
            return false;
        }
    }

    private static boolean hasPermissionNode(User user, PermissionNode node) {
        Optional<Node> own = user.data().toCollection().stream().filter(n -> {
            return n.equals(node, NodeEqualityPredicate.IGNORE_EXPIRY_TIME);
        }).findFirst();

        return (own.isPresent() && compareContext(own.get().getContexts(), node.getContexts()));
    }

    private static boolean hasMetaNode(User user, MetaNode node) {
        Optional<Node> own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(node.getMetaKey());
        }).findFirst();
        if(!own.isPresent()) return false;

        MetaNode metaNode = (MetaNode) own.get();
        String metaValue = metaNode.getMetaValue();

        if(!compareContext(node.getContexts(), metaNode.getContexts()))
            return false;

        if(UtilMath.isInt(metaValue) && UtilMath.isInt(node.getMetaValue())) {
            return Integer.parseInt(metaValue) >= Integer.parseInt(node.getMetaValue());
        } else {
            return metaNode.equals(node);
        }
    }

    private static boolean hasPrefixNode(User user, PrefixNode prefixNode) {
        String prefix = getPlayerPrefix(user.getUniqueId());
        return prefix.equals(ChatColor.translateAlternateColorCodes('&', prefixNode.getMetaValue()));
    }

    private static boolean hasSuffixNode(User user, SuffixNode suffixNode) {
        String suffix = getPlayerSuffix(user.getUniqueId());
        return suffix.equals(ChatColor.translateAlternateColorCodes('&', suffixNode.getMetaValue()));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Build Nodes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static PermissionNode buildPermissionNode(String key, boolean value, String server, String world) {
        PermissionNode.Builder builder = PermissionNode.builder();
        builder.permission(key);
        builder.value(value);
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);
        builder.context(contextBuilder.build());

        return builder.build();
    }

    public static InheritanceNode buildInheritanceNode(String group, String server, String world) {
        InheritanceNode.Builder builder = InheritanceNode.builder();
        builder.group(group);
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);
        builder.context(contextBuilder.build());

        return builder.build();
    }

    public static MetaNode buildMetaNode(String metaKey, String metaValue, String server, String world) {
        MetaNode.Builder builder = MetaNode.builder();
        builder.key(metaKey);
        builder.value(metaValue);
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);
        builder.context(contextBuilder.build());

        return builder.build();
    }

    public static PrefixNode buildPrefixNode(String prefix, int priority, String server, String world) {
        PrefixNode.Builder builder = PrefixNode.builder();
        builder.prefix(prefix);
        builder.value(true);
        builder.priority(priority);
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);
        builder.context(contextBuilder.build());

        return builder.build();
    }

    public static SuffixNode buildSuffixNode(String suffix, int priority, String server, String world) {
        SuffixNode.Builder builder = SuffixNode.builder();
        builder.suffix(suffix);
        builder.value(true);
        builder.priority(priority);
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);
        builder.context(contextBuilder.build());

        return builder.build();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Add Nodes to a Player ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void addNode(Player player, Node node) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        if(node instanceof MetaNode || node instanceof InheritanceNode) {
            Optional<Node> own = user.data().toCollection().stream().filter(n -> {
                return n instanceof MetaNode || n instanceof InheritanceNode;
            }).filter(n -> {
                if (n instanceof InheritanceNode) {
                    return n instanceof InheritanceNode;
                } else {
                    return ((MetaNode) n).getMetaKey().equals(((MetaNode) node).getMetaKey());
                }
            }).filter(n -> {
                return n.getContexts().equals(node.getContexts());
            }).findFirst();
            if(own.isPresent())
                user.data().remove(own.get());
        }
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    public static boolean removeNode(Player player, Node node) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        user.data().remove(node);
        api.getUserManager().saveUser(user);

        return true;
    }

    public static void addGroupNode(Player player, Group group) {
        User user = getUser(player.getUniqueId());
        InheritanceNode node = buildInheritanceNode(group.getName(), null, null);

        removeGroupNodes(player);
        user.data().add(node);
        user.setPrimaryGroup(group.getName());
        api.getUserManager().saveUser(user);
    }

    public static void removeGroupNodes(Player player) {
        User user = getUser(player.getUniqueId());
        ArrayList<Node> toRemove = Lists.newArrayList();
        user.data().toCollection().forEach(node -> {
            if (node instanceof InheritanceNode)
                toRemove.add(node);
        });

        toRemove.forEach(node -> user.data().remove(node));
        api.getUserManager().saveUser(user);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Get specific Node values

    public static String getPlayerPrefix(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getPrefix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getPrefix());
    }

    public static String getPlayerSuffix(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(api.getContextManager().getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        return (metaData.getSuffix() == null) ? "" : ChatColor.translateAlternateColorCodes('&', metaData.getSuffix());
    }

    public static String getMetaValue(UUID uuid, String key, String server, String world) {
        User user = api.getUserManager().getUser(uuid);
        Optional<Node> own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(key);
        }).findFirst();
        if(!own.isPresent()) return null;

        MetaNode node = (MetaNode) own.get();
        ImmutableContextSet contextSet = getContexts(server, world);

        if(!compareContext(contextSet, node.getContexts()))
            return null;

        return node.getMetaValue();
    }

    public static ImmutableContextSet getContexts(String server, String world) {
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        if(server != null && !server.equals("global"))
            contextBuilder.add("server", server);
        if(world != null && !world.equals("global"))
            contextBuilder.add("world", world);

        return contextBuilder.build();
    }

    private static boolean compareContext(ImmutableContextSet set1, ImmutableContextSet set2) {
        for(Context context : set1)
            if(!set2.contains(context))
                return false;

        return true;
    }

    public static boolean hasMetaValue(Player player, String key, int minimum, String server, String world) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Optional<Node> own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(key);
        }).findFirst();
        if(!own.isPresent() || !compareContext(own.get().getContexts(), getContexts(server, world))) return false;
        MetaNode node = (MetaNode) own.get();
        String value = node.getMetaValue();

        return (UtilMath.isInt(value) && Integer.valueOf(value) >= minimum);
    }

    public static boolean hasMetaValue(Player player, String key, String value, String server, String world) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Optional<Node> own = user.data().toCollection().stream().filter(n -> {
            return n instanceof MetaNode;
        }).filter(n -> {
            return ((MetaNode) n).getMetaKey().equals(key);
        }).findFirst();
        if(!own.isPresent() || !compareContext(own.get().getContexts(), getContexts(server, world))) return false;
        MetaNode metaNode = (MetaNode) own.get();

        return metaNode.getMetaValue().equals(value);
    }

    public static PermissionManager getInstance() {
        return instance;
    }

    public static LuckPerms getApi() {
        return api;
    }
}
