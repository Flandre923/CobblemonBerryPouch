//package com.gitlab.srcmc.rctmod.recipe;
//
//import com.gitlab.srcmc.rctmod.helper.ItemNBTHelper;
//import com.gitlab.srcmc.rctmod.item.BerryPouch;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.CraftingContainer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//
//public class BerryPouchUpgradeRecipe implements CraftingRecipe {
//    private final CraftingRecipe baseRecipe; // 基础的合成配方 (例如 ShapedRecipe)
//    private final ResourceLocation id;
//
//    public BerryPouchUpgradeRecipe(ResourceLocation id, CraftingRecipe baseRecipe) {
//        this.id = id;
//        this.baseRecipe = baseRecipe;
//    }
//
//    public final CraftingRecipe getBaseRecipe(){
//        return baseRecipe;
//    }
//
//    @Override
//    public boolean matches(CraftingInput recipeInput, Level level) {
//        return baseRecipe.matches(recipeInput, level);
//    }
//
//    @Override
//    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
//        ItemStack resultStack = baseRecipe.assemble(recipeInput, provider);
//        // 1. 查找合成容器中的 BerryPouch (假设只有一个)
//        ItemStack sourcePouchStack = ItemStack.EMPTY;
//        for (int i = 0; i < recipeInput.size(); i++) {
//            ItemStack ingredient = recipeInput.getItem(i);
//            if (ingredient.getItem() instanceof BerryPouch && !ingredient.isEmpty()) {
//                sourcePouchStack = ingredient.copy(); // 获取源 BerryPouch 的副本
//                break; // 假设配方中只允许一个 BerryPouch
//            }
//        }
//        if (resultStack.getItem() instanceof BerryPouch && !sourcePouchStack.isEmpty()) {
//            ItemNBTHelper.transItemList(sourcePouchStack, resultStack);
//        }
//        return resultStack;
//    }
//
//    @Override
//    public boolean canCraftInDimensions(int width, int height) {
//        return baseRecipe.canCraftInDimensions(width, height);
//    }
//
//
//    public ItemStack getResultItem(HolderLookup.Provider provider) {
//        return baseRecipe.getResultItem(provider);
//    }
//
//    public ResourceLocation getId() {
//        return id;
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return BerryPouchUpgradeRecipeSerializer.INSTANCE;
//    }
//
//    @Override
//    public RecipeType<?> getType() {
//        return RecipeType.CRAFTING; // 假设是 crafting 类型，如果需要可以创建自定义 RecipeType
//    }
//
//    @Override
//    public CraftingBookCategory category() {
//        return null;
//    }
//}