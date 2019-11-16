package com.bbn.hadder.commands.misc;

/*
 * @author Skidder / GregTCLTK
 */

import com.bbn.hadder.Hadder;
import com.bbn.hadder.commands.Command;
import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.utils.MessageEditor;
import net.dv8tion.jda.api.EmbedBuilder;

public class InviteCommand implements Command {

    @Override
    public void executed(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        event.getTextChannel().sendMessage(new MessageEditor().setDefaultSettings(MessageEditor.Messagetype.INFO, builder).setTitle("Invite me!").setDescription("[Invite me here!](discordapp.com/oauth2/authorize?client_id="  + Hadder.shardManager.getGuilds().get(0).getSelfMember().getId() + "&scope=bot&permissions=470133879)").build()).queue();
    }

    @Override
    public String[] labels() {
        return new String[]{"invite"};
    }

    @Override
    public String description() {
        return "Hadder Bot Invite";
    }

    @Override
    public String usage() {
        return "";
    }
}