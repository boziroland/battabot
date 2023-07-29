package bot.actualcommands.audiocommands

import bot.commandmanagement.ICommand
import bot.lavaplayer.GuildAudioManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


class PlayCommand(
    private val guildAudioManager: GuildAudioManager,
) : ICommand {
    override fun command() = "play"

    override fun help() = "play music duh"

    override fun execute(
        args: Array<String>,
        event: MessageReceivedEvent,
    ) {
        if (args.size < 2) return

        if (args[1].isLink()) { // meh
            guildAudioManager.loadAndPlay("${args[1]}&c=TVHTML5&cver=7.20190319", event, false)
        } else {
            val searchBase = if (soundcloudParamIsSet(args)) "scsearch" else "ytsearch"
            val startIndex = if (args[1].startsWith("-")) 2 else 1

            val searchArgs = args.slice(startIndex until args.size).joinToString(" ")

            guildAudioManager.loadAndPlay("$searchBase:$searchArgs", event, false)
        }
        guildAudioManager.musicManager.scheduler.isLooping = false
    }

    private fun soundcloudParamIsSet(args: Array<String>) = args[1].equals("-sc", ignoreCase = true)

    private fun String.isLink() = this.startsWith("https://")

    override fun getAliases() = listOf("p")
}