package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.echelon.EchelonService;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
public class Battle {

    private final Fortress fortress;
    private final Town defendersTown;
    private final Town invadersTown;
    private final Set<UUID> invadersUUID;
    private final BoundingBox greenBox;
    private final BoundingBox blueBox;
    private BossBar bossBar;

    private final int staticHP;
    private int mutableHP;

    private int firstTaskID;
    private int secondTaskID;
    private int positionTaskID;

    private final int maxDamageForSeconds;
    private final double fortressBorderDamage;

    private final boolean glowInvaders;
    private int glowPeriod;
    private int glowDuration;

    public Battle(Fortress fortress, Town invadersTown, Set<UUID> invadersUUID, Town defendersTown) {
        this.fortress = fortress;

        this.defendersTown = defendersTown;

        this.invadersTown = invadersTown;
        this.invadersUUID = invadersUUID;

        this.greenBox = fortress.getGreenBoundingBox();
        this.blueBox = fortress.getBlueBoundingBox();

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        this.bossBar = Bukkit
                .createBossBar(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getName(),
                        settingsHandler.getStartBattleDelay()),
                        BarColor.YELLOW, BarStyle.SOLID);

        this.staticHP = settingsHandler.getFortressHP(); //da config
        this.mutableHP = staticHP;

        this.fortressBorderDamage = settingsHandler.getFortressBorderDamage();
        this.maxDamageForSeconds = settingsHandler.getMaxDamageForSeconds();

        this.glowInvaders = settingsHandler.isGlowInvaders();

        if (glowInvaders) {
            this.glowPeriod = settingsHandler.getGlowPeriod();
            this.glowDuration = settingsHandler.getGlowDuration();
        }

    }

    public void firstPhase() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class), SettingsHandler.getInstance().getStartBattleDelay(),
                () -> {

                    Message.BATTLE_ALLERT.broadcast(fortress.getFormattedName(), defendersTown.getFormattedName());

                    bossBar = Bukkit
                            .createBossBar(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFormattedName(),
                                    TimeUtil.formatSeconds(SettingsHandler.getInstance().getStartBattleDelay())),
                                    BarColor.YELLOW, BarStyle.SOLID);

                    bossBar.setProgress(0);

                    invadersTown.getResidents()
                            .stream()
                            .map(Resident::getPlayer)
                            .filter(Objects::nonNull)
                            .forEach(p -> bossBar.addPlayer(p));

                    defendersTown.getResidents()
                            .stream()
                            .map(Resident::getPlayer)
                            .filter(Objects::nonNull)
                            .forEach(p -> bossBar.addPlayer(p));

                },
                () -> {

                    Message.BATTLE_START_BROADCAST.broadcast(fortress.getFormattedName());
                    secondPhase();

                },
                (s) -> {

                    bossBar.setTitle(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFormattedName(),
                            TimeUtil.formatSeconds(s.getSecondsLeft())));

                    bossBar.setProgress((float) (s.getTotalSeconds() - s.getSecondsLeft()) / s.getTotalSeconds());

                    if (invadersUUID.isEmpty()) {
                        Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFormattedName());

                        BattleService.getInstance().resolveBattle(fortress,
                                fortress.getOwner(),
                                0);

                    }

                });

        firstTaskID = countdown.scheduleTimer();

    }

    public void secondPhase() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class), SettingsHandler.getInstance().getBattleTimeLimit(),
                () -> {
                    //
                    bossBar.setColor(BarColor.RED);
                    bossBar.setProgress(1);
                    checkInvadersPositionTask();

                },

                () -> {

                    Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getName());

                    BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getOwner(), staticHP - mutableHP);

                },
                (s) -> {

                    mutableHP = Math.max(0, mutableHP - Math.min(maxDamageForSeconds, countGreenBoxInvaders()));

                    bossBar.setTitle(Message.BOSSBAR_FORMAT.asString(fortress.getFormattedName(),
                            TimeUtil.formatSeconds(s.getSecondsLeft()),
                            mutableHP));

                    bossBar.setProgress((float) mutableHP / staticHP);

                    if (this.mutableHP <= 0) {

                        Message.BATTLE_ENDED_BROADCAST2.broadcast(fortress.getFormattedName(),
                                invadersTown.getFormattedName());

                        BattleService.getInstance().resolveBattle(fortress,
                                invadersTown.getName(),
                                staticHP);
                    }

                    if (glowInvaders
                            && s.getTotalSeconds() != 0
                            && s.getSecondsLeft() % glowPeriod == 0
                            && s.getTotalSeconds() != s.getSecondsLeft()) {
                        invadersUUID.stream()
                                .map(Bukkit::getPlayer)
                                .filter(Objects::nonNull)
                                .forEach(player -> player
                                        .addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,
                                                glowDuration,
                                                1)));
                    }


                    if (invadersUUID.isEmpty()) {

                        Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFormattedName());

                        BattleService.getInstance().resolveBattle(fortress,
                                fortress.getOwner(),
                                staticHP - mutableHP);
                    }

                });
        this.secondTaskID = countdown.scheduleTimer();

    }

    private Integer countGreenBoxInvaders() {

        return Math.toIntExact(new HashSet<>(invadersUUID)
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(player -> player.getLocation().toVector())
                .filter(greenBox::contains)
                .count());

    }

    private void checkInvadersPositionTask() {

        this.positionTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FWFortress.plugin, () ->
                new HashSet<>(invadersUUID)
                        .stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .filter(p -> !blueBox.contains(p.getBoundingBox()))
                        .forEach(player -> {
                            player.damage(fortressBorderDamage);
                            player.sendActionBar(Message.BATTLE_LEAVE_ACTIONBAR.asString());
                        }), 0L, 20L);
    }

    public void stopBattle() {
        if (SettingsHandler.getInstance().isFWEchelonLoaded()) {
            EchelonService echelonService = EchelonService.getInstance();
            if (!invadersUUID.isEmpty())
                invadersUUID.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(echelonService::removePlayerMutexActivity);
        }

        this.bossBar.removeAll();
        Bukkit.getScheduler().cancelTask(this.firstTaskID);
        Bukkit.getScheduler().cancelTask(this.secondTaskID);
        Bukkit.getScheduler().cancelTask(this.positionTaskID);

    }

    public Fortress getFortress() {
        return fortress;
    }

    public boolean isInvaders(UUID uuid) {
        return invadersUUID.contains(uuid);
    }

    public boolean isTownInvolved(String townname) {
        return invadersTown.getName().equals(townname) || defendersTown.getName().equals(townname);
    }

    public void addPlayerToBossBar(Player player) {
        bossBar.addPlayer(player);
    }

    public void removeInvaders(Player player) {
        invadersUUID.remove(player.getUniqueId());
        if (SettingsHandler.getInstance().isFWEchelonLoaded())
            EchelonService.getInstance().removePlayerMutexActivity(player);
    }
        


}
