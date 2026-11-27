package com.dibya.headskins.client;

import net.fabricmc.api.ClientModInitializer;

public class HeadSkinsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client rendering is handled via ArmorStandRendererMixin, ArmorStandModelMixin, and CustomHeadLayerMixin.
    }
}
