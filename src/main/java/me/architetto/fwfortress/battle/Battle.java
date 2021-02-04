package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.TownyUtil;
import org.bukkit.Bukkit;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import org.bukkit.util.BoundingBox;

import java.util.*;

public class Battle {

    private Fortress fortress;

    private BoundingBox greenArea;
    private BoundingBox blueArea;

    private List<UUID> activeInvaders;

    private Town attackersTown;

    private BossBar bossBar;

    private int fortressHP;

    private int fortressBorderDamage;
    private int maxDamageForSeconds;

    private int battleTaskID;
    private int positionTaskID;

    public Battle(Fortress fortress, List<UUID> invaders, Town enemyTownName) {

        this.fortress = fortress;

        this.greenArea = fortress.getGreenBoundingBox();
        this.blueArea = fortress.getBlueBoundingBox();

        this.fortressHP = fortress.getCurrentHP();

        this.fortressBorderDamage = SettingsHandler.getInstance().getFortressBorderDamage();
        this.maxDamageForSeconds = SettingsHandler.getInstance().getMaxDamageForSeconds();

        this.activeInvaders = invaders;

        this.attackersTown = enemyTownName;

    }

    public boolean isInvaders(UUID uuid) {
        return this.activeInvaders.contains(uuid);
    }

    public void removeInvaders(UUID uuid) {
        this.bossBar.removePlayer(Bukkit.getPlayer(uuid));
        this.activeInvaders.remove(uuid);
    }

    public Fortress getFortress() {
        return this.fortress;
    }

    public void firstStepBattle() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class),SettingsHandler.getInstance().getStartBattleDelay(),
                () -> {

                    Message.BATTLE_ALLERT.broadcast(fortress.getFortressName(), fortress.getCurrentOwner());

                    this.bossBar = Bukkit
                            .createBossBar(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFortressName(),
                                    SettingsHandler.getInstance().getStartBattleDelay()),
                                    BarColor.YELLOW, BarStyle.SOLID);

                    this.bossBar.setProgress(0);

                    this.activeInvaders.forEach(uuid -> this.bossBar.addPlayer(Bukkit.getPlayer(uuid)));

                    TownyUtil.getTownResidentsFromTownName(this.fortress.getCurrentOwner()).forEach(resident -> {
                        if (resident.getPlayer() != null)
                            this.bossBar.addPlayer(resident.getPlayer());});

                },
                () -> {
            //
                    Message.BATTLE_START_BROADCAST.broadcast(this.fortress.getFormattedName());

                    checkInvadersPositionTask();

                    secondStepBattle();

                },
                (s) -> {
            //
                    this.bossBar.setTitle(Message.BOSSBAR_COUNTDOWN_FORMAT.asString(fortress.getFortressName(), s.getSecondsLeft()));
                    this.bossBar.setProgress((float) (s.getTotalSeconds() - s.getSecondsLeft()) / s.getTotalSeconds());

                });
        countdown.scheduleTimer();
    }

    private void secondStepBattle() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class), SettingsHandler.getInstance().getBattleTimeLimit(),
                () -> {
            //
                    this.bossBar.setColor(BarColor.RED);
                    this.bossBar.setProgress(1);

                },

                () -> {

                    Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFortressName());

                    BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getCurrentOwner(), this.fortressHP);

                },
                (s) -> {

                    this.fortressHP = Math.max(0,this.fortressHP - Math.min(this.maxDamageForSeconds,getInvadersInsideGreenArea()));

                    this.bossBar.setTitle(Message.BOSSBAR_FORMAT.asString(fortress.getFortressName(),
                            s.getSecondsLeft(),
                            this.fortressHP));

                    this.bossBar.setProgress((float) this.fortressHP / this.fortress.getCurrentHP());

                    if (this.activeInvaders.isEmpty()) {

                        Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFortressName());

                        BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getCurrentOwner(),
                                this.fortressHP);
                    }

                    if (this.fortressHP <= 0) {

                        Message.BATTLE_ENDED_BROADCAST2.broadcast(fortress.getFortressName(),this.attackersTown.getFormattedName());

                        BattleService.getInstance().resolveBattle(this.fortress, this.attackersTown.getFormattedName(),
                                SettingsHandler.getInstance().getFortressHP());
                    }

                });
        this.battleTaskID = countdown.scheduleTimer();
    }



    private void checkInvadersPositionTask() {
        this.positionTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FWFortress.plugin, () ->
                new ArrayList<>(activeInvaders)
                .stream().map(Bukkit::getPlayer)
                .filter(player -> player != null && !blueArea.contains(player.getBoundingBox()))
                .forEach(this::playerLeaveFortressArea),0L,20L);
        }

    private Integer getInvadersInsideGreenArea() {

        return (int) new ArrayList<>(this.activeInvaders).stream().map(Bukkit::getPlayer)
                .filter(player -> player != null && this.greenArea.contains(player.getLocation().toVector())).count();

    }

    public void playerLeaveFortressArea(Player player) {
        player.damage(this.fortressBorderDamage);
        player.sendActionBar(Message.BATTLE_LEAVE_ACTIONBAR.asString());
    }

    public void stopBattle() {
        this.bossBar.removeAll();
        Bukkit.getScheduler().cancelTask(this.positionTaskID);
        Bukkit.getScheduler().cancelTask(this.battleTaskID);
    }

}
