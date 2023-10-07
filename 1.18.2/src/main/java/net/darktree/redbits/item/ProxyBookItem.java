package net.darktree.redbits.item;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.PatchouliProxy;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProxyBookItem extends Item {

	private static final Identifier BOOK = new Identifier(RedBits.NAMESPACE, "guide");
	private final PatchouliProxy proxy;

	public static ProxyBookItem createInstance() {
		return new ProxyBookItem(new Settings().maxCount(1), PatchouliProxy.create());
	}

	private ProxyBookItem(Settings settings, PatchouliProxy proxy) {
		super(settings);
		this.proxy = proxy;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (proxy == null) tooltip.add(new TranslatableText("message.redbits.patchouli").formatted(Formatting.DARK_RED));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (proxy != null && player instanceof ServerPlayerEntity serverPlayer) {
			proxy.openBook(serverPlayer, BOOK);
		}

		// always returns success, even if there is no proxy
		return TypedActionResult.success(stack);
	}

}
