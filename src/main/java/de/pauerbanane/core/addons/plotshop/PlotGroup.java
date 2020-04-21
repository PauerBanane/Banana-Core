package de.pauerbanane.core.addons.plotshop;

public class PlotGroup {

    private final String groupID;

    private int purchaseLimit;

    private String permission;

    private boolean autoReset;

    private int rentDays;

    public PlotGroup(String groupID, int purchaseLimit) {
        this.groupID = groupID;
        this.purchaseLimit = purchaseLimit;
        this.permission = "plots." + groupID;
        this.autoReset = true;
        this.rentDays = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("ID: ").append(this.groupID).append(" | Miettage: ").append(this.rentDays).append(" | Kauflimit: ").append(this.purchaseLimit).append(" | Reset: ").append(this.autoReset)
                .append(" | Permission ").append(this.permission).toString();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PlotGroup))
            return false;
        PlotGroup other = (PlotGroup)obj;
        if (this.groupID == null) {
            if (other.groupID != null)
                return false;
        } else if (!this.groupID.equals(other.groupID)) {
            return false;
        }
        return true;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getPermission() {
        return permission;
    }

    public int getPurchaseLimit() {
        return purchaseLimit;
    }

    public int getRentDays() {
        return rentDays;
    }

    public void setRentDays(int rentDays) {
        this.rentDays = rentDays;
    }

    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setPurchaseLimit(int purchaseLimit) {
        this.purchaseLimit = purchaseLimit;
    }

    public boolean isAutoReset() {
        return autoReset;
    }
}
