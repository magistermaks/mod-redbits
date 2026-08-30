package net.darktree.redbits.item;

import net.darktree.redbits.RedBits;
import net.darktree.redbits.utils.PatchouliProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.function.Consumer;

public class ProxyBookItem extends Item {

	private static final Identifier BOOK = Identifier.fromNamespaceAndPath(RedBits.NAMESPACE, "guide");
	private final PatchouliProxy proxy;

	public static ProxyBookItem createInstance() {
		return new ProxyBookItem(new Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, BOOK)), PatchouliProxy.create());
	}

	private ProxyBookItem(Properties settings, PatchouliProxy proxy) {
		super(settings);
		this.proxy = proxy;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
		if (proxy == null) tooltip.accept(Component.translatable("message.redbits.patchouli").withStyle(ChatFormatting.DARK_RED));
	}

	@Override
	public InteractionResult use(Level world, Player player, InteractionHand hand) {
		if (proxy != null && player instanceof ServerPlayer serverPlayer) {
			proxy.openBook(serverPlayer, BOOK);
		}

		// always returns success, even if there is no proxy
		return InteractionResult.SUCCESS;
	}

}
