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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RepairCommand extends SubCommand {
    @Override
    public String getName() {
        return "repair";
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
        if (!sender.hasPermission("fwfortress.userplus")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        String fortressName = args[1];

        Optional<Fortress> fortressO = FortressService.getInstance().getFortress(fortressName);

        if (!fortressO.isPresent()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Nessuna fortezza con questo nome"));
            return;
        }

        Fortress fortress = fortressO.get();

        if (BattleService.getInstance().isOccupied(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Non e' possibile riparare la fortezza durante un attacco"));
            return;
        }

        //--------------------------------------------------------------//

        Resident resident = null;

        try {
            resident = TownyAPI.getInstance().getDataSource().getResident(sender.getName());
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }

        Town town = null;

        if (resident == null || !resident.hasTown())
            return;

        if (!resident.isMayor()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Solo il sindaco puo' riparare la fortezza"));
            return;
        }

        try {
            town = TownyAPI.getInstance().getDataSource().getTown(resident.getTown().getName());
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }

        if (town == null)
            return;

        if (!fortress.getCurrentOwner().equals(town.getName())) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Puoi riparare solo le fortezze che sono" +
                    " sotto il controllo della tua citta'"));
            return;
        }

        //---------------------------------------------------------------//

        if (fortress.getLastRepair() != 0) {
            long remain = SettingsHandler.getInstance().getRepairCooldown() -
                    (System.currentTimeMillis() - fortress.getLastRepair());
            if (remain > 0) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza potra' essere riparata tra : " +
                        ChatColor.YELLOW + String.format("%d ORE : %d MINUTI : %d SECONDI",
                        TimeUnit.MILLISECONDS.toHours(remain),
                        TimeUnit.MILLISECONDS.toMinutes(remain) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remain)),
                        TimeUnit.MILLISECONDS.toSeconds(remain) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remain)))));
                return;
            }
        }


        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        double missinghp = settingsHandler.getFortressHP() - fortress.getFortressHP();

        if (missinghp == 0) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza e' già al massimo degli HP"));
            return;
        }

        int repairedHp =  Math.min(settingsHandler.getFortressHP(),(int)((missinghp / 100) * settingsHandler.getRepairPercentage()) + fortress.getFortressHP());

        BankAccount bankAccount = town.getAccount();

        try {
            if (bankAccount.canPayFromHoldings(1000))
                bankAccount.withdraw(1000,"Riparazione fortezza");
        } catch (EconomyException e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatFormatter.formatSuccessMessage("La fortezza e' stata riparata. Costo riparazione : "
                + ChatColor.YELLOW + 1000));

        sender.sendMessage(ChatFormatter.formatListMessage("HP Precedenti : " + ChatColor.YELLOW + fortress.getFortressHP()));
        sender.sendMessage(ChatFormatter.formatListMessage("HP Recuparati : " + ChatColor.YELLOW + (repairedHp - fortress.getFortressHP())));
        sender.sendMessage(ChatFormatter.formatListMessage("HP Attuali : " + ChatColor.YELLOW + repairedHp));


        fortress.setFortressHP(repairedHp);
        fortress.setLastRepair(System.currentTimeMillis());
        FortressService.getInstance().updateFortressFile(fortress.getFortressName());


    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }
        return null;
    }
}
