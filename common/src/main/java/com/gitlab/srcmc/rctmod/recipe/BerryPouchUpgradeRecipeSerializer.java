//package com.gitlab.srcmc.rctmod.recipe;
//
//import com.gitlab.srcmc.rctmod.ModCommon;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParseException;
//import com.mojang.serialization.Decoder;
//import com.mojang.serialization.Encoder;
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.core.Registry;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.crafting.CraftingRecipe;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.ShapedRecipe;
//import org.jetbrains.annotations.Nullable;
//
//public class BerryPouchUpgradeRecipeSerializer implements RecipeSerializer<BerryPouchUpgradeRecipe> {
//    public static final BerryPouchUpgradeRecipeSerializer INSTANCE = new BerryPouchUpgradeRecipeSerializer();
//    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "berry_pouch_upgrade");
//
//    private BerryPouchUpgradeRecipeSerializer() {}
//
//
//    @Override
//    public MapCodec<BerryPouchUpgradeRecipe> codec() {
//        return RecordCodecBuilder.mapCodec(instance -> instance.group(
//                ResourceLocation.CODEC.fieldOf("id").forGetter(BerryPouchUpgradeRecipe::getId),
//                BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().<CraftingRecipe>dispatch(
//                        "type",
//                        CraftingRecipe::getSerializer,
//                        serializer -> (MapCodec<? extends CraftingRecipe>) serializer.codec() // 显式类型转换
//                ).fieldOf("base").forGetter(recipe -> recipe.getBaseRecipe())
//        ).apply(instance, BerryPouchUpgradeRecipe::new));
//    }
//
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, BerryPouchUpgradeRecipe> streamCodec() {
//        return StreamCodec.of(
//                // Encoder to network
//                (buffer, recipe) -> {
//                    buffer.writeResourceLocation(recipe.getId());
//                    RecipeSerializer<?> baseRecipeSerializer = recipe.getBaseRecipe().getSerializer();
//                    if (baseRecipeSerializer == null) {
//                        throw new RuntimeException("Cannot serialize baseRecipe with null serializer for network");
//                    }
//                    buffer.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getKey(baseRecipeSerializer));
//                    StreamCodec<RegistryFriendlyByteBuf, ? extends CraftingRecipe> baseRecipeStreamCodec = baseRecipeSerializer.streamCodec();
//                    baseRecipeStreamCodec.encode(buffer,recipe.getBaseRecipe());
//                },
//                // Decoder from network
//                buffer -> {
//                    ResourceLocation recipeId = buffer.readResourceLocation();
//                    ResourceLocation baseRecipeSerializerId = buffer.readResourceLocation();
//                    RecipeSerializer<?> baseRecipeSerializer = Registry.RECIPE_SERIALIZER.get(baseRecipeSerializerId);
//                    if (baseRecipeSerializer == null) {
//                        throw new RuntimeException("Unknown recipe serializer type from network: " + baseRecipeSerializerId);
//                    }
//                    CraftingRecipe baseRecipe = (CraftingRecipe) baseRecipeSerializer.fromNetwork(buffer);
//                    return new BerryPouchUpgradeRecipe(recipeId, baseRecipe);
//                }
//        );
//    }
//}