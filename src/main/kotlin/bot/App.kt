package bot

import bot.utils.Constants
import bot.utils.UtilsKt
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.LoggerFactory
import java.util.*

private const val CONFIG_FILE = "config/ConfigurationKeys.properties"

object App : ListenerAdapter() {
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    private val intents: List<GatewayIntent> = listOf(
        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.MESSAGE_CONTENT,
    )

    private val token = UtilsKt.getPropertiesFromResourceFile(CONFIG_FILE)
        ?.getProperty("DiscordToken")
        .also {
            require(it!!.isNotBlank()) { "Discord bot token is missing!" }
        }

    val jda: JDA = JDABuilder.create(token, intents)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .build()
        .awaitReady()

    val defaultChannels = jda.guilds.associateWith { guild ->
        guild.textChannels.firstOrNull { channel ->
            "general" in channel.name.lowercase(Locale.getDefault())
        } ?: guild.textChannels.first()
    }
}

fun main() {
    try {
        Constants.defaultTextChannels = App.defaultChannels
        App.jda.apply {
            presence.activity = Activity.watching("Help command: ${Constants.PREFIX}help")
            addEventListener(MyEventListener())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
