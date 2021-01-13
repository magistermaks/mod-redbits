package net.darktree.redbits.blocks;

import net.minecraft.block.PressurePlateBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class ComplexPressurePlateBlock extends PressurePlateBlock {

    public interface CollisionCondition {
        List<Entity> call( World world, Box box );
    }

    private final CollisionCondition collisionCondition;

    public ComplexPressurePlateBlock(CollisionCondition condition, Settings settings) {
        super(ActivationRule.EVERYTHING, settings);
        this.collisionCondition = condition;
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        List<Entity> list = collisionCondition.call( world, BOX.offset(pos) );

        for (Entity entity : list) {
            if (!entity.canAvoidTraps()) {
                return 15;
            }
        }

        return 0;
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add( new TranslatableText( this.getTranslationKey() + ".tooltip" ).formatted( Formatting.GRAY ) );
    }
}
