package com.github.flandre923.berrypouch.neoforge.compat.jei;

import com.github.flandre923.berrypouch.mixins.ShapedRecipeMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ClientRecipeHelper {
	private ClientRecipeHelper() {}

	public static <I extends RecipeInput, T extends Recipe<I>, U extends Recipe<?>> List<RecipeHolder<T>> transformAllRecipesOfType(RecipeType<T> recipeType, Class<U> filterRecipeClass, Function<U, T> transformRecipe) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) {
			return Collections.emptyList();
		}

		return level.getRecipeManager()
				.getAllRecipesFor(recipeType)
				.stream()
				.filter(r -> filterRecipeClass.isInstance(r.value()))
				.map(r -> new RecipeHolder<>(r.id(), transformRecipe.apply(filterRecipeClass.cast(r.value()))))
				.toList();
	}

	public static <I extends RecipeInput, T extends Recipe<I>, U extends Recipe<?>> List<RecipeHolder<T>> transformAllRecipesOfTypeIntoMultiple(RecipeType<T> recipeType, Class<U> filterRecipeClass, Function<U, List<RecipeHolder<T>>> transformRecipe) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) {
			return Collections.emptyList();
		}

		return level.getRecipeManager()
				.getAllRecipesFor(recipeType)
				.stream()
				.filter(r -> filterRecipeClass.isInstance(r.value()))
				.map(r -> transformRecipe.apply(filterRecipeClass.cast(r.value())))
				.collect(ArrayList::new, List::addAll, List::addAll);
	}

	public static CraftingRecipe copyShapedRecipe(ShapedRecipe recipe) {
		return new ShapedRecipe("", recipe.category(), ((ShapedRecipeMixin)recipe).getPatternAccessor(),((ShapedRecipeMixin) recipe).getResultAccess());
	}

	public static CraftingRecipe copyShapelessRecipe(ShapelessRecipe recipe) {
		return new ShapelessRecipe("", recipe.category(), ((ShapedRecipeMixin) recipe).getResultAccess(), recipe.getIngredients());
	}

	public static <I extends RecipeInput> ItemStack assemble(Recipe<I> recipe, I container) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) {
			throw new NullPointerException("level must not be null.");
		}
		RegistryAccess registryAccess = level.registryAccess();
		return recipe.assemble(container, registryAccess);
	}
}
