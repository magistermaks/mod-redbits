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
import net.darktree.redbits.utils.PatchouliProxy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.MinecartComparatorLogicRegistry;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RedBits implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("RedBits");
	public static final Settings CONFIG = AutoConfig.register(Settings.class, GsonConfigSerializer::new).getConfig();
	public static final String NAMESPACE = "redbits";

	private final static List<Item> lamps = new ArrayList<>();
	private final static List<Item> torches = new ArrayList<>();
	private final static List<Item> carts = new ArrayList<>();
	private final static List<Item> gates = new ArrayList<>();
	private final static List<Item> cubes = new ArrayList<>();
	private final static List<Item> plates = new ArrayList<>();
	private final static List<Item> buttons = new ArrayList<>();

	private final static Predicate<Entity> CANT_AVOID_TRAPS = n -> !n.isIgnoringBlockTriggers();
	public final static CollisionCondition COLLISION_CONDITION_PET = (world, box) -> world.getEntitiesOfClass(TamableAnimal.class, box).stream().anyMatch(n -> n.isTame() && !n.isIgnoringBlockTriggers());
	public final static CollisionCondition COLLISION_CONDITION_PLAYERS = (world, box) -> world.getEntitiesOfClass(Player.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_HOSTILE = (world, box) -> world.getEntitiesOfClass(Monster.class, box).stream().anyMatch(CANT_AVOID_TRAPS);
	public final static CollisionCondition COLLISION_CONDITION_VILLAGER = (world, box) -> world.getEntitiesOfClass(Villager.class, box).stream().anyMatch(CANT_AVOID_TRAPS);

	// Sounds
	public static final SoundEvent DETECTOR_CLICK = registerSound("detector_click");
	public static final SoundEvent EMITTER_CLICK = registerSound("emitter_click");
	public static final SoundEvent FLIP_FLOP_CLICK = registerSound("flip_flop_click");
	public static final SoundEvent LATCH_CLICK = registerSound("latch_click");
	public static final SoundEvent TIMER_CLICK = registerSound("timer_click");

	private static Function<BlockBehaviour.Properties, Block> getButtonFactory(BlockSetType type) {
		return setting -> new LargeButtonBlock(type, setting.noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
	}

	private static Function<BlockBehaviour.Properties, Block> getGateFactory(Function<BlockBehaviour.Properties, Block> factory) {
		return setting -> factory.apply(setting.instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
	}

	private static Function<BlockBehaviour.Properties, Block> getPressurePlateFactory(ComplexPressurePlateBlock.CollisionCondition condition, MapColor color) {
		return setting -> new ComplexPressurePlateBlock(condition, setting.sound(SoundType.STONE).forceSolidOn().requiresCorrectToolForDrops().noCollision().strength(0.5f).mapColor(MapColor.COLOR_BLACK));
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
	public final static Block PALE_OAK_LARGE_BUTTON = registerBlock("pale_oak_large_button", getButtonFactory(BlockSetType.PALE_OAK));
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
	public final static Block OBSIDIAN_PRESSURE_PLATE = registerBlock("obsidian_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_PLAYERS, MapColor.COLOR_BLACK));
	public final static Block CRYING_OBSIDIAN_PRESSURE_PLATE = registerBlock("crying_obsidian_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_HOSTILE, MapColor.COLOR_BLACK));
	public final static Block END_STONE_PRESSURE_PLATE = registerBlock("end_stone_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_VILLAGER, MapColor.SAND));
	public final static Block BASALT_PRESSURE_PLATE = registerBlock("basalt_pressure_plate", getPressurePlateFactory(COLLISION_CONDITION_PET, MapColor.COLOR_BLACK));

	public static final EntityType<EmitterMinecartEntity> EMITTER_MINECART = EntityType.Builder.of(EmitterMinecartEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.98F, 0.7F)
			.passengerAttachments(0.1875F)
			.clientTrackingRange(8)
			.build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(NAMESPACE, "emitter_minecart")));

	// Other Components
	public final static Block REDSTONE_LAMP = registerBlock("redstone_lamp", settings -> new RedstoneLampBlock(settings.lightLevel(n -> n.getValue(BlockStateProperties.LIT) ? 1 : 0).emissiveRendering((a, b, c) -> a.getValue(BlockStateProperties.LIT)).strength(0.3f).sound(SoundType.GLASS).isValidSpawn(Blocks::always)));
	public final static Block RGB_LAMP = registerBlock("rgb_lamp", settings -> new AnalogLampBlock(settings.lightLevel(n -> n.getValue(AnalogLampBlock.POWER) > 0 ? 1 : 0).emissiveRendering((a, b, c) -> a.getValue(AnalogLampBlock.POWER) > 0).strength(0.3F).strength(0.3f).sound(SoundType.GLASS).isValidSpawn(Blocks::always)));
	public final static Block REDSTONE_EMITTER = registerBlock("emitter", settings -> new EmitterBlock(settings.requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE).forceSolidOn()));
	public final static Block VISION_SENSOR = registerBlock("vision_sensor", settings -> new VisionSensorBlock(settings.requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE).forceSolidOn()));
	public final static Block INVERTED_REDSTONE_TORCH = registerBlock("inverted_redstone_torch", settings -> new InvertedRedstoneTorchBlock(settings.pushReaction(PushReaction.DESTROY).noCollision().instabreak().lightLevel(n -> n.getValue(BlockStateProperties.LIT) ? 7 : 0).sound(SoundType.WOOD)));
	public final static Block INVERTED_REDSTONE_WALL_TORCH = registerBlock("inverted_redstone_wall_torch", settings -> new WallInvertedRedstoneTorchBlock(settings.pushReaction(PushReaction.DESTROY).noCollision().instabreak().lightLevel(n -> n.getValue(BlockStateProperties.LIT) ? 7 : 0).sound(SoundType.WOOD)));
	public final static Item EMITTER_MINECART_ITEM = registerItem("emitter_minecart", settings -> new MinecartItem(EMITTER_MINECART, settings.stacksTo(1)));
	public final static Item GUIDE = ProxyBookItem.createInstance();

	// Statistics
	public static final Identifier INTERACT_WITH_SIGHT_SENSOR = Identifier.fromNamespaceAndPath(NAMESPACE, "interact_with_sight_sensor");
	public static final Identifier INTERACT_WITH_REDSTONE_EMITTER = Identifier.fromNamespaceAndPath(NAMESPACE, "interact_with_redstone_emitter");

	// Network
	public static final C2SLookAtPacket LOOK_AT_PACKET = new C2SLookAtPacket();

	// Advancements
	public static final ParameterlessCriterion LOOK_AT_SENSOR_CRITERION = CriteriaTriggers.register("redbits:look_at_sensor", new ParameterlessCriterion());
	public static final ParameterlessCriterion USE_REDSTONE_EMITTER_CRITERION = CriteriaTriggers.register("redbits:use_redstone_emitter", new ParameterlessCriterion());

	@Override
	public void onInitialize() {
		torches.add(registerItem("inverted_redstone_torch", settings -> new StandingAndWallBlockItem(INVERTED_REDSTONE_TORCH, INVERTED_REDSTONE_WALL_TORCH, Direction.DOWN, settings.useBlockDescriptionPrefix())));
		carts.add(EMITTER_MINECART_ITEM);

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
		registerItem("pale_oak_large_button", PALE_OAK_LARGE_BUTTON, buttons);
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

		// bugfix for patchouli, remove once they call it themselves
		if (PatchouliProxy.isModLoaded()) {
			RecipeSynchronization.synchronizeRecipeSerializer(ShapedRecipe.SERIALIZER);
			RecipeSynchronization.synchronizeRecipeSerializer(ShapelessRecipe.SERIALIZER);
		}

		// Register the guide item
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(NAMESPACE, "guide"), GUIDE);

		// Register statistics
		registerStat(INTERACT_WITH_SIGHT_SENSOR);
		registerStat(INTERACT_WITH_REDSTONE_EMITTER);

		// Register custom minecart
		Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(NAMESPACE, "emitter_minecart"), EMITTER_MINECART);
		MinecartComparatorLogicRegistry.register(EMITTER_MINECART, (minecart, state, pos) -> minecart.getPower());

		// Network
		LOOK_AT_PACKET.register();

		initializePatchouliCompatibility();
		appendItemsToGroup();
	}

	private void initializePatchouliCompatibility() {

		if (CONFIG.add_guide_to_loot_tables) {
			LOGGER.info("Adding RedBits Patchouli guide book to loot tables...");

			List<ResourceKey<LootTable>> tables = List.of(
					BuiltInLootTables.STRONGHOLD_LIBRARY,
					BuiltInLootTables.SPAWN_BONUS_CHEST,
					BuiltInLootTables.VILLAGE_CARTOGRAPHER
			);

			LootTableEvents.MODIFY.register((key, builder, source, registries) -> {

				if (!source.isBuiltin()) {
					return;
				}

				if (tables.stream().anyMatch(table -> table.equals(key))) {
					LootPool.Builder pool = LootPool.lootPool()
							.add(LootItem.lootTableItem(GUIDE));

					builder.withPool(pool);
				}
			});
		}

		if (CONFIG.add_guide_to_creative_menu) {
			LOGGER.info("Adding RedBits Patchouli guide book to creative menu...");

			CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
				content.accept(new ItemStack(GUIDE, 1));
			});
		}
	}

	private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> factory) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RedBits.NAMESPACE, name));
		BlockBehaviour.Properties settings = BlockBehaviour.Properties.of().setId(key);
		return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(settings));
	}

	private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RedBits.NAMESPACE, name));
		Item.Properties settings = new Item.Properties().setId(key);
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings));
	}

	private void registerItem(String name, Item item, List<Item> group) {
		group.add(item);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(NAMESPACE, name), item);
	}

	private void registerItem(String name, Block block, List<Item> group) {
		group.add(registerItem(name, settings -> new BlockItem(block, settings.useBlockDescriptionPrefix())));
	}

	private void registerStat(Identifier id) {
		Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
		Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
	}

	public static void appendItemsToGroup() {


		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
			content.insertAfter(Items.COMPARATOR, gates.stream().map(ItemStack::new).toList());
			content.insertAfter(Items.TNT_MINECART, carts.stream().map(ItemStack::new).toList());
			content.insertAfter(Items.REDSTONE_LAMP, lamps.stream().map(ItemStack::new).toList());
			content.insertAfter(Items.TARGET, cubes.stream().map(ItemStack::new).toList());
			content.insertAfter(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, plates.stream().map(ItemStack::new).toList());
			content.insertAfter(Items.REDSTONE_TORCH, torches.stream().map(ItemStack::new).toList());

			content.acceptAll(buttons.stream().map(ItemStack::new).toList());
		});
	}

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.fromNamespaceAndPath(RedBits.NAMESPACE, id);
		SoundEvent sound = SoundEvent.createVariableRangeEvent(identifier);
		Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, sound);
		return sound;
	}

}
