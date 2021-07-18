package net.darktree.redbits.dashloader;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.darktree.redbits.utils.ColorProperty;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.blockstate.property.DashProperty;

@DashObject(ColorProperty.class)
public class DashColorProperty implements DashProperty {
    
    @Serialize(order = 0)
    public final String name;

    public DashColorProperty(@Deserialize("name") String name) {
        this.name = name;
    }

    @DashConstructor(ConstructorMode.OBJECT)
    public DashColorProperty(ColorProperty colorProperty) {
        name = colorProperty.getName();
    }

    @Override
    public ColorProperty toUndash(DashRegistry registry) {
        return ColorProperty.of(name);
    }
    
}
