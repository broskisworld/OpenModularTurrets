package openmodularturrets.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;

public class ChamberTierTwoItem extends Item {

    public ChamberTierTwoItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedChamberTierTwo);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":chamberTierTwo");
    }
}