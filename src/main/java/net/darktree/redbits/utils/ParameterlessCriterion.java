package net.darktree.redbits.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class ParameterlessCriterion extends SimpleCriterionTrigger<ParameterlessCriterion.Conditions> {

	@Override
	public Codec<ParameterlessCriterion.Conditions> codec() {
		return ParameterlessCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, ParameterlessCriterion.Conditions::matches);
	}

	public record Conditions(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<ParameterlessCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player)
		).apply(instance, Conditions::new));

		public boolean matches() {
			return true;
		}

	}

}