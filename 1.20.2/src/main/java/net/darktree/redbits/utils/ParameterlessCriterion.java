package net.darktree.redbits.utils;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ParameterlessCriterion extends AbstractCriterion<ParameterlessCriterion.Conditions> {

	@Override
	public ParameterlessCriterion.Conditions conditionsFromJson(JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		return new ParameterlessCriterion.Conditions(optional);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, Conditions::matches);
	}

	public static class Conditions extends AbstractCriterionConditions {

		public Conditions(Optional<LootContextPredicate> predicate) {
			super(predicate);
		}

		public boolean matches() {
			return true;
		}

		@Override
		public JsonObject toJson() {
			return super.toJson();
		}

	}

}