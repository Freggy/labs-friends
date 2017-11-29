import de.bergwerklabs.atlantis.api.friends.*
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.atlas.api.AtlasClient
import de.bergwerklabs.atlas.api.AtlasPacketListener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

/**
 * Created by Yannic Rieger on 01.11.2017.
 * <p>
 * @author Yannic Rieger
 */
class FriendsApi {
    
    companion object {
    
        private val client = AtlasClient()
        private val service = AtlantisPackageService()
        private val responseListeners: MutableSet<AtlasPacketListener<FriendInviteResponsePacket>> = HashSet()
        private val requestListeners: MutableSet<AtlasPacketListener<FriendInviteRequestPacket>> = HashSet()
        
        
        init {
            this.client.register()
            
            this.client.registerListener(FriendInviteResponsePacket::class.java, object: AtlasPacketListener<FriendInviteResponsePacket> {
                override fun onPacketReceived(packet: FriendInviteResponsePacket) {
                    responseListeners.forEach { listener -> listener.onPacketReceived(packet) }
                }
            })
    
            this.client.registerListener(FriendInviteRequestPacket::class.java, object: AtlasPacketListener<FriendInviteRequestPacket> {
                override fun onPacketReceived(packet: FriendInviteRequestPacket) {
                    requestListeners.forEach { listener -> listener.onPacketReceived(packet) }
                }
            })
            
        }
        
        @JvmStatic
        fun sendInvite(sender: UUID, receiver: UUID, callback: AtlantisPackageService.Callback<FriendInviteResponsePacket>) {
            this.client.sendPacket(FriendInviteRequestPacket(this.client.clientInformation, sender, receiver), FriendInviteResponsePacket::class.java, callback)
        }
    
        @JvmStatic
        fun sendInvite(sender: UUID, receiver: UUID) {
            this.client.sendPacket(FriendInviteRequestPacket(this.client.clientInformation, sender, receiver))
        }
        
        @JvmStatic
        fun respondToInvite(sender: UUID, receiver: UUID, response: FriendRequestResponse) {
            this.client.sendPacket(FriendInviteResponsePacket(this.client.clientInformation, sender, receiver, response))
        }
        
        @JvmStatic
        fun removeFriend(from: UUID, toRemove: UUID) {
            this.service.sendPackage(RemoveFriendPacket(toRemove, from))
        }
        
        @JvmStatic
        fun retrieveFriendInfo(player: UUID): FriendInfo {
            val packet = this.service.sendRequestWithFuture(FriendInfoRequestPacket(player), FriendInfoResponsePacket::class.java)
                                     .get(4, TimeUnit.SECONDS)
            
            return FriendInfo(packet.friends, packet.pendingInvites)
        }
        
        fun follow(follower: UUID, toFollow: UUID) {
            // TODO:
        }
        
        fun registerFriendResponseListener(listener: AtlasPacketListener<FriendInviteResponsePacket>) {
            this.responseListeners.add(listener)
        }
    
        fun registerFriendRequestListener(listener: AtlasPacketListener<FriendInviteRequestPacket>) {
            this.requestListeners.add(listener)
        }
    }
}