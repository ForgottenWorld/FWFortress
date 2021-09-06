package me.architetto.fwfortress.fortress;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.*;
import me.architetto.fwfortress.FWFortress;
import org.bukkit.*;

public class FortressParticleEffects {
    
    private static FortressParticleEffects fortressParticleEffects;

    private final EffectManager effectManager;

    private FortressParticleEffects() {
        if(fortressParticleEffects != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.effectManager = new EffectManager(FWFortress.getPlugin(FWFortress.class));

    }

    public static FortressParticleEffects getInstance() {
        if (fortressParticleEffects == null) {
            fortressParticleEffects = new FortressParticleEffects();
        }
        return fortressParticleEffects;
    }

    public void fortressGreenAreaEffect(Location location) {

        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        Location cornerGreen = new Location(c.getWorld(),blockX - 16,location.getBlockY() + 3,blockZ - 16);

        CuboidEffect cuboidEffect = new CuboidEffect(this.effectManager);

        cuboidEffect.setLocation(cornerGreen);
        cuboidEffect.setTargetLocation(cornerGreen);

        cuboidEffect.particle = Particle.REDSTONE;
        cuboidEffect.color = Color.OLIVE;
        cuboidEffect.particleSize = 2;
        cuboidEffect.particles = 40;

        cuboidEffect.xLength = 48;
        cuboidEffect.zLength = 48;
        cuboidEffect.yLength = 8;

        cuboidEffect.iterations = 100;
        cuboidEffect.visibleRange = 70F;

        cuboidEffect.start();

    }

    public void fortressBlueAreaEffect(Location location) {

        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        Location cornerBlue = new Location(c.getWorld(),blockX - 32,location.getBlockY() + 3,blockZ - 32);

        CuboidEffect cuboidEffect = new CuboidEffect(this.effectManager);

        cuboidEffect.setLocation(cornerBlue);
        cuboidEffect.setTargetLocation(cornerBlue);

        cuboidEffect.particle = Particle.REDSTONE;
        cuboidEffect.color = Color.BLUE;
        cuboidEffect.particleSize = 2;
        cuboidEffect.particles = 50;

        cuboidEffect.xLength = 80;
        cuboidEffect.zLength = 80;
        cuboidEffect.yLength = 8;

        cuboidEffect.iterations = 100;
        cuboidEffect.visibleRange = 70F;

        cuboidEffect.start();

    }

}
