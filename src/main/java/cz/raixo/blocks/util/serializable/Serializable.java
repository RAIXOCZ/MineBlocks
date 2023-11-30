package cz.raixo.blocks.util.serializable;

import java.io.DataOutput;
import java.io.IOException;

public interface Serializable {

    void serialize(DataOutput output) throws IOException;

}
