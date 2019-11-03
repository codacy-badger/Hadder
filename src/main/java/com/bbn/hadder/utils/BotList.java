package com.bbn.hadder.utils;

/*
 * @author Skidder / GregTCLTK
 */

import com.bbn.hadder.Hadder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BotList {

    private static String MythicalBotList = "https://mythicalbots.xyz/api/bot/637002314162372639";
    private static String BotsForDiscord = "https://botsfordiscord.com/api/bot/637002314162372639";
    private static String DiscordBotList = "https://discordbotlist.com/api/bots/637002314162372639/stats";
    private static String DiscordBestBots = "https://discordsbestbots.xyz/api/bots/637002314162372639/stats";

    private static JSONObject json = new JSONObject();

    public static void post() {
        json.put("server_count", Hadder.shardManager.getGuilds().size());
        json.put("guilds", Hadder.shardManager.getGuilds().size());
        json.put("users", Hadder.shardManager.getUsers().size());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        File configfile = new File("./config.json");

        JSONObject config = null;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get(configfile.toURI()))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mythical Bot List

        Request mythicalbotlist = new Request.Builder()
                .url(MythicalBotList)
                .post(body)
                .addHeader("Authorization", config.getString("MythicalBotList"))
                .build();

        try {
            new OkHttpClient().newCall(mythicalbotlist).execute().close();
            System.out.println("Successfully posted count for the Mythical Bot List!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // BotsForDiscord

        Request botsfordiscord = new Request.Builder()
                .url(BotsForDiscord)
                .post(body)
                .addHeader("Authorization", config.getString("BotsForDiscord"))
                .build();

        try {
            new OkHttpClient().newCall(botsfordiscord).execute().close();
            System.out.println("Successfully posted count to Bots For Discord!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Discord Bot List

        Request discordbotlist = new Request.Builder()
                .url(DiscordBotList)
                .post(body)
                .addHeader("Authorization", "Bot " + config.getString("DiscordBotList"))
                .build();

        try {
            new OkHttpClient().newCall(discordbotlist).execute().close();
            System.out.println("Successfully posted count for the Discord Bot List");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Discord Best Bots

        Request discordbestbots = new Request.Builder()
                .url(DiscordBestBots)
                .post(body)
                .addHeader("Authorization", config.getString("DiscordBestBots"))
                .build();

        try {
            new OkHttpClient().newCall(discordbestbots).execute().close();
            System.out.println("Successfully posted count to Discord Best Bots!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
