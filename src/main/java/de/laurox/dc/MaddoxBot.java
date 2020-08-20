package de.laurox.dc;

import de.laurox.dc.commands.CalcSkillCommand;
import de.laurox.dc.commands.StatsCommand;
import de.laurox.dc.commands.TopCommand;
import de.laurox.dc.database.DBMngr;
import de.laurox.dc.database.Updater;
import de.laurox.dc.leaderboard.LeaderboardSchedule;
import de.laurox.dc.listener.ReactEvent;
import de.laurox.dc.util.CommandProcessor;
import de.laurox.dc.verify.VCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class MaddoxBot {

    private static final String TOKEN = "NzE3Mzc5NzIwOTgyOTU0MDQ1.XwTDTw.-9q-p7vhMvDPm-t1h_KRIwGRC54";

    private static JDA jda;
    private static DBMngr dbMngr;
    private static String prefix = "!";
    private static Logger logger;

    public static void main(String[] args) throws LoginException, InterruptedException {
        dbMngr = new DBMngr();

        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        // Disable parts of the cache
        jdaBuilder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        jdaBuilder.setBulkDeleteSplittingEnabled(false);

        // Disable compression (not recommended)
        // jdaBuilder.setCompression(Compression.NONE);

        jdaBuilder.setActivity(Activity.playing("Drachen töten!"));

        jdaBuilder.addEventListeners(new ReactEvent());

        // own command processor
        CommandProcessor commands = new CommandProcessor();

        // adding own commands
        commands.addCommand(new StatsCommand("stats", 0));
        commands.addCommand(new VCommand("verify", 1));
        commands.addCommand(new CalcSkillCommand("calcskill", 2));
        commands.addCommand(new TopCommand("top", 1));

        // lost zeug
        jdaBuilder.addEventListeners(commands);
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        jda = jdaBuilder.build().awaitReady();
        logger = new Logger();

        Updater updater = new Updater();
        // updater.startScheduler();

        LeaderboardSchedule leaderboard = new LeaderboardSchedule();
        // leaderboard.startScheduler();
    }

    public static JDA getJda() {
        return jda;
    }

    public static DBMngr getDbMngr() {
        return dbMngr;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static Logger getLogger() {
        return logger;
    }
}
