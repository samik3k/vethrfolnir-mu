/*
 * This file is strictly bounded by the creators of Vethrfolnir and its prohibited
 * for commercial use, or any use what so ever.
 * Copyright © Vethrfolnir Project 2013
 */
package com.vethrfolnir.game.network.mu;

import com.vethrfolnir.game.network.mu.send.*;

/**
 * @author Vlad
 *
 */
public class MuPackets {
	
	/**
	 * Sends the HelloClient packet, dose not have any parameters.
	 */
	public static final HelloClient HelloClient = new HelloClient();
	
	/**
	 * Sends the result of the Auth attempt
	 * @param authResult[AuthResult:0]
	 */
	public static final ExClientAuthAnswer ExAuthAnswer = new ExClientAuthAnswer();
	
	/**
	 * Sends the Character list packet, dose not have any parameters.
	 */
	public static final LobbyCharacterList EnterLobby = new LobbyCharacterList();
	
	/**
	 * Sends the boolean that lets the client know that Summoners are available for creation 
	 */
	public static final AllowSummonerCreation AllowSummonerCreation = new AllowSummonerCreation(); 

	/**
	 * Sends the answer that the character on the slot has been created or not XD<br>
	 * Requires: A boolean and a AccountCharacterInfo/null
	 */
	public static final LobbyCreateCharacter CharacterCreateAnswer = new LobbyCreateCharacter();
	
	/**
	 * Sends the answer that the character was deleted or not XD<br>
	 * Requires: A boolean, was it deleted or not?
	 */
	public static final LobbyDeleteCharacter CharacterDeleteAnswer = new LobbyDeleteCharacter();
	
	/**
	 * Sends the answer that the character was selected, prepare to enter the fray!
	 * Requires: A boolean and a AccountCharacterInfo
	 */
	public static final LobbyCharacterSelected CharacterSelectedAnswer = new LobbyCharacterSelected();
	
	/**
	 * Sends the enter world packet
	 */
	public static final EnterWorld EnterWorld = new EnterWorld();
	
	/**
	 * Sends the logout packet<br>
	 * Requires: An integer, specifying the logout type
	 */
	public static final Logout Logout = new Logout();
	
	/**
	 * Sends the level up packet, giving the player points and the animation<br>
	 * Requires: The PlayerStats component
	 */
	public static final UserLevelUp LevelUp = new UserLevelUp();
	
	/**
	 * Sends the full inventory packet
	 */
	public static final InventoryInfo InventoryInfo = new InventoryInfo();
	
	/**
	 * Send the status info of hp/sd and mana/whatever
	 * Requires StatusInfo.TYPE and a boolean for current hp or maxhp
	 */
	public static final StatusInfo StatusInfo = new StatusInfo();

	/**
	 * Send the state change packet, for an entity, it includes active effects
	 * Requires: Effect Id
	 */
	public static final StateChange StateChange = new StateChange();
	
	/**
	 * Moves the object
	 * Requires: GameObject Entity
	 */
	public static final MoveObject MoveObject = new MoveObject();

	/**
	 * Sends the Player information packet, that can update the players apparel and visually appear for players around him
	 * Requires: boolean toSelf? and if its not to self it requires the entity in question
	 */
	public static final PlayerInfo PlayerInfo = new PlayerInfo();
	
	/**
	 * Sends twords the world this clients intention
	 * Requires: ActionUpdate.#TYPE also a target if needed
	 */
	public static final ActionUpdate ActionUpdate = new ActionUpdate();
}
