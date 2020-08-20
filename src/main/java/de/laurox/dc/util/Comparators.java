package de.laurox.dc.util;

import java.util.Comparator;

public final class Comparators {

    public static Comparator<PlayerObject> chooseComparator(String s) {
        Comparator<PlayerObject> comparator = switch(s) {
            case "skills" -> skills;
            case "slayer" -> slayer;
            case "average" -> average;

            case "farming" -> farming;
            case "mining" -> mining;
            case "combat" -> combat;
            case "foraging" -> foraging;
            case "fishing" -> fishing;
            case "enchanting" -> enchanting;
            case "alchemy" -> alchemy;
            case "taming" -> taming;

            case "zombie" -> zombie;
            case "spider" -> spider;
            case "wolf" -> wolf;

            default -> average;
        };

        return comparator;
    }

    public static Comparator<PlayerObject> skills = (o1, o2) -> -1 * Integer.compare(Integer.parseInt(o1.getTotalSkillXP().replace(",", "")), Integer.parseInt(o2.getTotalSkillXP().replace(",", "")));

    public static Comparator<PlayerObject> slayer = (o1, o2) -> -1 * Integer.compare(Integer.parseInt(o1.getTotalSlayerXP().replace(",", "")), Integer.parseInt(o2.getTotalSlayerXP().replace(",", "")));

    public static Comparator<PlayerObject> average = (o1, o2) -> -1 * Double.compare(Double.parseDouble(o1.getSkillAverage()), Double.parseDouble(o2.getSkillAverage()));

    public static Comparator<PlayerObject> farming = (o1, o2) -> -1 * Float.compare(o1.getFarming(), o2.getFarming());

    public static Comparator<PlayerObject> mining = (o1, o2) -> -1 * Float.compare(o1.getMining(), o2.getMining());

    public static Comparator<PlayerObject> combat = (o1, o2) -> -1 * Float.compare(o1.getCombat(), o2.getCombat());

    public static Comparator<PlayerObject> foraging = (o1, o2) -> -1 * Float.compare(o1.getForaging(), o2.getForaging());

    public static Comparator<PlayerObject> fishing = (o1, o2) -> -1 * Float.compare(o1.getFishing(), o2.getFishing());

    public static Comparator<PlayerObject> enchanting = (o1, o2) -> -1 * Float.compare(o1.getEnchanting(), o2.getEnchanting());

    public static Comparator<PlayerObject> alchemy = (o1, o2) -> -1 * Float.compare(o1.getAlchemy(), o2.getAlchemy());

    public static Comparator<PlayerObject> taming = (o1, o2) -> -1 * Float.compare(o1.getTaming(), o2.getTaming());

    public static Comparator<PlayerObject> zombie = (o1, o2) -> -1 * Integer.compare(o1.getZombie(), o2.getZombie());

    public static Comparator<PlayerObject> spider = (o1, o2) -> -1 * Integer.compare(o1.getSpider(), o2.getSpider());

    public static Comparator<PlayerObject> wolf = (o1, o2) -> -1 * Integer.compare(o1.getWolf(), o2.getWolf());


}
