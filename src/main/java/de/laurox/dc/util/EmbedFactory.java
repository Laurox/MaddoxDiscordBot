package de.laurox.dc.util;

import de.laurox.dc.MaddoxBot;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

public class EmbedFactory {

    private static NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);

    private static String getKey(PlayerObject object, String key) {
       return "" + switch (key) {
            case "skills" -> object.getTotalSkillXP() + "  [" + object.getSkillAverage() +"]";
            case "slayer" -> makeLength(object.getTotalSlayerXP(), 9) + "  [" + PlayerObject.slayerXpToLevel(object.getZombie(), true) + "/" + PlayerObject.slayerXpToLevel(object.getSpider(), true) + "/" + PlayerObject.slayerXpToLevel(object.getWolf(), true) +"]";
            case "average" -> makeLength(object.getSkillAverage(), 5) + "  (" + object.getTotalSkillXP() +")";

            case "farming" -> formatter.format(Math.round(object.getFarming())) + "  [" + PlayerObject.skillXpToLevel(object.getFarming()) +"]";
            case "mining" -> formatter.format(Math.round(object.getMining())) + "  [" + PlayerObject.skillXpToLevel(object.getMining()) +"]";
            case "combat" -> formatter.format(Math.round(object.getCombat())) + "  [" + PlayerObject.skillXpToLevel(object.getCombat()) +"]";
            case "foraging" -> formatter.format(Math.round(object.getForaging())) + "  [" + PlayerObject.skillXpToLevel(object.getForaging()) +"]";
            case "fishing" -> formatter.format(Math.round(object.getFishing())) + "  [" + PlayerObject.skillXpToLevel(object.getFishing()) +"]";
            case "enchanting" -> formatter.format(Math.round(object.getEnchanting())) + "  [" + PlayerObject.skillXpToLevel(object.getEnchanting()) +"]";
            case "alchemy" -> formatter.format(Math.round(object.getAlchemy())) + "  [" + PlayerObject.skillXpToLevel(object.getAlchemy()) +"]";
            case "taming" -> formatter.format(Math.round(object.getTaming())) + "  [" + PlayerObject.skillXpToLevel(object.getTaming()) +"]";

            case "zombie" -> formatter.format(Math.round(object.getZombie())) + "  [" + PlayerObject.slayerXpToLevel(object.getZombie()) +"]";
            case "spider" -> formatter.format(Math.round(object.getSpider())) + "  [" + PlayerObject.slayerXpToLevel(object.getSpider()) +"]";
            case "wolf" -> formatter.format(Math.round(object.getWolf())) + "  [" + PlayerObject.slayerXpToLevel(object.getWolf()) +"]";

            default -> object.getSkillAverage();
        };
    }

    public static MessageEmbed generateEmbedFromTop(String title, String description, int color, String key, User user, List<PlayerObject> tops, int page, int size) {
        StringBuilder value = new StringBuilder("```yml");
        for (int i = 0; i < tops.size(); i++) {
            value.append("\n[").append(((page - 1) * size) + i+1).append("] ").append(tops.get(i).getName()).append(":\n\t>> ").append(getKey(tops.get(i), key));
        }
        value.append("```");

        String str = key.toLowerCase() + " Rangliste:";
        String fieldName = str.substring(0, 1).toUpperCase() + str.substring(1);

        String nick = MaddoxBot.getJda().getGuildById(531863332156866583L).getMember(user).getNickname();

        return new MessageEmbed(
                null,
                title,
                description,
                EmbedType.UNKNOWN,
                OffsetDateTime.now(),
                color,
                null,
                null,
                null,
                null,
                new MessageEmbed.Footer("Abgefragt von " + (nick == null ? user.getName() : nick), user.getAvatarUrl(), null), // Author Info or Bot info
                null,
                List.of(new MessageEmbed.Field(fieldName, value.toString(), false))
        );
    }

    public static MessageEmbed leaderboardEmbed(String title, String description, int color) {
        StringBuilder value = new StringBuilder("```yml");
        List<PlayerObject> averages = MaddoxBot.getDbMngr().getTopMembers(1, 10, Comparators.average);
        for (int i = 0; i < averages.size(); i++) {
            value.append("\n[").append(i+1).append("] ").append(averages.get(i).getName()).append(":\n\t>> ").append(getKey(averages.get(i), "average"));
        }
        value.append("```");

        StringBuilder value2 = new StringBuilder("```yml");
        List<PlayerObject> slayers = MaddoxBot.getDbMngr().getTopMembers(1, 10, Comparators.slayer);
        for (int i = 0; i < slayers.size(); i++) {
            value2.append("\n[").append(i+1).append("] ").append(slayers.get(i).getName()).append(":\n\t>> ").append(getKey(slayers.get(i), "slayer"));
        }
        value2.append("```");

        StringBuilder value3 = new StringBuilder("```yml");
        List<PlayerObject> allMembers = MaddoxBot.getDbMngr().getAllGuildMembers();
        double average = allMembers.stream().map(PlayerObject::getSkillAverage).mapToDouble(Double::parseDouble).average().getAsDouble();
        long totalSkillXP = (long) allMembers.stream().map(PlayerObject::getTotalSkillXP).mapToLong(s -> Long.parseLong(s.replace(",", ""))).average().getAsDouble();
        long totalSlayerXP = (long) allMembers.stream().map(PlayerObject::getTotalSlayerXP).mapToLong(s -> Long.parseLong(s.replace(",", ""))).average().getAsDouble();
        value3.append(makeLength("\nAverage:", 16)).append(Math.round(average * 100) / 100.0);
        value3.append(makeLength("\nSkill-EXP:", 16)).append(formatter.format(totalSkillXP));
        value3.append(makeLength("\nSlayer-EXP:", 16)).append(formatter.format(totalSlayerXP));
        value3.append("```");

        return new MessageEmbed(
                null,
                title,
                description,
                EmbedType.UNKNOWN,
                OffsetDateTime.now(),
                color,
                null,
                null,
                null,
                null,
                new MessageEmbed.Footer("Abgefragt von " + MaddoxBot.getJda().getSelfUser().getName(), MaddoxBot.getJda().getSelfUser().getAvatarUrl(), null), // Author Info or Bot info
                null,
                List.of(
                        new MessageEmbed.Field("Gilden Stats:", value3.toString(), false),
                        new MessageEmbed.Field("Average Rangliste:", value.toString(), true), new MessageEmbed.Field("Slayer Rangliste:", value2.toString(), true)
                )
        );
    }

    private static String makeLength(String input, int len) {
        if (input.length() != len) {
            int dif = len - input.length();
            StringBuilder inputBuilder = new StringBuilder(input);
            for (int i = 0; i < dif; i++) {
                inputBuilder.append(" ");
            }
            input = inputBuilder.toString();
        }
        return input;
    }

}
