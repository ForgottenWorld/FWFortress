package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

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
            sender.sendMessage(ChatFormatter.formatErrorMessage("Devi trovarti all'interno di una fortezza per poterla conquistare"));
            return;
        }

        Town senderTown;

        try{
            senderTown = TownyAPI.getInstance().getDataSource().getResident(sender.getName()).getTown();
        } catch (NotRegisteredException e) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Devi essere un cittadino per avviare una conquista !"));
            return;
        }

        if (fortress.get().getCurrentOwner().equals(senderTown.getName())) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Questa fortezza e' gia' sotto il controlo della tua citta' !"));
            return;
        }

        if (FortressService.getInstance().getFortressContainer().values()
                .stream().noneMatch(f -> f.getFirstOwner().equals(senderTown.getName()))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Non puoi invadere una fortezza se la tua citta' non ne ha mai costruita una"));
            return;
        }

        List<UUID> senderTownResidentUUID = senderTown.getResidents().stream().map(Resident::getPlayer)
                .map(Player::getUniqueId).collect(Collectors.toCollection(ArrayList::new));

        List<UUID> invadersListUUID = new ArrayList<>();


        for (Entity entity : sender.getLocation().getChunk().getEntities()) {

            if (!(entity instanceof Player))
                continue;

            Player player = ((Player) entity).getPlayer();

            if (player != null && senderTownResidentUUID.contains(player.getUniqueId()))
                invadersListUUID.add(player.getUniqueId());
        }


        BattleService.getInstance().startNewBattle(fortress.get(), invadersListUUID, senderTown.getName());



    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}
