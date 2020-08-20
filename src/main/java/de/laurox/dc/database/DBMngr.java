package de.laurox.dc.database;

import com.mongodb.*;
import de.laurox.dc.util.Comparators;
import de.laurox.dc.util.PlayerObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DBMngr {

    private MongoClient mongoClient;
    private DB database;
    private DBCollection members, guilds, links;

    public DBMngr() {
        this.mongoClient = new MongoClient();

        this.database = mongoClient.getDB("sbstats");
        this.members = database.getCollection("members");
        this.guilds = database.getCollection("guilds");
        this.links = database.getCollection("links");
    }

    public boolean exist(PlayerObject playerObject) {
        DBObject queryObject = new BasicDBObject("uuid", playerObject.getUuid());
        return 1 == members.find(queryObject).count();
    }

    public void insertMember(PlayerObject playerObject) {
        DBObject queryObject = new BasicDBObject("uuid", playerObject.getUuid());
        members.update(queryObject, playerObject.toDBObject(), true, false);
    }

    public List<DBObject> getGuild(String guildID) {
        DBObject queryObject = new BasicDBObject("guildID", guildID);
        DBCursor cursor = guilds.find(queryObject);
        Iterator<DBObject> dbObjectIterator = cursor.iterator();
        List<DBObject> result = new LinkedList<>();
        while (dbObjectIterator.hasNext()) {
            result.add(dbObjectIterator.next());
        }
        return result;
    }

    public void link(String discordID, String uuid, String name) {
        DBObject queryObject = new BasicDBObject("discordID", discordID);
        DBObject linkedObject = new BasicDBObject().append("discordID", discordID).append("uuid", uuid).append("name", name);
        links.update(queryObject, linkedObject, true, false);
    }

    public boolean isVerified(String discordID) {
        DBObject queryObject = new BasicDBObject("discordID", discordID);
        return 1 == links.find(queryObject).count();
    }

    public boolean isVerifiedUUID(String uuid) {
        DBObject queryObject = new BasicDBObject("uuid", uuid);
        return 1 == links.find(queryObject).count();
    }

    public DBObject getLinkedPlayer(String discordID) {
        DBObject queryObject = new BasicDBObject("discordID", discordID);
        return links.find(queryObject).one();
    }

    public DBObject getLinkedPlayerUUID(String uuid) {
        DBObject queryObject = new BasicDBObject("uuid", uuid);
        return links.find(queryObject).one();
    }

    public DBObject getPlayerStats(String uuid) {
        DBObject queryObject = new BasicDBObject("uuid", uuid);
        return members.find(queryObject).one();
    }

    public String getNameFromDB(String uuid) {
        DBObject queryObject = new BasicDBObject("uuid", uuid);
        if (members.find(queryObject).count() >= 1) {
            return members.find(queryObject).one().get("name").toString();
        }
        return null;
    }

    public List<String> getAllGuildMembersLoaded() {
        DBObject queryObject = new BasicDBObject("guild", "5d2e186677ce8415c3fd0074");
        Iterator<DBObject> dbObjectIterator = members.find(queryObject).iterator();

        List<String> result = new LinkedList<>();
        while (dbObjectIterator.hasNext()) {
            DBObject current = dbObjectIterator.next();
            result.add(current.get("uuid").toString());
        }
        return result;
    }

    public List<PlayerObject> getTopMembers(int page, int size, Comparator<PlayerObject> comparator) {
        DBObject queryObject = new BasicDBObject("guild", "5d2e186677ce8415c3fd0074");

        int skips = size * (page - 1);
        List<DBObject> result = members.find(queryObject).toArray();
        List<PlayerObject> playerObjects = result.stream().map(object -> new PlayerObject(object, true)).sorted(comparator).collect(Collectors.toList());
        int limit = Math.min(skips + size, playerObjects.size());
        List<PlayerObject> returnVal = new LinkedList<>();
        for (int i = skips; i < limit; i++) {
            returnVal.add(playerObjects.get(i));
        }
        return returnVal;
    }

    public List<PlayerObject> getAllGuildMembers() {
        DBObject queryObject = new BasicDBObject("guild", "5d2e186677ce8415c3fd0074");
        return members.find(queryObject).toArray().stream().map(object -> new PlayerObject(object, true)).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        /*
        PlayerObject playerObject = new PlayerObject("ibot", "7f72bf51fd5f47889d8dc2ee9a7c596c");
        PlayerObject playerObject2 = new PlayerObject("LauroxTV", "af6ead9af5234812be80b49a4c48573d");
        PlayerObject playerObject3 = new PlayerObject("PHIDRA1", "466e80053e6b4bb1a235a2f746fb731b");
        DBMngr dbMngr = new DBMngr();
        dbMngr.insertMember(playerObject);
        dbMngr.insertMember(playerObject2);
        dbMngr.insertMember(playerObject3);

         */
        // System.out.println(dbMngr.exist(playerObject));

        DBMngr dbMngr = new DBMngr();
        System.out.println(dbMngr.getTopMembers(1, 10, Comparators.chooseComparator("farming")).stream().map(PlayerObject::getName).collect(Collectors.joining(" ")));
    }

}
