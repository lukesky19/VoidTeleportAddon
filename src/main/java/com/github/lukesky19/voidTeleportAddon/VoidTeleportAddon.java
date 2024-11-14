package com.github.lukesky19.voidTeleportAddon;

import world.bentobox.bentobox.api.addons.Addon;

public final class VoidTeleportAddon extends Addon {

    @Override
    public void onEnable() {
        this.registerListener(new VoidListener(this));
    }

    @Override
    public void onDisable() {}
}
