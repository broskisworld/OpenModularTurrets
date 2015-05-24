package openmodularturrets.tileentity.turrets;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.entity.projectiles.BulletProjectile;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.util.TurretHeadUtils;

public class TeleporterTurretTileEntity extends TurretHead {
	public TeleporterTurretTileEntity() {
		super();
		this.turretTier = 4;
	}

	@Override
	public void updateEntity() {
		setSide();
		this.base = getBase();

		if (rotationAmimation >= 360F) {
			rotationAmimation = 0F;
		}
		rotationAmimation = rotationAmimation + 0.03F;

		if (worldObj.isRemote) {
			return;
		}

		if (ticks % 5 == 0) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		if (recoilState > 0.0F) {
			recoilState = recoilState - 0.01F;
		}

		ticks++;

		// BASE IS OKAY
		if (base == null || base.getBaseTier() < this.turretTier) {
			this.getWorldObj().func_147480_a(xCoord, yCoord, zCoord, true);
		} else {
			TurretHeadUtils.updateSolarPanelAddon(base);
			TurretHeadUtils.updateRedstoneReactor(base);

			int power_required = Math
					.round(this.getTurretPowerUsage()
							* (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
							* (1 + TurretHeadUtils
									.getScattershotUpgradesUpgrades(base)));

			// power check
			if ((base.getEnergyStored(ForgeDirection.UNKNOWN) < power_required)
					|| (!base.isActive())) {
				return;
			}

			// is there a target, and Has it died in the previous tick?
			if (target == null
					|| target.isDead
					|| this.getWorldObj().getEntityByID(target.getEntityId()) == null
					|| ((EntityLivingBase) target).getHealth() <= 0.0F) {
				target = getTargetWithMinRange();
			}

			// did we even get a target previously?
			if (target == null) {
				return;
			}

			this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord,
					zCoord) + 3.2F;
			this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord,
					yCoord, zCoord);

			// has cooldown passed?
			if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtils
					.getFireRateUpgrades(base)))) {
				return;
			}

			// Can the turret still see the target? (It's moving)
			if (target != null) {
				if (!TurretHeadUtils.canTurretSeeTarget(this,
						(EntityLivingBase) target)) {
					target = null;
					return;
				}
			}
			if (target != null && target instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) target;

				if (TurretHeadUtils.isTrustedPlayer(entity.getDisplayName(),
						base)) {
					target = null;
					return;
				}
			}
			if (target != null) {
				if (chebyshevDistance(target, base)) {
					target = null;
					return;
				}
			}

			ItemStack ammo = null;

			if (this.requiresAmmo()) {
				if (this.requiresSpecificAmmo()) {
					for (int i = 0; i <= TurretHeadUtils
							.getScattershotUpgradesUpgrades(base); i++) {
						ammo = TurretHeadUtils
								.useSpecificItemStackItemFromBase(base,
										this.getAmmo());
					}
				} else {
					ammo = TurretHeadUtils.useAnyItemStackFromBase(base);
				}

				// Is there ammo?
				if (ammo == null) {
					return;
				}
			}

			// Consume energy
			base.setEnergyStored(base.getEnergyStored(ForgeDirection.UNKNOWN)
					- power_required);

			target.setPosition(this.xCoord + 0.5F, this.yCoord + 1.0F,
					zCoord + 0.5F);
			worldObj.updateEntity(target);

			for (int i = 0; i <= 10; i++) {
				Random random = new Random();
				float var21 = (random.nextFloat() - 0.5F) * 0.2F;
				float var22 = (random.nextFloat() - 0.5F) * 0.2F;
				float var23 = (random.nextFloat() - 0.5F) * 0.2F;
				worldObj.spawnParticle("portal",
						xCoord + 0.5f + random.nextGaussian(), yCoord + 0.5f
								+ random.nextGaussian(),
						zCoord + 0.5f + random.nextGaussian(), (double) var21,
						(double) var22, (double) var23);
			}

			target = null;

		}

		this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord,
				this.zCoord, ModInfo.ID + ":" + this.getLaunchSoundEffect(),
				0.6F, 1.0F);

		ticks = 0;

	}

	@Override
	public int getTurretRange() {
		return ConfigHandler.getTeleporter_turret().getRange();
	}

	@Override
	public int getTurretPowerUsage() {
		return ConfigHandler.getTeleporter_turret().getPowerUsage();
	}

	@Override
	public int getTurretFireRate() {
		return ConfigHandler.getTeleporter_turret().getFireRate();
	}

	@Override
	public double getTurretAccuracy() {
		return ConfigHandler.getTeleporter_turret().getAccuracy();
	}

	@Override
	public boolean requiresAmmo() {
		return false;
	}

	@Override
	public boolean requiresSpecificAmmo() {
		return false;
	}

	@Override
	public Item getAmmo() {
		return null;
	}

	@Override
	public TurretProjectile createProjectile(World world, Entity target,
			ItemStack ammo) {
		return null;
	}

	@Override
	public String getLaunchSoundEffect() {
		return "teleport";
	}
}
