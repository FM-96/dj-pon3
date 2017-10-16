package at.felixmuellner.djpon3.listeners;

import at.felixmuellner.djpon3.EmbedCreator;
import at.felixmuellner.djpon3.SongPlayer;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

/**
 * Created by Felix on 06.04.2017.
 */
public class ReactionAddListener implements IListener<ReactionAddEvent> {
    private Emoji[] reactionNumbers = {
            EmojiManager.getForAlias("one"), EmojiManager.getForAlias("two"),
            EmojiManager.getForAlias("three"), EmojiManager.getForAlias("four"),
            EmojiManager.getForAlias("five"), EmojiManager.getForAlias("six"),
            EmojiManager.getForAlias("seven"), EmojiManager.getForAlias("eight"),
            EmojiManager.getForAlias("nine")
    };

    @Override
    public void handle(ReactionAddEvent event) {
        if (!event.getMessage().getAuthor().equals(event.getClient().getOurUser()) || event.getUser().equals(event.getClient().getOurUser())) {
            return;
        }

        IEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
        boolean play = messageEmbed.getTitle().contains("Play");

        if (messageEmbed.getColor().equals(new Color(214, 81, 177))) {
            if (event.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias("x").getUnicode())) {
                //cancel
                RequestBuffer.request(() -> {
                    event.getMessage().edit(EmbedCreator.canceled(play));
                }).get();
                RequestBuffer.request(() -> {
                    event.getMessage().addReaction(EmojiManager.getForAlias("wastebasket"));
                });
                this.removeReactions(event.getMessage());
            } else if (messageEmbed.getDescription().contains(event.getReaction().getEmoji().getName())) {
                //select song
                int selectionNumber = -1;
                for (int i = 0; i < reactionNumbers.length; ++i) {
                    if (event.getReaction().getEmoji().getName().equals(reactionNumbers[i].getUnicode())) {
                        selectionNumber = i;
                        break;
                    }
                }

                if (selectionNumber != -1) {
                    int songTitleStart = messageEmbed.getDescription().indexOf(reactionNumbers[selectionNumber].getUnicode()) + 2;
                    int songTitleEnd = messageEmbed.getDescription().indexOf("\n", songTitleStart);
                    if (songTitleEnd == -1) {
                        songTitleEnd = messageEmbed.getDescription().length();
                    }
                    String songTitle = messageEmbed.getDescription().substring(songTitleStart, songTitleEnd).trim();
                    RequestBuffer.request(() -> {
                        event.getMessage().edit(EmbedCreator.playSong(play, songTitle));
                    }).get();
                    RequestBuffer.request(() -> {
                        event.getMessage().addReaction(EmojiManager.getForAlias("wastebasket"));
                    });

                    //play song
                    SongPlayer.addSong(event.getGuild(), songTitle, play);
                    this.removeReactions(event.getMessage());
                }
            }
        } else {
            if (event.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias("wastebasket").getUnicode())) {
                //delete
                RequestBuffer.request(() -> {
                    event.getMessage().delete();
                });
            }
        }
    }

    private void removeReactions(IMessage message) {
        IUser clientUser = message.getClient().getOurUser();
        for (IReaction reaction : message.getReactions()) {
            if (reaction.getUserReacted(clientUser) && !reaction.getEmoji().getName().equals(EmojiManager.getForAlias("wastebasket").getUnicode())) {
                RequestBuffer.request(() -> {
                    reaction.getMessage().removeReaction(clientUser, reaction);
                });
            }
        }
    }
}
