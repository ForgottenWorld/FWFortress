package me.architetto.fwfortress.echelon;

import it.forgottenworld.echelonapi.FWEchelonApi;
import it.forgottenworld.echelonapi.mutexactivity.MutexActivityService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class EchelonService {

    private static EchelonService echelonHolder;

    private MutexActivityService mutexActivityService;

    private EchelonService() {
        if(echelonHolder != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

    }

    public static EchelonService getInstance() {
        if(echelonHolder == null)
            echelonHolder = new EchelonService();

        return echelonHolder;
    }

    public boolean loadEchelonService() {

        FWEchelonApi echelon = (FWEchelonApi) Bukkit.getPluginManager().getPlugin("FWEchelon");

        if (Objects.isNull(echelon))
            return false;

        mutexActivityService = echelon.getMutexActivityService();

        return mutexActivityService.registerMutexActivity(new EchelonMutexActivity());
    }

    public boolean isPlayerInMutexActivity(Player player) {
        return mutexActivityService.isPlayerInMutexActivity(player);
    }

    public void addPlayerMutexActivity(Player player) {
        mutexActivityService.playerJoinMutexActivity(player,"FWFortress", false);
    }

    public void removePlayerMutexActivity(Player player) {
        mutexActivityService.removePlayerFromMutexActivity(player, "FWFortress");
    }

    public String getPlayerMutexActivityName(Player player) {
        return mutexActivityService.getPlayerMutexActivityName(player);
    }

    public void forceRemoveAllPlayersFromFortressActivity() {
        mutexActivityService.forceRemoveAllPlayersFromMutexActivity("FWFortress","Reload command");
    }

}
