package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;

import org.apache.commons.lang.StringEscapeUtils;

public final class PotPvPScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(TitleGetter.forStaticString(PotPvPSI.getInstance().getConfig().getString("Scoreboard.Title")));
        configuration.setScoreGetter(new MultiplexingScoreGetter(
            new MatchScoreGetter(),
            new LobbyScoreGetter()
        ));

        return configuration;
    }

}
