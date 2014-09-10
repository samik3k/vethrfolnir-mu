/*
 * This file is strictly bounded by the creators of Vethrfolnir and its prohibited
 * for commercial use, or any use what so ever.
 * Copyright © Vethrfolnir Project 2013
 */
package com.vethrfolnir.game.network.mu.received;

import io.netty.buffer.ByteBuf;

import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vethrfolnir.game.entitys.EntityWorld;
import com.vethrfolnir.game.module.DatabaseAccess;
import com.vethrfolnir.game.network.MuNetworkServer;
import com.vethrfolnir.game.network.mu.MuClient;
import com.vethrfolnir.game.network.mu.MuPackets;
import com.vethrfolnir.game.network.mu.crypt.MuCryptUtils;
import com.vethrfolnir.game.network.mu.send.ExClientAuthAnswer.AuthResult;
import com.vethrfolnir.game.services.dao.AccountDAO;
import com.vethrfolnir.game.templates.AccountInfo;
import com.vethrfolnir.network.NetworkClient;
import com.vethrfolnir.network.ReadPacket;

import corvus.corax.processing.annotation.Config;
import corvus.corax.processing.annotation.Inject;

/**
 * @author Vlad
 *
 */
public final class ExRequestAuth extends ReadPacket {

	@Config(key = "Client.Version", value = "No Version")
	private String version;

	@Config(key = "Client.Serial", value = "No Serial")
	private String serial;
	
	@Config(key = "Game.Capacity", value = "100")
	private int capacity;
	
	@Config(key = "Account.AutoCreate", value = "false")
	private boolean autoCreateAccount;
	
	@Inject
	private EntityWorld entityWorld;
	
	/* (non-Javadoc)
	 * @see com.vethrfolnir.network.ReadPacket#read(com.vethrfolnir.network.NetworkClient, io.netty.buffer.ByteBuf, java.lang.Object[])
	 */
	@Override
	public void read(NetworkClient context, ByteBuf buff, Object... params) {
		
		if(MuNetworkServer.onlineClients() >= capacity) {
			MuClient client = as(params[0]);
			invalidate(buff);
			
			client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.ServerIsFull);
			return;
		}
		
		byte[] data1 = new byte[10];
		byte[] data2 = new byte[10];

		buff.getBytes(4, data1);
		buff.getBytes(14, data2);

		MuCryptUtils.Dec3bit(data1, 0, 10);
		MuCryptUtils.Dec3bit(data2, 0, 10);
		
		buff.setBytes(4, data1);
		buff.setBytes(14, data2);

		buff.readerIndex(4);
		
		String user = readConcatS(buff, 10, 0x00);
		String password = readConcatS(buff, 10, 0x00);

		buff.readerIndex(38);
		
		String version = readS(buff, 5);
		String mainSerial = readS(buff, 16);
		System.out.println("user: [" + user + "] pass[" + password + "] - "+" Version:["+version+"] "+" Serial: ["+mainSerial+"]");
		
		enqueue(context, user, password, version, mainSerial);
	}
	
	/* (non-Javadoc)
	 * @see com.vethrfolnir.network.ReadPacket#invokeLater(java.lang.Object[])
	 */
	@Override
	public void invokeLater(Object... buff) {
		final MuClient client = as(buff[0]);
		final String accountName = as(buff[1]);
		String password = as(buff[2]);
		
		if(buff[3].equals(version) && buff[4].equals(serial)) {
			AccountDAO dao = DatabaseAccess.AccountAccess();

			AccountInfo info = dao.getInfo(accountName);

			if(info == null) {
				
				if(autoCreateAccount) {
					client.auth(accountName);
					dao.createAccount(accountName, password, client);
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.AuthSucceeded);
				}
				else
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.AccountInvalid);
			}
			else {
				
				if(info.accessLevel < 0) {
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.AccountBlocked);
					return;
				}

				final AtomicBoolean alreadyOn = new AtomicBoolean(false);

				entityWorld.getClients().forEach((e) -> {
					MuClient iClient = e.getClient();
					
					if(client == iClient || client.getAccount() == null)
						return; // continue
					
					if(iClient.getAccount().getAccountName().equalsIgnoreCase(accountName)) {
						alreadyOn.set(true);
					}
				});

				if(alreadyOn.get()) {
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.AccountAlredyConnected);
					return;
				}

				try
				{
					MessageDigest md = MessageDigest.getInstance("md5");
			
					byte[] DB = md.digest(password.getBytes("UTF-8"));

					for(int i = 0; i < DB.length; i++) {
						if(DB[i] != info.passwordHash[i]) {
							client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.InvalidPassword);
							return;
						}
					}

					client.auth(accountName);
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.AuthSucceeded);
					dao.update(client);
				}
				catch (Exception e) {
					client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.InvalidPassword);
				}
			}
		}
		else
			client.sendPacket(MuPackets.ExAuthAnswer, AuthResult.WrongVersion);
	}

}
