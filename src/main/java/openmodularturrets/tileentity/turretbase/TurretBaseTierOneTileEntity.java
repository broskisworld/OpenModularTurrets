package openmodularturrets.tileentity.turretbase;

import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierFive;
import openmodularturrets.blocks.turretbases.BlockTurretBaseTierOne;
import openmodularturrets.handler.ConfigHandler;

import java.util.logging.Logger;

public class TurretBaseTierOneTileEntity extends TurretBase {

    public TurretBaseTierOneTileEntity() {
        this(ConfigHandler.getBaseTierOneMaxCharge(), ConfigHandler.getBaseTierOneMaxIo());
    }

    public TurretBaseTierOneTileEntity(int MaxEnergyStorage, int MaxIO) {
        super(MaxEnergyStorage, MaxIO);
    }

    @Override
    public int getBaseTier() {
        return 1;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public String getInventoryName() {
        return "modtur.turretbaseone";
    }

    @Optional.Method(modid = "OpenComputers")
    @Override
    public String getComponentName() {
        return "tierOneTurretBase";
    }
}
