package de.pauerbanane.core.addons.afk;

public class Footprint {

    private float yaw;
    private long time;

    public Footprint(Float yaw, long time) {
        this.yaw = yaw;
        this.time = time;
    }

    public float getYaw() {
        return yaw;
    }

    public long getTime() {
        return time;
    }
}
