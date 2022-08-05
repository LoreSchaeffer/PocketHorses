package it.multicoredev.ph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.ph.listeners.HorseDismountListener;
import it.multicoredev.ph.listeners.PlayerInteractListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
public class PocketHorses extends JavaPlugin {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final GsonHelper gson = new GsonHelper(GSON);
    private final File configFile = new File(getDataFolder(), "config.json");

    private Config config;

    @Override
    public void onEnable() {
        if (!initStorage()) {
            onDisable();
            return;
        }
        getServer().getPluginManager().registerEvents(new HorseDismountListener(config), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(config), this);
    }

    @Override
    public void onDisable() {

    }

    private boolean initStorage() {
        if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
            if (!getDataFolder().mkdir()) {
                new IOException("Cannot create VanillaTowny directory").printStackTrace();
                return false;
            }
        }

        try {
            config = gson.autoload(configFile, new Config().init(), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
