package com.bbn.hadder.commands.nsfw;

/*
 * @author Skidder / GregTCLTK
 */

import com.bbn.hadder.commands.Command;
import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.utils.MessageEditor;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Instant;

public class PussyCommand implements Command {

    @Override
    public void executed(String[] args, CommandEvent event) {
        if (event.getTextChannel().isNSFW()) {

            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("https://nekos.life/api/v2/img/pussy").build();

            try {

                Response response = caller.newCall(request).execute();
                String url = response.body().string().replace("{\"url\":\"", "");

                EmbedBuilder builder = new EmbedBuilder();
                event.getTextChannel().sendMessage(new MessageEditor().setDefaultSettings(MessageEditor.MessageType.INFO, builder)
                        .setAuthor("GIF not showing? Click here", url.replace("\"}", ""))
                        .setImage(url.replace("\"}", ""))
                        .setTimestamp(Instant.now())
                        .setFooter("Pussy")
                        .build()).queue();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            EmbedBuilder builder = new EmbedBuilder();
            event.getTextChannel().sendMessage(new MessageEditor().setDefaultSettings(MessageEditor.MessageType.WARNING, builder).setTitle("No NSFW").setDescription("You can only execute this command in NSFW channels!").build()).queue();
        }
    }

    @Override
    public String[] labels() {
        return new String[]{"pussy"};
    }

    @Override
    public String description() {
        return "Shows a random pussy gif.";
    }

    @Override
    public String usage() {
        return "";
    }
}