package net.frozenorb.potpvp.setting.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /toggleglobalchat command, allows players to toggle {@link Setting#ENABLE_GLOBAL_CHAT} setting
 */
public final class ToggleGlobalChatCommand {

    @Command(names = {"toggleGlobalChat", "tgc", "togglechat"}, permission = "")
    public static void toggleGlobalChat(Player sender) {
        if (!Setting.ENABLE_GLOBAL_CHAT.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ENABLE_GLOBAL_CHAT);

        settingHandler.updateSetting(sender, Setting.ENABLE_GLOBAL_CHAT, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled global chat on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled global chat off.");
        }
    }

}