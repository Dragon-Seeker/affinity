package io.wispforest.affinity.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.wispforest.affinity.Affinity;
import io.wispforest.affinity.client.render.AbsoluteEnchantmentGlintHandler;
import io.wispforest.affinity.client.render.SkyCaptureBuffer;
import io.wispforest.affinity.item.StaffItem;
import io.wispforest.affinity.misc.MixinHooks;
import io.wispforest.affinity.misc.callback.PostItemRenderCallback;
import io.wispforest.affinity.object.AffinityBlocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @Final
    private ItemModels models;

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"))
    private void captureGlintColor(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        AbsoluteEnchantmentGlintHandler.prepareGlintColor(stack);
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void callPostRenderEvent(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        var item = MixinHooks.renderItem != null && MixinHooks.renderItem.present()
                ? MixinHooks.renderItem.get()
                : null;

        PostItemRenderCallback.EVENT.invoker().postRender(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model, item);

        MixinHooks.renderItem = null;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD")
    )
    private void punchAHoleIntoYourInventory(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci, @Local(argsOnly = true) LocalRef<VertexConsumerProvider> consumers) {
        if (!stack.isOf(AffinityBlocks.THE_SKY.asItem())) return;
        consumers.set(layer -> vertexConsumers.getBuffer(SkyCaptureBuffer.isIrisWorldRendering() ? SkyCaptureBuffer.SKY_STENCIL_ENTITY_LAYER : SkyCaptureBuffer.SKY_IMMEDIATE_LAYER));
    }

    @ModifyReturnValue(method = "getModel", at = @At("TAIL"))
    private BakedModel triggerStaffRevolverRendering(BakedModel original, ItemStack stack) {
        if (!(stack.getItem() instanceof StaffItem) || !stack.has(StaffItem.BUNDLED_STAFFS)) return original;
        return this.models.getModelManager().getModel(Affinity.id("item/staff_bundle"));
    }
}
