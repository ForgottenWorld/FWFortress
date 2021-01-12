package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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

    private String enemyTownName;

    private BossBar bossBar;

    private int fortressHP;

    private int fortressBorderDamage;
    private int maxDamageForSeconds;

    private int battleTaskID;
    private int positionTaskID;

    public Battle(Fortress fortress, List<UUID> invaders, String enemyTownName) {

        this.fortress = fortress;

        this.greenArea = fortress.getGreenBoundingBox();
        this.blueArea = fortress.getBlueBoundingBox();

        this.fortressHP = fortress.getCurrentHP();

        this.fortressBorderDamage = SettingsHandler.getInstance().getFortressBorderDamage();
        this.maxDamageForSeconds = SettingsHandler.getInstance().getMaxDamageForSeconds();

        this.activeInvaders = invaders;

        this.enemyTownName = enemyTownName;

        this.bossBar = Bukkit.createBossBar("PLACEHOLDER", BarColor.RED, BarStyle.SOLID);

    }

    public List<UUID> getActiveInvaders() {
        return new ArrayList<>(this.activeInvaders);
    }

    public Fortress getFortressInBattle() { return this.fortress; }

    public void initBattle() {

        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class),SettingsHandler.getInstance().getStartBattleDelay(),
                () -> Message.BATTLE_ALLERT.broadcast(fortress.getFortressName(),fortress.getCurrentOwner()),
                () -> {

                    sendMessageToInvaders(Message.BATTLE_START_INVADERS_ALLERT.asString(fortress.getFortressName()));

                    checkInvadersPosition();
                    startBattle();

                },
                (s) -> {

                    if (s.getSecondsLeft() == s.getTotalSeconds() - 10) {
                        sendMessageToTown(fortress.getCurrentOwner(),
                                Message.BATTLE_START_COUNTDOWN_ALLERT.asString(fortress.getFortressName(),s.getSecondsLeft()));

                        sendMessageToTown(this.enemyTownName,
                                Message.BATTLE_START_COUNTDOWN_ALLERT.asString(fortress.getFortressName(),s.getSecondsLeft()));

                    }

                    if (s.getSecondsLeft() == 10) {

                        sendMessageToTown(fortress.getCurrentOwner(), Message.BATTLE_START_ALLERT.asString(fortress.getFortressName()));

                        sendMessageToTown(this.enemyTownName, Message.BATTLE_START_ALLERT.asString(fortress.getFortressName()));

                    }

                });
        countdown.scheduleTimer();
    }

    private void startBattle() {
        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class), SettingsHandler.getInstance().getBattleTimeLimit(),
                () -> {

                    this.activeInvaders.forEach(uuid -> this.bossBar.addPlayer(Bukkit.getPlayer(uuid)));

                    try {
                        TownyAPI.getInstance().getDataSource().getTown(this.fortress.getCurrentOwner())
                                .getResidents().forEach(resident -> {
                                    if (resident.getPlayer() != null)
                                        this.bossBar.addPlayer(resident.getPlayer());});
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }
                },

                () -> {

                    Message.BATTLE_ENDED_BROADCAST1.broadcast(fortress.getFortressName());

                    BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getCurrentOwner(), this.fortressHP);

                },
                (s) -> {

                    this.fortressHP -= Math.min(this.maxDamageForSeconds,getInvadersInsideGreenArea());

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

                        Message.BATTLE_ENDED_BROADCAST2.broadcast(fortress.getFortressName(),this.enemyTownName);

                        BattleService.getInstance().resolveBattle(this.fortress, this.enemyTownName,
                                SettingsHandler.getInstance().getFortressHP());
                    }

                });
        this.battleTaskID = countdown.scheduleTimer();
    }

    public void removeInvaders(UUID uuid) {
        this.bossBar.removePlayer(Bukkit.getPlayer(uuid));
        this.activeInvaders.remove(uuid);
    }

    private void checkInvadersPosition() {
        this.positionTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FWFortress.plugin, () ->
                new ArrayList<>(activeInvaders)
                .stream().map(Bukkit::getPlayer)
                .filter(player -> player != null && !blueArea.contains(player.getBoundingBox()))
                .forEach(this::playerLeaveFortressArea),0L,10L);
        }

    private Integer getInvadersInsideGreenArea() {

        return (int) new ArrayList<>(this.activeInvaders).stream().map(Bukkit::getPlayer)
                .filter(player -> player != null && this.greenArea.contains(player.getBoundingBox())).count();

    }

    public void playerLeaveFortressArea(Player player) {
        player.damage(this.fortressBorderDamage);
        player.sendActionBar(Message.BATTLE_LEAVE_ACTIONBAR.asString());
    }

    private void sendMessageToInvaders(String msg) {
        new ArrayList<>(this.activeInvaders).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(msg);
        });
    }

    private void sendMessageToTown(String townName, String msg) {

        try {
            Town town = TownyAPI.getInstance().getDataSource().getTown(townName);
            TownyAPI.getInstance().getOnlinePlayersInTown(town).forEach(player -> player.sendMessage(msg));
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void stopBattle() {
        this.bossBar.removeAll();
        Bukkit.getScheduler().cancelTask(this.positionTaskID);
        Bukkit.getScheduler().cancelTask(this.battleTaskID);
    }

}
