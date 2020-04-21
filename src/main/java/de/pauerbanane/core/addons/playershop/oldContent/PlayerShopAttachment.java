package de.pauerbanane.core.addons.playershop.oldContent;

import de.pauerbanane.api.npcattachment.NPCDataHandler;
import de.pauerbanane.api.npcattachment.NPCInteractAttachment;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.playershop.PlayerShop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerShopAttachment implements NPCInteractAttachment {

    @Override
    public NPCDataHandler.NPCAttachmentType getAttachmentID() {
        return NPCDataHandler.NPCAttachmentType.PLAYERSHOP_DATA;
    }


    @Override
    public void onRightClick(NPCRightClickEvent e) {
        ShopStorageManager manager = PlayerShop.getInstance().getStorageManager();
        if(!manager.hasOldContents(e.getClicker())) {
            e.getClicker().sendMessage(F.error("Shop", "Es wurden keine Items von dir gefunden."));
            return;
        }

        ArrayList<ItemStack> items = manager.receiveStoragedItems(e.getClicker());
        SmartInventory.builder().provider(new ContentOverview(items)).title("Shopgegenstände").size(5).build().open(e.getClicker());
    }
}
