package com.maccimo.breakinglambda;

import java.util.function.Supplier;

public class AssignmentOrderMatter {

    private Supplier<String> firstSupplier;

    private Supplier<String> lastSupplier;

    public AssignmentOrderMatter() {
        this.lastSupplier = () -> "Farewell!";
        this.firstSupplier = () -> "Hallo!";
    }

    public String getFirstValue() {
        return firstSupplier.get();
    }

    public String getLastValue() {
        return lastSupplier.get();
    }

}
