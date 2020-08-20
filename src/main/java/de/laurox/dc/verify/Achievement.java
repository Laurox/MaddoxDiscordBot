package de.laurox.dc.verify;

import de.laurox.dc.MaddoxBot;
import de.laurox.dc.util.PlayerObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Achievement {

    private final PlayerObject player;
    private final User discordUser;

    private static Map<String, Long> roleIDs;
    static {
        Map<String, Long> roleIDs = new HashMap<>();
        roleIDs.put("farming", 729001392307437628L);
        roleIDs.put("mining", 729004521996681335L);
        roleIDs.put("combat", 729003865134858321L);
        roleIDs.put("foraging", 729001338632929451L);
        roleIDs.put("fishing", 729004343474520164L);
        roleIDs.put("enchanting", 729003717130584075L);
        roleIDs.put("alchemy", 729000938400120832L);
        roleIDs.put("taming", 729003940150247424L);

        roleIDs.put("30", 736967883879612526L);
        roleIDs.put("35", 736967885989347370L);
        roleIDs.put("40", 736967888593747968L);
        roleIDs.put("45", 736967890590498918L);

        roleIDs.put("777", 736968133176197231L);
        roleIDs.put("888", 736968128889880667L);
        roleIDs.put("999", 736968133176197231L);
        setRoleIDs(roleIDs);
    }

    public Achievement(PlayerObject playerObject, User discordUser) {
        this.player = playerObject;
        this.discordUser = discordUser;
    }

    public static void setRoleIDs(Map<String, Long> roleIDs) {
        Achievement.roleIDs = roleIDs;
    }

    public void check() {
        List<Long> toGrant = new LinkedList<>();
        List<Long> toRemove = new LinkedList<>();

        if(isFarming50()) {
            toGrant.add(roleIDs.get("farming"));
        }

        if(isMining50()) {
            toGrant.add(roleIDs.get("mining"));
        }

        if(isCombat50()) {
            toGrant.add(roleIDs.get("combat"));
        }

        if(isForaging50()) {
            toGrant.add(roleIDs.get("foraging"));
        }

        if(isFishing50()) {
            toGrant.add(roleIDs.get("fishing"));
        }

        if(isEnchanting50()) {
            toGrant.add(roleIDs.get("enchanting"));
        }

        if(isAlchemy50()) {
            toGrant.add(roleIDs.get("alchemy"));
        }

        if(isTaming50()) {
            toGrant.add(roleIDs.get("taming"));
        }

        switch (getAverageProgress()) {
            case 1:
                toGrant.add(roleIDs.get("30"));
                break;
            case 2:
                toRemove.add(roleIDs.get("30"));
                toGrant.add(roleIDs.get("35"));
                break;
            case 3:
                toRemove.add(roleIDs.get("35"));
                toGrant.add(roleIDs.get("40"));
                break;
            case 4:
                toRemove.add(roleIDs.get("40"));
                toGrant.add(roleIDs.get("45"));
                break;
            default:
                break;
        }

        switch (getSlayerProgress()) {
            case 1:
                toGrant.add(roleIDs.get("777"));
                break;
            case 2:
                toRemove.add(roleIDs.get("777"));
                toGrant.add(roleIDs.get("888"));
                break;
            case 3:
                toRemove.add(roleIDs.get("888"));
                toGrant.add(roleIDs.get("999"));
                break;
            default:
                break;

        }

        Guild mtg = MaddoxBot.getJda().getGuildById(531863332156866583L);
        List<Role> roles = toGrant.stream().map(mtg::getRoleById).collect(Collectors.toList());
        if(roles.size() > 0)
            roles.add(mtg.getRoleById(733074712627904552L));

        List<Role> roles1 = toRemove.stream().map(mtg::getRoleById).collect(Collectors.toList());
        List<Role> userRoles = mtg.getMember(discordUser).getRoles();

        roles.removeAll(userRoles);
        roles1.removeIf(role -> !userRoles.contains(role));


        roles.forEach(role -> {
            mtg.addRoleToMember(discordUser.getIdLong(), role).queue();
        });

        roles1.forEach(role -> {
            mtg.removeRoleFromMember(discordUser.getIdLong(), role).queue();
        });

        if(!roles.isEmpty()) {
            MaddoxBot.getJda().getUserById(discordUser.getIdLong()).openPrivateChannel().complete().sendMessage("Ich habe dir folgende Rollen gegeben: " + roles.stream().filter(id -> id.getIdLong() != 733074712627904552L).map(Role::getName).collect(Collectors.joining(" | "))).queue();
        }


    }

    public boolean isFarming50() {
        return (int) PlayerObject.skillXpToLevel(player.getFarming()) == 50;
    }

    public boolean isMining50() {
        return (int) PlayerObject.skillXpToLevel(player.getMining()) == 50;
    }

    public boolean isCombat50() {
        return (int) PlayerObject.skillXpToLevel(player.getCombat()) == 50;
    }

    public boolean isForaging50() {
        return (int) PlayerObject.skillXpToLevel(player.getForaging()) == 50;
    }

    public boolean isFishing50() {
        return (int) PlayerObject.skillXpToLevel(player.getFishing()) == 50;
    }

    public boolean isEnchanting50() {
        return (int) PlayerObject.skillXpToLevel(player.getEnchanting()) == 50;
    }

    public boolean isAlchemy50() {
        return (int) PlayerObject.skillXpToLevel(player.getAlchemy()) == 50;
    }

    public boolean isTaming50() {
        return (int) PlayerObject.skillXpToLevel(player.getTaming()) == 50;
    }

    /*
     *  -1 | API Disabled
     *   0 | No Rank
     *   1 | 30+
     *   2 | 35+
     *   3 | 40+
     *   4 | 45+
     */
    public int getAverageProgress() {
        String average = player.getSkillAverage();
        if(!average.equals("API Deaktiviert")) {
            int avg = (int) Math.floor(Double.parseDouble(average));
            if(avg >= 45) {
                return 4;
            } else if(avg >= 40) {
                return 3;
            } else if(avg >= 35) {
                return 2;
            } else if(avg >= 30) {
                return 1;
            }
            return 0;
        } else {
            return -1;
        }
    }

    /*
     *  0 | No Rank
     *  1 | 7/7/7
     *  2 | 8/8/8
     *  3 | 9/9/9
     */
    public int getSlayerProgress() {
        int zombie = (int) PlayerObject.slayerXpToLevel(player.getZombie());
        int spider = (int) PlayerObject.slayerXpToLevel(player.getSpider());
        int wolf = (int) PlayerObject.slayerXpToLevel(player.getWolf());

        if(zombie >= 9 && spider >= 9 && wolf >= 9) {
            return 3;
        } else if(zombie >= 8 && spider >= 8 && wolf >= 8) {
            return 2;
        }else if(zombie >= 7 && spider >= 7 && wolf >= 7) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(roleIDs.toString());
    }
}
