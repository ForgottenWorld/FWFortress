package me.architetto.fwfortress.command.userplus;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.LocationUtil;
import me.architetto.fwfortress.util.TownyUtil;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.CLAIM_CMD;
    }

    @Override
    public String getDescription() {
        return Message.CLAIM_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.CLAIM_CMD + " <fortress_name>" ;
    }

    @Override
    public String getPermission() {
        return "fwfortress.claim";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        Resident resident = TownyUtil.getResidentFromPlayerName(sender.getName());

        if (Objects.isNull(resident))
            return;

        Town senderTown = TownyUtil.getTownFromPlayerName(sender.getName());

        if (Objects.isNull(senderTown) || !resident.isMayor()) {
            Message.ERR_CLAIM_MAYOR_ONLY.send(sender);
            return;
        }

        if (FortressService.getInstance().getFortressContainer().stream().anyMatch(f -> f.getFirstOwner().equals(senderTown.getName()))) {
            Message.ERR_TOWN_ALREADY_BUILD_FORTRESS.send(sender,senderTown.getFormattedName());
            return;
        }

        Location location = sender.getLocation();

        if (!FortressCreationService.getInstance().checkFortressesDistance(sender, location))
            return;

        if  (!FortressCreationService.getInstance().checkTownDistance(sender, location))
            return;

        String fortressName = String.join("_", Arrays.copyOfRange(args, 1, args.length));

        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortress(fortressName);

        if (optionalFortress.isPresent()) {
            Message.ERR_FORTRESS_NAME_ALREADY_EXIST.send(sender);
            return;
        }

        FortressCreationService.getInstance().addNewFortress(fortressName,
                senderTown.getName(),
                senderTown.getName(),
                location,
                SettingsHandler.getInstance().getFortressHP(),
                0,
                0,
                LocationUtil.area3x3ChunkKeys(location));

        Message.SUCCESS_FORTRESS_CLAIM_BRADCAST.broadcast(senderTown.getFormattedName(),fortressName.replace("_"," "));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
