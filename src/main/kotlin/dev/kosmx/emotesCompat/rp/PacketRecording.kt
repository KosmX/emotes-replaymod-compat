package dev.kosmx.emotesCompat.rp

import com.replaymod.recording.ReplayModRecording
import dev.kosmx.playerAnim.core.data.KeyframeAnimation
import io.github.kosmx.emotes.api.proxy.INetworkInstance
import io.github.kosmx.emotes.common.CommonData
import io.github.kosmx.emotes.common.network.EmotePacket
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.UUID

var currentId: UUID? = null

fun saveC2SEmotePacket(emoteData: KeyframeAnimation?, player: UUID) {
    if (MinecraftClient.getInstance().player?.uuid == player && emoteData != null) {
        currentId = emoteData.uuid
        ReplayModRecording.instance?.connectionEventHandler?.packetListener?.save(
            EmotePacket.Builder()
                .configureToStreamEmote(emoteData, player)
                .build().write().let { emotePacket ->
                    val data = INetworkInstance.safeGetBytesFromBuffer(emotePacket)
                    ServerPlayNetworking.createS2CPacket(Identifier(CommonData.MOD_ID, CommonData.playEmoteID),
                        PacketByteBuf(Unpooled.buffer(data.size)).apply { this.writeBytes(data) })
                }
        )
    }
}

fun saveC2SStopPacket() {
    val player = MinecraftClient.getInstance().player?.uuid
    if (MinecraftClient.getInstance().player?.isMainPlayer == true && player != null)
    ReplayModRecording.instance?.connectionEventHandler?.packetListener?.save(
        EmotePacket.Builder()
            .configureToSendStop(currentId, player)
            .build().write().let {
                val data = INetworkInstance.safeGetBytesFromBuffer(it)
                ServerPlayNetworking.createS2CPacket(
                    Identifier(CommonData.MOD_ID, CommonData.playEmoteID),
                    PacketByteBuf(Unpooled.buffer(data.size).apply { this.writeBytes(data) })
                )
            }
    )

}
