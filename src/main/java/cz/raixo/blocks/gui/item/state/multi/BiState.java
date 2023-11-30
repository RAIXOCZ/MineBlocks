package cz.raixo.blocks.gui.item.state.multi;

import cz.raixo.blocks.gui.item.GuiItem;
import cz.raixo.blocks.gui.item.state.StateHandler;

import java.util.LinkedHashSet;
import java.util.Set;

public class BiState<A, B> implements StateHandler {

    private final Set<GuiItem<?>> items = new LinkedHashSet<>();
    private A a;
    private B b;

    public BiState(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public BiState() {
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
        updated();
    }

    public BiState<A, B> withA(A a) {
        this.a = a;
        return this;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
        updated();
    }

    public BiState<A, B> withB(B b) {
        this.b = b;
        return this;
    }

    protected void updated() {
        for (GuiItem<?> item : items) {
            item.stateUpdated();
        }
    }

    @Override
    public void onAdd(GuiItem<?> item) {
        items.add(item);
    }

    @Override
    public void onRemove(GuiItem<?> item) {
        items.remove(item);
    }

}
