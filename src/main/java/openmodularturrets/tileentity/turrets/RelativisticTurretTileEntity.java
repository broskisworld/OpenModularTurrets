package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.Reference;
import openmodularturrets.util.TurretHeadUtil;

public class RelativisticTurretTileEntity extends TurretHead {
    public RelativisticTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    @Override
    public void update() {
        setSide();
        this.base = getBase();

        if (worldObj.isRemote) {
            if (rotationAnimation >= 360F) {
                rotationAnimation = 0F;
            }
            rotationAnimation = rotationAnimation + 0.03F;
            return;
        }

        if (ticks % 5 == 0) {
            worldObj.markBlockForUpdate(this.pos);
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getBaseTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
        } else {
            concealmentChecks();
            TurretHeadUtil.updateSolarPanelAddon(base);

            //turret tick rate;
            if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            int power_required = Math.round(this.getTurretPowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                    base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

            // power check
            if ((base.getEnergyStored(EnumFacing.DOWN) < power_required) || (!base.isActive())) {
                return;
            }

            // is there a target, and Has it died in the previous tick?
            if (target == null || target.isDead || this.getWorld().getEntityByID(
                    target.getEntityId()) == null || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTargetWithoutEffect();
            }

            // did we even get a target previously?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtil.getAimYaw(target, this.pos) + 3.2F;
            this.rotationXY = TurretHeadUtil.getAimPitch(target, this.pos);

            // has cooldown passed?
            if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base)))) {
                return;
            }

            // Can the turret still see the target? (It's moving)
            if (target != null) {
                if (!TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) target)) {
                    target = null;
                    return;
                }
            }
            if (target != null && target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (TurretHeadUtil.isTrustedPlayer(entity.getUniqueID(), base)) {
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

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - power_required);
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 5,false, false));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 5,false, false));

            target = null;
        }

        this.getWorld().playSoundEffect(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                                           Reference.MOD_ID + ":" + this.getLaunchSoundEffect(), 0.6F, 1.0F);
        ticks = 0;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getRelativistic_turret().getRange();
    }

    @Override
    protected int getTurretPowerUsage() {
        return ConfigHandler.getRelativistic_turret().getPowerUsage();
    }

    @Override
    protected int getTurretFireRate() {
        return ConfigHandler.getRelativistic_turret().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getRelativistic_turret().getAccuracy();
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
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return null;
    }

    @Override
    protected String getLaunchSoundEffect() {
        return "relativistic";
    }
}
