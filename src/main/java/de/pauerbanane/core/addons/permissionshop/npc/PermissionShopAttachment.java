package de.pauerbanane.core.addons.permissionshop.npc;

import de.pauerbanane.api.npcattachment.NPCDataHandler;
import de.pauerbanane.api.npcattachment.NPCInteractAttachment;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.core.addons.permissionshop.PermissionShop;
import de.pauerbanane.core.addons.permissionshop.gui.PermissionShopInventory;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class PermissionShopAttachment implements NPCInteractAttachment {

    private final PermissionShop addon;

    public PermissionShopAttachment(PermissionShop addon) {
        this.addon = addon;
    }

    @Override
    public void onRightClick(NPCRightClickEvent e) {
        SmartInventory.builder().provider(new PermissionShopInventory(addon.getManager())).title("Permission-Shop").size(5).build().open(e.getClicker());
    }

    @Override
    public NPCDataHandler.NPCAttachmentType getAttachmentID() {
        return NPCDataHandler.NPCAttachmentType.PERMISSION_SHOP;
    }
}
