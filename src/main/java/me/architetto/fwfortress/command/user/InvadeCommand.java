package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class InvadeCommand extends SubCommand {
    @Override
    public String getName() {
        return "invade";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player sender, String[] args) {
        if (!sender.hasPermission("fwfortress.user")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        Optional<Fortress> fortress = FortressService.getInstance().getFortress(sender.getLocation().getChunk().getChunkKey());

        if (!fortress.isPresent()) {
            sender.sendMessage("Devi trovarti al centro della fortezza per eseguire il comando");
            //deve trovarsi nella fortezza per eseguire il comando
            return;
        }


        Optional<Resident> resident = TownyAPI.getInstance().getDataSource().getResidents().stream().filter(r -> r.getPlayer().equals(sender)).findFirst();
        if (!resident.isPresent()) {
            sender.sendMessage("Errore optional resident");
            return;
        }

        Resident res = resident.get();

        Optional<Town> town = TownyAPI.getInstance().getDataSource().getTowns().stream().filter(t -> t.getResidents().contains(res)).findFirst();

        if (!town.isPresent()) {
            sender.sendMessage("Devi essere cittadino di una citta' per avviare un'invasione");
            return;
        }

        Town tow = town.get();

        if (fortress.get().getCurrentOwner().equals(tow.getName())) {
            sender.sendMessage("Non puoi attaccare una fortezza posseduta dalla tua citta'");
            return;
        }

        List<UUID> enemies = new ArrayList<>();

        for (Entity entity : sender.getLocation().getChunk().getEntities()) {
            if (entity instanceof Player) {
                Player player = ((Player) entity).getPlayer();
                Optional<Resident> resident1 = tow.getResidents().stream().filter(resident2 -> resident2.getPlayer().equals(player)).findFirst();
                if (resident1.isPresent())
                    enemies.add(player.getUniqueId());
            }
        }

        Bukkit.getConsoleSender().sendMessage(fortress.get().getFortressName() + " // " + enemies.toString() + "//" + tow.getName());

        BattleService.getInstance().startNewBattle(fortress.get(), enemies, tow.getName());



    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}
