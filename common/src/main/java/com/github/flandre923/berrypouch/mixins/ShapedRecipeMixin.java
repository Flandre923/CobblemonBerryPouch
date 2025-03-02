package com.github.flandre923.berrypouch.mixins;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeMixin {

    @Accessor("pattern") // 使用 Accessor 注解创建访问器方法
    ShapedRecipePattern getPatternAccessor();

    @Accessor("result") // 使用 Accessor 注解创建修改器方法 (如果需要设置为 public 可写)
    ItemStack getResultAccess();


}