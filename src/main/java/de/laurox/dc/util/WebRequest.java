package de.laurox.dc.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebRequest {

    private int responseCode = 0;
    private JsonObject data;

    private String address;

    public WebRequest(String address) {
        this.address = address;

        performRequest();
    }

    private void performRequest() {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            this.responseCode = connection.getResponseCode();
            if(responseCode != 200)
                return;

            InputStream responseStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String result = response.toString();
            this.data = new JsonParser().parse(result).getAsJsonObject();
        } catch (IOException e) {
            this.responseCode = -1;
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public JsonObject getData() {
        if(data == null)
            throw new IllegalStateException("data is null");
        else
            return data;
    }

    public boolean wasOkay() {
        return responseCode == 200;
    }

}
