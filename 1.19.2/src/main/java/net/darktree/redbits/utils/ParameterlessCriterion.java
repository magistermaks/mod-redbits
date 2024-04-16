package net.darktree.redbits.utils;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ParameterlessCriterion extends AbstractCriterion<ParameterlessCriterion.Conditions> {

	private final Identifier identifier;

	public ParameterlessCriterion(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(identifier, predicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, Conditions::matches);
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	public static class Conditions extends AbstractCriterionConditions {


		public Conditions(Identifier id, EntityPredicate.Extended entity) {
			super(id, entity);
		}

		public boolean matches() {
			return true;
		}

	}

}