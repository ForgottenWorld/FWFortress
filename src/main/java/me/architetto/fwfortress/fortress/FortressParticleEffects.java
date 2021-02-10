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

        Location cornerGreen = new Location(c.getWorld(),blockX,location.getBlockY() + 3,blockZ);

        CuboidEffect greenArea = new CuboidEffect(effectManager);
        greenArea.setLocation(cornerGreen);
        greenArea.particle = Particle.REDSTONE;
        greenArea.color = Color.GREEN;
        greenArea.particleSize = 3;
        greenArea.period = 20;
        greenArea.xLength = 16;
        greenArea.zLength = 16;
        greenArea.yLength = 3;
        greenArea.iterations = 30;
        greenArea.start();

    }

    public void fortressBlueAreaEffect(Location location) {
        Chunk c = location.getChunk();

        int blockX = c.getX() << 4;
        int blockZ = c.getZ() << 4;

        Location cornerBlue = new Location(c.getWorld(),blockX - 16,location.getBlockY() + 3,blockZ - 16);

        CuboidEffect blueArea = new CuboidEffect(effectManager);
        blueArea.setLocation(cornerBlue);
        blueArea.particle = Particle.REDSTONE;
        blueArea.color = Color.BLUE;
        blueArea.particleSize = 3;
        blueArea.particles = 24;
        blueArea.period = 20;
        blueArea.xLength = 48;
        blueArea.zLength = 48;
        blueArea.yLength = 8;
        blueArea.iterations = 30;
        blueArea.start();

    }

}
