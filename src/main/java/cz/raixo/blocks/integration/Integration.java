package cz.raixo.blocks.integration;

public interface Integration {

    String getPluginName();
    int getPriority();
    default void disable() {};

}
