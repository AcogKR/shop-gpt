package dev.acog.shop

import io.heartpattern.springfox.paper.command.annotation.CommandHandler
import io.heartpattern.springfox.paper.command.model.CommandInvocation
import io.typecraft.bukkit.view.BukkitView
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.stereotype.Component

@Component
class ShopCommand(
    private val plugin: JavaPlugin,
    private val shopService: ShopService
) {

    @CommandHandler(name = ["shop"])
    fun user(invocation: CommandInvocation) {
        val player = invocation.sender as Player
        if (shopService.item.type == Material.AIR || shopService.price <= 0) {
            player.sendMessage("상점이 없습니다.")
            return
        }
        BukkitView.openView(shopService.getShopView(player), player, plugin)
    }

    @CommandHandler(name = ["shop-manager"])
    fun manager(invocation: CommandInvocation) {
        val player = invocation.sender as Player

        val index = invocation.args.getOrNull(0)
            ?: run {
                player.sendMessage("판매하실 가격을 입력 해주세요")
                return
            }

        val price = index.toDoubleOrNull()
            ?: run {
                player.sendMessage("가격을 정확하게 입력 해주세요")
                return
            }
        if (price < 0) {
            player.sendMessage("아이템의 가격을 음수로 설정할수 없습니다.")
            return
        }

        val shopItem = player.inventory.itemInMainHand
        if (shopItem.type == Material.AIR) {
            player.sendMessage("아이템을 손에 들고 계셔야 합니다.")
            return
        }

        shopService.setShopItem(shopItem, price)
        player.sendMessage("판매 아이템 및 가격을 설정 했습니다.")
    }

}