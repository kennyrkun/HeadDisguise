package io.github.kennyrkun.HeadDisguise;

import com.codingforcookies.armorequip.ArmorEquipEvent;

import com.codingforcookies.armorequip.ArmorListener;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: add permission for headdisguise
// headdisguise.disguise
// headdisguise.disguise.creeper
// headdisguise.disguise.zombie
// headdisguise.disguise.skeleton
// headdisguise.disguise.witherskeleton

public class HeadDisguise extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);

        getLogger().info("HeadDisguise enabled.");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("HeadDisguise disabled.");
    }

    // TODO: see if this event is triggered
    // FIXME: I don't think ArmorEquipEvent supports Skulls
    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event)
    {
        Player p = (Player)event.getPlayer();
        ItemStack newHelmet = event.getNewArmorPiece();
        ItemStack oldHelmet = event.getOldArmorPiece();

        // TODO: see if this can be written better.
        if (newHelmet == null) // if there is a new helmet
            return;
        else
            if (!newHelmet.getType().equals(Material.SKULL_ITEM)) // make sure the helmet is now a skull
                return;

        short helmetType = newHelmet.getDurability();

        switch (helmetType)
        {
            // zombie
            case 2:
            {
                if (!p.hasPermission("headdisguise.disguise.zombie"))
                    return;

                // TODO: see if p.getnearbyentieis takes radius or location
                for (Entity entity : p.getNearbyEntities(32, 32, 32))
                    if (entity instanceof Zombie)
                        ((Zombie) entity).setTarget(null);

                break;
            }
            // skeleton or wither skeleton
            case 0: // regular
            {
                if (!p.hasPermission("headdisguise.disguise.skeleton.normal"))
                    return;

                for (Entity entity : p.getNearbyEntities(32, 32, 32))
                    if (entity instanceof Skeleton)
                        if (((Skeleton) entity).getSkeletonType() == Skeleton.SkeletonType.NORMAL)
                            ((Skeleton) entity).setTarget(null);

                break;
            }
            case 1: // wither
            {
                if (!p.hasPermission("headdisguise.disguise.skeleton.wither"))
                    return;

                for (Entity entity : p.getNearbyEntities(32, 32, 32))
                    if (entity instanceof Skeleton)
                        if (((Skeleton) entity).getSkeletonType() == Skeleton.SkeletonType.WITHER)
                            ((Skeleton) entity).setTarget(null);

                break;
            }
            // creeper
            case 4:
            {
                if (!p.hasPermission("headdisguise.disguise.creeper"))
                    return;

                for (Entity entity : p.getNearbyEntities(32, 32, 32))
                    if (entity instanceof Creeper)
                        ((Creeper) entity).setTarget(null);

                break;
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (event.getTarget() instanceof Player)
        {
            Player p = (Player)event.getTarget();
            ItemStack helmet = p.getInventory().getHelmet();

            if (helmet == null)
                return;
            else
                if (!helmet.getType().equals(Material.SKULL_ITEM))
                    return;

            EntityType et = event.getEntityType();

            switch (et)
            {
                case ZOMBIE:
                {
                    if (!p.hasPermission("headdisguise.disguise.zombie"))
                        return;

                    if (helmet.getDurability() == 2) // 2 is Zombie Skull
                        event.setCancelled(true);

                    break;
                }
                case SKELETON:
                {
                    Skeleton s = (Skeleton)event.getEntity();
                    Skeleton.SkeletonType st = s.getSkeletonType();

                    if (helmet.getDurability() == 0) // 1 is Wither Skull
                    {
                        if (!p.hasPermission("headdisguise.disguise.skeleton.normal"))
                            return;

                        if (st == Skeleton.SkeletonType.NORMAL)
                            event.setCancelled(true);
                    }
                    else if (helmet.getDurability() == 1) // 0 is Skeleton Skull
                    {
                        if (!p.hasPermission("headdisguise.disguise.skeleton.wither"))
                            return;

                         if (st == Skeleton.SkeletonType.WITHER)// a regular skeleton
                            event.setCancelled(true);
                    }

                    break;
                }
                case CREEPER:
                {
                    if (!p.hasPermission("headdisguise.disguise.creeper"))
                        return;

                    if (helmet.getDurability() == 4) // 4 is Creeper Skull
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }
}
