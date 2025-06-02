package org.schoolustc.structs

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.locale.Language
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.entity.BlockEntityType
import org.schoolustc.datapack.knowledgeBankList
import org.schoolustc.items.DrinkMachineBlock.Companion.randDrinkMachineItem
import org.schoolustc.structs.Classroom.Type.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.struct.scope.View


class Classroom(config: StructGenConfig,val block:Block,val type: Type): MyStructFixedSize(Companion,config){
    companion object : MyStructFixedSizeInfo<Classroom>(
        "classroom",
        Point(8,5,12)
    ) {
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag): Classroom {
            return Classroom(
                tag.read("C"),
                tag.read("b"),
                tag.read("t")
            )
        }
        override fun Classroom.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("b",block)
            tag.write("t",type)
        }
        val tools = listOf(
            Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
            Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
            Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE,
            Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL,
            Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL,
            Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
            Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE,
            Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE,
            Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE
        )
        val ores = listOf(
            Items.COAL,
            Items.IRON_INGOT,
            Items.COPPER_INGOT,
            Items.GOLD_INGOT,
            Items.REDSTONE,
            Items.LAPIS_LAZULI,
            Items.EMERALD,
            Items.DIAMOND,
            Items.NETHERITE_INGOT,
            Items.QUARTZ
        )
        val armor = listOf(
            Items.LEATHER_HELMET,
            Items.LEATHER_CHESTPLATE,
            Items.LEATHER_LEGGINGS,
            Items.LEATHER_BOOTS,
            Items.CHAINMAIL_HELMET,
            Items.CHAINMAIL_CHESTPLATE,
            Items.CHAINMAIL_LEGGINGS,
            Items.CHAINMAIL_BOOTS,
            Items.GOLDEN_HELMET,
            Items.GOLDEN_CHESTPLATE,
            Items.GOLDEN_LEGGINGS,
            Items.GOLDEN_BOOTS,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS
        )
        fun hoverTextPaper(vararg str:String) = Items.PAPER.defaultInstance.apply {
            val displayTag = CompoundTag()
            val loreList = ListTag()
            str.forEach {
                if(it === str.last()) loreList.add(StringTag.valueOf(Component.Serializer.toJson(
                    Component.literal(it).withStyle(ChatFormatting.GOLD)
                )))
                else loreList.add(StringTag.valueOf(Component.Serializer.toJson(
                    Component.literal(it)
                )))
            }
            displayTag.put("Lore", loreList)
            orCreateTag.put("display", displayTag)
        }
        val paper1 = hoverTextPaper("同学的重力加速度总是测出10+","这似乎预示着一教下方的矿物分布","彩蛋：30%概率生成金矿")
        val paper2 = hoverTextPaper("据说有一种神奇的药水","能强行知道别人的GPA","彩蛋：某喷溅药水")
        val paper3 = hoverTextPaper("据说食堂的饮料贩卖机有概率卡住...","彩蛋：贩卖机概率故障")
    }
    enum class Type(val string: String){
        TEACHING("teaching"),
        FOOD("food"),
        BOOK("book"),
        LAB("lab"),
        BED("bed"),
        EMPTY("empty");
        val id get() = "classroom_inner_$string"
    }
    override fun StructBuildScopeWithConfig.build() {
        val light = SEA_LANTERN
        val floor = SMOOTH_STONE
        inRelativeView {
            block fillWall fixedArea

            AIR fill Area(xMax.range,1..<ySize,2..4)

            val glassX = GLASS_PANE.state.connected(Direction2D.XPlus,Direction2D.XMin)
            val glassZ = GLASS_PANE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
            val glassY = 2..<ySize
            glassZ fill Area(0.range,glassY,2..4)
            glassZ fill Area(0.range,glassY,7..9)
            glassZ fill Area(xMax.range,glassY,7..9)
            glassX fill Area(3..4,glassY,0.range)
            glassX fill Area(3..4,glassY,zMax.range)

            floor fill Area(1..xSize - 2,0.range,1..zSize - 2)
            light fill Point(2,0,2)
            light fill Point(2,0,zSize - 3)
            light fill Point(xSize - 3,0,2)
            light fill Point(xSize - 3,0,zSize - 3)
            light fill Area((xSize/2).let{it-1..it},0.range,(zSize/2).let { it-1..it })

            fun randKnowledgeBookStack():ItemStack {
                val knowledgeBank = rand from knowledgeBankList.associateWith { it.knowledge.size }
                val knowledge = rand from knowledgeBank.knowledge
                return ItemStack(Items.WRITTEN_BOOK).apply {
                    count = 1
                    orCreateTag.run {
                        write("title", knowledge.title)
                        write("author", "")
                        val pages = ListTag()
                        pages.add(
                            StringTag.valueOf(
                                "{\"text\":\""
                                        + Language.getInstance().getOrDefault("text.subject", "subject")
                                        + ":"
                                        + knowledgeBank.subject
                                        + "\n\n"
                                        + knowledge.info
                                        + "\"}"
                            )
                        )
                        put("pages", pages)
                    }
                }
            }
            fun randEnchantBook():ItemStack{
                val enchantedBook = ItemStack(Items.ENCHANTED_BOOK)
                val allEnchantments = BuiltInRegistries.ENCHANTMENT.stream().toList()
                if (allEnchantments.isEmpty()) return ItemStack.EMPTY
                val selected = rand from allEnchantments
                val level = rand from selected.minLevel..selected.maxLevel
                EnchantedBookItem.addEnchantment(enchantedBook, EnchantmentInstance(selected, level))
                return enchantedBook
            }
            fun randTool() = (rand from tools).defaultInstance
            fun randOre() = (rand from ores).defaultInstance.apply { count = rand from 1..10 }
            fun randArmor() = (rand from armor).defaultInstance
            fun randPaper() = rand from listOf(paper1, paper2, paper3)
            fun randExpBottle() = Items.EXPERIENCE_BOTTLE.defaultInstance.apply { count = rand from 10..63 }
            fun treatChest(chance:Double,randItem:()->ItemStack) = View.Treatment(BlockEntityType.CHEST) { entity ->
                entity.clearContent()
                val full = entity.containerSize
                (0..<full).forEach {
                    rand.withChance(chance){
                        entity.setItem(it,randItem())
                    }
                }
            }
            fun treatShulker(chance:Double, randItem:()->ItemStack) = View.Treatment(BlockEntityType.SHULKER_BOX) { entity ->
                entity.clearContent()
                val full = entity.containerSize
                (0..<full).forEach {
                    rand.withChance(chance){
                        entity.setItem(it,randItem())
                    }
                }
            }
            val afterTreatment:List<View.Treatment<*>> = when(type){
                TEACHING -> listOf(treatChest(0.3){
                    rand.from(
                        { randKnowledgeBookStack() } to 3,
                        { Items.PAPER.defaultInstance.apply { count = rand from 5..40 } } to 1,
                        { Items.WRITABLE_BOOK.defaultInstance } to 1,
                        { Items.BOOKSHELF.defaultInstance.apply { count = rand from 1..5 } } to 0.5,
                        { Items.BOOK.defaultInstance.apply { count = rand from 3..12 } } to 1,
                        { randEnchantBook() } to 3,
                        { randExpBottle() } to 1,
                        { randPaper() } to 0.1,
                    )()
                })
                FOOD -> listOf()
                BOOK -> listOf(treatChest(0.5){
                    rand.from(
                        { randKnowledgeBookStack() } to 3,
                        { Items.PAPER.defaultInstance.apply { count = rand from 5..40 } } to 1,
                        { Items.WRITABLE_BOOK.defaultInstance } to 1,
                        { Items.BOOKSHELF.defaultInstance.apply { count = rand from 1..5 } } to 0.5,
                        { Items.BOOK.defaultInstance.apply { count = rand from 3..12 } } to 1,
                        { randEnchantBook() } to 3,
                        { randExpBottle() } to 1,
                        { randPaper() } to 0.1
                    )()
                })
                LAB -> listOf(treatChest(0.3){
                    rand.from(
                        { randKnowledgeBookStack() } to 3,
                        { Items.PAPER.defaultInstance.apply { count = rand from 5..40 } } to 1,
                        { Items.WRITABLE_BOOK.defaultInstance } to 1,
                        { Items.BOOK.defaultInstance.apply { count = rand from 3..12 } } to 1,
                        { randTool() } to 3,
                        { randArmor() } to 3,
                        { randEnchantBook() } to 2,
                        { randOre() } to 2,
                        { randExpBottle() } to 1,
                        { randPaper() } to 0.1
                    )()
                },treatShulker(0.3){
                    rand.randDrinkMachineItem() ?: ItemStack.EMPTY
                })
                BED -> listOf(treatShulker(0.3){
                    rand.from(
                        { randTool() } to 1,
                        { randOre() } to 1,
                        { Items.WRITABLE_BOOK.defaultInstance } to 1,
                        { randArmor() } to 3,
                        { randExpBottle() } to 1,
                        { randPaper() } to 0.1
                    )()
                })
                EMPTY -> listOf()
            }
            val nbtPos = Point(1,1,1)
            if(type != EMPTY){
                putNbtStruct(type.id,nbtPos, listOf(),afterTreatment)
            }
        }
    }
}

