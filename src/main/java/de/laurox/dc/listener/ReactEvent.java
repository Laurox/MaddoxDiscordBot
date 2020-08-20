package de.laurox.dc.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class ReactEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().toLowerCase().contains("laurox") || event.getMessage().getContentRaw().toLowerCase().contains("lau")) {
            event.getMessage().addReaction(new EmoteImpl(728041697178681414L, (GuildImpl) event.getGuild())).queue();
        }

        if(event.getMessage().getContentRaw().toLowerCase().contains("blackrecruit") || event.getMessage().getContentRaw().toLowerCase().contains("flo")) {
            event.getMessage().addReaction(new EmoteImpl(739865609839575090L, (GuildImpl) event.getGuild())).queue();
        }

        if(event.getAuthor().getIdLong() == (428309921415561218L) || event.getMessage().getContentRaw().toLowerCase().contains("miqdam")) {
            event.getMessage().addReaction(new EmoteImpl(739118135763796049L, (GuildImpl) event.getGuild())).queue();
        }
    }
}
