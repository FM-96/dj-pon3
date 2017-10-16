package at.felixmuellner.djpon3.listeners;

import at.felixmuellner.djpon3.Configuration;
import at.felixmuellner.djpon3.EmbedCreator;
import at.felixmuellner.djpon3.GuildSettingsManager;
import at.felixmuellner.djpon3.SongPlayer;
import at.felixmuellner.djpon3.songs.Song;
import at.felixmuellner.djpon3.songs.SongManager;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Felix on 05.04.2017.
 */
public class MessageReceivedListener implements IListener<MessageReceivedEvent> {
    private String prefix;
    private Emoji[] reactionNumbers = {
            EmojiManager.getForAlias("one"), EmojiManager.getForAlias("two"),
            EmojiManager.getForAlias("three"), EmojiManager.getForAlias("four"),
            EmojiManager.getForAlias("five"), EmojiManager.getForAlias("six"),
            EmojiManager.getForAlias("seven"), EmojiManager.getForAlias("eight"),
            EmojiManager.getForAlias("nine")
    };
    private SongManager songManager = SongManager.getInstance();

    public MessageReceivedListener(Configuration configuration) {
        this.prefix = configuration.getPrefix();
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        if (event.getMessage().getMentions().contains(event.getClient().getOurUser()) && !event.getAuthor().isBot()) {
            //send usage information if non-bot mentions us
            this.sendUsageInformation(event);
            return;
        } else if (!event.getMessage().getContent().startsWith(this.prefix + " ")) {
            return;
        }

        String command = event.getMessage().getContent().substring(this.prefix.length() + 1);

        //play song / queue song
        if (command.startsWith("play ") || command.startsWith("queue ")) {
            boolean play = command.startsWith("play ");
            String songTitle = command.substring(command.indexOf(" ")+1);

            List<Song> searchResults = this.songManager.search(songTitle);

            if (searchResults.size() == 0) {
                IMessage sentMessage = RequestBuffer.request(() -> {
                    return event.getMessage().getChannel().sendMessage(EmbedCreator.noResults(play));
                }).get();
                RequestBuffer.request(() -> {
                    sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
                });
            } else if (searchResults.size() == 1) {
                IMessage sentMessage = RequestBuffer.request(() -> {
                    return event.getMessage().getChannel().sendMessage(EmbedCreator.playSong(play, searchResults.get(0).getTitle()));
                }).get();
                RequestBuffer.request(() -> {
                    sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
                });

                SongPlayer.addSong(event.getGuild(), searchResults.get(0).getTitle(), play);
            } else {
                int numberOfResults;
                if (searchResults.size() > 9) {
                    numberOfResults = 9;
                } else {
                    numberOfResults = searchResults.size();
                }

                String messageContent = "";

                for (int i = 0; i < numberOfResults; ++i) {
                    messageContent += reactionNumbers[i].getUnicode() + "  " + searchResults.get(i).getTitle() + "\n\n";
                }

                String finalMessageContent = messageContent;
                IMessage sentMessage = RequestBuffer.request(() -> {
                    return event.getMessage().getChannel().sendMessage(EmbedCreator.searchResults(play, finalMessageContent));
                }).get();

                for (int i = 0; i < numberOfResults; ++i) {
                    int finalI = i;
                    RequestBuffer.request(() -> {
                        sentMessage.addReaction(reactionNumbers[finalI]);
                    }).get();
                }
                RequestBuffer.request(() -> {
                    sentMessage.addReaction(EmojiManager.getForAlias("x"));
                });
            }
        }
        //random
        else if (command.equals("random")) {
            //act as playrandom if the queue is empty and as queuerandom if there are currently songs in the queue
            boolean startPlaying = !(SongPlayer.getQueue(event.getGuild()).size() > 0);

            String randomSong = this.songManager.randomSong().getTitle();
            IMessage sentMessage = RequestBuffer.request(() -> {
                return event.getMessage().getChannel().sendMessage(EmbedCreator.playSong(startPlaying, randomSong));
            }).get();
            RequestBuffer.request(() -> {
                sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
            });
            SongPlayer.addSong(event.getGuild(), randomSong, startPlaying);
        }
        //playrandom
        else if (command.equals("playrandom")) {
            String randomSong = this.songManager.randomSong().getTitle();
            IMessage sentMessage = RequestBuffer.request(() -> {
                return event.getMessage().getChannel().sendMessage(EmbedCreator.playSong(true, randomSong));
            }).get();
            RequestBuffer.request(() -> {
                sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
            });
            SongPlayer.addSong(event.getGuild(), randomSong, true);
        }
        //queuerandom
        else if (command.equals("queuerandom")) {
            String randomSong = this.songManager.randomSong().getTitle();
            IMessage sentMessage = RequestBuffer.request(() -> {
                return event.getMessage().getChannel().sendMessage(EmbedCreator.playSong(false, randomSong));
            }).get();
            RequestBuffer.request(() -> {
                sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
            });
            SongPlayer.addSong(event.getGuild(), randomSong, false);
        }
        //play
        else if (command.equals("play")) {
            SongPlayer.play(event.getGuild());
        }
        //pause
        else if (command.equals("pause")) {
            SongPlayer.pause(event.getGuild());
        }
        //next / skip
        else if (command.equals("next") || command.equals("skip")) {
            SongPlayer.next(event.getGuild());
        }
        //stop
        else if (command.equals("stop")) {
            SongPlayer.stop(event.getGuild());
        }
        //queue
        else if (command.equals("queue")) {
            List<String> queue = SongPlayer.getQueue(event.getGuild());
            IMessage sentMessage = RequestBuffer.request(() -> {
                return event.getMessage().getChannel().sendMessage(EmbedCreator.showQueue(queue, SongPlayer.isPlay(event.getGuild())));
            }).get();
            RequestBuffer.request(() -> {
                sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
            });
        }
        //interrupt
        else if (command.startsWith("interrupt ")) {
            String stringValue = command.substring(10);
            boolean booleanValue;
            if (stringValue.equals("true") || stringValue.equals("yes")) {
                booleanValue = true;
            } else if (stringValue.equals("false") || stringValue.equals("no")) {
                booleanValue = false;
            } else {
                //invalid; send usage information
                this.sendUsageInformation(event);
                return;
            }
            //save setting
            EnumSet<Permissions> permissions = event.getAuthor().getPermissionsForGuild(event.getGuild());
            if (permissions.contains(Permissions.ADMINISTRATOR)) {
                GuildSettingsManager.getInstance().setAllowInterruption(event.getGuild(), booleanValue);
            }
        }
        //channel
        else if (command.startsWith("channel ")) {
            long channelId;
            try {
                channelId = Long.parseLong(command.substring(8));
            } catch (Exception e) {
                channelId = -1;
            }
            if (event.getGuild().getVoiceChannelByID(channelId) == null) {
                //invalid; send usage information
                this.sendUsageInformation(event);
                return;
            }
            //save setting
            EnumSet<Permissions> permissions = event.getAuthor().getPermissionsForGuild(event.getGuild());
            if (permissions.contains(Permissions.ADMINISTRATOR) || permissions.contains(Permissions.VOICE_MOVE_MEMBERS)) {
                GuildSettingsManager.getInstance().setVoiceChannelId(event.getGuild(), channelId);
                event.getGuild().getVoiceChannelByID(channelId).join();
            }
        }
        //invalid
        else {
            //send usage information
            this.sendUsageInformation(event);
        }
    }

    private void sendUsageInformation(MessageReceivedEvent event) {
        IMessage sentMessage = RequestBuffer.request(() -> {
            return event.getMessage().getChannel().sendMessage(EmbedCreator.usage(this.prefix, event.getAuthor(), event.getGuild()));
        }).get();
        RequestBuffer.request(() -> {
            sentMessage.addReaction(EmojiManager.getForAlias("wastebasket"));
        });
    }
}
