/*
 * This file is strictly bounded by the creators of Vethrfolnir and its prohibited
 * for commercial use, or any use what so ever.
 * Copyright © Vethrfolnir Project 2013
 */
package com.vethrfolnir.game.network.mu.send;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;

import com.vethrfolnir.game.entitys.ComponentIndex;
import com.vethrfolnir.game.entitys.GameObject;
import com.vethrfolnir.game.entitys.annotation.FetchIndex;
import com.vethrfolnir.game.entitys.components.Positioning;
import com.vethrfolnir.game.network.mu.MuClient;
import com.vethrfolnir.network.NetworkClient;
import com.vethrfolnir.network.WritePacket;

/**
 * @author Vlad
 *
 */
public class ActionUpdate extends WritePacket {

	public static final int AttackTarget = 0x78;
	public static final int SitDown = 0x80;
	public static final int Clap = 133;
	
	public static final int SillyClap = 200;

	@FetchIndex
	private ComponentIndex<Positioning> pos;
	
	
	@Override
	public void write(NetworkClient context, ByteBuf buff, Object... params) {
		MuClient client = (MuClient) context;
		int type = as(params[0]);
		
		GameObject target = null;

		if(params.length > 1)
			target = as(params[1]);

		Positioning positioning = client.getEntity().get(pos);
		writeArray(buff, 0xC1, 0x09, 0x18);
		writeSh(buff, client.getClientId(), ByteOrder.BIG_ENDIAN);
		writeC(buff, positioning.getHeading());
		
		writeC(buff, type); // attack type? 0x78 = melee
		
		// Target if any
		writeSh(buff, target == null ? 0x00 : target.getWorldIndex(), ByteOrder.BIG_ENDIAN);
	}

}
