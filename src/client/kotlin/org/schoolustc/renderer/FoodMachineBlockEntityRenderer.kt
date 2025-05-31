package org.schoolustc.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.schoolustc.items.FoodMachineBlockEntity
import org.schoolustc.items.MONEY_CARD

class FoodMachineBlockEntityRenderer(context: BlockEntityRendererProvider.Context):BlockEntityRenderer<FoodMachineBlockEntity> {
    companion object {
        fun register() {
            BlockEntityRendererRegistryImpl.register(FoodMachineBlockEntity.type,::FoodMachineBlockEntityRenderer)
        }
    }
    override fun render(
        entity: FoodMachineBlockEntity,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        fun withStack(block:()->Unit){
            poseStack.pushPose()
            block()
            poseStack.popPose()
        }

        val facing = entity.blockState.getValue(HorizontalDirectionalBlock.FACING)
        val lightLevel = entity.level?.run {
            LevelRenderer.getLightColor(this,entity.blockPos.offset(facing.normal))
        } ?: combinedLight


        withStack {
            poseStack.translate(0.5, 0.5, 0.5)
            poseStack.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(facing.toYRot().toDouble()).toFloat(), 0f, -1f, 0f)))

            withStack {
                poseStack.translate(-0.15, 0.15, 0.505)
                val scale = 0.5f
                poseStack.scale(scale, scale, 0.01f)
                Minecraft.getInstance().itemRenderer.renderStatic(
                    entity.item.defaultInstance,
                    ItemDisplayContext.FIXED,
                    lightLevel,
                    combinedOverlay,
                    poseStack,
                    bufferSource,
                    entity.level,
                    0
                )
            }

            withStack {
                poseStack.translate(-0.15, -0.3, 0.505)
                val scale = 0.3f
                poseStack.scale(scale, scale, 0.01f)
                Minecraft.getInstance().itemRenderer.renderStatic(
                    MONEY_CARD.defaultInstance,
                    ItemDisplayContext.FIXED,
                    lightLevel,
                    combinedOverlay,
                    poseStack,
                    bufferSource,
                    entity.level,
                    0
                )
            }
        }
        withStack {
            val str = entity.cost.toString()
            val font = Minecraft.getInstance().font
            val width = font.width(str)
            val height = font.lineHeight
            poseStack.translate(0.5, 0.5, 0.5)
            poseStack.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(facing.toYRot().toDouble()).toFloat(), 0f, -1f, 0f)))
            poseStack.translate(0.0,-0.33,0.505)
            val scale = -0.03f
            poseStack.scale(scale, scale, scale)
            poseStack.mulPose(Axis.YP.rotationDegrees(180F))
            font.drawInBatch(
                str,
                0f,
                -height/2f,
                0xA0FFFFFFu.toInt(),
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                lightLevel
            )
        }
    }
}