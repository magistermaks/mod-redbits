package net.darktree.redbits;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.darktree.interference.LootInjector;
import net.darktree.redbits.blocks.*;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock.CollisionCondition;
import net.darktree.redbits.config.Settings;
import net.darktree.redbits.entity.EmitterMinecartEntity;
import net.darktree.redbits.item.ProxyBookItem;
import net.darktree.redbits.network.C2SLookAtPacket;
import net.darktree.redbits.utils.ParameterlessCriterion;
import net.darktree.redbits.utils.RecipeCriterion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.MinecartComparatorLogicRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RedBits implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("RedBits");
	public static final Settings CONFIG = AutoConfig.register(Settings.class, GsonConfigSerializer::new).getConfig();
	public static final Item.Settings SETTINGS = new Item.Settings().group(ItemGroup.REDSTONE);
	public static final String NAMESPACE = "redbits";

	private final static List<ItemStack> lamps = new ArrayList<>();
	private final static List<ItemStack> torches = new ArrayList<>();
	private final static List<ItemStack> carts = new ArrayList<>();
	private final static List<ItemStack> gates = new ArrayList<>();
	private final static List<ItemStack> cubes = new ArrayList<>();
	private final static List<ItemStack> plates = new ArrayList<>();
	private final static List<ItemStack> buttons = new ArrayList<>();

	private final static Predicate<Entity> CANT_AVOID_TRAPS = n -> !n.canAvoidTraps();
	public final static CollisionCondition COLLISION_CONDITION_PET = (world, box) -> world.getNonSpectatingEntities(TameableEntity.class, box).stream().anyMatch(n -> n.isTamed() && !n.canAvoidTraps());
	public final static CollisionCondition COLLISION_CONDITION_PLAYERS = (world, box) -> world.getNonSpectatingEntities(PlayerEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_HOSTILE = (world, box) -> world.getNonSpectatingEntities(HostileEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_VILLAGER = (world, box) -> world.getNonSpectatingEntities(VillagerEntity.class, box).stream().anyMatch(CANT_AVOID_TRAPS);

	// Sounds
	public static final SoundEvent DETECTOR_CLICK = registerSound("detector_click");
	public static final SoundEvent EMITTER_CLICK = registerSound("emitter_click");
	public static final SoundEvent FLIP_FLOP_CLICK = registerSound("flip_flop_click");
	public static final SoundEvent LATCH_CLICK = registerSound("latch_click");
	public static final SoundEvent TIMER_CLICK = registerSound("timer_click");

	// Buttons
	public final static Block OAK_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block SPRUCE_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block BIRCH_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block JUNGLE_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block ACACIA_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block DARK_OAK_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block CRIMSON_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block WARPED_LARGE_BUTTON = new LargeButtonBlock(true, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	public final static Block STONE_LARGE_BUTTON = new LargeButtonBlock(false, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F));
	public final static Block POLISHED_BLACKSTONE_LARGE_BUTTON = new LargeButtonBlock(false, AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F));

	// Gates
	public final static Block INVERTER = new InverterBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block T_FLIP_FLOP = new FlipFlopBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block DETECTOR = new DetectorBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block TWO_WAY_REPEATER = new TwoWayRepeaterBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block LATCH = new LatchBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block TIMER = new TimerBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block BRIDGE = new BridgeBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block PROJECTOR = new ProjectorBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));
	public final static Block JUNCTION = new JunctionBlock(AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.WOOD));

	// Pressure Plates
	public final static Block OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock(COLLISION_CONDITION_PLAYERS, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F));
	public final static Block CRYING_OBSIDIAN_PRESSURE_PLATE = new ComplexPressurePlateBlock(COLLISION_CONDITION_HOSTILE, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F));
	public final static Block END_STONE_PRESSURE_PLATE = new ComplexPressurePlateBlock(COLLISION_CONDITION_VILLAGER, AbstractBlock.Settings.of(Material.STONE, MapColor.PALE_YELLOW).requiresTool().noCollision().strength(0.5F));
	public final static Block BASALT_PRESSURE_PLATE = new ComplexPressurePlateBlock(COLLISION_CONDITION_PET, AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().noCollision().strength(0.5F));

	// Other Components
	public final static Block REDSTONE_LAMP = new RedstoneLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).luminance(n -> n.get(Properties.LIT) ? 1 : 0).postProcess((a, b, c) -> a.get(Properties.LIT)).emissiveLighting((a, b, c) -> a.get(Properties.LIT)).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning((BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true));
	public final static Block RGB_LAMP = new AnalogLampBlock(FabricBlockSettings.of(Material.REDSTONE_LAMP).luminance(n -> n.get(AnalogLampBlock.POWER) > 0 ? 1 : 0).postProcess((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).emissiveLighting((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).strength(0.3F).sounds(BlockSoundGroup.GLASS).allowsSpawning((BlockState state, BlockView world, BlockPos pos, EntityType<?> type) -> true));
	public final static Block REDSTONE_EMITTER = new EmitterBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).solidBlock((BlockState state, BlockView world, BlockPos pos) -> true));
	public final static Block VISION_SENSOR = new VisionSensorBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.5F).solidBlock((BlockState state, BlockView world, BlockPos pos) -> true));
	public final static Block INVERTED_REDSTONE_TORCH = new InvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance((n) -> n.get(Properties.LIT) ? 7 : 0).sounds(BlockSoundGroup.WOOD));
	public final static Block INVERTED_REDSTONE_WALL_TORCH = new WallInvertedRedstoneTorchBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().breakInstantly().luminance((n) -> n.get(Properties.LIT) ? 7 : 0).sounds(BlockSoundGroup.WOOD));
	public final static Item EMITTER_MINECART_ITEM = new MinecartItem(EmitterMinecartEntity.EMITTER, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION));
	public final static Item GUIDE = ProxyBookItem.createInstance();

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

	// Advancements
	public static final ParameterlessCriterion LOOK_AT_SENSOR_CRITERION = Criteria.register(new ParameterlessCriterion(new Identifier(NAMESPACE, "look_at_sensor")));
	public static final ParameterlessCriterion USE_REDSTONE_EMITTER_CRITERION = Criteria.register(new ParameterlessCriterion(new Identifier(NAMESPACE, "use_redstone_emitter")));
	public static final RecipeCriterion RECIPE_CRAFTED_CRITERION = Criteria.register(new RecipeCriterion(new Identifier(NAMESPACE, "recipe_crafted")));

	@Override
	public void onInitialize() {
		registerBlock("inverted_redstone_torch", INVERTED_REDSTONE_TORCH);
		registerBlock("inverted_redstone_wall_torch", INVERTED_REDSTONE_WALL_TORCH);
		registerItem("inverted_redstone_torch", new WallStandingBlockItem(INVERTED_REDSTONE_TORCH, INVERTED_REDSTONE_WALL_TORCH, SETTINGS), torches);
		registerItem("emitter_minecart", EMITTER_MINECART_ITEM, carts);

		register("two_way_repeater", TWO_WAY_REPEATER, gates);
		register("t_flip_flop", T_FLIP_FLOP, gates);
		register("inverter", INVERTER, gates);
		register("detector", DETECTOR, gates);
		register("latch", LATCH, gates);
		register("timer", TIMER, gates);
		register("bridge", BRIDGE, gates);
		register("projector", PROJECTOR, gates);
		register("junction", JUNCTION, gates);

		register("emitter", REDSTONE_EMITTER, cubes);
		register("vision_sensor", VISION_SENSOR, cubes);

		register("oak_large_button", OAK_LARGE_BUTTON, buttons);
		register("spruce_large_button", SPRUCE_LARGE_BUTTON, buttons);
		register("birch_large_button", BIRCH_LARGE_BUTTON, buttons);
		register("jungle_large_button", JUNGLE_LARGE_BUTTON, buttons);
		register("acacia_large_button", ACACIA_LARGE_BUTTON, buttons);
		register("dark_oak_large_button", DARK_OAK_LARGE_BUTTON, buttons);
		register("crimson_large_button", CRIMSON_LARGE_BUTTON, buttons);
		register("warped_large_button", WARPED_LARGE_BUTTON, buttons);
		register("stone_large_button", STONE_LARGE_BUTTON, buttons);
		register("polished_blackstone_large_button", POLISHED_BLACKSTONE_LARGE_BUTTON, buttons);

		register("obsidian_pressure_plate", OBSIDIAN_PRESSURE_PLATE, plates);
		register("crying_obsidian_pressure_plate", CRYING_OBSIDIAN_PRESSURE_PLATE, plates);
		register("end_stone_pressure_plate", END_STONE_PRESSURE_PLATE, plates);
		register("basalt_pressure_plate", BASALT_PRESSURE_PLATE, plates);

		register("redstone_lamp", REDSTONE_LAMP, lamps);
		register("rgb_lamp", RGB_LAMP, lamps);

		// Register the guide item
		Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "guide"), GUIDE);

		// Register statistics
		registerStat(INTERACT_WITH_SIGHT_SENSOR);
		registerStat(INTERACT_WITH_REDSTONE_EMITTER);

		// Register custom minecart
		Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, "emitter_minecart"), EMITTER_MINECART);
		MinecartComparatorLogicRegistry.register(EMITTER_MINECART, (minecart, state, pos) -> minecart.getPower());

		// Network
		LOOK_AT_PACKET.register();

		// Check is Patchouli is present in the mod list
		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			initializePatchouliCompatibility();
		}
	}

	private void initializePatchouliCompatibility() {
		ItemStack stack = new ItemStack(GUIDE, 1);

		if (CONFIG.add_guide_to_loot_tables) {
			LOGGER.info("Adding RedBits Patchouli guide book to loot tables...");

			LootInjector.injectEntry(LootTables.STRONGHOLD_LIBRARY_CHEST, stack, 40);
			LootInjector.injectEntry(LootTables.SPAWN_BONUS_CHEST, stack, 90);
			LootInjector.injectEntry(LootTables.VILLAGE_CARTOGRAPHER_CHEST, stack, 35);
		}
	}

	private void registerBlock(String name, Block block) {
		Registry.register(Registry.BLOCK, new Identifier(NAMESPACE, name), block);
	}

	private void registerItem(String name, Item item, List<ItemStack> group) {
		group.add(new ItemStack(item));
		Registry.register(Registry.ITEM, new Identifier(NAMESPACE, name), item);
	}

	private void register(String name, Block block, List<ItemStack> group) {
		registerBlock(name, block);
		registerItem(name, new BlockItem(block, SETTINGS), group);
	}

	private void registerStat(Identifier id) {
		Registry.register(Registry.CUSTOM_STAT, id, id);
		Stats.CUSTOM.getOrCreateStat(id, StatFormatter.DEFAULT);
	}

	private static SoundEvent registerSound(String id){
		Identifier identifier = new Identifier(RedBits.NAMESPACE, id);
		SoundEvent sound = new SoundEvent(identifier);
		Registry.register(Registry.SOUND_EVENT, identifier, sound);
		return sound;
	}

}
