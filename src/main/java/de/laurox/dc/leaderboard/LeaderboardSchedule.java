package de.laurox.dc.leaderboard;

import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.EmbedFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaderboardSchedule {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Runnable leaderboardFunc = new Runnable() {
        @Override
        public void run() {
            MaddoxBot.getLogger().logString("♻️ | Hourly Leaderboard Update at " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            MaddoxBot.getJda().getGuildById(531863332156866583L).getTextChannelById(714549273429016650L).sendMessage(EmbedFactory.leaderboardEmbed("__MineTogether Leaderboard__", "Dieses Leaderboard zeigt die Top #10 Spieler unserer Gilde im SkillAverage und TotalSlayerXP\n", 0x398ff9)).queue();
        }
    };

    public void startScheduler() {
        System.out.println("Starting Leaderboard-Scheduler");
        scheduler.scheduleWithFixedDelay(leaderboardFunc, 0, 1, TimeUnit.HOURS);
    }
}
