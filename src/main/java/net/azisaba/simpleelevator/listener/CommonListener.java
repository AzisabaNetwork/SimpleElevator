package net.azisaba.simpleelevator.listener;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.azisaba.simpleelevator.SimpleElevator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.stream.IntStream;

public class CommonListener implements Listener {
    @EventHandler
    public void onElevatorDown(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if ( !event.isSneaking() ) {
            return;
        }

        Block baseFrom = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if ( !isFloor(baseFrom) ) {
            return;
        }

        Block baseTo = tryFindFloor(baseFrom, BlockFace.DOWN);
        if ( baseTo == null ) {
            return;
        }

        if ( !player.hasPermission("elevator.use") ) {
            player.sendMessage(ChatColor.RED + "あなたはエレベーターを下る権限を持っていません！");
            return;
        }

        Location playerTo = baseTo.getRelative(BlockFace.UP).getLocation();
        playerTo.setX(playerTo.getX() + 0.5);
        playerTo.setY(playerTo.getY() + 0.5);
        playerTo.setZ(playerTo.getZ() + 0.5);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        playerTo.setYaw(yaw);
        playerTo.setPitch(pitch);
        player.teleport(playerTo);

        player.playSound(playerTo, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.getWorld().spawnParticle(Particle.TOTEM, playerTo, 50, 0.2, 0.2, 0.2, 0.5);
    }

    @EventHandler
    public void onElevatorUp(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if ( player.isOnGround() ) {
            return;
        }
        if ( player.isDead() ) {
            return;
        }
        if ( player.isFlying() ) {
            return;
        }
        if ( player.isSwimming() ) {
            return;
        }
        if ( player.getVelocity().getY() <= 0 ) {
            return;
        }

        Block baseFrom = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if ( !isFloor(baseFrom) ) {
            return;
        }

        Block baseTo = tryFindFloor(baseFrom, BlockFace.UP);
        if ( baseTo == null ) {
            return;
        }

        if ( !player.hasPermission("elevator.use") ) {
            player.sendMessage(ChatColor.RED + "あなたはエレベーターを上る権限を持っていません！");
            return;
        }

        Location playerTo = baseTo.getRelative(BlockFace.UP).getLocation();
        playerTo.setX(playerTo.getX() + 0.5);
        playerTo.setY(playerTo.getY() + 0.5);
        playerTo.setZ(playerTo.getZ() + 0.5);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        playerTo.setYaw(yaw);
        playerTo.setPitch(pitch);
        player.teleport(playerTo);

        player.playSound(playerTo, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.getWorld().spawnParticle(Particle.TOTEM, playerTo, 50, 0.2, 0.2, 0.2, 0.5);
    }

    private boolean isSafe(Block block) {
        return block.getType().isTransparent();
    }

    private boolean isFloor(Block baseFrom) {
        if (!baseFrom.getType().name().equals(SimpleElevator.INSTANCE.getConfig().getString("baseBlockType", Material.DIAMOND_BLOCK.name()))) {
            return false;
        }
        return IntStream.range(1, 3).allMatch(distance -> isSafe(baseFrom.getRelative(BlockFace.UP, distance)));
    }

    //3ブロック以上の空間があるかの判定
    private Block tryFindFloor(Block baseFrom, BlockFace face) {
        Vector direction = face.getDirection();
        Location loc = baseFrom.getLocation().setDirection(direction);
        int maxDistance = baseFrom.getWorld().getMaxHeight();
        BlockIterator it = new BlockIterator(loc, 0, maxDistance);

        Iterators.advance(it, 3);
        for ( Block baseTo : Lists.newArrayList(it) ) {
            if ( isFloor(baseTo) ) {
                return baseTo;
            }
            if ( !isSafe(baseTo) ) {
                return null;
            }
        }
        return null;
    }
}
