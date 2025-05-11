package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.schoolustc.MyRegister
import org.schoolustc.structureDsl.read

val STUDENT_CARD = MyRegister.registerItem(
    "student_card",
    StudentCardItem(Item.Properties().stacksTo(1)),
    CreativeModeTabs.INGREDIENTS
)

val MONEY_CARD = MyRegister.registerItem(
    "money_card",
    MoneyCardItem(Item.Properties().stacksTo(1)),
    CreativeModeTabs.INGREDIENTS
)

private val CARD_MACHINE_PAIR = MyRegister.registerBlockItem(
    "card_machine",
    CardMachineBlock(BlockBehaviour.Properties.of()),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val CARD_MACHINE_ITEM = CARD_MACHINE_PAIR.second
val CARD_MACHINE_BLOCK = CARD_MACHINE_PAIR.first

