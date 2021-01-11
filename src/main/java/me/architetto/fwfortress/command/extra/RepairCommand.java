package me.architetto.fwfortress.command.extra;

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
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RepairCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.REPAIR_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.REPAIR_CMD_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "fwfortress.repair";
    }

    @Override
    public int getArgsRequired() {
        return 1;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressName = args[1];

        Optional<Fortress> fortressO = FortressService.getInstance().getFortress(fortressName);

        if (!fortressO.isPresent()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_FORTRESS_NAME2));
            return;
        }

        Fortress fortress = fortressO.get();

        if (BattleService.getInstance().isOccupied(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_REPAIR1));
            return;
        }

        Resident resident;
        Town town;

        try { 
            resident = TownyAPI.getInstance().getDataSource().getResident(sender.getName());
        } catch (NotRegisteredException e) { 
            e.printStackTrace();
            return;
        }

        try {
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_REPAIR2));
            return;
        }

        if (!resident.isMayor()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_REPAIR2));
            return;
        }

        if (!fortress.getCurrentOwner().equals(town.getName())) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_REPAIR3));
            return;
        }

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (fortress.getLastRepair() != 0) {
            long remainCooldown = settingsHandler.getRepairCooldown() -
                    (System.currentTimeMillis() - fortress.getLastRepair());

            if (remainCooldown > 0) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza potra' essere riparata tra : " +
                        ChatColor.YELLOW + String.format("%d ORE : %d MINUTI : %d SECONDI",
                        TimeUnit.MILLISECONDS.toHours(remainCooldown),
                        TimeUnit.MILLISECONDS.toMinutes(remainCooldown) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainCooldown)),
                        TimeUnit.MILLISECONDS.toSeconds(remainCooldown) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainCooldown)))));
                return;
            }
        }

        double missingHP = settingsHandler.getFortressHP() - fortress.getCurrentHP();

        if (missingHP == 0) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza e' già al massimo degli HP"));
            return;
        }

        int repairedFortressHP =  Math.min(settingsHandler.getFortressHP(),
                (int)(missingHP * (settingsHandler.getRepairPercentage() / 100f)) + fortress.getCurrentHP());

        BankAccount bankAccount = town.getAccount();

        try {
            if (bankAccount.canPayFromHoldings(settingsHandler.getRepairCost()))
                bankAccount.withdraw(settingsHandler.getRepairCost(), "Riparazione fortezza");
            else
                sender.sendMessage(ChatFormatter.formatErrorMessage("La citta' non possiede i fondi necessari" +
                        " per riparare la fortezza. Costo riparazione : " +
                        settingsHandler.getRepairCost()));
        } catch (EconomyException e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatFormatter.formatSuccessMessage("La fortezza e' stata riparata : "));
        sender.sendMessage(ChatFormatter.formatListMessage("HP Precedenti : " + ChatColor.YELLOW + fortress.getCurrentHP()));
        sender.sendMessage(ChatFormatter.formatListMessage("HP Attuali : " + ChatColor.YELLOW + repairedFortressHP));

        fortress.setCurrentHP(repairedFortressHP);
        fortress.setLastRepair(System.currentTimeMillis());
        FortressService.getInstance().saveFortress(fortress);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }
        return null;
    }
}
