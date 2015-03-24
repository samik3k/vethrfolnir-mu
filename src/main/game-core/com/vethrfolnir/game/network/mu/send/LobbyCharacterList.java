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
package com.vethrfolnir.game.network.mu.send;

import io.netty.buffer.ByteBuf;

import java.util.List;

import com.vethrfolnir.game.module.DatabaseAccess;
import com.vethrfolnir.game.module.MuAccount;
import com.vethrfolnir.game.network.mu.MuClient;
import com.vethrfolnir.game.network.mu.packets.MuWritePacket;
import com.vethrfolnir.game.services.dao.AccountDAO;
import com.vethrfolnir.game.templates.AccountCharacterInfo;

import corvus.corax.Corax;
import corvus.corax.config.CorvusConfig;

/**
 * @author Vlad
 *
 */
public class LobbyCharacterList extends MuWritePacket {
	
	@Override
	public void write(MuClient client, ByteBuf buff, Object... params) {

		MuAccount account = client.getAccount();
		AccountDAO dao = DatabaseAccess.AccountAccess();
		
		int count = dao.getLobbyCharacterSize(account.getAccountName()); // character count

		CorvusConfig config = Corax.config();
		boolean rf = config.getProperty("ForceAllowRageFighter", false);
		boolean mg = config.getProperty("ForceAllowMagicGladiator", false);
		boolean dl = config.getProperty("ForceAllowDarkLord", false);
		
		int allowed = 0x01; // default 0
		// calculate here how many to show
		
		// forces
		if(rf)
			allowed = 0x01;
		
		if(mg)
			allowed = 0x04;
		
		if(dl)
			allowed = 0x05;

		System.out.println(getClass().getSimpleName()+" : sending "+count+" characters for "+account.getAccountName()+" account.");
		
		if(count == 0) {
			writeArray(buff, 0xC1, 0x18, 0xF3, 0x00, allowed, 0x00, 0x00/** size**/, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
			return;
		}
		
		List<AccountCharacterInfo> characters = account.getCharacterList();
		
		// static
		writeArray(buff, 0xC1, 0x00, 0xF3, 0x00, allowed, 0x00, count, 0x00);
		for (int i = 0; i < characters.size(); i++) {
			AccountCharacterInfo info = characters.get(i);

			writeC(buff, info.slot); // position - starts from 0
			writeS(buff, info.name, 10);
			writeC(buff, 0x00); // Name Split
			writeSh(buff, info.level);
			writeC(buff, info.access); // Ctl Code :D aka access lvl
			writeC(buff, info.classId << 1);
			writeArray(buff, info.wearBytes);
			writeC(buff, 0xFF); // guild Status
		}
	}

}
