package dev.kosmx.emotesCompat.rp

import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MODID: String = "emotes-compat-rp"

val LOGGER: Logger by lazy { LoggerFactory.getLogger(MODID) }

fun initializeClient() {
    LOGGER.info("Hello Fabric")
}