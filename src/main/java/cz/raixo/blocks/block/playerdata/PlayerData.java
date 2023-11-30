package cz.raixo.blocks.block.playerdata;

import cz.raixo.blocks.util.serializable.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PlayerData implements Serializable {

    public static PlayerData deserialize(DataInput input) throws IOException {
        return new PlayerData(new UUID(input.readLong(), input.readLong()), input.readUTF(), input.readInt());
    }

    private final UUID uuid;
    private final String displayName;
    private int breaks;

    public void incrementBreaks() {
        breaks++;
    }

    @Override
    public void serialize(DataOutput output) throws IOException {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
        output.writeUTF(displayName);
        output.writeInt(breaks);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

}
