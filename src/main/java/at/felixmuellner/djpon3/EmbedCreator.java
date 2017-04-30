package at.felixmuellner.djpon3;

import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;

/**
 * Created by Felix on 13.04.2017.
 */
public class EmbedCreator {
    public static EmbedObject canceled(boolean play) {
        return new EmbedBuilder()
                .withColor(new Color(221, 46, 68))
                .withTitle(play ? "Play Song" : "Queue Song")
                .withDescription(EmojiManager.getForAlias("x").getUnicode())
                .build();
    }

    public static EmbedObject noResults(boolean play) {
        return new EmbedBuilder()
                .withColor(new Color(221, 46, 68))
                .withTitle(play ? "Play Song" : "Queue Song")
                .withDescription(EmojiManager.getForAlias("no_entry_sign").getUnicode())
                .build();
    }

    public static EmbedObject playSong(boolean play, String title) {
        return new EmbedBuilder()
                .withColor(new Color(93, 173, 236))
                .withTitle(play ? "Play Song" : "Queue Song")
                .withDescription(":musical_note:  " + title)
                .build();
    }

    public static EmbedObject showQueue(List<String> queue, boolean play) {
        EmbedBuilder builder = new EmbedBuilder()
                .withColor(new Color(57, 255, 20))
                .withTitle("View Queue");

        if (queue.isEmpty()) {
            return builder.withDescription(":no_entry_sign:").build();
        }

        String description = "";
        for (int i = 0; i < queue.size(); ++i) {
            if (i == 0) {
                if (play) {
                    description += ":arrow_forward:  " + queue.get(i) + "\n";
                } else {
                    description += ":pause_button:  " + queue.get(i) + "\n";
                }
            } else {
                description += ":musical_note:  " + queue.get(i) + "\n";
            }
        }
        return builder.withDescription(description).build();
    }

    public static EmbedObject searchResults(boolean play, String content) {
        return new EmbedBuilder()
                .withColor(new Color(214, 81, 177))
                .withTitle(play ? "Play Song" : "Queue Song")
                .withDescription(content)
                .build();
    }

    public static EmbedObject usage(String prefix, IUser user, IGuild guild) {
        EmbedBuilder builder = new EmbedBuilder()
                .withColor(new Color(57, 255, 20))
                .withTitle("Usage")
                .appendField("Play Song", "`" + prefix + " play <song title>`", false)
                .appendField("Queue Song", "`" + prefix + " queue <song title>`", false)
                .appendField("Play/Queue Random Song", "`" + prefix + " random`", false)
                .appendField("Play Random Song", "`" + prefix + " playrandom`", false)
                .appendField("Queue Random Song", "`" + prefix + " queuerandom`", false)
                .appendField("Play", "`" + prefix + " play`", false)
                .appendField("Pause", "`" + prefix + " pause`", false)
                .appendField("Next Song", "`" + prefix + " next`", false)
                .appendField("Stop", "`" + prefix + " stop`", false)
                .appendField("View Queue", "`" + prefix + " queue`", false);

        if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            builder.appendField("Allow Interruptions?", "`" + prefix + " interrupt <true/false/yes/no>`", false)
                    .appendField("Set Voice Channel", "`" + prefix + " channel <channel id>`", false);
        } else if (user.getPermissionsForGuild(guild).contains(Permissions.VOICE_MOVE_MEMBERS)) {
            builder.appendField("Set Voice Channel", "`" + prefix + " channel <channel id>`", false);
        }
        return builder.build();
    }
}
