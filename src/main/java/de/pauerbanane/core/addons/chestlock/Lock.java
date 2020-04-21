package de.pauerbanane.core.addons.chestlock;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

public class Lock {

    private final UUID owner;
    private final Set<UUID> members;

    private Lock(UUID uuid, Set<UUID> member) {
        this.owner = uuid;
        this.members = member;
    }

    public Lock(UUID owner) {
        this.owner = owner;
        this.members = Sets.newHashSet();
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public boolean canAccess(UUID uuid) {
        return uuid.equals(this.owner) || this.members.contains(uuid);
    }

    public boolean addMember(UUID uuid) {
        if (this.members.size() >= 5 || this.members.contains(uuid)) {
            return false;
        }
        this.members.add(uuid);
        return true;
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }
}