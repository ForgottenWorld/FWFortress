package me.architetto.fwfortress.localization;

import me.architetto.fwfortress.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Message {

    PREFIX("fwfortress_prefix", false),

    BOSSBAR_FORMAT("bossbar_format",false),
    BOSSBAR_COUNTDOWN_FORMAT("bossbar_countdown_format",false),

    FORTRESS_AREA_ACTIONBAR("fortress_area_actionbar",false),
    BATTLE_LEAVE_ACTIONBAR("battle_leave_actionbar",false),

    ERR_PERMISSION("err_permission",true),
    ERR_SYNTAX("err_syntax",true),

    ERR_RELOAD("err_reload",true),
    ERR_FORTRESS_NAME_ALREADY_EXIST("err_fortress_name_already_exist",true),
    ERR_FORTRESS_DOES_NOT_EXIST("err_fortress_does_not_exist",true),
    ERR_FORTRESS_DISABLED("err_fortress_disabled",true),
    ERR_FORTRESS_ALREADY_OWNED("err_fortress_already_owned",true),
    ERR_FORTRESS_UNDER_INVADE("err_fortress_under_invade",true),
    ERR_INVADE_ALLIED_FORTRESS("err_invade_allied_fortress",true),
    ERR_INSUFFICIENT_INVADERS("err_insufficient_invaders",true),
    ERR_CREATION_MODE("err_creation_mode",true),
    ERR_BATTLE_TIME_RANGE("err_battle_time_range",true),
    ERR_INVALID_INVADE_POSITION("err_invalid_invade_position",true),
    ERR_INVADE_COOLDOWN("err_invade_cooldown",true),
    ERR_NO_BATTLE_IS_RUNNING("err_no_battle_is_running",true),
    ERR_TOWN_DISTANCE("err_town_distance", true),
    ERR_FORTRESS_DISTANCE("err_fortress_distance",true),
    ERR_BLOCK_EVENT("err_block_event",true),
    ERR_NOT_PART_OF_A_TOWN("err_not_part_of_a_town",true),
    ERR_INVALID_TOWN_NAME("err_invalid_town_name",true),
    ERR_INVALID_INPUT_VALUE("err_invalid_input_value",true),
    ERR_FORTRESS_OCCUPIED("err_fortress_occupied",true),

    TELEPORT_DEATH_EVENT("teleport_death_event",true),

    SUCCESS_RELOAD("success_reload",true),
    SUCCESS_FORTRESS_CREATED("success_fortress_created",true),
    SUCCESS_FORTRESS_DELETED("fortress_deleted",true),
    SUCCESS_MESSAGE("success_message",true),

    FORTRESS_INFO("fortress_info",false),
    FORTRESS_RETURN_FREE("fortress_return_free",true),
    FORTRESS_OWNED_DELETED("fortress_owned_deleted_broadcast",true),
    FORTRESS_CLAIM_BROADCAST("fortress_claim_broadcast",true),

    BATTLE_ALLERT("battle_allert_broadcast",true),
    BATTLE_START_BROADCAST("battle_start_broadcast",true),
    BATTLE_ENDED_BROADCAST1("battle_ended_broadcast1",true),
    BATTLE_ENDED_BROADCAST2("battle_ended_broadcast2",true),
    BATTLE_STOPPED("battle_stopped",true),

    CREATION_MODE_MSG("creation_mode_msg",true),

    TELEPORT_DENY("teleport_deny",true),

    EXCEPTION_MESSAGE("exception_allert_message",true),

    SETTINGS_INFO("settings_info",false),

    //Command description
    CREATE_COMMAND("create_command",false),
    DELETE_COMMAND("delete_command",false),
    RELOAD_COMMAND("reload_command",false),
    STOP_COMMAND("stop_command",false),
    TELEPORT_COMMAND("teleport_command", false),
    SET_COMMAND("set_command",false),
    INFO_COMMAND("info_command",false),
    INVADE_COMMAND("invade_command",false),

    //FWEchelon messages
    ERR_ECHELON_ACTIVITY("err_echelon_activity",true);

    private final String message;
    private final boolean showPrefix;
    private final LocalizationManager localizationManager;

    Message(String message, boolean showPrefix) {
        this.message = MessageUtil.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
        this.localizationManager = LocalizationManager.getInstance();
    }

    public void send(CommandSender sender, Object... objects) {
        sender.sendMessage(asString(objects));
    }

    public void broadcast(Object... objects) {
        Bukkit.getServer().broadcastMessage(asString(objects));
    }

    public String asString(Object... objects) {
        return format(objects);
    }

    private String format(Object... objects) {
        String string = localizationManager.localize(this.message);
        if(this.showPrefix) {
            string = localizationManager.localize(PREFIX.message) + " " + string;
        }
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            string = string.replace("{" + i + "}", String.valueOf(o));
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }


}
