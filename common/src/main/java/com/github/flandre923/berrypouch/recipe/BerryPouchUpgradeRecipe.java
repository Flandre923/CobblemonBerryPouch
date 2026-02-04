package com.github.flandre923.berrypouch.recipe;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.mixins.ShapedRecipeMixin;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Optional;

// Inspired by Sophisticated Backpacks by P3pp3rF1y (https://github.com/P3pp3rF1y/SophisticatedBackpacks)
public class BerryPouchUpgradeRecipe extends ShapedRecipe implements IWrapperRecipe<ShapedRecipe> {
    private final ShapedRecipe compose;

    public BerryPouchUpgradeRecipe(ShapedRecipe compose) {
        super(compose.getGroup(), compose.category(), ((ShapedRecipeMixin)compose).getPatternAccessor(), ((ShapedRecipeMixin)compose).getResultAccess());
        this.compose = compose;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
        ItemStack upgradedBackpack = super.assemble(inv, registries);
        getBerryPouch(inv).map(ItemStack::getComponents).ifPresent(upgradedBackpack::applyComponents);
        return upgradedBackpack;
    }

    private Optional<ItemStack> getBerryPouch(CraftingInput inv) {
        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack slotStack = inv.getItem(slot);
            if (slotStack.getItem() instanceof BerryPouch) {
                return Optional.of(slotStack);
            }
        }
        return Optional.empty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistries.Recipes.BACKPACK_UPGRADE_RECIPE_SERIALIZER.get();
    }

    public static class Serializer extends RecipeWrapperSerializer<ShapedRecipe, BerryPouchUpgradeRecipe> {
        public Serializer() {
            super(BerryPouchUpgradeRecipe::new, RecipeSerializer.SHAPED_RECIPE);
        }
    }

    @Override
    public ShapedRecipe getCompose() {
        return compose;
    }
}