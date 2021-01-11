package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
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
                () -> sendGlobalMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " +
                        ChatColor.YELLOW + fortress.getFortressName() +
                        ChatColor.AQUA + " protetta dalla citta' di " +
                        ChatColor.YELLOW + fortress.getCurrentOwner() +
                        ChatColor.AQUA + " sta' per essere attaccata da " +
                        ChatColor.YELLOW + enemyTownName)),
                () -> {

                    sendMessageToEnemies(ChatFormatter.formatMessage(ChatColor.AQUA + "La battaglia per la conquista della fortezza " +
                            ChatColor.YELLOW + fortress.getFortressName() +
                                    ChatColor.AQUA + " e' cominciata ! Da questo momento lasciare la fortezza equivale a morire!"));

                    checkInvadersPosition();

                    startBattle();

                },
                (s) -> {
            //
                    if (s.getSecondsLeft() == s.getTotalSeconds() - 2) {
                        sendMessageToTown(fortress.getCurrentOwner(), ChatFormatter.formatMessage(ChatColor.AQUA +
                                "L'attacco alla fortezza " + ChatColor.YELLOW + fortress.getFortressName()
                                + ChatColor.AQUA + " partira' tra " + ChatColor.YELLOW + s.getSecondsLeft()
                                + ChatColor.AQUA + " secondi!"));

                        sendMessageToEnemies(ChatFormatter.formatMessage(ChatColor.AQUA +
                                "L'attacco alla fortezza " + ChatColor.YELLOW + fortress.getFortressName()
                                + ChatColor.AQUA + " partira' tra " + ChatColor.YELLOW + s.getSecondsLeft()
                                + ChatColor.AQUA + " secondi!"));
                    }

                    if (s.getSecondsLeft() == 5) {

                        sendMessageToTown(fortress.getCurrentOwner(), ChatFormatter.formatMessage(ChatColor.AQUA +
                                "Tra pochi secondi la tua fortezza " + ChatColor.YELLOW + fortress.getFortressName() +
                                ChatColor.AQUA + " verra' attacata. Corri a proteggerla !"));

                        sendMessageToEnemies(ChatFormatter.formatMessage(ChatColor.AQUA +
                                "Preparati alla battaglia, rimani nella fortezza fino a conquistarla"));

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
                                        this.bossBar.addPlayer(resident.getPlayer()); });
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }
                },

                () -> {

                    sendGlobalMessage(ChatFormatter.formatMessage(ChatColor.YELLOW + fortress.getFortressName() +
                            ChatColor.AQUA + " ha resistito all'attacco di ") +
                            ChatColor.YELLOW + enemyTownName);

                    BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getCurrentOwner(), this.fortressHP);


                },
                (s) -> {

                    this.fortressHP -= Math.min(this.maxDamageForSeconds,getInvadersInsideGreenArea());

                    this.bossBar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + fortress.getFortressName() +
                            ChatColor.AQUA + " [ TIMER : " + ChatColor.YELLOW + s.getSecondsLeft() +
                            ChatColor.AQUA + " ] [ HP : " + ChatColor.YELLOW + this.fortressHP +
                            ChatColor.AQUA + " ]");

                    this.bossBar.setProgress((float) this.fortressHP / this.fortress.getCurrentHP());

                    if (this.activeInvaders.isEmpty()) {
                        sendGlobalMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " + ChatColor.YELLOW + fortress.getFortressName() +
                                ChatColor.AQUA + " ha resistito all'attacco di ") +
                                ChatColor.YELLOW + enemyTownName);

                        BattleService.getInstance().resolveBattle(this.fortress, this.fortress.getCurrentOwner(),
                                this.fortressHP);
                    }

                    if (this.fortressHP <= 0) {
                        sendGlobalMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " +
                                ChatColor.YELLOW + fortress.getFortressName() +
                                ChatColor.AQUA + " e' stata conquistata da ") +
                                ChatColor.YELLOW + enemyTownName);

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
        player.sendActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + "TORNA NELLA FORTEZZA");
    }

    private void sendGlobalMessage(String msg) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));
    }

    private void sendMessageToEnemies(String msg) {
        new ArrayList<>(this.activeInvaders).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(msg);
        });
    }

    public void sendMessageToPlayerInFortressArea(String msg) {
        for (long fortChunkKeys : this.fortress.getChunkKeys()) {
            for (Entity entity : Bukkit.getWorld(this.fortress.getWorldName()).getChunkAt(fortChunkKeys).getEntities()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.sendMessage(msg);
                }
            }
        }
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
