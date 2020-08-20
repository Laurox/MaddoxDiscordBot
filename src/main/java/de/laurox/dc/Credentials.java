package de.laurox.dc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Credentials {

    public void loadCredentials() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("credentials.json");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);

        try {

            StringBuilder jsonStringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                jsonStringBuilder.append(line);
                line = reader.readLine();
            }
            JsonObject jsonObject = new JsonParser().parse(jsonStringBuilder.toString()).getAsJsonObject();
            MaddoxBot.setTOKEN(jsonObject.get("DISCORD_TOKEN").getAsString());

            reader.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
