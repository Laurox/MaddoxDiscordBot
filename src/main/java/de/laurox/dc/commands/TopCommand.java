package de.laurox.dc.commands;

import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.CommandParent;
import de.laurox.dc.util.Comparators;
import de.laurox.dc.util.EmbedFactory;
import de.laurox.dc.util.PlayerObject;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class TopCommand extends CommandParent {

    public TopCommand(String commandName, int minArgs) {
        super(commandName, minArgs);
    }

    @Override
    protected void processCommand(MessageReceivedEvent eventInstance) {
        if(super.getArgs()[0].toLowerCase().equals("leaderboard")) {
            eventInstance.getChannel().sendMessage(EmbedFactory.leaderboardEmbed("__MineTogether Leaderboard__", "Dieses Leaderboard zeigt die Top #10 Spieler unserer Gilde im SkillAverage und TotalSlayerXP\n", 0x398ff9)).queue();
            return;
        }

        int page = 1;
        int maxPages = (int) Math.ceil(MaddoxBot.getDbMngr().getAllGuildMembersLoaded().size() / 10.0);
        if(super.getArgs().length == super.getMinArgs() + 1) {
            page = Integer.parseInt(super.getArgs()[1]);
        }

        if(page > maxPages) {
            page = maxPages;
        }

        List<PlayerObject> tops = MaddoxBot.getDbMngr().getTopMembers(page, 10, Comparators.chooseComparator(super.getArgs()[0]));

        int index = -2;
        List<PlayerObject> allGuildMembers = null;
        if(MaddoxBot.getDbMngr().isVerified(eventInstance.getAuthor().getId())) {
            allGuildMembers = MaddoxBot.getDbMngr().getAllGuildMembers();
            allGuildMembers.sort(Comparators.chooseComparator(super.getArgs()[0].toLowerCase()));
            String uuid = MaddoxBot.getDbMngr().getLinkedPlayer(eventInstance.getAuthor().getId()).get("uuid").toString();
            PlayerObject playerObject = new PlayerObject(MaddoxBot.getDbMngr().getPlayerStats(uuid));
            index = allGuildMembers.indexOf(playerObject);
        }

        String description = "Seite `#" + page + "` von `#" + maxPages + "`.\nUm die Seiten zu wechseln, nutze `!top [type] [page]`.\n" + ((index < 0) ? "" : "\nDu bist Platz `#" + (index + 1) + "` von `#" + allGuildMembers.size() + "`");

        eventInstance.getChannel().sendMessage(EmbedFactory.generateEmbedFromTop("__MineTogether Leaderboard__", description, 0x1FB744, super.getArgs()[0].toLowerCase(), eventInstance.getAuthor(), tops, page, 10)).queue();
        // eventInstance.getChannel().sendMessage(EmbedGenerator.leaderboardEmbed("MineTogether Leaderboard", null, 0x000000)).queue();
    }

    @Override
    protected boolean validateArgs(String[] args, MessageReceivedEvent eventInstance) {
        super.setArgs(args);

        if(args.length == super.getMinArgs() || args.length == super.getMinArgs() + 1) {
            List<String> keys = List.of("skills", "slayer", "average", "farming", "mining", "combat", "foraging", "fishing", "enchanting", "alchemy", "taming", "spider", "zombie", "wolf", "leaderboard");
            if(!keys.contains(args[0].toLowerCase())) {
                return false;
            }

            if(args.length == super.getMinArgs() + 1) {
                try {
                    int page = Integer.parseInt(args[1]);
                    if(page <= 0)
                        return false;
                } catch (NumberFormatException e) {
                    return false;
                }

            }

            return true;
        }

        return false;
    }

    @Override
    protected void invalidMessage(MessageReceivedEvent eventInstance) {
        eventInstance.getChannel().sendMessage("Ungültige Argumente! Nutze `!top [type] [page]`\n\n__Verfügbare Types:__\nAllgemeines: skills, slayer, average\nSkills: farming, mining, combat, foraging, fishing, enchanting, alchemy, taming\nSlayers: zombie, spider, wolf\n\n__Page:__\nSeitenzahl, optionales Argument").queue();
    }
}
