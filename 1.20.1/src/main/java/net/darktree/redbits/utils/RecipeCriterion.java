package net.darktree.redbits.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeCriterion extends AbstractCriterion<RecipeCriterion.Conditions> {

	private Identifier identifier;

	public RecipeCriterion(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	protected RecipeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		Identifier recipe = new Identifier(JsonHelper.getString(jsonObject, "recipe_id"));
		return new RecipeCriterion.Conditions(identifier, lootContextPredicate, recipe);
	}

	public void trigger(ServerPlayerEntity player, Identifier recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static class Conditions extends AbstractCriterionConditions {

		private final Identifier recipe;

		public Conditions(Identifier identifier, LootContextPredicate player, Identifier recipe) {
			super(identifier, player);
			this.recipe = recipe;
		}

		boolean matches(Identifier recipe) {
			return recipe.equals(this.recipe);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject json = super.toJson(predicateSerializer);
			json.addProperty("recipe_id", this.recipe.toString());
			return json;
		}
	}

}