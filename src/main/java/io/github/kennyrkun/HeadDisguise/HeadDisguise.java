package io.github.kennyrkun.HeadDisguise;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.codingforcookies.armorequip.ArmorListener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;

// TODO: add permission for headdisguise
// headdisguise.disguise
// headdisguise.disguise.creeper
// headdisguise.disguise.zombie
// headdisguise.disguise.skeleton
// headdisguise.disguise.witherskeleton

public class HeadDisguise extends JavaPlugin implements Listener
{
    private static final Set<Material> SKULLS = EnumSet.of(
            Material.CREEPER_HEAD,
            Material.DRAGON_HEAD,
            Material.ZOMBIE_HEAD,
            Material.SKELETON_SKULL,
            Material.WITHER_SKELETON_SKULL
        );

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
        else if (!SKULLS.contains(newHelmet.getType())) // make sure the helmet is now a skull
                return;

        Material helmetType = newHelmet.getType();

        switch (helmetType)
        {
            // zombie
            case ZOMBIE_HEAD:
            {
                if (!p.hasPermission("headdisguise.disguise.zombie"))
                    return;

                for (Entity entity : p.getNearbyEntities(35, 35, 35))
                    if (entity instanceof Zombie)
                        ((Zombie) entity).setTarget(null);

                break;
            }
            // skeleton or wither skeleton
            case SKELETON_SKULL: // regular
            {
                if (!p.hasPermission("headdisguise.disguise.skeleton.normal"))
                    return;

                for (Entity entity : p.getNearbyEntities(35, 35, 35))
                    if (entity instanceof Skeleton)
                        ((Skeleton) entity).setTarget(null);

                break;
            }
            case WITHER_SKELETON_SKULL: // wither
            {
                if (!p.hasPermission("headdisguise.disguise.skeleton.wither"))
                    return;

                for (Entity entity : p.getNearbyEntities(35, 35, 35))
                    if (entity instanceof WitherSkeleton)
                        ((Skeleton) entity).setTarget(null);

                break;
            }
            // creeper
            case CREEPER_HEAD:
            {
                if (!p.hasPermission("headdisguise.disguise.creeper"))
                    return;

                for (Entity entity : p.getNearbyEntities(35, 35, 35))
                    if (entity instanceof Creeper)
                        ((Creeper) entity).setTarget(null);

                break;
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (!(event.getEntity() instanceof Monster))
            return;

        if (event.getTarget() instanceof Player)
        {
            Player p = (Player) event.getTarget();
            ItemStack helmet = p.getInventory().getHelmet();

            if (helmet == null)
                return;
            else if (!SKULLS.contains(helmet.getType()))
                return;

            EntityType et = event.getEntityType();

            switch (et)
            {
                case ZOMBIE:
                {
                    if (!p.hasPermission("headdisguise.disguise.zombie"))
                        return;

                    if (helmet.getType().equals(Material.ZOMBIE_HEAD))
                        event.setCancelled(true);

                    break;
                }
                case WITHER_SKELETON:
                {
                    if (!p.hasPermission("headdisguise.disguise.skeleton.normal"))
                        return;

                    if (helmet.getType().equals(Material.WITHER_SKELETON_SKULL))
                        event.setCancelled(true);

                    break;
                }
                case SKELETON:
                {
                    if (!p.hasPermission("headdisguise.disguise.skeleton.wither"))
                        return;

                    if (helmet.getType().equals(Material.SKELETON_SKULL))
                        event.setCancelled(true);
                }
                case CREEPER:
                {
                    if (!p.hasPermission("headdisguise.disguise.creeper"))
                        return;

                    if (helmet.getType().equals(Material.CREEPER_HEAD))
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }
}
