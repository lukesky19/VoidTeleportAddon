package com.github.lukesky19.voidTeleportAddon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.util.teleport.SafeSpotTeleport;

import java.util.Map;
import java.util.Optional;

public class VoidListener implements Listener {
    private final VoidTeleportAddon voidTeleportAddon;

    public VoidListener(VoidTeleportAddon voidTeleportAddon) {
        this.voidTeleportAddon = voidTeleportAddon;
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            if (event.getEntity() instanceof Player player) {
                IslandWorldManager iwm = voidTeleportAddon.getPlugin().getIWM();
                Optional<GameModeAddon> addon = iwm.getAddon(player.getWorld());
                if (addon.isPresent()) {
                    if (player.hasPermission(addon.get().getPermissionPrefix() + "voidteleport")) {
                        User user = User.getInstance(player);
                        Optional<Island> island = voidTeleportAddon.getIslandsManager().getIslandAt(user.getLocation());
                        if (island.isPresent()) {
                            for(Map.Entry<String, Location> entry : island.get().getHomes().entrySet()) {
                                Location home = entry.getValue();
                                if(home != null && isLocationSafe(home)) {
                                    event.setCancelled(true);

                                    new SafeSpotTeleport.Builder(voidTeleportAddon.getPlugin())
                                            .entity(player)
                                            .location(home)
                                            .failureMessage("Unable to find safe location")
                                            .build();

                                    return;
                                }
                            }

                            Location spawnPoint = island.get().getSpawnPoint(user.getWorld().getEnvironment());
                            if (spawnPoint != null && isLocationSafe(spawnPoint)) {
                                event.setCancelled(true);

                                new SafeSpotTeleport.Builder(voidTeleportAddon.getPlugin())
                                        .entity(player)
                                        .location(spawnPoint)
                                        .failureMessage("Unable to find safe location")
                                        .build();

                                return;
                            }

                            Location islandCenter = island.get().getCenter();
                            if (isLocationSafe(islandCenter)) {
                                event.setCancelled(true);

                                new SafeSpotTeleport.Builder(voidTeleportAddon.getPlugin())
                                        .entity(player)
                                        .location(islandCenter)
                                        .failureMessage("Unable to find safe location")
                                        .build();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isLocationSafe(Location location) {
        Location target = location.clone().subtract(0, 1, 0);
        Material material = target.getBlock().getType();

        return material.isSolid() && material != Material.AIR;
    }
}