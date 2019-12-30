package com.bbn.hadder.commands.moderation;

/*
 * @author Skidder / GregTCLTK
 */

import com.bbn.hadder.commands.Command;
import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.core.Perm;
import com.bbn.hadder.core.Perms;
import com.bbn.hadder.utils.MessageEditor;
import net.dv8tion.jda.api.entities.Member;

@Perms(Perm.BAN_MEMBERS)
public class BanCommand implements Command {

    @Override
    public void executed(String[] args, CommandEvent event) {
        if (event.getMessage().getMentionedMembers().size() == 1) {
            Member victim = event.getMessage().getMentionedMembers().get(0);
            if (!event.getAuthor().getId().equals(victim.getId())) {
                if (!event.getJDA().getSelfUser().getId().equals(victim.getId())) {
                    if (event.getGuild().getSelfMember().canInteract(victim)) {
                        event.getGuild().ban(victim, 0, "Banned by " + event.getAuthor().getAsTag()).queue();
                        event.getTextChannel().sendMessage(
                                event.getMessageEditor().getMessage(
                                        MessageEditor.MessageType.INFO,
                                        "commands.moderation.ban.success.title",
                                        "",
                                        "commands.moderation.ban.success.description",
                                        victim.getUser().getName() + ".").build()).queue();
                    } else {
                        event.getTextChannel().sendMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.NO_SELF_PERMISSION).build()).queue();
                    }
                } else {
                    event.getTextChannel().sendMessage(
                            event.getMessageEditor().getMessage(
                                    MessageEditor.MessageType.WARNING,
                                    "commands.moderation.ban.error.title",
                                    "commands.moderation.ban.myself.error.description").build()).queue();
                }
            } else {
                event.getTextChannel().sendMessage
                        (event.getMessageEditor().getMessage(
                                MessageEditor.MessageType.WARNING,
                                "commands.moderation.ban.error.title",
                                "commands.moderation.ban.yourself.error.description").build()).queue();
            }
        } else if (event.getMessage().getMentionedMembers().size() == 0) {
            event.getHelpCommand().sendHelp(this, event);
        } else if (event.getMessage().getMentionedMembers().size() > 1) {
            for (int i = 0; i < event.getMessage().getMentionedMembers().size(); i++) {
                Member member = event.getMessage().getMentionedMembers().get(i);
                if (!event.getAuthor().getId().equals(member.getId())) {
                    if (!event.getJDA().getSelfUser().getId().equals(member.getId())) {
                        if (event.getGuild().getSelfMember().canInteract(member)) {
                            event.getGuild().ban(member, 0).reason("Mass Ban by " + event.getAuthor().getAsTag()).queue();
                        } else {
                            event.getTextChannel().sendMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.NO_SELF_PERMISSION).build()).queue();
                        }
                    } else {
                        event.getTextChannel().sendMessage(
                                event.getMessageEditor().getMessage(
                                        MessageEditor.MessageType.WARNING,
                                        "commands.moderation.ban.error.title",
                                        "commands.moderation.ban.myself.error.description").build()).queue();
                    }
                } else {
                    event.getTextChannel().sendMessage(event.getMessageEditor().getMessage(
                            MessageEditor.MessageType.WARNING,
                            "commands.moderation.ban.error.title",
                            "commands.moderation.ban.yourself.error.description").build()).queue();
                }
            }
            event.getTextChannel().sendMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.INFO,
                    "commands.moderation.ban.success.title",
                    "",
                    "commands.moderation.ban.massban.success.description",
                    String.valueOf(event.getMessage().getMentionedMembers().size())).build()).queue();
        }
    }

    @Override
    public String[] labels() {
        return new String[]{"ban"};
    }

    @Override
    public String description() {
        return "commands.moderation.ban.help.description";
    }

    @Override
    public String usage() {
        return "[User(s)]";
    }

    @Override
    public String example() {
        return "@Skidder";
    }
}
