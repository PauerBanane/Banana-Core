package de.pauerbanane.core.addons.jumppads.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.jumppads.Jumppad;
import de.pauerbanane.core.addons.jumppads.JumppadManager;
import de.pauerbanane.core.addons.jumppads.JumppadVector;
import de.pauerbanane.core.data.conditions.AcidIslandLevelCondition;
import de.pauerbanane.core.addons.jumppads.gui.AdminRemoveJumppadConditionGUI;
import de.pauerbanane.core.data.conditions.Condition;
import org.bukkit.entity.Player;

@CommandAlias("jumppad")
@CommandPermission("command.jumppad")
public class JumppadCommand extends BaseCommand {

    private JumppadManager manager;

    public JumppadCommand(JumppadManager manager) {
        this.manager = manager;
    }

    @Subcommand("create")
    @CommandCompletion("@region")
    public void create(Player sender, ProtectedRegion region) {
        JumppadVector vector = new JumppadVector(sender.getEyeLocation().getDirection(), 1);

        Jumppad jumppad = new Jumppad(sender.getWorld(), region, 1, null, vector);

        if (manager.registerJumppad(jumppad)) {
            sender.sendMessage(F.main("Jumppad", "Das Jumppad wurde erstellt."));
        } else
            sender.sendMessage(F.error("Jumppad", "In dieser Region existiert bereits ein Jumppad."));
    }

    @Subcommand("delete")
    @CommandCompletion("@jumppad")
    public void delete(Player sender, Jumppad jumppad) {
        manager.removeJumppad(jumppad);
        sender.sendMessage(F.main("Jumppad", "Das Jumppad wurde entfernt."));
    }

    @Subcommand("addcondition")
    @CommandCompletion("@jumppad @condition @nothing")
    public void addCondition(Player sender, Jumppad jumppad, Condition.Type type, String value) {
        if (!Condition.isValidValue(type,value)) {
            sender.sendMessage(F.error("Jumppad", "Dieser Wert ist nicht möglich für diese Voraussetzung."));
            return;
        }

        jumppad.addCondition(new Condition.Builder().setType(type).setValue(value).build());

        sender.sendMessage(F.main("Jumppad", "Die Voraussetzung wurde dem Jumppad hinzugefügt."));
    }

    @Subcommand("removeCondition")
    @CommandCompletion("@jumppad")
    public void removeCondition(Player sender, Jumppad jumppad) {
        SmartInventory.builder().provider(new AdminRemoveJumppadConditionGUI(jumppad)).size(3).title("§fJumppad§8: §eVoraussetzungen").build().open(sender);
    }

    @Subcommand("set default power")
    @CommandCompletion("@jumppad @nothing")
    public void setDefaultPower(Player sender, Jumppad jumppad, double power) {
        jumppad.setDefaultPower(power);

        sender.sendMessage(F.main("Jumppad", "Du hast die Stärke des Jumppads auf §e" + power + " §7gesetzt."));
    }

    @Subcommand("useplayerfacing")
    @CommandCompletion("@jumppad")
    public void usePlayerFacing(Player sender, Jumppad jumppad) {
        jumppad.setMainVector(null);
        jumppad.setTargetVector(null);
        jumppad.setTargetLocation(null);

        sender.sendMessage(F.main("Jumppad", "Das Jumppad berücksichtigt nun die Blickrichtung des Spielers."));
    }

    @Subcommand("set main vector")
    @CommandCompletion("@jumppad")
    public void setVector(Player sender, Jumppad jumppad) {
        if (jumppad.getMainVector() == null) {
            jumppad.setMainVector(new JumppadVector(sender.getEyeLocation().getDirection(), 1));
        } else
            jumppad.getMainVector().setVector(sender.getEyeLocation().getDirection());

        sender.sendMessage(F.main("Jumppad", "Du hast die Richtung des Jumppads geändert."));
    }

    @Subcommand("set main power")
    @CommandCompletion("@jumppad @nothing")
    public void setPower(Player sender, Jumppad jumppad, double power) {
        if (jumppad.getMainVector() == null) {
            jumppad.setMainVector(new JumppadVector(sender.getEyeLocation().getDirection(), power));
        } else
            jumppad.getMainVector().setPower(power);

        sender.sendMessage(F.main("Jumppad", "Du hast die Stärke des Jumppads auf §e" + power + " §7gesetzt."));
    }

    @Subcommand("set target location")
    @CommandCompletion("@jumppad")
    public void setTargetLocation(Player sender, Jumppad jumppad) {
        if (jumppad.getTargetLocation() == null || jumppad.getTargetVector() == null) {
            jumppad.setTargetLocation(sender.getLocation());
            jumppad.setTargetVector(new JumppadVector(sender.getEyeLocation().getDirection(), 1));
        } else
            jumppad.setTargetLocation(sender.getLocation());

        sender.sendMessage(F.main("Jumppad", "Du hast die Ziel-Location gesetzt."));
    }

    @Subcommand("set target vector")
    @CommandCompletion("@jumppad")
    public void setTargetVector(Player sender, Jumppad jumppad) {
        if (jumppad.getTargetLocation() == null || jumppad.getTargetVector() == null) {
            jumppad.setTargetLocation(sender.getLocation());
            jumppad.setTargetVector(new JumppadVector(sender.getEyeLocation().getDirection(), 1));
        } else
            jumppad.getTargetVector().setVector(sender.getEyeLocation().getDirection());

        sender.sendMessage(F.main("Jumppad", "Du hast die Ziel-Richtung gesetzt."));
    }

    @Subcommand("set target power")
    @CommandCompletion("@jumppad @nothing")
    public void setTargetPower(Player sender, Jumppad jumppad, double power) {
        if (jumppad.getTargetLocation() == null || jumppad.getTargetVector() == null) {
            jumppad.setTargetLocation(sender.getLocation());
            jumppad.setTargetVector(new JumppadVector(sender.getEyeLocation().getDirection(), power));
        } else
            jumppad.getTargetVector().setPower(power);

        sender.sendMessage(F.main("Jumppad", "Du hast die Ziel-Stärke gesetzt."));
    }

}
