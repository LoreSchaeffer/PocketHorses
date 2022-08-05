package it.multicoredev.ph.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.ph.Config;
import it.multicoredev.ph.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

/**
 * Copyright © 2022 by Lorenzo Magni
 * This file is part of PocketHorses.
 * PocketHorses is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class PlayerInteractListener implements Listener {
    private final Messages messages;

    public PlayerInteractListener(Config config) {
        messages = config.messages;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.getType().equals(Material.SADDLE)) return;

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("horse") || !nbt.getBoolean("horse")) return;

        String horseType = nbt.getString("horse_type");
        String horseNBTString = nbt.getString("horse_nbt");

        Block target = event.getClickedBlock();
        if (target == null) return;
        Location horseLocation = target.getLocation().add(0, 1, 0);

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (!query.getApplicableRegions(BukkitAdapter.adapt(horseLocation)).getRegions().isEmpty()) {
            Chat.send(messages.regionFound, event.getPlayer());
            return;
        }

        AbstractHorse horse = (AbstractHorse) horseLocation.getWorld().spawnEntity(horseLocation, EntityType.valueOf(horseType));
        NBTEntity horseNBT = new NBTEntity(horse);
        horseNBT.mergeCompound(new NBTContainer(new String(Base64.getDecoder().decode(horseNBTString))));

        event.getPlayer().getInventory().remove(item);
    }
}
