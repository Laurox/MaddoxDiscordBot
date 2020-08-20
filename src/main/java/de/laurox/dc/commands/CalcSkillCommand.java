package de.laurox.dc.commands;

import de.laurox.dc.util.CommandParent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.NumberFormat;
import java.util.Locale;

public class CalcSkillCommand extends CommandParent {

    private static NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);

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

    public CalcSkillCommand(String commandName, int minArgs) {
        super(commandName, minArgs);

        super.setDescription("Zeigt dir die XP-Differenz zwischen zwei Skill-Level");
    }

    @Override
    protected void processCommand(MessageReceivedEvent eventInstance) {
        int from = Integer.parseInt(super.getArgs()[0]);
        int to = Integer.parseInt(super.getArgs()[1]);

        if(from > to) {
            int swap = from;
            from = to;
            to = swap;
        }

        int start = from == 0 ? 0 : skillXP[from-1];
        int end = to == 0 ? 0 : skillXP[to-1];

        int dif = end - start;

        eventInstance.getChannel().sendMessage("Der Unterschied von `" + from + "` zu `" + to + "` beträgt **" + formatter.format(dif) + " XP**.").queue();
    }

    @Override
    protected boolean validateArgs(String[] args, MessageReceivedEvent eventInstance) {
        super.setArgs(args);
        if(args.length == super.getMinArgs()) {
            try {
                int a = Integer.parseInt(args[0]);
                int b = Integer.parseInt(args[1]);

                return a <= 50 && b <= 50 && a >= 0 && b >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void invalidMessage(MessageReceivedEvent eventInstance) {
        eventInstance.getChannel().sendMessage("Ungültige Argumente: Nutze `!calcskill [from] [to]`. Die Werte müssen im Bereich von `[0, 50]` liegen").queue();
    }
}
