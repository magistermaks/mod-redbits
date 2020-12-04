package net.darktree.redbits;

import net.darktree.redbits.blocks.*;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock.CollisionCondition;
import net.darktree.redbits.utils.ColorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;


public class RedBits implements ModInitializer, ClientModInitializer {

	@SuppressWarnings("unchecked")
	public final static CollisionCondition COLLISION_CONDITION_PET = ( World world, Box box ) -> {
		List<TameableEntity> l = world.getNonSpectatingEntities(TameableEntity.class, box);
		l.removeIf( n -> !n.isTamed() ); return (List<Entity>) (List<?>) l;
	};
	public final static CollisionCondition COLLISION_CONDITION_PLAYERS = ( World world, Box box ) -> world.getNonSpectatingEntities(PlayerEntity.class, box);
	public final static CollisionCondition COLLISION_CONDITION_HOSTILE = ( World world, Box box ) -> world.getNonSpectatingEntities(HostileEntity.class, box);
	public final static CollisionCondition COLLISION_CONDITION_VILLAGER = ( World world, Box box ) -> world.getNonSpectatingEntities(AbstractTraderEntity.class, box);

	public final static Block OAK_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block SPRUCE_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block BIRCH_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block JUNGLE_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block ACACIA_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block DARK_OAK_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block CRIMSON_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block WARPED_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block STONE_LARGE_BUTTON = new LargeButtonBlock( false, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F));
	public final static Block POLISHED_BLACKSTONE_LARGE_BUTTON = new LargeButtonBlock( false, AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F));
	public final static Block REDSTONE_LAMP = new RedstoneLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).lightLevel((n) -> n.get(Properties.LIT) ? 1 : 0).postProcess((a, b, c) -> a.get(Properties.LIT)).emissiveLighting((a, b, c) -> a.get(Properties.LIT)).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning( (BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true ) );
	public final static Block INVERTER = new InverterBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block T_FLIP_FLOP = new FlipFlopBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block DETECTOR = new DetectorBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block TWO_WAY_REPEATER = new TwoWayRepeaterBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block LATCH = new LatchBlock(AbstractBlock.Settings.of(Material.SUPPORTED).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block REDSTONE_EMITTER = new EmitterBlock();
	public final static Block OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_PLAYERS, AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().noCollision().strength(0.5F) );
	public final static Block CRYING_OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_HOSTILE, AbstractBlock.Settings.of(Material.STONE, MaterialColor.BLACK).requiresTool().noCollision().strength(0.5F) );
	public final static Block END_STONE_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_VILLAGER, AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().noCollision().strength(0.5F) );
	public final static Block BASALT_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_PET, AbstractBlock.Settings.of(Material.STONE, MaterialColor.SAND).requiresTool().noCollision().strength(0.5F) );
	public final static Block INVERTED_REDSTONE_TORCH = new InvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel( (n) -> n.get(Properties.LIT) ? 7 : 0 ).sounds(BlockSoundGroup.WOOD));
	public final static Block INVERTED_REDSTONE_WALL_TORCH = new WallInvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.SUPPORTED).noCollision().breakInstantly().lightLevel( (n) -> n.get(Properties.LIT) ? 7 : 0 ).sounds(BlockSoundGroup.WOOD));
	public final static Block RGB_LAMP = new AnalogLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).lightLevel((n) -> n.get(AnalogLampBlock.POWER) > 0 ? 1 : 0).postProcess((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).emissiveLighting((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning( (BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true ) );
	public final static Block POWER_OBSERVER = new PowerObserverBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER).nonOpaque());

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("redbits", "inverted_redstone_torch"), INVERTED_REDSTONE_TORCH);
		Registry.register(Registry.BLOCK, new Identifier("redbits", "inverted_redstone_wall_torch"), INVERTED_REDSTONE_WALL_TORCH);
		Registry.register(Registry.ITEM, new Identifier("redbits", "inverted_redstone_torch"), new WallStandingBlockItem(INVERTED_REDSTONE_TORCH, INVERTED_REDSTONE_WALL_TORCH, (new Item.Settings()).group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "two_way_repeater"), TWO_WAY_REPEATER);
		Registry.register(Registry.ITEM, new Identifier("redbits", "two_way_repeater"), new BlockItem(TWO_WAY_REPEATER, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "t_flip_flop"), T_FLIP_FLOP);
		Registry.register(Registry.ITEM, new Identifier("redbits", "t_flip_flop"), new BlockItem(T_FLIP_FLOP, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "inverter"), INVERTER);
		Registry.register(Registry.ITEM, new Identifier("redbits", "inverter"), new BlockItem(INVERTER, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "detector"), DETECTOR);
		Registry.register(Registry.ITEM, new Identifier("redbits", "detector"), new BlockItem(DETECTOR, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "latch"), LATCH);
		Registry.register(Registry.ITEM, new Identifier("redbits", "latch"), new BlockItem(LATCH, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "emitter"), REDSTONE_EMITTER);
		Registry.register(Registry.ITEM, new Identifier("redbits", "emitter"), new BlockItem(REDSTONE_EMITTER, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "redstone_lamp"), REDSTONE_LAMP);
		Registry.register(Registry.ITEM, new Identifier("redbits", "redstone_lamp"), new BlockItem(REDSTONE_LAMP, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "oak_large_button"), OAK_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "oak_large_button"), new BlockItem(OAK_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "spruce_large_button"), SPRUCE_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "spruce_large_button"), new BlockItem(SPRUCE_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "birch_large_button"), BIRCH_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "birch_large_button"), new BlockItem(BIRCH_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "jungle_large_button"), JUNGLE_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "jungle_large_button"), new BlockItem(JUNGLE_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "acacia_large_button"), ACACIA_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "acacia_large_button"), new BlockItem(ACACIA_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "dark_oak_large_button"), DARK_OAK_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "dark_oak_large_button"), new BlockItem(DARK_OAK_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "crimson_large_button"), CRIMSON_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "crimson_large_button"), new BlockItem(CRIMSON_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "warped_large_button"), WARPED_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "warped_large_button"), new BlockItem(WARPED_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "stone_large_button"), STONE_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "stone_large_button"), new BlockItem(STONE_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "polished_blackstone_large_button"), POLISHED_BLACKSTONE_LARGE_BUTTON);
		Registry.register(Registry.ITEM, new Identifier("redbits", "polished_blackstone_large_button"), new BlockItem(POLISHED_BLACKSTONE_LARGE_BUTTON, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "obsidian_pressure_plate"), OBSIDIAN_PRESSURE_PLATE);
		Registry.register(Registry.ITEM, new Identifier("redbits", "obsidian_pressure_plate"), new BlockItem(OBSIDIAN_PRESSURE_PLATE, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "crying_obsidian_pressure_plate"), CRYING_OBSIDIAN_PRESSURE_PLATE);
		Registry.register(Registry.ITEM, new Identifier("redbits", "crying_obsidian_pressure_plate"), new BlockItem(CRYING_OBSIDIAN_PRESSURE_PLATE, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "end_stone_pressure_plate"), END_STONE_PRESSURE_PLATE);
		Registry.register(Registry.ITEM, new Identifier("redbits", "end_stone_pressure_plate"), new BlockItem(END_STONE_PRESSURE_PLATE, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "basalt_pressure_plate"), BASALT_PRESSURE_PLATE);
		Registry.register(Registry.ITEM, new Identifier("redbits", "basalt_pressure_plate"), new BlockItem(BASALT_PRESSURE_PLATE, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "rgb_lamp"), RGB_LAMP);
		Registry.register(Registry.ITEM, new Identifier("redbits", "rgb_lamp"), new BlockItem(RGB_LAMP, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("redbits", "power_observer"), POWER_OBSERVER);
		Registry.register(Registry.ITEM, new Identifier("redbits", "power_observer"), new BlockItem(POWER_OBSERVER, new Item.Settings().group(ItemGroup.REDSTONE)));
	}

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(INVERTER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(T_FLIP_FLOP, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(DETECTOR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(LATCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(TWO_WAY_REPEATER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(INVERTED_REDSTONE_TORCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(INVERTED_REDSTONE_WALL_TORCH, RenderLayer.getCutout());
		ColorProviderRegistry.ITEM.register( (stack, tintIndex) -> RedstoneWireBlock.getWireColor(1), REDSTONE_EMITTER );
		ColorProviderRegistry.BLOCK.register( (state, view, pos, tintIndex) -> RedstoneWireBlock.getWireColor( state.get( EmitterBlock.POWER ) ), REDSTONE_EMITTER );
		ColorProviderRegistry.ITEM.register( (stack, tintIndex) -> ColorProvider.getColor(0), RGB_LAMP );
		ColorProviderRegistry.BLOCK.register( (state, view, pos, tintIndex) -> ColorProvider.getColor(state.get(AnalogLampBlock.POWER)), RGB_LAMP );
	}
}
