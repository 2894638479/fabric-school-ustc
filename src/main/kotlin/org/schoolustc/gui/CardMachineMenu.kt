package org.schoolustc.gui

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.*
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.schoolustc.items.CARD_MACHINE_BLOCK
import org.schoolustc.items.MONEY_CARD
import org.schoolustc.items.MoneyCardItem.Companion.createDate
import org.schoolustc.items.MoneyCardItem.Companion.money
import org.schoolustc.items.MoneyCardItem.Companion.ownerName
import org.schoolustc.items.MoneyCardItem.Companion.ownerUUID
import org.schoolustc.items.STUDENT_CARD
import java.time.ZonedDateTime

class CardMachineMenu(val containerId:Int,val inventory:Inventory,val access:ContainerLevelAccess):AbstractContainerMenu(type,containerId) {
    companion object {
        val type = Registry.register(
            BuiltInRegistries.MENU,
            "card_machine",
            MenuType(
                {id,inv -> CardMachineMenu(id,inv,ContainerLevelAccess.NULL)},
                FeatureFlags.VANILLA_SET
            )
        )
        fun register(){ type }
    }
    private val materialContainer = TransientCraftingContainer(this,2,1)
    private val resultContainer = ResultContainer()
    private val resultSlot = object :ResultSlot(inventory.player,materialContainer,resultContainer, 0, 116, 35){
        override fun onTake(player: Player, itemStack: ItemStack) { onTake() }
    }
    private var onTake:()->Unit = {}
    init {
        this.addSlot(resultSlot)
        for (i in 0..1) {
            this.addSlot(Slot(materialContainer, i, 25 + i * 31, 35))
        }
        for (j in 0..2) {
            for (k in 0..8) {
                this.addSlot(Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18))
            }
        }
        for (j in 0..8) {
            this.addSlot(Slot(inventory, j, 8 + j * 18, 142))
        }
    }
    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        val slot = slots[i]
        val stack = slot.item
        val copy = stack.copy()
        if(i == 0) if(moveItemStackTo(stack,3,39,true)) onTake() else return ItemStack.EMPTY
        if(i == 1 || i == 2) if(!moveItemStackTo(stack,3,39,true)) return ItemStack.EMPTY
        if(i > 2) if(!moveItemStackTo(stack,1,3,false)) return ItemStack.EMPTY
        slot.setChanged()
        slotsChanged(slot.container)
        return copy
    }

    fun match(slot:Pair<ItemStack,ItemStack>,item:Pair<Item,Item>,task:(ItemStack,ItemStack)->Unit) {
        if(slot.first.`is`(item.first) && slot.second.`is`(item.second)){
            task(slot.first,slot.second)
        } else if(slot.first.`is`(item.second) && slot.second.`is`(item.first)) {
            task(slot.second,slot.first)
        }
    }
    fun getMaterial(i:Int) = materialContainer.items[i]
    fun setMaterial(i:Int,item:ItemStack) = materialContainer.setItem(i,item)
    fun replaceMaterial(orig:ItemStack,new:ItemStack){
        for (i in 0..<materialContainer.containerSize){
            if(getMaterial(i) === orig){
                setMaterial(i,new)
                return
            }
        }
    }
    override fun slotsChanged(container: Container) {
        fun match(item:Pair<Item,Item>,task:(ItemStack,ItemStack)->Unit) = match(getMaterial(0) to getMaterial(1),item,task)
        resultSlot.set(ItemStack.EMPTY)
        match(Items.EMERALD to MONEY_CARD){ emerald, moneyCard ->
            onTake = {
                setMaterial(0,ItemStack.EMPTY)
                setMaterial(1,ItemStack.EMPTY)
            }
            resultSlot.set(moneyCard.copy().apply { money += emerald.count*100 })
        }
        match(Items.EMERALD to STUDENT_CARD){ emerald, studentCard ->
            onTake = {
                emerald.count--
            }
            resultSlot.set(MONEY_CARD.defaultInstance.apply {
                ownerName = studentCard.ownerName
                ownerUUID = studentCard.ownerUUID
                createDate = ZonedDateTime.now()
            })
        }
        match(Items.EMERALD to Items.AIR){ emerald, air ->
            onTake = {
                emerald.count--
            }
            resultSlot.set(STUDENT_CARD.defaultInstance.apply {
                ownerName = inventory.player.name.string
                ownerUUID = inventory.player.stringUUID
                createDate = ZonedDateTime.now()
            })
        }
        super.slotsChanged(container)
    }
    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, CARD_MACHINE_BLOCK)
    }

    override fun removed(player: Player) {
        super.removed(player)
        clearContainer(player,materialContainer)
    }
}