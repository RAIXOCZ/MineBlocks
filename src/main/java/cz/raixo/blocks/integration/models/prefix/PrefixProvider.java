package cz.raixo.blocks.integration.models.prefix;

import java.util.UUID;

public interface PrefixProvider {

    String provide(UUID player);

}
