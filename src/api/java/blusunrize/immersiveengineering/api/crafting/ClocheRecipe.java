/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.immersiveengineering.api.crafting;

import blusunrize.immersiveengineering.api.crafting.ClocheRenderFunction.ClocheRenderReference;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.minecraft.core.Holder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClocheRecipe extends IESerializableRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<ClocheRecipe>> SERIALIZER;

	public final List<Lazy<ItemStack>> outputs;
	public final Ingredient seed;
	public final Ingredient soil;
	public final int time;
	public final ClocheRenderReference renderReference;
	public final ClocheRenderFunction renderFunction;

	public static final CachedRecipeList<ClocheRecipe> RECIPES = new CachedRecipeList<>(IERecipeTypes.CLOCHE);
	private static final List<Pair<Ingredient, ResourceLocation>> soilTextureList = new ArrayList<>();

	public ClocheRecipe(List<Lazy<ItemStack>> outputs, Ingredient seed, Ingredient soil, int time, ClocheRenderReference renderReference)
	{
		super(outputs.get(0), IERecipeTypes.CLOCHE);
		this.outputs = outputs;
		this.seed = seed;
		this.soil = soil;
		this.time = time;
		this.renderReference = renderReference;
		this.renderFunction = ClocheRenderFunction.RENDER_FUNCTION_FACTORIES.get(renderReference.getType()).apply(renderReference.getBlock());
	}

	public ClocheRecipe(Lazy<ItemStack> output, Ingredient seed, Ingredient soil, int time, ClocheRenderReference renderReference)
	{
		this(ImmutableList.of(output), seed, soil, time, renderReference);
	}

	// Allow for more dynamic recipes in subclasses
	public List<Lazy<ItemStack>> getOutputs(ItemStack seed, ItemStack soil)
	{
		return this.outputs;
	}

	// Allow for more dynamic recipes in subclasses
	public int getTime(ItemStack seed, ItemStack soil)
	{
		return this.time;
	}

	@Override
	protected IERecipeSerializer<ClocheRecipe> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		return this.outputs.get(0).get();
	}

	public static ClocheRecipe findRecipe(Level level, ItemStack seed, ItemStack soil, @Nullable ClocheRecipe hint)
	{
		if (seed.isEmpty() || soil.isEmpty())
			return null;
		if (hint != null && hint.matches(seed, soil))
			return hint;
		for(RecipeHolder<ClocheRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().matches(seed, soil))
				return recipe.value();
		return null;
	}

	public boolean matches(ItemStack seed, ItemStack soil)
	{
		return this.seed.test(seed)&&this.soil.test(soil);
	}

	public static boolean isValidCombinationInMenu(ItemStack seed, ItemStack soil, Level level)
	{
		for(RecipeHolder<ClocheRecipe> recipe : RECIPES.getRecipes(level))
			if((seed.isEmpty()||recipe.value().seed.test(seed))&&(soil.isEmpty()||recipe.value().soil.test(soil)))
				return true;
		return false;
	}

	/* ========== SOIL TEXTURE ========== */

	/**
	 * Registers a given input to cause a replacement of the rendered soil texture
	 *
	 * @param soil
	 * @param texture
	 */
	public static void registerSoilTexture(Ingredient soil, ResourceLocation texture)
	{
		soilTextureList.add(Pair.of(soil, texture));
	}

	public static ResourceLocation getSoilTexture(ItemStack soil)
	{
		for(Pair<Ingredient, ResourceLocation> entry : soilTextureList)
			if(entry.getFirst().test(soil))
				return entry.getSecond();
		return null;
	}
}
