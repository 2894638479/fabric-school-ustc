package org.schoolustc.items

import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import org.schoolustc.MyRegister


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
    GET_GPA_POTION_ITEM
    SCHOOL_FENCE_GATE_BLOCK
    SCHOOL_FENCE_GATE_ITEM
    FOOD_MACHINE_ITEM
    FOOD_MACHINE_BLOCK
    FoodMachineBlockEntity.register()
    DRINK_MACHINE_BLOCK
    DRINK_MACHINE_ITEM
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

val CARD_MACHINE_PAIR = MyRegister.registerBlockItem(
    "card_machine",
    CardMachineBlock(
        BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
    ),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val CARD_MACHINE_ITEM = CARD_MACHINE_PAIR.second
val CARD_MACHINE_BLOCK = CARD_MACHINE_PAIR.first

private val TEACHING_TABLE_PAIR = MyRegister.registerBlockItem(
    "teaching_table",
    TeachingTableBlock(
        BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
    ),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val TEACHING_TABLE_ITEM = TEACHING_TABLE_PAIR.second
val TEACHING_TABLE_BLOCK = TEACHING_TABLE_PAIR.first

private val GRADING_MACHINE_PAIR = MyRegister.registerBlockItem(
    "grading_machine",
    GradingMachineBlock(
        BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
    ),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val GRADING_MACHINE_BLOCK = GRADING_MACHINE_PAIR.first
val GRADING_MACHINE_ITEM = GRADING_MACHINE_PAIR.second
val QUESTION_ITEM = MyRegister.registerItem(
    "question",
    QuestionItem(Item.Properties().stacksTo(1)),
    CreativeModeTabs.TOOLS_AND_UTILITIES
)
val GET_GPA_POTION_ITEM = MyRegister.registerItem(
    "get_gpa_potion",
    GetGPAPotionItem(Item.Properties()),
    CreativeModeTabs.COMBAT
)

private val SCHOOL_FENCE_GATE_PAIR = MyRegister.registerBlockItem(
    "school_fence_gate",
    SchoolFenceGate(
        BlockBehaviour.Properties.of()
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0f, 3.0f)
            .requiresCorrectToolForDrops(),
    ),
    CreativeModeTabs.BUILDING_BLOCKS
)
val SCHOOL_FENCE_GATE_BLOCK = SCHOOL_FENCE_GATE_PAIR.first
val SCHOOL_FENCE_GATE_ITEM = SCHOOL_FENCE_GATE_PAIR.second

private val FOOD_MACHINE_PAIR = MyRegister.registerBlockItem(
    "food_machine",
    FoodMachineBlock(
        BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
    ),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val FOOD_MACHINE_BLOCK = FOOD_MACHINE_PAIR.first
val FOOD_MACHINE_ITEM = FOOD_MACHINE_PAIR.second
private val DRINK_MACHINE_PAIR = MyRegister.registerBlockItem(
    "drink_machine",
    DrinkMachineBlock(
        BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
    ),
    CreativeModeTabs.FUNCTIONAL_BLOCKS
)
val DRINK_MACHINE_BLOCK = DRINK_MACHINE_PAIR.first
val DRINK_MACHINE_ITEM = DRINK_MACHINE_PAIR.second

