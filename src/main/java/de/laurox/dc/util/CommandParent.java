package de.laurox.dc.util;

import de.laurox.dc.MaddoxBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CommandParent {

    private final String prefix;
    private final String commandName;
    private final int minArgs;

    private String description = "";

    private String[] args;

    public CommandParent(String commandName, int minArgs) {
        this.commandName = commandName;
        this.minArgs = minArgs;

        this.prefix = MaddoxBot.getPrefix(); // getPrefix from MainInstance
    }

    // has to be implemented
    protected abstract void processCommand(MessageReceivedEvent eventInstance);

    protected abstract boolean validateArgs(String[] args, MessageReceivedEvent eventInstance);

    // can be overwritten
    protected void invalidMessage(MessageReceivedEvent eventInstance) {

    }

    public String getPrefix() {
        return prefix;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasDescription() {
        return !description.equals("");
    }


}
