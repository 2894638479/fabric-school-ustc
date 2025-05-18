package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.schoolustc.MyRegister
import org.schoolustc.structureDsl.read



fun registerItemAndBlock(){
    STUDENT_CARD
    MONEY_CARD
    CARD_MACHINE_ITEM
    CARD_MACHINE_BLOCK
    QUESTION_ITEM
    TEACHING_TABLE_ITEM
    TEACHING_TABLE_BLOCK
    GRADING_MACHINE_BLOCK
    GRADING_MACHINE_ITEM
}

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

private val TEACHING_TABLE_PAIR = MyRegister.registerBlockItem(
    "teaching_table",
    TeachingTableBlock(BlockBehaviour.Properties.of()),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val TEACHING_TABLE_ITEM = TEACHING_TABLE_PAIR.second
val TEACHING_TABLE_BLOCK = TEACHING_TABLE_PAIR.first

private val GRADING_MACHINE_PAIR = MyRegister.registerBlockItem(
    "grading_machine",
    GradingMachineBlock(BlockBehaviour.Properties.of()),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val GRADING_MACHINE_BLOCK = GRADING_MACHINE_PAIR.first
val GRADING_MACHINE_ITEM = GRADING_MACHINE_PAIR.second
val QUESTION_ITEM = MyRegister.registerItem(
    "question",
    QuestionItem(Item.Properties().stacksTo(1)),
    CreativeModeTabs.TOOLS_AND_UTILITIES
)

