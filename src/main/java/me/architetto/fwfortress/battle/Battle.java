package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class Battle {

    private Fortress fortress;
    private Town defendersTown;
    private Town invadersTown;
    private Set<UUID> invadersUUID;
    private BoundingBox greenBox;
    private BoundingBox blueBox;
    private BossBar bossBar;

    private int staticHP;
    private int mutableHP;

    private int firstTaskID;
    private int secondTaskID;
    private int positionTaskID;

    private final int maxDamageForSeconds;
    private final double fortressBorderDamage;

    public Battle(Fortress fortress, Town invadersTown, Set<UUID> invadersUUID) {
        this.fortress = fortress;

        try {
            this.defendersTown = TownyAPI.getInstance().getDataSource().getTown(fortress.getOwner());
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }

        this.invadersTown = invadersTown;
        this.invadersUUID = invadersUUID;

        this.greenBox = fortress.getGreenBoundingBox();
        this.blueBox = fortress.getBlueBoundingBox();

        this.bossBar = Bukkit
                .createBossBar(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getName(),
                        SettingsHandler.getInstance().getStartBattleDelay()),
                        BarColor.YELLOW, BarStyle.SOLID);

        this.staticHP = SettingsHandler.getInstance().getFortressHP(); //da config
        this.mutableHP = staticHP;

        this.fortressBorderDamage = SettingsHandler.getInstance().getFortressBorderDamage();
        this.maxDamageForSeconds = SettingsHandler.getInstance().getMaxDamageForSeconds();

    }

    public void firstPhase() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class), SettingsHandler.getInstance().getStartBattleDelay(),
                () -> {

                    Message.BATTLE_ALLERT.broadcast(fortress.getFormattedName(), defendersTown.getFormattedName());

                    bossBar = Bukkit
                            .createBossBar(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFormattedName(),
                                    SettingsHandler.getInstance().getStartBattleDelay()),
                                    BarColor.YELLOW, BarStyle.SOLID);

                    bossBar.setProgress(0);

                    invadersTown.getResidents().stream().map(Resident::getPlayer).filter(Objects::nonNull)
                            .forEach(p -> bossBar.addPlayer(p));

                    defendersTown.getResidents().stream().map(Resident::getPlayer).filter(Objects::nonNull)
                            .forEach(p -> bossBar.addPlayer(p));

                },
                () -> {

                    Message.BATTLE_START_BROADCAST.broadcast(fortress.getFormattedName());
                    secondPhase();

                },
                (s) -> {

                    bossBar.setTitle(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFormattedName(), s.getSecondsLeft()));
                    bossBar.setProgress((float) (s.getTotalSeconds() - s.getSecondsLeft()) / s.getTotalSeconds());

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
                            s.getSecondsLeft(),
                            mutableHP));

                    bossBar.setProgress((float) mutableHP / staticHP);

                    if (invadersUUID.isEmpty()) {

                        Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFormattedName());

                        BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getOwner(),
                                staticHP - mutableHP);
                    }

                    if (this.mutableHP <= 0) {

                        Message.BATTLE_ENDED_BROADCAST2.broadcast(fortress.getFormattedName(),invadersTown.getFormattedName());

                        BattleService.getInstance().resolveBattle(fortress, invadersTown.getName(),
                                staticHP);
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
                .filter(vector -> greenBox.contains(vector))
                .count());

    }

    private void checkInvadersPositionTask() {

        this.positionTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FWFortress.plugin, () ->
                new HashSet<>(invadersUUID)
                        .stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .filter(p -> !blueBox.contains(p.getBoundingBox()))
                        .forEach(player -> player.damage(fortressBorderDamage)), 0L, 20L);
    }

    public void stopBattle() {
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

    public void removeInvaders(Player player) {
        //todo: la bossbar viene tolta solo al termine della battaglia
        //bossBar.removePlayer(player);
        invadersUUID.remove(player.getUniqueId());
    }
        


}
