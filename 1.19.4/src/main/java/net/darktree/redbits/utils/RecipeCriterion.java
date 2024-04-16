package net.darktree.redbits.utils;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

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
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Identifier recipe = new Identifier(JsonHelper.getString(obj, "recipe_id"));
		return new Conditions(identifier, predicate, recipe);
	}

	public void trigger(ServerPlayerEntity player, Identifier recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static class Conditions extends AbstractCriterionConditions {

		private final Identifier recipe;

		public Conditions(Identifier identifier, EntityPredicate.Extended player, Identifier recipe) {
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