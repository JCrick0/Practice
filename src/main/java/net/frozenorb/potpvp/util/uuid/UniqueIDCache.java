package net.frozenorb.potpvp.util.uuid;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.uuid.impl.BukkitUUIDCache;

import java.util.UUID;

public final class UniqueIDCache {
    private static UUIDCache impl=null;
    private static boolean initiated=false;

    private UniqueIDCache() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated=true;
        impl = new BukkitUUIDCache();
        PotPvPSI.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), PotPvPSI.getInstance());
    }

    public static UUID uuid(String name) {
        return impl.uuid(name);
    }

    public static String name(UUID uuid) {
        return impl.name(uuid);
    }

    public static void ensure(UUID uuid) {
        impl.ensure(uuid);
    }

    public static void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }

    public static UUIDCache getImpl() {
        return impl;
    }
}

