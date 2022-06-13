package ca.lukegrahamlandry.modularprofessions.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ProfessionsXpCapProvider implements ICapabilitySerializable<CompoundTag> {
    public static Capability<ProfessionsXp> CAP = CapabilityManager.get(new CapabilityToken<>(){});
    private final ProfessionsXpImpl data;

    public ProfessionsXpCapProvider(){
        this.data = new ProfessionsXpImpl();
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap.equals(CAP)) return (LazyOptional<T>) LazyOptional.of(() -> this.data);
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.data.write();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.data.read(nbt);
    }
}
