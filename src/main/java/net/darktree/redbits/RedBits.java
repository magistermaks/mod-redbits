package net.darktree.redbits;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.darktree.interference.LootInjector;
import net.darktree.interference.MessageInjector;
import net.darktree.redbits.blocks.*;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock.CollisionCondition;
import net.darktree.redbits.config.Settings;
import net.darktree.redbits.entity.EmitterMinecartEntity;
import net.darktree.redbits.network.C2SLookAtPacket;
import net.darktree.redbits.utils.ColorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.MinecartComparatorLogicRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.common.item.PatchouliItems;

import java.util.function.Predicate;

public class RedBits implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("RedBits");
	public static final Settings CONFIG = AutoConfig.register(Settings.class, GsonConfigSerializer::new).getConfig();
	public static final Item.Settings SETTINGS = new Item.Settings().group(ItemGroup.REDSTONE);
	public static final String NAMESPACE = "redbits";

	private final static Predicate<Entity> CANT_AVOID_TRAPS = n -> !n.canAvoidTraps();
	public final static CollisionCondition COLLISION_CONDITION_PET = ( World world, Box box ) -> world.getNonSpectatingEntities(TameableEntity.class, box).stream().anyMatch(n -> n.isTamed() && !n.canAvoidTraps());
	public final static CollisionCondition COLLISION_CONDITION_PLAYERS = ( World world, Box box ) -> world.getNonSpectatingEntities(PlayerEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_HOSTILE = ( World world, Box box ) -> world.getNonSpectatingEntities(HostileEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_VILLAGER = ( World world, Box box ) -> world.getNonSpectatingEntities(VillagerEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);

	// Buttons
	public final static Block OAK_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block SPRUCE_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block BIRCH_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block JUNGLE_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block ACACIA_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block DARK_OAK_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block CRIMSON_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block WARPED_LARGE_BUTTON = new LargeButtonBlock( true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block STONE_LARGE_BUTTON = new LargeButtonBlock( false, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F));
	public final static Block POLISHED_BLACKSTONE_LARGE_BUTTON = new LargeButtonBlock( false, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F));

	// Gates
	public final static Block INVERTER = new InverterBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block T_FLIP_FLOP = new FlipFlopBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block DETECTOR = new DetectorBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block TWO_WAY_REPEATER = new TwoWayRepeaterBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block LATCH = new LatchBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block TIMER = new TimerBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));

	// Pressure Plates
	public final static Block OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_PLAYERS, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F) );
	public final static Block CRYING_OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_HOSTILE, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F) );
	public final static Block END_STONE_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_VILLAGER, AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().noCollision().strength(0.5F) );
	public final static Block BASALT_PRESSURE_PLATE = new ComplexPressurePlateBlock( COLLISION_CONDITION_PET, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F) );

	// Other Components
	public final static Block REDSTONE_LAMP = new RedstoneLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).lightLevel((n) -> n.get(Properties.LIT) ? 1 : 0).postProcess((a, b, c) -> a.get(Properties.LIT)).emissiveLighting((a, b, c) -> a.get(Properties.LIT)).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning( (BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true ) );
	public final static Block RGB_LAMP = new AnalogLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).lightLevel((n) -> n.get(AnalogLampBlock.POWER) > 0 ? 1 : 0).postProcess((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).emissiveLighting((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning( (BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true ) );
	public final static Block REDSTONE_EMITTER = new EmitterBlock( AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).solidBlock( (BlockState state, BlockView world, BlockPos pos) -> true ) );
	public final static Block VISION_SENSOR = new VisionSensorBlock( AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).solidBlock( (BlockState state, BlockView world, BlockPos pos) -> true ) );
	public final static Block INVERTED_REDSTONE_TORCH = new InvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance( (n) -> n.get(Properties.LIT) ? 7 : 0 ).sounds(BlockSoundGroup.WOOD));
	public final static Block INVERTED_REDSTONE_WALL_TORCH = new WallInvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance( (n) -> n.get(Properties.LIT) ? 7 : 0 ).sounds(BlockSoundGroup.WOOD));
	public final static Item EMITTER_MINECART_ITEM = new MinecartItem(EmitterMinecartEntity.EMITTER, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION));

	// Statistics
	public static final Identifier INTERACT_WITH_SIGHT_SENSOR = new Identifier(NAMESPACE, "interact_with_sight_sensor");
	public static final Identifier INTERACT_WITH_REDSTONE_EMITTER = new Identifier(NAMESPACE, "interact_with_redstone_emitter");

	// Entities, Java why can't I do it normally? Generics are a pain worse than templates
	public static final EntityType<EmitterMinecartEntity> EMITTER_MINECART = (EntityType<EmitterMinecartEntity>) (Object) FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, EmitterMinecartEntity::new)
			.dimensions(EntityDimensions.fixed(0.98f, 0.7f))
			.build();

	// Network
	public static final C2SLookAtPacket LOOK_AT_PACKET = new C2SLookAtPacket(new Identifier(NAMESPACE, "look_at"));

	@Override
	public void onInitialize() {
		registerBlock("inverted_redstone_torch", INVERTED_REDSTONE_TORCH);
		registerBlock("inverted_redstone_wall_torch", INVERTED_REDSTONE_WALL_TORCH);
		registerItem("inverted_redstone_torch", new WallStandingBlockItem(INVERTED_REDSTONE_TORCH, INVERTED_REDSTONE_WALL_TORCH, SETTINGS));
		registerItem("emitter_minecart", EMITTER_MINECART_ITEM);
		register("two_way_repeater", TWO_WAY_REPEATER);
		register("t_flip_flop", T_FLIP_FLOP);
		register("inverter", INVERTER);
		register("detector", DETECTOR);
		register("latch", LATCH);
		register("emitter", REDSTONE_EMITTER);
		register("redstone_lamp", REDSTONE_LAMP);
		register("oak_large_button", OAK_LARGE_BUTTON);
		register("spruce_large_button", SPRUCE_LARGE_BUTTON);
		register("birch_large_button", BIRCH_LARGE_BUTTON);
		register("jungle_large_button", JUNGLE_LARGE_BUTTON);
		register("acacia_large_button", ACACIA_LARGE_BUTTON);
		register("dark_oak_large_button", DARK_OAK_LARGE_BUTTON);
		register("crimson_large_button", CRIMSON_LARGE_BUTTON);
		register("warped_large_button", WARPED_LARGE_BUTTON);
		register("stone_large_button", STONE_LARGE_BUTTON);
		register("polished_blackstone_large_button", POLISHED_BLACKSTONE_LARGE_BUTTON);
		register("obsidian_pressure_plate", OBSIDIAN_PRESSURE_PLATE);
		register("crying_obsidian_pressure_plate", CRYING_OBSIDIAN_PRESSURE_PLATE);
		register("end_stone_pressure_plate", END_STONE_PRESSURE_PLATE);
		register("basalt_pressure_plate", BASALT_PRESSURE_PLATE);
		register("rgb_lamp", RGB_LAMP);
		register("timer", TIMER);
		register("vision_sensor", VISION_SENSOR);

		// Register statistics
		registerStat(INTERACT_WITH_SIGHT_SENSOR);
		registerStat(INTERACT_WITH_REDSTONE_EMITTER);

		// Register custom minecart
		Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, "emitter_minecart"), EMITTER_MINECART);
		MinecartComparatorLogicRegistry.register(EMITTER_MINECART, (minecart, state, pos) -> minecart.getPower());

		// Network
		LOOK_AT_PACKET.register();

		// Check is Patchouli is present in the mod list
		if( FabricLoader.getInstance().isModLoaded("patchouli") ) {
			initializePatchouliCompatibility();
		}
	}

	private void initializePatchouliCompatibility() {
		if(CONFIG.add_guide_to_loot_tables) {
			LOGGER.info("RedBits detected Patchouli! Adding guide book to loot tables...");

			NbtCompound tag = new NbtCompound();
			tag.putString("patchouli:book", "redbits:guide");
			ItemStack stack = new ItemStack(PatchouliItems.BOOK);
			stack.setNbt(tag);

			LootInjector.injectEntry(LootTables.STRONGHOLD_LIBRARY_CHEST, stack, 30);
			LootInjector.injectEntry(LootTables.SPAWN_BONUS_CHEST, stack, 80);
			LootInjector.injectEntry(LootTables.VILLAGE_CARTOGRAPHER_CHEST, stack, 30);
		}else{
			LOGGER.warn("RedBits detected Patchouli, but loot table extensions where disabled! Skipping!");
		}
	}

	private void registerBlock( String name, Block block ) {
		Registry.register(Registry.BLOCK, new Identifier( NAMESPACE, name ), block);
	}

	private void registerItem( String name, Item item ) {
		Registry.register(Registry.ITEM, new Identifier( NAMESPACE, name ), item);
	}

	private void register( String name, Block block ) {
		registerBlock( name, block );
		registerItem( name, new BlockItem( block, SETTINGS ) );
	}

	private void registerStat( Identifier id ) {
		Registry.register(Registry.CUSTOM_STAT, id, id);
		Stats.CUSTOM.getOrCreateStat(id, StatFormatter.DEFAULT);
	}

}
