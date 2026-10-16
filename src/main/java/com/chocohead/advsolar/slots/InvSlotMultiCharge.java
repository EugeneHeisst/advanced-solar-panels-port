package com.chocohead.advsolar.slots;

import ic2.api.item.ElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.world.item.ItemStack;

/** Several charge slots filled in order from one energy budget. */
public class InvSlotMultiCharge extends InvSlot {
    public final int tier;

    public InvSlotMultiCharge(TileEntityInventory base, int tier, int count, Access access) {
        super(base, "charge", access, count, InvSide.TOP);
        this.tier = tier;
    }

    @Override public boolean accepts(ItemStack stack) { return ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, false, true) > 0; }

    /** @return the energy put into the items */
    public double charge(double amount) {
        double charged = 0;
        for (int i = 0; i < size() && amount > 0; i++) {
            ItemStack stack = get(i);
            if (stack.isEmpty()) continue;
            double in = ElectricItem.manager.charge(stack, amount, tier, false, false);
            amount -= in;
            charged += in;
        }
        return charged;
    }
}
