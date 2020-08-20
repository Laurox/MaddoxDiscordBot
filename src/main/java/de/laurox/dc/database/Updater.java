package de.laurox.dc.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.PlayerObject;
import de.laurox.dc.util.WebRequest;
import de.laurox.dc.verify.Achievement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final DBMngr dbMngr;

    public Updater() {
        this.dbMngr = new DBMngr();
    }

    private final Runnable updateFunc = new Runnable() {
        @Override
        public void run() {
            WebRequest request = new WebRequest("https://api.slothpixel.me/api/guilds/Blackrecruit?key=2de53a86-31a1-42d5-9c95-7a29a818750b");

            while (request.getResponseCode() == 0) {
            }

            if (!request.wasOkay())
                throw new RuntimeException("Fix me! -> Updater");

            Set<String> toUpdate = new HashSet<>();
            request.getData().get("members").getAsJsonArray().forEach(user -> {
                String uuid = user.getAsJsonObject().get("uuid").getAsString();
                toUpdate.add(uuid);
            });

            toUpdate.addAll(dbMngr.getAllGuildMembersLoaded());

            // System.out.println("Task: Update " + toUpdate.size() + " Members at " + LocalDateTime.now());
            MaddoxBot.getLogger().logString("♻️ | Start Updating " + toUpdate.size() + " Members at " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));

            toUpdate.forEach(uuid -> {
                String name = MaddoxBot.getDbMngr().getNameFromDB(uuid);

                if (name == null) {
                    name = getNameFromMonjang(uuid);
                }

                // System.out.println("Updating: " + name + " (" + uuid + ")");

                PlayerObject playerObject = new PlayerObject(name, uuid);

                if(MaddoxBot.getDbMngr().isVerifiedUUID(uuid)) {
                    Achievement achievement = new Achievement(playerObject, MaddoxBot.getJda().getUserById(MaddoxBot.getDbMngr().getLinkedPlayerUUID(uuid).get("discordID").toString()));
                    achievement.check();
                }

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            // System.out.println("Updating done at " + LocalDateTime.now());
            MaddoxBot.getLogger().logString("♻️ | Updating Done at " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        }
    };

    public void startScheduler() {
        System.out.println("Starting Update-Scheduler");
        scheduler.scheduleWithFixedDelay(updateFunc, 0, 10, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }

    private static String getNameFromMonjang(String uuid) {
        try {
            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != 200) {
                MaddoxBot.getLogger().logString("⚠️ | Error getting name of" + uuid + "(Updater): RQ - " + connection.getResponseCode());
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

            JsonArray names = new JsonParser().parse(result).getAsJsonArray();

            return names.get(names.size() - 1).getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
