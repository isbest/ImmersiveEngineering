/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.immersiveengineering.api.excavator;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class MineralMix extends IESerializableRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<MineralMix>> SERIALIZER;

	public static final CachedRecipeList<MineralMix> RECIPES = new CachedRecipeList<>(IERecipeTypes.MINERAL_MIX);

	public final List<StackWithChance> outputs;
	public final List<StackWithChance> spoils;
	public final int weight;
	public final float failChance;
	public final ImmutableSet<ResourceKey<Level>> dimensions;
	public final Block background;

	public MineralMix(List<StackWithChance> outputs, List<StackWithChance> spoils, int weight,
					  float failChance, List<ResourceKey<Level>> dimensions, Block background)
	{
		super(LAZY_EMPTY, IERecipeTypes.MINERAL_MIX);
		this.weight = weight;
		this.failChance = failChance;
		this.outputs = outputs;
		this.spoils = spoils;
		this.dimensions = ImmutableSet.copyOf(dimensions);
		this.background = background;
	}

	@Override
	protected IERecipeSerializer<MineralMix> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		return ItemStack.EMPTY;
	}

	public static String getPlainName(ResourceLocation id)
	{
		String path = id.getPath();
		return path.substring(path.lastIndexOf("/")+1);
	}

	public static String getTranslationKey(ResourceLocation id)
	{
		return Lib.DESC_INFO+"mineral."+getPlainName(id);
	}

	public ItemStack getRandomOre(Random rand)
	{
		float r = rand.nextFloat();
		for(StackWithChance o : outputs)
			if(o.chance() >= 0)
			{
				r -= o.chance();
				if(r < 0)
					return o.stack().get();
			}
		return ItemStack.EMPTY;
	}

	public ItemStack getRandomSpoil(Random rand)
	{
		float r = rand.nextFloat();
		for(StackWithChance o : spoils)
			if(o.chance() >= 0)
			{
				r -= o.chance();
				if(r < 0)
					return o.stack().get();
			}
		return ItemStack.EMPTY;
	}

	public boolean validDimension(ResourceKey<Level> dim)
	{
		if(dimensions!=null&&!dimensions.isEmpty())
			return dimensions.contains(dim);
		return true;
	}
}
