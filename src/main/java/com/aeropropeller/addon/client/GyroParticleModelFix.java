package com.aeropropeller.addon.client;

import com.aeropropeller.addon.AeroPropellerAddon;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * The Bare Metals resource pack's gyroscopic base model does not define a
 * particle texture, so the mining crack particles use the purple/black missing
 * texture. Borrow the upper bearing model's particle sprite instead.
 */
@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GyroParticleModelFix {
    private static final String BASE_MODEL = "integrated_gyroscopic_propeller_base";
    private static final String UPPER_MODEL = "integrated_gyroscopic_propeller_bearing";

    private GyroParticleModelFix() {
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        TextureAtlasSprite particle = null;
        for (var entry : event.getModels().entrySet()) {
            if (matches(entry.getKey(), UPPER_MODEL)) {
                particle = entry.getValue().getParticleIcon();
                break;
            }
        }
        if (particle == null) {
            return;
        }
        for (var entry : event.getModels().entrySet()) {
            if (matches(entry.getKey(), BASE_MODEL)) {
                entry.setValue(new ParticleModel(entry.getValue(), particle));
            }
        }
    }

    private static boolean matches(ModelResourceLocation location, String path) {
        String id = location.id().getPath();
        return id.equals(path) || id.endsWith("/" + path);
    }

    private record ParticleModel(BakedModel delegate, TextureAtlasSprite particle) implements BakedModel {
        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
            return delegate.getQuads(state, side, random);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return delegate.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return delegate.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return delegate.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return delegate.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemTransforms getTransforms() {
            return delegate.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return delegate.getOverrides();
        }
    }
}
