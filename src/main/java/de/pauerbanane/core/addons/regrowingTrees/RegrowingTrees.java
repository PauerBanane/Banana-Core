package de.pauerbanane.core.addons.regrowingTrees;

import com.google.common.collect.ImmutableList;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import de.pauerbanane.core.addons.regrowingTrees.commands.RegrowingTreeCommand;
import de.pauerbanane.core.addons.regrowingTrees.listener.RegrowingTreesListener;

import java.util.Collection;
import java.util.stream.Collectors;

public class RegrowingTrees extends Addon {

    private static RegrowingTrees instance;

    private TreeManager manager;

    private final int seconds = 30;

    @Override
    public void onEnable() {
        instance = this;
        this.manager = new TreeManager(this);

        registerCommandContext();

        registerCommand(new RegrowingTreeCommand(this));
        registerListener(new RegrowingTreesListener(this));
    }

    @Override
    public void onDisable() {
        manager.shutdown();
    }

    private void registerCommandContext() {
        commandManager.getCommandContexts().registerContext(Tree.class, c -> {
            String arg = c.popFirstArg();
            Tree tree = manager.getTree(arg);
            if(tree == null) {
                throw new InvalidCommandArgument("Es existiert kein Baum auf dieser Region: " + arg);
            } else
                return tree;
        });
        commandManager.getCommandCompletions().registerCompletion("regrowingTree", c -> {
            return (Collection)manager.getTreeMap().stream().map(Tree::getRegionID).collect(Collectors.toSet());
        });
    }

    @Override
    public void onReload() {

    }

    public static RegrowingTrees getInstance() {
        return instance;
    }

    public int getSeconds() {
        return seconds;
    }

    public TreeManager getManager() {
        return manager;
    }
}
