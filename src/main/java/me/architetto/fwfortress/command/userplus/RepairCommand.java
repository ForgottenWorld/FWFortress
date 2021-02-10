package me.architetto.fwfortress.command.userplus;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.BankAccount;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.TownyUtil;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RepairCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.REPAIR_CMD;
    }

    @Override
    public String getDescription() {
        return Message.REPAIR_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.REPAIR_CMD + " <fortress_name>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.repair";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressName = args[1];

        Optional<Fortress> fortressO = FortressService.getInstance().getFortress(fortressName);

        if (!fortressO.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = fortressO.get();

        if (BattleService.getInstance().isOccupied(fortressName)) {
            Message.ERR_REPAIR_1.send(sender,fortress.getFormattedName());

            return;
        }

        Resident resident;
        Town town;

        try { 
            resident = TownyAPI.getInstance().getDataSource().getResident(sender.getName());
        } catch (NotRegisteredException e) {
            Message.ERR_RES_NOT_REGISTERED.send(sender);
            return;
        }

        try {
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            Message.ERR_NOT_A_MAYOR.send(sender);
            return;
        }

        if (!resident.isMayor()) {
            Message.ERR_NOT_A_MAYOR.send(sender);
            return;
        }

        if (!fortress.getCurrentOwner().equals(town.getName())) {
            Message.ERR_REPAIR_2.send(sender,fortress.getFormattedName());
            return;
        }

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (fortress.getLastRepair() != 0) {
            long remainCooldown = settingsHandler.getRepairCooldown() -
                    (System.currentTimeMillis() - fortress.getLastRepair());

            if (remainCooldown > 0) {
                Message.ERR_REPAIR_COOLDOWN.send(sender,fortress.getFormattedName(),
                        TimeUnit.MILLISECONDS.toHours(remainCooldown),
                        TimeUnit.MILLISECONDS.toMinutes(remainCooldown) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainCooldown)),
                        TimeUnit.MILLISECONDS.toSeconds(remainCooldown) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainCooldown)));
                return;
            }
        }

        double missingHP = settingsHandler.getFortressHP() - fortress.getCurrentHP();

        if (missingHP == 0) {
            Message.ERR_FORTRESS_MAX_HP.send(sender,fortress.getFormattedName());
            return;
        }

        int repairedFortressHP =  Math.min(settingsHandler.getFortressHP(),
                (int)(missingHP * (settingsHandler.getRepairPercentage() / 100f)) + fortress.getCurrentHP());

        BankAccount bankAccount = town.getAccount();

        try {
            if (bankAccount.canPayFromHoldings(settingsHandler.getRepairCost()))
                bankAccount.withdraw(settingsHandler.getRepairCost(), "fortress repair");
            else {
                Message.ERR_PAY_RAPAIR.send(sender,settingsHandler.getRepairCost());
                return;
            }
        } catch (EconomyException e) {
            e.printStackTrace();
        }

        fortress.setCurrentHP(repairedFortressHP);
        fortress.setLastRepair(System.currentTimeMillis());
        FortressService.getInstance().updateFortress(fortress);

        Message.SUCCESS_REPAIR.send(sender,fortress.getFormattedName());

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {

            Town town = TownyUtil.getTownFromPlayerName(player.getName());

            if (Objects.isNull(town))
                return null;

            return FortressService.getInstance().getFortressContainer().stream()
                    .filter(fortress -> fortress.getCurrentOwner().equals(town.getName()))
                    .map(Fortress::getFortressName).collect(Collectors.toList());
        }

        return null;
    }
}
