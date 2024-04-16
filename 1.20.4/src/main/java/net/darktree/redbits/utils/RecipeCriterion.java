package net.darktree.redbits.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

// polyfill, Minecraft version before did not have the minecraft:recipe_crafted criterion
public class RecipeCriterion extends AbstractCriterion<RecipeCriterion.Conditions> {

	@Override
	public Codec<RecipeCriterion.Conditions> getConditionsCodec() {
		return RecipeCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Identifier recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public record Conditions(Optional<LootContextPredicate> player, Identifier recipe) implements AbstractCriterion.Conditions {

		public static final Codec<RecipeCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(RecipeCriterion.Conditions::player),
				Identifier.CODEC.fieldOf("recipe_id").forGetter(RecipeCriterion.Conditions::recipe)
		).apply(instance, RecipeCriterion.Conditions::new));

		boolean matches(Identifier recipe) {
			return recipe.equals(this.recipe);
		}

	}
}
