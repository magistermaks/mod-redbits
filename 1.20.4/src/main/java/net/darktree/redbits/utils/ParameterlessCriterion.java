package net.darktree.redbits.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darktree.redbits.RedBits;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class ParameterlessCriterion extends AbstractCriterion<ParameterlessCriterion.Conditions> {

	@Override
	public Codec<ParameterlessCriterion.Conditions> getConditionsCodec() {
		return ParameterlessCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, ParameterlessCriterion.Conditions::matches);
	}

	public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {

		public static final Codec<ParameterlessCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(ParameterlessCriterion.Conditions::player)
		).apply(instance, ParameterlessCriterion.Conditions::new));

		public boolean matches() {
			return true;
		}

	}

}