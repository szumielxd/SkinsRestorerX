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
package net.skinsrestorer.bukkit.utils;

import net.skinsrestorer.mappings.mapping1_18.Mapping1_18;
import net.skinsrestorer.mappings.mapping1_18_2.Mapping1_18_2;
import net.skinsrestorer.mappings.mapping1_19.Mapping1_19;
import net.skinsrestorer.mappings.mapping1_19_1.Mapping1_19_1;
import net.skinsrestorer.mappings.mapping1_19_2.Mapping1_19_2;
import net.skinsrestorer.mappings.mapping1_19_3.Mapping1_19_3;
import net.skinsrestorer.mappings.mapping1_19_4.Mapping1_19_4;
import net.skinsrestorer.mappings.mapping1_20.Mapping1_20;
import net.skinsrestorer.mappings.mapping1_20_2.Mapping1_20_2;
import net.skinsrestorer.mappings.mapping1_20_4.Mapping1_20_4;
import net.skinsrestorer.mappings.shared.IMapping;
import net.skinsrestorer.shared.utils.FluentList;
import org.bukkit.Server;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class MappingManager {
    private static final List<IMapping> mappings = FluentList.of(
            new Mapping1_18(),
            new Mapping1_18_2(),
            new Mapping1_19(),
            new Mapping1_19_1(),
            new Mapping1_19_2(),
            new Mapping1_19_3(),
            new Mapping1_19_4(),
            new Mapping1_20(),
            new Mapping1_20_2(),
            new Mapping1_20_4()
    );

    public static Optional<IMapping> getMapping(Server server) {
        Optional<String> mappingVersion = getMappingsVersion(server);

        if (mappingVersion.isEmpty()) {
            return Optional.empty();
        }

        for (IMapping mapping : mappings) {
            if (mapping.getSupportedVersions().contains(mappingVersion.get())) {
                return Optional.of(mapping);
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings({"deprecation"})
    public static Optional<String> getMappingsVersion(Server server) {
        org.bukkit.UnsafeValues craftMagicNumbers = server.getUnsafe();
        try {
            Method method = craftMagicNumbers.getClass().getMethod("getMappingsVersion");
            return Optional.of((String) method.invoke(craftMagicNumbers, new Object[0]));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
