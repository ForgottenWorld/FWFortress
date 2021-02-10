package me.architetto.fwfortress.localization;

import me.architetto.fwfortress.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public enum Message {

    PREFIX("fwfortress_prefix", false),
    BOSSBAR_FORMAT("bossbar_format",false),
    BOSSBAR_COUNTDOWN_FORMAT("bossbar_countdown_format",false),

    FORTRESS_AREA_ALLERT("fortress_area_allert",false),

    ERR_PERMISSION("err_permission",true),
    ERR_SYNTAX("err_syntax",true),
    ERR_RELOAD("err_reload",true),
    ERR_TOWN_NAME("err_town_name",true),
    ERR_FORTRESS_NAME_ALREADY_EXIST("err_fortress_name_already_exist",true),
    ERR_FORTRESS_DOES_NOT_EXIST("err_fortress_does_not_exist",true),
    ERR_TOWN_ALREADY_BUILD_FORTRESS("err_town_already_build_fortress",true),
    ERR_FORTRESS_ALREADY_OWNED("err_fortress_already_owned",true),
    ERR_FORTRESS_UNDER_INVADE("err_fortress_under_invade",true),
    ERR_TONW_CAN_NOT_INVADE("err_town_can_not_invade",true),
    ERR_INVADE_ALLIED_FORTRESS("err_invade_allied_fortress",true),
    ERR_INSUFFICIENT_INVADERS("err_insufficient_invaders",true),
    ERR_CREATION_MODE("err_creation_mode",true),
    ERR_BATTLE_DISABLED("err_battle_disabled",true),
    ERR_BATTLE_TIME_RANGE("err_battle_time_range",true),
    ERR_INVALID_INVADE_POSITION("err_invalid_invade_position",true),
    ERR_INVADE_COOLDOWN("err_invade_cooldown",true),
    ERR_REPAIR_COOLDOWN("err_repair_cooldown",true),
    ERR_FORTRESS_MAX_HP("err_fortress_max_hp",true),
    ERR_PAY_RAPAIR("err_pay_repair",true),
    ERR_REPAIR_1("err_repair1",true),
    ERR_REPAIR_2("err_repair2",true),
    ERR_FORTRESS_DISTANCE("err_fortress_distance",true),
    ERR_NO_BATTLE_IS_RUNNING("err_no_battle_is_running",true),
    ERR_TOWN_DISTANCE("err_town_distance",true),

    ERR_INVADE_BUILDABLE("err_invade_buildable",true),

    ERR_BLOCK_EVENT("err_block_event",true),

    ERR_RES_NOT_REGISTERED("err_res_not_registered",true),
    ERR_NOT_PART_OF_A_TOWN("err_not_part_of_a_town",true),
    ERR_NOT_A_MAYOR("err_not_a_mayor",true),
    ERR_CLAIM_MAYOR_ONLY("err_claim_mayor_only",true),

    TELEPORT_DEATH_EVENT("teleport_death_event",true),

    SUCCESS_RELOAD("success_reload",true),
    SUCCESS_FORTRESS_CREATED("success_fortress_created",true),
    SUCCESS_FORTRESS_CLAIM_BRADCAST("success_fortress_claim_broadcast",true),
    SUCCESS_REPAIR("success_repair",true),

    TOGGLE_BATTLE_ENABLED("toggle_battle_enabled",true),
    TOGGLE_BATTLE_DISABLED("toggle_battle_disabled",true),

    FORTRESS_INFO("fortress_info",false),
    FORTRESS_FALL_IN_RUIN1("fortress_fall_in_ruin1",true),
    FORTRESS_FALL_IN_RUIN2("fortress_fall_in_ruin2",true),
    FORTRESS_DELETED_BROADCAST("fortress_deleted_broadcast",true),

    BATTLE_ALLERT("battle_allert_broadcast",true),
    BATTLE_START_BROADCAST("battle_start_broadcast",true),
    BATTLE_ENDED_BROADCAST1("battle_ended_broadcast1",true),
    BATTLE_ENDED_BROADCAST2("battle_ended_broadcast2",true),
    BATTLE_STOPPED("battle_stopped",true),

    BATTLE_LEAVE_ACTIONBAR("battle_leave_actionbar",false),

    CREATION_MODE_MSG_1("creation_mode_msg1",true),

    CREATE_COMMAND("create_command",false),
    DELETE_COMMAND("delete_command",false),
    RELOAD_COMMAND("reload_command",false),
    TOGGLE_COMMAND("toggle_command",false),
    STOP_COMMAND("stop_command",false),

    INFO_COMMAND("info_command",false),
    CLAIM_COMMAND("claim_command",false),
    INVADE_COMMAND("invade_command",false),
    REPAIR_COMMAND("repair_command",false);

    private final String message;
    private final boolean showPrefix;
    private final LocalizationManager localizationManager;

    Message(String message, boolean showPrefix) {
        this.message = StringUtil.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
        this.localizationManager = LocalizationManager.getInstance();
    }

    public void send(CommandSender sender, Object... objects) {
        sender.sendMessage(asString(objects));
    }

    public void groupSend(List<CommandSender> senders, Object... objects) {
        String s = asString(objects);
        senders.forEach(p -> p.sendMessage(s));
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
