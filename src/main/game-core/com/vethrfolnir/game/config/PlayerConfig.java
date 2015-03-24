/**
 * Copyright (C) 2013-2014 Project-Vethrfolnir
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vethrfolnir.game.config;

import corvus.corax.Corax;
import corvus.corax.config.Config;

/**
 * @author Vlad
 *
 */
public class PlayerConfig {

	@Config(key = "Player.FreeTeleport", subscribe = true, value = "false")
	public static boolean TeleportFree = false;

	@Config(key = "Player.TeleportNoRestriction", subscribe = true, value = "false")
	public static boolean TeleportNoRestriction = false;

	static {
		Corax.process(new PlayerConfig());
	}
}
