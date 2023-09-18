package net.fabricmc.streetparts.custom;

import net.fabricmc.streetparts.register.ModLightBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LightGenItem extends Item {

    private static final int HEIGHT = 4;
    private static final int LENGTH = 2;
    private static int DIR_X;
    private static int DIR_Z;

    public LightGenItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Block block = world.getBlockState(context.getBlockPos()).getBlock();

        final Map<String, NbtElement> entries;

        if (!world.isClient()) {

            Direction direction = context.getHorizontalPlayerFacing().getOpposite();
            BlockRotation rotation;
            switch (direction){
                case EAST -> {DIR_X = 1; DIR_Z = 0; rotation = BlockRotation.CLOCKWISE_90;}
                case WEST -> {DIR_X = -1; DIR_Z = 0; rotation = BlockRotation.COUNTERCLOCKWISE_90;}
                case SOUTH -> {DIR_X = 0; DIR_Z = 1; rotation = BlockRotation.CLOCKWISE_180;}
                default -> {DIR_X = 0; DIR_Z = -1; rotation = BlockRotation.NONE;}
            }
            placeStructure(world, context, direction, rotation);

        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        tooltip.add(Text.translatable("Height: " + Integer.toString(HEIGHT) + ", Length: " + Integer.toString(LENGTH)).formatted(Formatting.BLUE));
    }

    private void placeStructure(World world, ItemUsageContext context, Direction direction, BlockRotation rotation) {
        //Place Block
        if (world.getBlockState(context.getBlockPos().add(0,1,0)).getBlock() == Blocks.AIR) {
            // Place Post
            BlockState state_post = ModLightBlocks.BLACK_LIGHT_POST.getDefaultState();
            for (int i = 1; i < HEIGHT; i++) {
                world.setBlockState(context.getBlockPos().add(0,i,0), state_post);
            }

            // Place Corner
            BlockState state_corner = ModLightBlocks.BLACK_LIGHT_CORNER.getDefaultState().rotate(rotation);
            world.setBlockState(context.getBlockPos().add(0,HEIGHT,0), state_corner);

            // Place Arm
            BlockState state_arm = ModLightBlocks.BLACK_LIGHT_ARM.getDefaultState().rotate(rotation);
            for (int i = 1; i < LENGTH; i++) {
                world.setBlockState(context.getBlockPos().add(i*DIR_X,HEIGHT,i*DIR_Z), state_arm);
            }

            // Place Light
            BlockState state_light = ModLightBlocks.BLACK_LIGHT_LIGHT.getDefaultState().rotate(rotation);
            world.setBlockState(context.getBlockPos().add(DIR_X*LENGTH,HEIGHT,DIR_Z*LENGTH), state_light);

            // PLay Sound
            Objects.requireNonNull(context.getPlayer()).playSound(SoundEvents.BLOCK_STONE_PLACE, 1.0f, 1.0f);
        }
    }
}
