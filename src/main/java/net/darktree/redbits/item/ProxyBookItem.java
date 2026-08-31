package net.darktree.redbits.item;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.PatchouliProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class ProxyBookItem extends Item {

	private static final Identifier BOOK = Identifier.of(RedBits.NAMESPACE, "guide");
	private final PatchouliProxy proxy;

	public static ProxyBookItem createInstance() {
		return new ProxyBookItem(new Settings().maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, BOOK)), PatchouliProxy.create());
	}

	private ProxyBookItem(Settings settings, PatchouliProxy proxy) {
		super(settings);
		this.proxy = proxy;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		if (proxy == null) tooltip.add(Text.translatable("message.redbits.patchouli").formatted(Formatting.DARK_RED));
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		if (proxy != null && player instanceof ServerPlayerEntity serverPlayer) {
			proxy.openBook(serverPlayer, BOOK);
		}

		// always returns success, even if there is no proxy
		return ActionResult.SUCCESS;
	}

}
