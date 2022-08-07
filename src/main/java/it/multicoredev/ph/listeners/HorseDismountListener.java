package it.multicoredev.ph.listeners;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.ph.Config;
import it.multicoredev.ph.Messages;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
public class HorseDismountListener implements Listener {
    private final Messages messages;

    public HorseDismountListener(Config config) {
        messages = config.messages;
    }

    @EventHandler
    public void onHorseDismount(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        LivingEntity rider = event.getExited();
        if (!(rider instanceof Player player)) return;
        if (
                !(vehicle instanceof Horse) &&
                        !(vehicle instanceof Donkey) &&
                        !(vehicle instanceof Mule) &&
                        !(vehicle instanceof SkeletonHorse) &&
                        !(vehicle instanceof ZombieHorse)
        ) return;

        AbstractHorse horse = (AbstractHorse) vehicle;
        if (horse.getOwner() == null) return;
        if (horse.getOwner() != player) return;

        AbstractHorseInventory horseInventory = horse.getInventory();
        if (horseInventory.getSaddle() == null) return;

        if (horse.isLeashed()) {
            Entity leashHolder = horse.getLeashHolder();
            if (!(leashHolder instanceof Player)) leashHolder.remove();

            horse.getWorld().dropItemNaturally(leashHolder.getLocation(), new ItemStack(Material.LEAD));
        }

        NBTEntity horseNBT = new NBTEntity(horse);

        ItemStack saddle = new ItemStack(Material.SADDLE);
        ItemMeta meta = saddle.getItemMeta();
        if (meta != null) {
            if (horse.getCustomName() != null) meta.setDisplayName(horse.getCustomName());
            List<String> lore = new ArrayList<>();
            lore.add(Chat.getTranslated(messages.horseOf.replace("{player}", horse.getOwner().getName())));
            lore.add(Chat.getTranslated(messages.horseHealth.replace("{health}", String.valueOf(horse.getHealth()))));
            meta.setLore(lore);
            saddle.setItemMeta(meta);
        }

        NBTItem saddleNBT = new NBTItem(saddle);
        saddleNBT.setBoolean("horse", true);
        saddleNBT.setString("horse_type", horse.getType().name());
        saddleNBT.setString("horse_nbt", Base64.getEncoder().encodeToString(horseNBT.toString().getBytes(StandardCharsets.UTF_8)));
        saddle = saddleNBT.getItem();

        horse.remove();
        if (!player.getInventory().addItem(saddle).isEmpty()) {
            horse.getWorld().dropItemNaturally(horse.getLocation(), saddle);
        }
    }
}
