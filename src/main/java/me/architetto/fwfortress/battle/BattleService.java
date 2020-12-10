package me.architetto.fwfortress.battle;


import java.util.HashMap;

public class BattleService {

    //WIP WIP WIP WIP

    private static BattleService battleService;

    private HashMap<String,Battle> battleContainer;
    //private HashMap<String,Long> fortressLastBattle;


    private BattleService() {
        if(battleService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //inizializzare le variabili

        this.battleContainer = new HashMap<>();
        //this.fortressLastBattle = new HashMap<>(); //va inizializzata con un valore preso da config per ogni fortezza

    }

    public static BattleService getInstance() {
        if(battleService == null) {
            battleService = new BattleService();
        }
        return battleService;
    }

    //todo

}
