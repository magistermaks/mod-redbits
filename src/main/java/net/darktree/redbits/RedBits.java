package net.darktree.redbits;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.darktree.redbits.blocks.*;
import net.darktree.redbits.blocks.ComplexPressurePlateBlock.CollisionCondition;
import net.darktree.redbits.blocks.custom.*;
import net.darktree.redbits.blocks.gate.*;
import net.darktree.redbits.config.Settings;
import net.darktree.redbits.entity.EmitterMinecartEntity;
import net.darktree.redbits.item.ProxyBookItem;
import net.darktree.redbits.network.C2SLookAtPacket;
import net.darktree.redbits.utils.ParameterlessCriterion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.MinecartComparatorLogicRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RedBits implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("RedBits");
	public static final Settings CONFIG = AutoConfig.register(Settings.class, GsonConfigSerializer::new).getConfig();
	public static final Item.Settings SETTINGS = new Item.Settings();
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

	private static Function<AbstractBlock.Settings, Block> getButtonFactory(BlockSetType type) {
		return setting -> new LargeButtonBlock(type, setting.noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY));
	}

	private static Function<AbstractBlock.Settings, Block> getGateFactory(Function<AbstractBlock.Settings, Block> factory) {
		return setting -> factory.apply(setting.breakInstantly().sounds(BlockSoundGroup.WOOD).pistonBehavior(PistonBehavior.DESTROY));
	}

	private static Function<AbstractBlock.Settings, Block> getPressurePlateFactory(ComplexPressurePlateBlock.CollisionCondition condition, MapColor color) {
		return setting -> new ComplexPressurePlateBlock(condition, setting.sounds(BlockSoundGroup.STONE).solid().requiresTool().noCollision().strength(0.5f).mapColor(MapColor.BLACK));
	}

	// Buttons
	public final static Block OAK_LARGE_BUTTON = registerBlock("oak_large_button", getButtonFactory(BlockSetType.OAK));
	public final static Block SPRUCE_LARGE_BUTTON = registerBlock("spruce_large_button", getButtonFactory(BlockSetType.SPRUCE));
	public final static Block BIRCH_LARGE_BUTTON = registerBlock("birch_large_button", getButtonFactory(BlockSetType.BIRCH));
	public final static Block JUNGLE_LARGE_BUTTON = registerBlock("jungle_large_button", getButtonFactory(BlockSetType.JUNGLE));
	public final static Block ACACIA_LARGE_BUTTON = registerBlock("acacia_large_button", getButtonFactory(BlockSetType.ACACIA));
	public final static Block DARK_OAK_LARGE_BUTTON = registerBlock("dark_oak_large_button", getButtonFactory(BlockSetType.DARK_OAK));
	public final static Block MANGROVE_LARGE_BUTTON = registerBlock("mangrove_large_button", getButtonFactory(BlockSetType.MANGROVE));
	public final static Block CHERRY_LARGE_BUTTON = registerBlock("cherry_large_button", getButtonFactory(BlockSetType.CHERRY));
	public final static Block BAMBOO_LARGE_BUTTON = registerBlock("bamboo_large_button", getButtonFactory(BlockSetType.BAMBOO));
	public final static Block CRIMSON_LARGE_BUTTON = registerBlock("crimson_large_button", getButtonFactory(BlockSetType.CRIMSON));
	public final static Block WARPED_LARGE_BUTTON = registerBlock("warped_large_button", getButtonFactory(BlockSetType.WARPED));
	public final static Block STONE_LARGE_BUTTON = registerBlock("stone_large_button", getButtonFactory(BlockSetType.STONE));
	public final static Block POLISHED_BLACKSTONE_LARGE_BUTTON = registerBlock("polished_blackstone_large_button", getButtonFactory(BlockSetType.POLISHED_BLACKSTONE));

	// Gates
	public final static Block INVERTER = registerBlock("inverter", getGateFactory(InverterBlock::new));
	public final static Block T_FLIP_FLOP = registerBlock("t_flip_flop", getGateFactory(FlipFlopBlock::new));
	public final static Block DETECTOR = registerBlock("detector", getGateFactory(DetectorBlock::new));
	public final static Block TWO_WAY_REPEATER = registerBlock("two_way_repeater", getGateFactory(TwoWayRepeaterBlock::new));
	public final static Block LATCH = registerBlock("latch", getGateFactory(LatchBlock::new));
	public final static Block TIMER = registerBlock("timer", getGateFactory(TimerBlock::new));
	public final static Block BRIDGE = registerBlock("bridge", getGateFactory(BridgeBlock::new));
	public final static Block PROJECTOR = registerBlock("projector", getGateFactory(ProjectorBlock::new));
	public final static Block JUNCTION = registerBlock("junction", getGateFactory(JunctionBlock::new));

	// Pressure Plates
	public final static Block OBSIDIAN_PRESSURE_PLATE = registerBlock("obsidian_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_PLAYERS, MapColor.BLACK));
	public final static Block CRYING_OBSIDIAN_PRESSURE_PLATE = registerBlock("crying_obsidian_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_HOSTILE, MapColor.BLACK));
	public final static Block END_STONE_PRESSURE_PLATE = registerBlock("end_stone_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_VILLAGER, MapColor.PALE_YELLOW));
	public final static Block BASALT_PRESSURE_PLATE = registerBlock("basalt_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_PET, MapColor.BLACK));

	public static final EntityType<EmitterMinecartEntity> EMITTER_MINECART = EntityType.Builder.create(EmitterMinecartEntity::new, SpawnGroup.MISC)
			.dimensions(0.98F, 0.7F)
			.passengerAttachments(0.1875F)
			.maxTrackingRange(8)
			.build("emitter_minecart");

	// Other Components
	public final static Block REDSTONE_LAMP = registerBlock("redstone_lamp", settings -> new RedstoneLampBlock(settings.luminance(n -> n.get(Properties.LIT) ? 1 : 0).postProcess((a, b, c) -> a.get(Properties.LIT)).emissiveLighting((a, b, c) -> a.get(Properties.LIT)).strength(0.3f).sounds(BlockSoundGroup.GLASS).allowsSpawning(Blocks::always)));
	public final static Block RGB_LAMP = registerBlock("rgb_lamp", settings -> new AnalogLampBlock(settings.luminance(n -> n.get(AnalogLampBlock.POWER) > 0 ? 1 : 0).postProcess((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).emissiveLighting((a, b, c) -> a.get(AnalogLampBlock.POWER) > 0).strength(0.3F).strength(0.3f).sounds(BlockSoundGroup.GLASS).allowsSpawning(Blocks::always)));
	public final static Block REDSTONE_EMITTER = registerBlock("emitter", settings -> new EmitterBlock(settings.requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE).solid()));
	public final static Block VISION_SENSOR = registerBlock("vision_sensor", settings -> new VisionSensorBlock(settings.requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE).solid()));
	public final static Block INVERTED_REDSTONE_TORCH = registerBlock("inverted_redstone_torch", settings -> new InvertedRedstoneTorchBlock(settings.pistonBehavior(PistonBehavior.DESTROY).noCollision().breakInstantly().luminance(n -> n.get(Properties.LIT) ? 7 : 0).sounds(BlockSoundGroup.WOOD)));
	public final static Block INVERTED_REDSTONE_WALL_TORCH = registerBlock("inverted_redstone_wall_torch", settings -> new WallInvertedRedstoneTorchBlock(settings.pistonBehavior(PistonBehavior.DESTROY).noCollision().breakInstantly().luminance(n -> n.get(Properties.LIT) ? 7 : 0).sounds(BlockSoundGroup.WOOD)));
	public final static Item EMITTER_MINECART_ITEM = registerItem("emitter_minecart", settings -> new MinecartItem(EmitterMinecartEntity.TYPE, settings.maxCount(1)));
	public final static Item GUIDE = ProxyBookItem.createInstance();

	// Statistics
	public static final Identifier INTERACT_WITH_SIGHT_SENSOR = Identifier.of(NAMESPACE, "interact_with_sight_sensor");
	public static final Identifier INTERACT_WITH_REDSTONE_EMITTER = Identifier.of(NAMESPACE, "interact_with_redstone_emitter");

	// Network
	public static final C2SLookAtPacket LOOK_AT_PACKET = new C2SLookAtPacket();

	// Advancements
	public static final ParameterlessCriterion LOOK_AT_SENSOR_CRITERION = Criteria.register("redbits:look_at_sensor", new ParameterlessCriterion());
	public static final ParameterlessCriterion USE_REDSTONE_EMITTER_CRITERION = Criteria.register("redbits:use_redstone_emitter", new ParameterlessCriterion());

	@Override
	public void onInitialize() {
		torches.add(new ItemStack(registerItem("inverted_redstone_torch", settings -> new VerticallyAttachableBlockItem(INVERTED_REDSTONE_TORCH, INVERTED_REDSTONE_WALL_TORCH, settings, Direction.DOWN))));
		carts.add(new ItemStack(EMITTER_MINECART_ITEM));

		registerItem("two_way_repeater", TWO_WAY_REPEATER, gates);
		registerItem("t_flip_flop", T_FLIP_FLOP, gates);
		registerItem("inverter", INVERTER, gates);
		registerItem("detector", DETECTOR, gates);
		registerItem("latch", LATCH, gates);
		registerItem("timer", TIMER, gates);
		registerItem("bridge", BRIDGE, gates);
		registerItem("projector", PROJECTOR, gates);
		registerItem("junction", JUNCTION, gates);

		registerItem("emitter", REDSTONE_EMITTER, cubes);
		registerItem("vision_sensor", VISION_SENSOR, cubes);

		registerItem("oak_large_button", OAK_LARGE_BUTTON, buttons);
		registerItem("spruce_large_button", SPRUCE_LARGE_BUTTON, buttons);
		registerItem("birch_large_button", BIRCH_LARGE_BUTTON, buttons);
		registerItem("jungle_large_button", JUNGLE_LARGE_BUTTON, buttons);
		registerItem("acacia_large_button", ACACIA_LARGE_BUTTON, buttons);
		registerItem("dark_oak_large_button", DARK_OAK_LARGE_BUTTON, buttons);
		registerItem("mangrove_large_button", MANGROVE_LARGE_BUTTON, buttons);
		registerItem("cherry_large_button", CHERRY_LARGE_BUTTON, buttons);
		registerItem("bamboo_large_button", BAMBOO_LARGE_BUTTON, buttons);
		registerItem("crimson_large_button", CRIMSON_LARGE_BUTTON, buttons);
		registerItem("warped_large_button", WARPED_LARGE_BUTTON, buttons);
		registerItem("stone_large_button", STONE_LARGE_BUTTON, buttons);
		registerItem("polished_blackstone_large_button", POLISHED_BLACKSTONE_LARGE_BUTTON, buttons);

		registerItem("obsidian_pressure_plate", OBSIDIAN_PRESSURE_PLATE, plates);
		registerItem("crying_obsidian_pressure_plate", CRYING_OBSIDIAN_PRESSURE_PLATE, plates);
		registerItem("end_stone_pressure_plate", END_STONE_PRESSURE_PLATE, plates);
		registerItem("basalt_pressure_plate", BASALT_PRESSURE_PLATE, plates);

		registerItem("redstone_lamp", REDSTONE_LAMP, lamps);
		registerItem("rgb_lamp", RGB_LAMP, lamps);

		// Register the guide item
		Registry.register(Registries.ITEM, Identifier.of(NAMESPACE, "guide"), GUIDE);

		// Register statistics
		registerStat(INTERACT_WITH_SIGHT_SENSOR);
		registerStat(INTERACT_WITH_REDSTONE_EMITTER);

		// Register custom minecart
		Registry.register(Registries.ENTITY_TYPE, Identifier.of(NAMESPACE, "emitter_minecart"), EMITTER_MINECART);
		MinecartComparatorLogicRegistry.register(EMITTER_MINECART, (minecart, state, pos) -> minecart.getPower());

		// Network
		LOOK_AT_PACKET.register();

		initializePatchouliCompatibility();
		appendItemsToGroup();
	}

	private void initializePatchouliCompatibility() {

		if (CONFIG.add_guide_to_loot_tables) {
			LOGGER.info("Adding RedBits Patchouli guide book to loot tables...");

			List<RegistryKey<LootTable>> tables = List.of(
					LootTables.STRONGHOLD_LIBRARY_CHEST,
					LootTables.SPAWN_BONUS_CHEST,
					LootTables.VILLAGE_CARTOGRAPHER_CHEST
			);

			LootTableEvents.MODIFY.register((key, builder, source, registries) -> {

				if (!source.isBuiltin()) {
					return;
				}

				if (tables.stream().anyMatch(table -> table.equals(key))) {
					LootPool.Builder pool = LootPool.builder()
							.with(ItemEntry.builder(GUIDE));

					builder.pool(pool);
				}
			});
		}

		if (CONFIG.add_guide_to_creative_menu) {
			LOGGER.info("Adding RedBits Patchouli guide book to creative menu...");

			ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
				content.add(new ItemStack(GUIDE, 1));
			});
		}
	}

	private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> factory) {
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(RedBits.NAMESPACE, name));
		AbstractBlock.Settings settings = AbstractBlock.Settings.create();
		return Registry.register(Registries.BLOCK, key, factory.apply(settings));
	}

	private static Item registerItem(String name, Function<Item.Settings, Item> factory) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(RedBits.NAMESPACE, name));
		Item.Settings settings = new Item.Settings();
		return Registry.register(Registries.ITEM, key, factory.apply(settings));
	}

	private void registerItem(String name, Item item, List<ItemStack> group) {
		group.add(new ItemStack(item));
		Registry.register(Registries.ITEM, Identifier.of(NAMESPACE, name), item);
	}

	private void registerItem(String name, Block block, List<ItemStack> group) {
		group.add(new ItemStack(registerItem(name, settings -> new BlockItem(block, settings))));
	}

	private void registerStat(Identifier id) {
		Registry.register(Registries.CUSTOM_STAT, id, id);
		Stats.CUSTOM.getOrCreateStat(id, StatFormatter.DEFAULT);
	}

	public static void appendItemsToGroup() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(Items.COMPARATOR, gates);
			content.addAfter(Items.TNT_MINECART, carts);
			content.addAfter(Items.REDSTONE_LAMP, lamps);
			content.addAfter(Items.TARGET, cubes);
			content.addAfter(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, plates);
			content.addAfter(Items.REDSTONE_TORCH, torches);

			content.addAll(buttons);
		});
	}

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of(RedBits.NAMESPACE, id);
		SoundEvent sound = SoundEvent.of(identifier);
		Registry.register(Registries.SOUND_EVENT, identifier, sound);
		return sound;
	}

}
