package me.architetto.fwfortress.echelon;


import it.forgottenworld.echelonapi.mutexactivity.MutexActivity;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EchelonMutexActivity implements MutexActivity {

    @Override
    public String getId() {
        return "FWFortress";
    }

    @Override
    public void onAllPlayersForceRemoved(String s) {
        if (!BattleService.getInstance().getCurrentBattle().isEmpty())
            BattleService.getInstance().getCurrentBattle().forEach(battle -> {
                Message.BATTLE_STOPPED.broadcast(battle.getFortress().getFormattedName());
                battle.stopBattle();
            });
    }

    @Override
    public void onPlayerForceRemoved(Player player, String s) {
        if (!BattleService.getInstance().getCurrentBattle().isEmpty()) {
            BattleService.getInstance().getCurrentBattle().forEach(battle -> {
                if (battle.isInvaders(player.getUniqueId())) {
                    battle.removeInvaders(player);
                }
            });
        }
    }
}
