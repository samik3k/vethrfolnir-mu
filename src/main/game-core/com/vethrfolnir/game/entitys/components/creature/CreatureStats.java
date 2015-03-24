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
package com.vethrfolnir.game.entitys.components.creature;

import com.vethrfolnir.game.entitys.Component;
import com.vethrfolnir.game.entitys.GameObject;
import com.vethrfolnir.game.network.mu.MuPackets;
import com.vethrfolnir.game.network.mu.send.StatusInfo;
import com.vethrfolnir.game.staticdata.world.Region;
import com.vethrfolnir.game.templates.npc.NpcTemplate;
import com.vethrfolnir.services.threads.CorvusThreadPool;

import corvus.corax.Corax;

/**
 * @author Vlad
 *
 */
public class CreatureStats implements Component {

	protected GameObject entity;

	protected int maxHealthPoints = 1245, maxCombatPoints = 1245;
	protected int maxManaPoints = 1245, maxStaminaPoints = 1245;

	protected int healthPoints = 1200, combatPoints = 1200;
	protected int manaPoints = 1200, staminaPoints = 1200;

	protected int level;
	
	public CreatureStats() { /* Player */ }
	
	public CreatureStats(NpcTemplate npcTemplate) {
		healthPoints = maxHealthPoints = npcTemplate.HP;
		manaPoints = maxManaPoints = npcTemplate.MP;
	}

	@Override
	public void initialize(GameObject entity) {
		this.entity = entity;
	}

	/**
	 * @param attacker
	 * @param damage
	 * @return took damage
	 */
	public boolean takeDamageHp(GameObject attacker, int damage) {

		if(isDead() || !entity.isVisable())
			return false;

		healthPoints = Math.max(0, healthPoints - damage);

		if(entity.isPlayer()) {
			entity.sendPacket(MuPackets.StatusInfo, StatusInfo.STATUS_HPSD, true);
		}

		if(healthPoints <= 0) {
			GameObject broadcaster = this.entity.isPlayer() ? entity : attacker; // npcs cant kill each other
			Region region = entity.get(CreatureMapping.Positioning).getCurrentRegion();
			region.broadcastToKnown(broadcaster, MuPackets.Death, entity, attacker);
			broadcaster.sendPacket(MuPackets.Death, entity, attacker);
			
			final long regenTime = entity.isPlayer() ? 3000 : (entity.get(CreatureMapping.CreatureState).getRegenTime() * 1000);

			final CorvusThreadPool threadPool = Corax.fetch(CorvusThreadPool.class);
			threadPool.schedule(()-> {
				entity.setVisible(false); // he's dead qn
				
				//TODO Proper resurrect timer for players.
				threadPool.schedule(()-> {
					CreatureStats.this.revive();
				}, regenTime);
			}, 3500);
		}
		
		return true;
	}

	private void revive() {

		healthPoints = maxHealthPoints;
		manaPoints = maxManaPoints;
		
		staminaPoints = maxStaminaPoints;
		combatPoints = maxCombatPoints;
		
		if(entity.isPlayer()) {
			//entity.get(CreatureMapping.Positioning).getCurrentRegion().transfer(entity);
			
			entity.sendPacket(MuPackets.PlayerInfo, true);
			entity.sendPacket(MuPackets.StatusInfo, StatusInfo.STATUS_HPSD, true);
			entity.sendPacket(MuPackets.StatusInfo, StatusInfo.STATUS_MPST, true);
		}

		entity.setVisible(true);
	}

	public boolean isDead() {
		return healthPoints <= 0;
	}
	
	/**
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the maxHealthPoints
	 */
	public int getMaxHealthPoints() {
		return maxHealthPoints;
	}

	/**
	 * @param maxHealthPoints the maxHealthPoints to set
	 */
	public void setMaxHealthPoints(int maxHealthPoints) {
		this.maxHealthPoints = maxHealthPoints;
	}

	/**
	 * @return the maxCombatPoints
	 */
	public int getMaxCombatPoints() {
		return maxCombatPoints;
	}

	/**
	 * @param maxCombatPoints the maxCombatPoints to set
	 */
	public void setMaxCombatPoints(int maxCombatPoints) {
		this.maxCombatPoints = maxCombatPoints;
	}

	/**
	 * @return the maxManaPoints
	 */
	public int getMaxManaPoints() {
		return maxManaPoints;
	}

	/**
	 * @param maxManaPoints the maxManaPoints to set
	 */
	public void setMaxManaPoints(int maxManaPoints) {
		this.maxManaPoints = maxManaPoints;
	}

	/**
	 * @return the maxStaminaPoints
	 */
	public int getMaxStaminaPoints() {
		return maxStaminaPoints;
	}

	/**
	 * @param maxStaminaPoints the maxStaminaPoints to set
	 */
	public void setMaxStaminaPoints(int maxStaminaPoints) {
		this.maxStaminaPoints = maxStaminaPoints;
	}

	/**
	 * @return the healthPoints
	 */
	public int getHealthPoints() {
		return healthPoints;
	}

	/**
	 * @param healthPoints the healthPoints to set
	 */
	public void setHealthPoints(int healthPoints) {
		this.healthPoints = healthPoints;
	}

	/**
	 * @return the combatPoints
	 */
	public int getCombatPoints() {
		return combatPoints;
	}

	/**
	 * @param combatPoints the combatPoints to set
	 */
	public void setCombatPoints(int combatPoints) {
		this.combatPoints = combatPoints;
	}

	/**
	 * @return the manaPoints
	 */
	public int getManaPoints() {
		return manaPoints;
	}

	/**
	 * @param manaPoints the manaPoints to set
	 */
	public void setManaPoints(int manaPoints) {
		this.manaPoints = manaPoints;
	}

	/**
	 * @return the staminaPoints
	 */
	public int getStaminaPoints() {
		return staminaPoints;
	}

	/**
	 * @param staminaPoints the staminaPoints to set
	 */
	public void setStaminaPoints(int staminaPoints) {
		this.staminaPoints = staminaPoints;
	}

	@Override
	public void dispose() {
		entity = null;
	}

}
