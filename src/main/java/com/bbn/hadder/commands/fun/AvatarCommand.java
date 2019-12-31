package com.bbn.hadder.commands.fun;

/*
 * @author Skidder / GregTCLTK
 */

import com.bbn.hadder.commands.Command;
import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.utils.MessageEditor;
import net.dv8tion.jda.api.entities.Member;

public class AvatarCommand implements Command {

    @Override
    public void executed(String[] args, CommandEvent event) {
        if (args.length == 0) {
            Member member = event.getMember();
            event.getTextChannel().sendMessage(
                    event.getMessageEditor().getMessage(
                            MessageEditor.MessageType.INFO,
                            "commands.fun.avatar.success.title",
                            member.getUser().getAsTag(),
                            "",
                            "")
                    .setImage(member.getUser().getAvatarUrl())
                    .setFooter(member.getUser().getAsTag())
                    .build()).queue();
        } else if (event.getMessage().getMentionedMembers().size() == 1) {
            Member member = event.getMessage().getMentionedMembers().get(0);
            event.getTextChannel().sendMessage(
                    event.getMessageEditor().getMessage(
                            MessageEditor.MessageType.INFO,
                            "commands.fun.avatar.success.title",
                            member.getUser().getAsTag(),
                            "",
                            "")
                    .setImage(member.getUser().getAvatarUrl())
                    .setFooter(member.getUser().getAsTag())
                    .build()).queue();
        } else if (args[0].length() == 18){
            Member member = event.getGuild().getMemberById(args[0]);
            event.getTextChannel().sendMessage(
                    event.getMessageEditor().getMessage(
                            MessageEditor.MessageType.INFO,
                            "commands.fun.avatar.success.title",
                            member.getUser().getAsTag(),
                            "",
                            "")
                    .setImage(member.getUser().getAvatarUrl())
                    .setFooter(member.getUser().getAsTag())
                    .build()).queue();
        } else {
            event.getHelpCommand().sendHelp(this, event);
        }
    }

    @Override
    public String[] labels() {
        return new String[]{"avatar"};
    }

    @Override
    public String description() {
        return "commands.fun.avatar.help.description";
    }

    @Override
    public String usage() {
        return "[User]";
    }

    @Override
    public String example() {
        return "@Skidder";
    }
}
