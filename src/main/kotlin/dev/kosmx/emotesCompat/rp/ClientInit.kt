package dev.kosmx.emotesCompat.rp

import io.github.kosmx.emotes.api.events.client.ClientEmoteEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MODID: String = "emotes-compat-rp"

val LOGGER: Logger by lazy { LoggerFactory.getLogger(MODID) }

fun initializeClient() {
    LOGGER.info("Hello Fabric")

    //Save play emote c2s packet as s2c
    ClientEmoteEvents.EMOTE_PLAY.register { emoteData, userID -> saveC2SEmotePacket(emoteData, userID) }
    ClientEmoteEvents.LOCAL_EMOTE_STOP.register { saveC2SStopPacket() }


}