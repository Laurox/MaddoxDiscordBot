package de.laurox.dc;

import net.dv8tion.jda.api.entities.TextChannel;

public class Logger {

    private final long LOGGER_ID = 744662098000347207L;

    private TextChannel loggingChannel;

    public Logger() {
        this.loggingChannel = MaddoxBot.getJda().getTextChannelById(LOGGER_ID);
    }

    public void logString(String str) {
        loggingChannel.sendMessage(str).queue();
    }

}
