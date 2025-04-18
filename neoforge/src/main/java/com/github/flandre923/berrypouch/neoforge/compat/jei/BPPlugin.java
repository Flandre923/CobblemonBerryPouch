package com.github.flandre923.berrypouch.neoforge.compat.jei;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.recipe.BerryPouchUpgradeRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@SuppressWarnings("unused")
@JeiPlugin
public class BPPlugin implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        IModPlugin.super.registerRecipes(registration);
        registration.addRecipes(RecipeTypes.CRAFTING, ClientRecipeHelper.transformAllRecipesOfType(RecipeType.CRAFTING, BerryPouchUpgradeRecipe.class, ClientRecipeHelper::copyShapedRecipe));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IModPlugin.super.registerRecipeTransferHandlers(registration);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"default");
    }
}
