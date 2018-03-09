package de.bergwerklabs.friends.client.bungee.command

import de.bergwerklabs.atlantis.api.friends.FriendRequestResponse
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.friends.api.FriendsApi
import de.bergwerklabs.friends.client.bungee.friendsClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 29.11.2017.
 * <p>
 * @author Yannic Rieger
 */
class FriendAcceptCommand : BungeeCommand {
    
    override fun getUsage() = "/friend accept <name>"
    
    override fun getName() = "accept"
    
    override fun getDescription() = "Akzeptiert eine Freundschaftsanfrage."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            // Run async because friendsClient#process is blocking
            friendsClient!!.runAsync {
                
                if (args!!.isEmpty()) {
                    friendsClient!!.messenger.message("§cEin Fehler ist aufgetreten.", sender)
                    return@runAsync
                }
                
                friendsClient!!.process(args[0], sender, { acceptor, accepted ->
                    FriendsApi.respondToInvite(acceptor, accepted, FriendRequestResponse.ACCEPTED)
                    friendsClient!!.messenger.message("§7Du hast die Anfrage §aangenommen", sender)
                })
            }
        }
    }
}