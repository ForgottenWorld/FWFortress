package me.architetto.fwfortress.fortress;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.*;
import me.architetto.fwfortress.FWFortress;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class FortressParticleEffects {
    
    private static FortressParticleEffects fortressParticleEffects;

    private final EffectManager effectManager;

    private FortressParticleEffects() {
        if(fortressParticleEffects != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        effectManager = new EffectManager(FWFortress.getPlugin(FWFortress.class));

    }

    public static FortressParticleEffects getInstance() {
        if(fortressParticleEffects == null) {
            fortressParticleEffects = new FortressParticleEffects();
        }
        return fortressParticleEffects;
    }

    public void fortressGreenAreaEffect(Location location) {

        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        Location cornerGreen = new Location(c.getWorld(),blockX - 16,location.getBlockY() + 3,blockZ - 16);

        CuboidEffect greenArea = new CuboidEffect(effectManager);
        greenArea.setLocation(cornerGreen);
        greenArea.particle = Particle.REDSTONE;
        greenArea.color = Color.GREEN;
        greenArea.visibleRange = 70F;
        greenArea.particleSize = 2;
        greenArea.period = 10;
        greenArea.xLength = 48;
        greenArea.zLength = 48;
        greenArea.yLength = 8;
        greenArea.iterations = 30;
        greenArea.start();

    }

    public void fortressBlueAreaEffect(Location location) {
        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        Location cornerBlue = new Location(c.getWorld(),blockX - 32,location.getBlockY() + 3,blockZ - 32);

        CuboidEffect blueArea = new CuboidEffect(effectManager);
        blueArea.setLocation(cornerBlue);
        blueArea.particle = Particle.REDSTONE;
        blueArea.color = Color.BLUE;
        blueArea.visibleRange = 70F;
        blueArea.particleSize = 2;
        blueArea.particles = 24;
        blueArea.period = 10;
        blueArea.xLength = 80;
        blueArea.zLength = 80;
        blueArea.yLength = 8;
        blueArea.iterations = 30;
        blueArea.start();

    }

}
