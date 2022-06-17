package io.github.haykam821.woodenhoppers.data;

import java.util.function.Consumer;

import io.github.haykam821.woodenhoppers.Main;
import io.github.haykam821.woodenhoppers.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class WoodenHoppersRecipeProvider extends FabricRecipeProvider {
	private static final Identifier GROUP_ID = new Identifier(Main.MOD_ID, "wooden_hopper");
	private static final String GROUP = GROUP_ID.toString();

	public WoodenHoppersRecipeProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		for (ModBlocks modBlock : ModBlocks.values()) {
			WoodenHoppersRecipeProvider.offerWoodenHopperRecipe(exporter, modBlock.getBlock(), modBlock.getBase());
		}
	}

	public static void offerWoodenHopperRecipe(Consumer<RecipeJsonProvider> exporter, Block block, Block base) {
		ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(block)
			.input('#', Ingredient.ofItems(base))
			.input('C', Items.CHEST)
			.pattern("# #")
			.pattern("#C#")
			.pattern(" # ")
			.group(GROUP)
			.criterion(RecipeProvider.hasItem(base), RecipeProvider.conditionsFromItem(base));

		WoodenHoppersRecipeProvider.offerCraftingTo(exporter, builder);
	}

	private static void offerCraftingTo(Consumer<RecipeJsonProvider> exporter, ShapedRecipeJsonBuilder factory) {
		Identifier recipeId = CraftingRecipeJsonBuilder.getItemId(factory.getOutputItem());
		WoodenHoppersRecipeProvider.offerShapedTo(exporter, recipeId, factory);
	}

	private static void offerShapedTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId, ShapedRecipeJsonBuilder factory) {
		factory.validate(recipeId);

		Identifier advancementId = WoodenHoppersRecipeProvider.getAdvancementId(recipeId);
		factory.advancementBuilder
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);

		String group = factory.group == null ? "" : factory.group;
		exporter.accept(new ShapedRecipeJsonBuilder.ShapedRecipeJsonProvider(recipeId, factory.getOutputItem(), factory.outputCount, group, factory.pattern, factory.inputs, factory.advancementBuilder, advancementId));
	}

	private static Identifier getAdvancementId(Identifier recipeId) {
		return new Identifier(recipeId.getNamespace(), "recipes/" + recipeId.getPath());
	}
}