package com.github.flandre923.berrypouch.recipe;

import net.minecraft.world.item.crafting.Recipe;

// Inspired by Sophisticated Backpacks by P3pp3rF1y (https://github.com/P3pp3rF1y/SophisticatedBackpacks)
public interface IWrapperRecipe<T extends Recipe<?>> {
	T getCompose();
}
