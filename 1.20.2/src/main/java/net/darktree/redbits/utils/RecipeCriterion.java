package net.darktree.redbits.utils;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

// polyfill, Minecraft version before did not have the minecraft:recipe_crafted criterion
public class RecipeCriterion extends AbstractCriterion<RecipeCriterion.Conditions> {

	@Override
	protected RecipeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe_id"));
		return new RecipeCriterion.Conditions(optional, identifier);
	}

	public void trigger(ServerPlayerEntity player, Identifier recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static class Conditions extends AbstractCriterionConditions {

		private final Identifier recipe;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Identifier recipe) {
			super(playerPredicate);
			this.recipe = recipe;
		}

		boolean matches(Identifier recipe) {
			return recipe.equals(this.recipe);
		}

		@Override
		public JsonObject toJson() {
			JsonObject json = super.toJson();
			json.addProperty("recipe_id", this.recipe.toString());
			return json;
		}
	}

}

