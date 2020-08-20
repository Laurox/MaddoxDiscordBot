package de.laurox.dc.util;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import de.laurox.dc.MaddoxBot;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlayerObject {

    private static NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);

    private String name, uuid;
    private String guild, guildName;
    private int fairySouls;
    private int farming, mining, combat, foraging, fishing, enchanting, alchemy, taming;
    private int zombie, spider, wolf;

    private boolean hasProfile = false;
    private boolean skillAPI;

    private long lastUpdated;

    private static final long DELTA = 2 * 300000L; // x \times 5 Minutes
    private boolean top = false;

    private static final int[] skillXP = new int[]{
            50, 175, 375, 675, 1175,
            1925, 2925, 4425, 6425, 9925,
            14925, 22425, 32425, 47425, 67425,
            97425, 147425, 222425, 322425, 522425,
            822425, 1222425, 1722425, 2322425, 3022425,
            3822425, 4722425, 5722425, 6822425, 8022425,
            9322425, 10722425, 12222425, 13822425, 15522425,
            17322425, 19222425, 21222425, 23322425, 25522425,
            27822425, 30222425, 32722425, 35322425, 38022425,
            40822425, 43922425, 47322425, 51022425, 55022425
    };

    private static final int[] slayerXP = new int[]{
            5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000
    };

    public PlayerObject(DBObject dbObject) {
        if (dbObject == null)
            throw new RuntimeException("Fix me!");

        loadFromDatabase(dbObject);
    }

    public PlayerObject(DBObject dbObject, boolean top) {
        this.top = top;

        if (dbObject == null)
            throw new RuntimeException("Fix me!");

        loadFromDatabase(dbObject);
    }

    public PlayerObject(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
        if(MaddoxBot.getDbMngr().exist(this)) {
            loadFromDatabase(MaddoxBot.getDbMngr().getPlayerStats(uuid));
        } else {
            loadFromAPI(name, uuid);
        }

    }

    private void loadFromDatabase(DBObject dbObject) {
        this.name = dbObject.get("name").toString();
        this.uuid = dbObject.get("uuid").toString();

        if(System.currentTimeMillis() > (Long.parseLong(dbObject.get("lastupdated")+"") + DELTA) && !top) {
            loadFromAPI(name, uuid);
            MaddoxBot.getDbMngr().insertMember(this);
            return;
        }

        this.lastUpdated = Long.parseLong(dbObject.get("lastupdated")+"");
        this.guild = dbObject.get("guild") == null ? null : dbObject.get("guild").toString();
        this.guildName = dbObject.get("guildname") == null ? null : dbObject.get("guildname").toString();

        this.fairySouls = (int) dbObject.get("fairysouls");

        this.skillAPI = (boolean) dbObject.get("skillapi");
        if (this.skillAPI) {
            this.farming = ((Double) ((BasicDBList) dbObject.get("skills")).get(0)).intValue();
            this.mining = ((Double) ((BasicDBList) dbObject.get("skills")).get(1)).intValue();
            this.combat = ((Double) ((BasicDBList) dbObject.get("skills")).get(2)).intValue();
            this.foraging = ((Double) ((BasicDBList) dbObject.get("skills")).get(3)).intValue();
            this.fishing = ((Double) ((BasicDBList) dbObject.get("skills")).get(4)).intValue();
            this.enchanting = ((Double) ((BasicDBList) dbObject.get("skills")).get(5)).intValue();
            this.alchemy = ((Double) ((BasicDBList) dbObject.get("skills")).get(6)).intValue();
            this.taming = ((Double) ((BasicDBList) dbObject.get("skills")).get(7)).intValue();
        }


        this.zombie = (Integer) ((BasicDBList) dbObject.get("slayers")).get(0);
        this.spider = (Integer) ((BasicDBList) dbObject.get("slayers")).get(1);
        this.wolf = (Integer) ((BasicDBList) dbObject.get("slayers")).get(2);

        this.hasProfile = true;
    }

    private void loadFromAPI(String name, String uuid) {
        WebRequest request = new WebRequest("https://api.slothpixel.me/api/skyblock/profile/" + name + "?key=2de53a86-31a1-42d5-9c95-7a29a818750b");
        WebRequest playerRequest = new WebRequest("https://api.slothpixel.me/api/guilds/" + name + "?key=2de53a86-31a1-42d5-9c95-7a29a818750b");
        this.name = name;
        this.uuid = uuid;

        while (request.getResponseCode() == 0 && playerRequest.getResponseCode() == 0) {
        }

        if (!request.wasOkay() || !(playerRequest.wasOkay() || playerRequest.getResponseCode() == 404)) {
            MaddoxBot.getLogger().logString("⚠️ | Error at " + name + " (Update PlayerObject): RQ - " + request.getResponseCode() + " | PRQ - " + playerRequest.getResponseCode());
            return;
        }

        if (playerRequest.getResponseCode() == 404) {
            this.guild = null;
        } else {
            this.guild = playerRequest.getData().get("id").getAsString();
            this.guildName = playerRequest.getData().get("name").getAsString();
        }

        JsonObject object = request.getData().get("members").getAsJsonObject().get(uuid).getAsJsonObject();
        this.fairySouls = object.get("fairy_souls_collected").getAsInt();
        JsonObject skills = object.get("skills").getAsJsonObject();
        if (skills.has("farming") && skills.has("mining") && skills.has("combat")
                && skills.has("foraging") && skills.has("fishing") && skills.has("alchemy")
                && skills.has("taming")) {
            this.farming = (int) skills.get("farming").getAsJsonObject().get("xp").getAsFloat();
            this.mining = (int) skills.get("mining").getAsJsonObject().get("xp").getAsFloat();
            this.combat = (int) skills.get("combat").getAsJsonObject().get("xp").getAsFloat();
            this.foraging = (int) skills.get("foraging").getAsJsonObject().get("xp").getAsFloat();
            this.fishing = (int) skills.get("fishing").getAsJsonObject().get("xp").getAsFloat();
            this.enchanting = (int) skills.get("enchanting").getAsJsonObject().get("xp").getAsFloat();
            this.alchemy = (int) skills.get("alchemy").getAsJsonObject().get("xp").getAsFloat();
            this.taming = (int) skills.get("taming").getAsJsonObject().get("xp").getAsFloat();
            this.skillAPI = true;
        } else {
            this.skillAPI = false;
        }

        JsonObject slayers = object.get("slayer").getAsJsonObject();
        this.zombie = slayers.get("zombie").getAsJsonObject().get("xp").getAsInt();
        this.spider = slayers.get("spider").getAsJsonObject().get("xp").getAsInt();
        this.wolf = slayers.get("wolf").getAsJsonObject().get("xp").getAsInt();

        this.hasProfile = true;
        this.lastUpdated = System.currentTimeMillis();

        MaddoxBot.getDbMngr().insertMember(this);
    }

    public MessageEmbed generateEmbed() {
        return new MessageEmbed("https://sky.lea.moe/stats/" + name, "__" + name + "´s Stats:__", "Hier eine Übersicht über alle Stats des Spieler.\nKlicke auf den Titel um auf sein **SkyLea**-Profil zu kommen!\nZuletzt geupdated: " + millisToTime(lastUpdated), EmbedType.UNKNOWN, null, 0xA3FC4B, null, null, null, null, null, null,
                List.of(
                        new MessageEmbed.Field("Allgemein:", "```yml\nFairySouls:\t" + fairySouls + "/209\n" +
                                "Gilde:     \t" + (guild == null ? "Keine Gilde" : guildName) + "\n" +
                                "Average:   \t" + getSkillAverage() + "\n" +
                                "SkillXP:   \t" + getTotalSkillXP() + "\n" +
                                "SlayerXP:  \t" + getTotalSlayerXP() + "```", true),
                        new MessageEmbed.Field("Skills:", "```yml\n" +
                                (skillAPI ? (
                                        "Farming:   \t" + makeLength(skillXpToLevel(farming) + "", 5) + "\t" + formatter.format(farming) + "\n" +
                                                "Mining:    \t" + makeLength(skillXpToLevel(mining) + "", 5) + "\t" + formatter.format(mining) + "\n" +
                                                "Combat:    \t" + makeLength(skillXpToLevel(combat) + "", 5) + "\t" + formatter.format(combat) + "\n" +
                                                "Foraging:  \t" + makeLength(skillXpToLevel(foraging) + "", 5) + "\t" + formatter.format(foraging) + "\n" +
                                                "Fishing:   \t" + makeLength(skillXpToLevel(fishing) + "", 5) + "\t" + formatter.format(fishing) + "\n" +
                                                "Enchanting:\t" + makeLength(skillXpToLevel(enchanting) + "", 5) + "\t" + formatter.format(enchanting) + "\n" +
                                                "Alchemy:   \t" + makeLength(skillXpToLevel(alchemy) + "", 5) + "\t" + formatter.format(alchemy) + "\n" +
                                                "Taming:    \t" + makeLength(skillXpToLevel(taming) + "", 5) + "\t" + formatter.format(taming)) : "Skill API Deaktiviert!") + "```", false),
                        new MessageEmbed.Field("Slayers:", "```yml\n" +
                                "Zombie:    \t" + makeLength(slayerXpToLevel(zombie) + "", 5) + "\t" + formatter.format(zombie) + "\n" +
                                "Spider:    \t" + makeLength(slayerXpToLevel(spider) + "", 5) + "\t" + formatter.format(spider) + "\n" +
                                "Wolf:      \t" + makeLength(slayerXpToLevel(wolf) + "", 5) + "\t" + formatter.format(wolf) + "```", false)
                )
        );
    }

    private String millisToTime(long millis) {
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        Date currentDate = new Date(millis);
        return df.format(currentDate);
    }

    public String getTotalSkillXP() {
        if (skillAPI)
            return formatter.format(farming + mining + combat + foraging + fishing + enchanting + alchemy + taming);
        else
            return "API Deaktiviert";
    }

    public String getSkillAverage() {
        if (skillAPI)
            return "" + (double) Math.round((List.of(farming, mining, combat, foraging, fishing, enchanting, alchemy, taming).stream().mapToDouble(PlayerObject::skillXpToLevel).sum() / 8) * 100) / 100;
        else
            return "API Deaktiviert";
    }

    public String getTotalSlayerXP() {
        return formatter.format(zombie + spider + wolf);
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

    public static double slayerXpToLevel(int xp, boolean compact) {
        double pass = slayerXpToLevel(xp);
        if(compact) {
            return Math.round(pass * 10) / 10.0;
        } else {
            return pass;
        }
    }

    public static double slayerXpToLevel(int xp) {
        int left, needed;
        int level = 0;
        while (!(level > 8) && slayerXP[level] <= xp) {
            level++;

        }
        if (level >= 9) return level;
        if (level == 0) return 0;
        left = xp - slayerXP[level - 1];
        needed = slayerXP[level] - slayerXP[level - 1];
        return Math.round((level + (double) left / needed) * 100) / (double) 100;
    }

    public static double skillXpToLevel(float xp) {
        float left, needed;
        int level = 0;
        while (!(level > 49) && skillXP[level] <= xp) {
            level++;

        }
        if (level >= 50) return level;
        if (level == 0) return 0;
        left = xp - skillXP[level - 1];
        needed = skillXP[level] - skillXP[level - 1];
        return Math.round((level + (double) left / needed) * 100) / (double) 100;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public int getFairySouls() {
        return fairySouls;
    }

    public float getFarming() {
        return farming;
    }

    public float getMining() {
        return mining;
    }

    public float getCombat() {
        return combat;
    }

    public float getForaging() {
        return foraging;
    }

    public float getFishing() {
        return fishing;
    }

    public float getEnchanting() {
        return enchanting;
    }

    public float getAlchemy() {
        return alchemy;
    }

    public float getTaming() {
        return taming;
    }

    public int getZombie() {
        return zombie;
    }

    public int getSpider() {
        return spider;
    }

    public int getWolf() {
        return wolf;
    }

    public boolean hasProfile() {
        return hasProfile;
    }

    public DBObject toDBObject() {
        return new BasicDBObject()
                .append("uuid", uuid)
                .append("name", name)
                .append("guild", guild)
                .append("guildname", guildName)
                .append("lastupdated", lastUpdated)
                .append("fairysouls", fairySouls)
                .append("skillapi", skillAPI)
                .append("skills", new float[]{farming, mining, combat, foraging, fishing, enchanting, alchemy, taming})
                .append("slayers", new int[]{zombie, spider, wolf});
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerObject that = (PlayerObject) o;
        return Objects.equals(uuid, that.uuid);
    }

}
