package de.pauerbanane.core.addons.plotshop.listener;

import de.pauerbanane.core.addons.plotshop.PlotShop;

public class ExpireListener implements Runnable {

private final PlotShop addon;

    public ExpireListener(PlotShop addon) {
        this.addon = addon;
    }

    public void run() {
        this.addon.getManager().expireCheck();
    }
}