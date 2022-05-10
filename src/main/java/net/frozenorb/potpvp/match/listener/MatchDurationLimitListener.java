package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchEndReason;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.frozenorb.qlib.util.TimeUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MatchDurationLimitListener implements Listener {

    private static final int DURATION_LIMIT_SECONDS = (int) TimeUnit.MINUTES.toSeconds(8);
    private static final int DURATION_LIMIT_SECONDS_BOXING = (int) TimeUnit.SECONDS.toSeconds(90);
    private static final String TIME_WARNING_MESSAGE = ChatColor.RED + "The match will forcefully end in %s.";
    private static final String TIME_EXCEEDED_MESSAGE = ChatColor.RED.toString() + ChatColor.BOLD + "Match time exceeded %s. Ending match...";

    @EventHandler
    public void onMatchCountdownEnd(MatchStartEvent event) {
        Match match = event.getMatch();
        new BukkitRunnable() {

            int secondsRemaining;

            @Override
            public void run() {
                if (match.getKitType().getId().equalsIgnoreCase("boxing")) {
                    secondsRemaining = DURATION_LIMIT_SECONDS_BOXING;
                } else {
                    secondsRemaining = DURATION_LIMIT_SECONDS;
                }
                if (match.getState() != MatchState.IN_PROGRESS) {
                    cancel();
                    return;
                }

                // Very ugly to do it here, but I don't want to put another runnable per match
                if (match.getKitType().getId().equalsIgnoreCase("SUMO")) {
                    match.getTeams().forEach(t -> t.getAllMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.setSaturation(20);
                    }));
                }

                switch (secondsRemaining) {
                    case 120:
                    case 60:
                    case 30:
                    case 15:
                    case 10:
                    case 5:
                        match.messageAll(String.format(TIME_WARNING_MESSAGE, TimeUtils.formatIntoDetailedString(secondsRemaining)));
                        break;
                    case 0:
                        match.messageAll(String.format(TIME_EXCEEDED_MESSAGE, TimeUtils.formatIntoDetailedString(DURATION_LIMIT_SECONDS)));
//                        match.endMatch(MatchEndReason.DURATION_LIMIT_EXCEEDED);
                        Player chosen = Bukkit.getPlayer(event.getMatch().getTeams().get(0).getFirstAliveMember());
                        match.markDead(chosen);
                        match.addSpectator(chosen, null, true);
                        chosen.teleport(match.getArena().getSpectatorSpawn());
                        break;
                    default:
                        break;
                }

                secondsRemaining--;
            }

        }.runTaskTimer(PotPvPSI.getInstance(), 20L, 20L);
    }

}