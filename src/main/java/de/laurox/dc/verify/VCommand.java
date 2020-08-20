package de.laurox.dc.verify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.CommandParent;
import de.laurox.dc.util.PlayerObject;
import de.laurox.dc.util.WebRequest;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VCommand extends CommandParent {

    private static final String API_KEY = "2de53a86-31a1-42d5-9c95-7a29a818750b";

    private String uuid;

    public VCommand(String commandName, int minArgs) {
        super(commandName, minArgs);
    }

    @Override
    protected void processCommand(MessageReceivedEvent eventInstance) {
        WebRequest request = new WebRequest("https://api.hypixel.net/player?key=" + API_KEY + "&uuid=" + uuid);

        while (request.getResponseCode() == 0) { }

        if(request.getResponseCode() != 200) {
            super.invalidMessage(eventInstance);
        }

        JsonObject object = request.getData();

        if (object.has("player")) {
            object = object.get("player").getAsJsonObject();
            if (object.has("socialMedia")) {
                object = object.get("socialMedia").getAsJsonObject();
                if (object.has("links")) {
                    object = object.get("links").getAsJsonObject();
                    if (object.has("DISCORD")) {
                        String discordTag = object.get("DISCORD").getAsString();

                        if(discordTag.equals(eventInstance.getAuthor().getAsTag())) {
                            MaddoxBot.getDbMngr().link(eventInstance.getAuthor().getId(), uuid, super.getArgs()[0]);
                            PlayerObject playerObject = new PlayerObject(super.getArgs()[0], uuid);
                            MaddoxBot.getDbMngr().insertMember(playerObject);
                            Achievement achievement = new Achievement(playerObject, eventInstance.getAuthor());
                            achievement.check();

                            eventInstance.getChannel().sendMessage("Du hast dich erfolgreich verifiziert!\nDeine erhaltenen Rollen habe ich dir privat gesendet.\nAußerdem kannst du deine Stats nun direkt mit `!stats` abrufen.").queue();
                            return;
                        }

                    }
                }
            }
        }

        eventInstance.getChannel().sendMessage("Dein Discord-Tag auf Hypixel stimmt nicht mit deinem aktuellen überein, aktualisere oder setzte diesen bitte bevor du dich erneut versuchts zu verifizieren!\nWenn du dabei Hilfe brauchst, habe ich hier ein Tutorial für dich:\nhttps://youtu.be/8tP4kAfOOfY").queue();
    }

    @Override
    protected boolean validateArgs(String[] args, MessageReceivedEvent eventInstance) {
        super.setArgs(args);
        if(args.length != getMinArgs()) {
            return false;
        }
        String uuid = getUUIDfromIGN(args[0]);
        this.uuid = uuid;
        return uuid != null;
    }

    @Override
    protected void invalidMessage(MessageReceivedEvent eventInstance) {
        eventInstance.getChannel().sendMessage("Selbst verifizieren klappt bei deinen Skill EXP ned.").queue();
    }

    public String getUUIDfromIGN(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != 200) {
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
            return jsonObject.get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
