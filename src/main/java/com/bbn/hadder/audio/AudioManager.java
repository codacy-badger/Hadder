package com.bbn.hadder.audio;

import com.bbn.hadder.commands.CommandEvent;
import com.bbn.hadder.utils.MessageEditor;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Skidder / GregTCLTK
 */

public class AudioManager {

    public Map<String, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();
    private final AudioPlayerManager myManager = new DefaultAudioPlayerManager();

    public AudioManager() {
        AudioSourceManagers.registerRemoteSources(myManager);
        myManager.getConfiguration().setFilterHotSwapEnabled(true);
    }

    public boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    public AudioPlayer getPlayer(Guild guild) {
        AudioPlayer p;
        if (hasPlayer(guild)) {
            p = players.get(guild.getId()).getKey();
        } else {
            p = createPlayer(guild);
        }
        return p;
    }

    public TrackManager getTrackManager(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    public AudioPlayer createPlayer(Guild guild) {
        AudioPlayer nPlayer = myManager.createPlayer();
        TrackManager manager = new TrackManager(nPlayer, this);
        nPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(nPlayer));
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(nPlayer, manager));
        return nPlayer;
    }

    public void loadTrack(String identifier, CommandEvent event, Message msg) {
        Guild guild = event.getGuild();
        getPlayer(guild);

        myManager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                getTrackManager(guild).queue(track, event.getMember());
                msg.editMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.INFO,
                        "commands.music.play.success.loading.title", "⏯",
                        "", "")
                        .addField(event.getMessageEditor().getTerm("commands.music.play.success.title"), track.getInfo().title, false)
                        .addField(event.getMessageEditor().getTerm("commands.music.play.success.author"), track.getInfo().author, true)
                        .addField(event.getMessageEditor().getTerm("commands.music.play.success.length"),
                                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(track.getInfo().length),
                                        TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length) % TimeUnit.HOURS.toMinutes(1),
                                        TimeUnit.MILLISECONDS.toSeconds(track.getInfo().length) % TimeUnit.MINUTES.toSeconds(1)), true)
                        .build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    for (int i = 0; i < Math.min(playlist.getTracks().size(), 69); i++) {
                        getTrackManager(guild).queue(playlist.getTracks().get(i), event.getMember());
                    }
                    msg.editMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.INFO,
                            "commands.music.play.success.loading.title", "⏯",
                            "", "")
                            .addField(event.getMessageEditor().getTerm("commands.music.play.success.title"), playlist.getName(), true)
                            .addField(event.getMessageEditor().getTerm("commands.music.play.success.tracks"), String.valueOf(playlist.getTracks().size()), true)
                            .build()).queue();
                }
            }

            @Override
            public void noMatches() {
                msg.editMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.INFO,
                        "commands.music.play.error.match.title", "❌",
                        "commands.music.play.error.match.description", "")
                        .build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                msg.editMessage(event.getMessageEditor().getMessage(MessageEditor.MessageType.INFO,
                        "commands.music.play.error.load.title", "❌",
                        "commands.music.play.error.load.description", "")
                        .build()).queue();
            }
        });
    }

    public boolean isDj(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getName().equals("DJ"));
    }

    public boolean isCurrentDj(Member member) {
        return getTrackManager(member.getGuild()).getTrackInfo(getPlayer(member.getGuild()).getPlayingTrack()).getAuthor().equals(member);
    }

    public void forceSkipTrack(CommandEvent event) {
        getPlayer(event.getGuild()).stopTrack();
    }

    public String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    public String getOrNull(String s) {
        return s.isEmpty() ? "N/A" : s;
    }

}
