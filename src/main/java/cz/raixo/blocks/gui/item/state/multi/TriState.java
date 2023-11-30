package cz.raixo.blocks.gui.item.state.multi;

import cz.raixo.blocks.gui.item.state.StateHandler;

public class TriState<A, B, C> extends BiState<A, B> implements StateHandler {

    private C c;

    public TriState(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }

    public TriState() {
    }

    @Override
    public TriState<A, B, C> withA(A a) {
        super.withA(a);
        return this;
    }

    @Override
    public TriState<A, B, C> withB(B b) {
        super.withB(b);
        return this;
    }

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
        updated();
    }

    public TriState<A, B, C> withC(C c) {
        this.c = c;
        return this;
    }

}
