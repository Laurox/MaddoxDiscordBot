package de.laurox.dc.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.CommandParent;
import de.laurox.dc.util.PlayerObject;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class StatsCommand extends CommandParent {

    private static final List<String> blocked = List.of("technoblade", "cookienorookie", "igobylotsofnames", "sirfischbrot", "akinsoft", "agromc", "timedeo", "menacingbanana", "iceblades11", "hellcastle", "furryeboy", "trymacs_live", "toadstar0", "thirtyvirus", "chook100", "zachplaysan", "pigical", "im_a_squid_kid", "mekzz", "tommyinnit", "speedsilver", "refraction", "tweitschgum", "palikka", "multidissimo", "sweetbootiee", "mashclash", "ducttapedigger", "gamerstime", "castcrafter", "sparkofphoenix", "nullzee", "debitorlp", "zaplol", "benmascott", "masskill", "binner", "xzap", "furball", "rumathra", "camboqt", "clym", "fizfez", "iconz", "tubbo_", "presnt", "p0wer0wner");
    private static final List<String> quotes = List.of("Hast du dir die Regeln nicht durchgelesen?", "Selbst du hast mehr SkillXP als der!", "Bitte bestätigen sie die Anfrage mit *Ja* im Chat.", "Selbst deine Oma kennt die Regeln!", " Die Würde des Menschen ist unantastbar. Sie zu achten und zu schützen ist Verpflichtung aller staatlichen Gewalt. Aber nicht bei ihm. :regional_indicator_f:");

    private boolean block = false;

    private String uuid;
    private String name;

    public StatsCommand(String commandName, int minArgs) {
        super(commandName, minArgs);
    }

    @Override
    protected void processCommand(MessageReceivedEvent eventInstance) {
        PlayerObject player;
        if(super.getArgs().length == 0) {
            String uuid = (String) MaddoxBot.getDbMngr().getLinkedPlayer(eventInstance.getAuthor().getId()).get("uuid");
            player = new PlayerObject(MaddoxBot.getDbMngr().getPlayerStats(uuid));
        } else {
            player = new PlayerObject(name, uuid);
        }

        if(player.hasProfile()) {
            eventInstance.getChannel().sendMessage(player.generateEmbed()).queue();
        } else {
            eventInstance.getChannel().sendMessage("Dieser Spieler hat kein gültiges Skyblock Profil! Wenn du denkst dass dies ein Fehler ist kontaktiere bitte den Botadministrator.").queue();
        }
    }

    @Override
    protected boolean validateArgs(String[] args, MessageReceivedEvent eventInstance) {
        super.setArgs(args);

        if(args.length < getMinArgs())
            return false;


        if(args.length == 0) {
            if(MaddoxBot.getDbMngr().isVerified(eventInstance.getAuthor().getId())) {
                return true;
            } else {
                return false;
            }
        } else {
            if(blocked.contains(args[0].toLowerCase())) {
                this.block = true;
                return false;
            }
            return getUUIDfromIGN(args[0]) != null;
        }
    }

    @Override
    protected void invalidMessage(MessageReceivedEvent eventInstance) {
        if(block) {
            Random random = new Random();
            String quote = quotes.get(random.nextInt(quotes.size()));
            eventInstance.getChannel().sendMessage(quote).queue();
            block = false;
            return;
        }

        if(super.getArgs().length == 0) {
            eventInstance.getChannel().sendMessage("Du bist noch nicht verifiziert, bitte erledige dies mit `!verify <name>`. Danach kannst du deine Stats mit `!stats` abfragen. Sollte weiterhin ein Fehlerauftreten kontaktiere bitte den Botadministrator.").queue();
        } else {
            eventInstance.getChannel().sendMessage("Es gab ein Problem bei deiner Abfrage! Überprüfe den Spielernamen und probiere es erneut. Sollte weiterhin ein Fehlerauftreten kontaktiere bitte den Botadministrator.").queue();
        }

    }

    public String getUUIDfromIGN(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != 200) {
                MaddoxBot.getLogger().logString("⚠️ | Error getting uuid of" + uuid + " (Stats Command): RQ - " + connection.getResponseCode());
                return null;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            is.close();
            reader.close();

            String result = response.toString();

            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

            this.name = jsonObject.get("name").getAsString();
            this.uuid = jsonObject.get("id").getAsString();

            return jsonObject.get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
