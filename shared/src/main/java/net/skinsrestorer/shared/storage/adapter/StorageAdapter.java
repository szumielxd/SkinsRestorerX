/*
 * SkinsRestorer
 * Copyright (C) 2024  SkinsRestorer Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.skinsrestorer.shared.storage.adapter;

import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.property.SkinType;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.shared.storage.model.cache.MojangCacheData;
import net.skinsrestorer.shared.storage.model.player.LegacyPlayerData;
import net.skinsrestorer.shared.storage.model.player.PlayerData;
import net.skinsrestorer.shared.storage.model.skin.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StorageAdapter {
    void init();

    Optional<PlayerData> getPlayerData(UUID uuid) throws StorageException;

    void setPlayerData(UUID uuid, PlayerData data);

    Optional<PlayerSkinData> getPlayerSkinData(UUID uuid) throws StorageException;

    void removePlayerSkinData(UUID uuid);

    void setPlayerSkinData(UUID uuid, PlayerSkinData skinData);

    Optional<URLSkinData> getURLSkinData(String url, SkinVariant skinVariant) throws StorageException;

    void removeURLSkinData(String url, SkinVariant skinVariant);

    void setURLSkinData(String url, URLSkinData skinData);

    Optional<URLIndexData> getURLSkinIndex(String url) throws StorageException;

    void removeURLSkinIndex(String url);

    void setURLSkinIndex(String url, URLIndexData skinData);

    Optional<CustomSkinData> getCustomSkinData(String skinName) throws StorageException;

    void removeCustomSkinData(String skinName);

    void setCustomSkinData(String skinName, CustomSkinData skinData);

    Optional<LegacySkinData> getLegacySkinData(String skinName) throws StorageException;

    void removeLegacySkinData(String skinName);

    Optional<LegacyPlayerData> getLegacyPlayerData(String playerName) throws StorageException;

    void removeLegacyPlayerData(String playerName);

    Map<String, String> getStoredRandomSkins(int amount);

    Map<String, String> getStoredGUISkins(int offset);

    void purgeStoredOldSkins(long targetPurgeTimestamp) throws StorageException;

    Optional<MojangCacheData> getCachedUUID(String playerName) throws StorageException;

    void setCachedUUID(String playerName, MojangCacheData mojangCacheData);

    default void migrateLegacyPlayer(String playerName, UUID uuid) throws StorageException {
        Optional<LegacyPlayerData> legacyPlayerData = getLegacyPlayerData(playerName);
        if (legacyPlayerData.isEmpty()) {
            return;
        }

        // Handle migrated or new custom skins
        Optional<CustomSkinData> customSkinData = getCustomSkinData(legacyPlayerData.get().getSkinName());
        PlayerData playerData;
        if (customSkinData.isPresent()) {
            playerData = PlayerData.of(uuid, SkinIdentifier.ofCustom(customSkinData.get().getSkinName()));
        } else {
            playerData = PlayerData.of(uuid, SkinIdentifier.of(legacyPlayerData.get().getSkinName(), null, SkinType.LEGACY));
        }

        setPlayerData(uuid, playerData);

        removeLegacyPlayerData(playerName);
    }

    default boolean isLegacyCustomSkinTimestamp(long timestamp) {
        if (timestamp == 0L || timestamp == -1L) {
            return true;
        } else return timestamp >= 4102444800000L; // 2100-01-01
    }

    class StorageException extends Exception {
        public StorageException(Throwable cause) {
            super(cause);
        }
    }
}
