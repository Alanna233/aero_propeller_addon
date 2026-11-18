package com.aeropropeller.addon.client;

import com.aeropropeller.addon.blockentity.IntegratedPropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Client-side looping propeller sound. Aeronautics' own instance stops when the
 * bearing has no moved contraption, which never exists for this addon, so the
 * same two sound layers are driven from isActive()/angular speed instead.
 */
public final class AddonPropellerSounds {
    private AddonPropellerSounds() {
    }

    public static Object tick(IntegratedPropellerBearingBlockEntity bearing, Object current) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (current instanceof Holder holder) {
            if (bearing.isRemoved() || !bearing.isActive()
                    || holder.small.isStopped() || holder.large.isStopped()) {
                soundManager.stop(holder.small);
                soundManager.stop(holder.large);
                return null;
            }
            return holder;
        }
        if (bearing.isRemoved() || !bearing.isActive()) {
            return null;
        }

        Layer small = new Layer(bearing, false);
        Layer large = new Layer(bearing, true);
        soundManager.queueTickingSound(small);
        soundManager.queueTickingSound(large);
        return new Holder(small, large);
    }

    private record Holder(Layer small, Layer large) {
    }

    private static final class Layer extends AbstractTickableSoundInstance {
        private static final float LARGE_CUTOFF = 14.0F;
        private static final float LARGE_FALLOFF = 3.0F;

        private final IntegratedPropellerBearingBlockEntity bearing;
        private final boolean large;

        Layer(IntegratedPropellerBearingBlockEntity bearing, boolean large) {
            super(large ? AeroSoundEvents.PROPELLER_LARGE_LOOP.event()
                            : AeroSoundEvents.PROPELLER_SMALL_LOOP.event(),
                    SoundSource.AMBIENT, RandomSource.create());
            this.bearing = bearing;
            this.large = large;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.01F;
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            this.x = bearing.getBlockPos().getX();
            this.y = bearing.getBlockPos().getY();
            this.z = bearing.getBlockPos().getZ();
        }

        @Override
        public void tick() {
            if (bearing.isRemoved() || !bearing.isActive()) {
                stop();
                return;
            }
            float radius = (float) bearing.getPropellerRadius();
            float speed = Mth.clamp(Math.abs(bearing.getAngularSpeed()) / 64.0F, 0.0F, 1.0F);
            float layer = layerVolume(radius);
            this.volume = layer * speed * speed * radius;
            this.pitch = 1.6F * speed / Mth.clamp(radius / 5.0F, 1.0F, 5.0F);
        }

        private float layerVolume(float radius) {
            if (radius > LARGE_CUTOFF + LARGE_FALLOFF) {
                return 0.0F;
            }
            if (radius > LARGE_CUTOFF - LARGE_FALLOFF) {
                return (radius - (LARGE_CUTOFF - LARGE_FALLOFF)) / (2.0F * LARGE_FALLOFF);
            }
            return 1.0F;
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public boolean canPlaySound() {
            return true;
        }
    }
}
