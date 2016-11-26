package com.arsenarsen.userbot.command;

import com.arsenarsen.userbot.UserBot;
import com.arsenarsen.userbot.command.commands.Flippin;
import com.arsenarsen.userbot.command.commands.JavaREPL;
import com.arsenarsen.userbot.command.commands.Quote;
import com.arsenarsen.userbot.command.commands.Update;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ayy commands
 * <br>
 * Created by Arsen on 21.9.16..
 */
public class CommandDispatcher extends ListenerAdapter {

    private Map<String, Command> commands = new HashMap<>();

    public CommandDispatcher(){
        registerCommand(new JavaREPL());
        registerCommand(new Flippin());
        registerCommand(new Quote());
        registerCommand(new Update());
    }

    public boolean registerCommand(Command command){
        if(command.getName().contains(" ")){
            throw new IllegalArgumentException("Name must not have spaces!");
        }
        if(commands.containsKey(command.getName().toLowerCase())){
            return false;
        }
        commands.put(command.getName().toLowerCase(), command);
        return true;
    }

    public List<String> getCommands(){
        return commands.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();
        String content = msg.getRawContent().trim();
        String prefix = UserBot.getInstance().getConfig().getProperty("prefix");
        while(content.contains("  ")){
            content = content.replaceAll("\\s{2}", " ");
        }
        if(author.getId().equals(UserBot.getInstance().getConfig().getProperty("id"))
                && content.toLowerCase().startsWith(prefix.toLowerCase())){
            for(Command c : commands.values()){
                if(content.toLowerCase().startsWith(prefix.toLowerCase() + c.getName() + ' ')
                        || content.equalsIgnoreCase(prefix + c.getName())){
                    String[] split = split(content, c, prefix);
                    UserBot.LOGGER.info("Dispatching command '" + c.getName().toLowerCase() + "' with split: " + Arrays.toString(split));
                    c.dispatch(split, channel, msg);
                    break;
                }
            }
        }
    }

    private String[] split(String content, Command c, String prefix) {
        content = content.substring(c.getName().length() + prefix.length());
        if(content.startsWith(" ")){
            content = content.substring(1);
        }
        if(content.length() == 0){
            return new String[0];
        }
        return content.split("\\s");
    }

}
