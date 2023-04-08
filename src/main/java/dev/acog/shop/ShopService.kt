package dev.acog.shop

import io.heartpattern.javagpt.JavaGpt
import io.typecraft.bukkit.view.ChestView
import io.typecraft.bukkit.view.ViewAction
import io.typecraft.bukkit.view.ViewContents
import io.typecraft.bukkit.view.ViewControl
import io.typecraft.bukkit.view.item.BukkitItem
import jakarta.annotation.PostConstruct
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.stereotype.Service

@Service
class ShopService(
    private val plugin: JavaPlugin
) {

    var item: ItemStack = ItemStack(Material.AIR)
    var price: Double = 0.0

    private var gpt: PriceGPT = TODO()
    private var economy: Economy = Bukkit.getServicesManager().load(Economy::class.java)
        ?: throw Exception("Economy Class Null")

    fun setShopItem(item: ItemStack, price: Double) {
        this.item = item
        this.price = price
    }

    @PostConstruct
    fun start() {
        plugin.saveDefaultConfig()
        val key = plugin.config.getString("api-key") ?: throw Exception("ChatGPT Key null, config.yml")
        gpt = JavaGpt.generate(key, PriceGPT::class.java)
    }

    fun getShopView(player: Player) : ChestView =
        ChestView.just("GPT 상점", 1, ViewContents.ofControls(mapOf(4 to getShopItem(player))))

    private fun getShopItem(player: Player) : ViewControl { // ㅊ
        val bukkitItem = BukkitItem.from(item)
        val discountedPrice = gpt.discountedPrice(price)

        val priceLore =
            listOf(
                *bukkitItem.lore.toTypedArray(),
                "§e > §f원래 아이템 가격은 ${price}원이지만 오늘 기분이 좋기에",
                "§e   §f오늘은 ${discountedPrice}원만 받도록 하겠습니다.",
                "§e   ( 좌클릭시 해당 아이템을 구매 합니다 )"
            )

        return ViewControl.of(bukkitItem.withLore(priceLore).build()) { event ->
            if (event.click == ClickType.LEFT) {
                val balance: Double = economy.getBalance(player)
                if (balance >= discountedPrice) {
                    player.sendMessage("아이템을 구매 했습니다.")
                    player.inventory.addItem(item)
                    economy.withdrawPlayer(player, discountedPrice)
                } else {
                    player.sendMessage("보유 금액이 부족합니다.")
                }
            }
            ViewAction.CLOSE
        }

    }
}