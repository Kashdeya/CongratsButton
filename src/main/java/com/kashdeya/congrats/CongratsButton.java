package com.kashdeya.congrats;

import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod(modid="CB", name="Congrats Button", version="1.0.0", acceptedMinecraftVersions="[1.10.2]")

public class CongratsButton {
	
	@Mod.EventHandler
	  public void preInit(FMLPreInitializationEvent event)
	  {
	    MinecraftForge.EVENT_BUS.register(this);
	  }
	  
	  @SubscribeEvent
	  public void onInteract(PlayerInteractEvent.RightClickBlock event)
	  {
	    World world = event.getWorld();
	    BlockPos basePos = event.getPos();
	    IBlockState state = world.getBlockState(basePos);
	    if ((state.getBlock() instanceof BlockButton))
	    {
	      int radius = 3;
	      BlockPos centerPos = basePos.offset(((EnumFacing)state.getValue(BlockButton.FACING)).getOpposite(), radius + 1);
	      if (world.getBlockState(centerPos).getBlock() == Blocks.BEACON)
	      {
	        BlockPos cornerPos = centerPos.add(-radius, -radius, -radius);
	        int j;
	        for (int i = 0; i < radius * 2 + 1; i++) {
	          for (j = 0; j < radius * 2 + 1; j++) {
	            for (int k = 0; k < radius * 2 + 1; k++)
	            {
	              BlockPos testPos = cornerPos.add(i, j, k);
	              IBlockState stateAt = world.getBlockState(testPos);
	              if ((stateAt.getBlock() != Blocks.BEACON) && (stateAt.getBlock() != Blocks.DIAMOND_BLOCK)) {
	                return;
	              }
	            }
	          }
	        }
	        EntityPlayer player = event.getEntityPlayer();
	        for (Achievement a : AchievementList.ACHIEVEMENTS) {
	          player.addStat(a);
	        }
	        if ((player instanceof EntityPlayerMP))
	        {
	          SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("Congratulations!!!").setStyle(new Style().setColor(TextFormatting.RED)), 5, 60, 20);
	          ((EntityPlayerMP)player).connection.sendPacket(packet);
	        }
	        if (!world.isRemote) {
	          for (int i = 0; i < 50; i++)
	          {
	            int colorCount = world.rand.nextInt(3) + 1;
	            int[] colors = new int[colorCount];
	            for (int j1 = 0; j1 < colorCount; j1++) {
	              colors[j1] = world.rand.nextInt(16777215);
	            }
	            ItemStack stack = new ItemStack(Items.FIREWORKS);
	            stack.setTagCompound(new NBTTagCompound());
	            
	            NBTTagCompound explosion = new NBTTagCompound();
	            explosion.setIntArray("Colors", colors);
	            
	            int type = 1;
	            double rand = Math.random();
	            if (rand > 0.25D) {
	              if (rand > 0.9D) {
	                type = 2;
	              } else {
	                type = 0;
	              }
	            }
	            explosion.setInteger("Type", type);
	            if (Math.random() < 0.05D) {
	              if (Math.random() < 0.5D) {
	                explosion.setBoolean("Flicker", true);
	              } else {
	                explosion.setBoolean("Trail", true);
	              }
	            }
	            stack.getTagCompound().setTag("Explosion", explosion);
	            
	            NBTTagCompound fireworks = new NBTTagCompound();
	            fireworks.setInteger("Flight", (int)Math.random() * 5 + 1);
	            
	            NBTTagList explosions = new NBTTagList();
	            explosions.appendTag(explosion);
	            fireworks.setTag("Explosions", explosions);
	            
	            stack.getTagCompound().setTag("Fireworks", fireworks);
	            
	            double x = player.posX + (Math.random() - 0.5D) * 20.0D;
	            double y = player.posY + (Math.random() - 0.5D) * 3.0D;
	            double z = player.posZ + (Math.random() - 0.5D) * 20.0D;
	            
	            EntityFireworkRocket rocket = new EntityFireworkRocket(world, x, y, z, stack);
	            world.spawnEntityInWorld(rocket);
	          }
	        }
	      }
	    }
	  }
}
