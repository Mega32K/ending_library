package com.mega.endinglib.api.server;

import com.mega.endinglib.util.java.Args;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaServerTask extends ServerTask {
    private final Consumer<LambdaServerTask> update;
    private final Function<LambdaServerTask, Boolean> shouldRemoved;

    public LambdaServerTask(Args initArgs, Consumer<LambdaServerTask> update, Function<LambdaServerTask, Boolean> shouldRemoved) {
        super(initArgs);
        this.update = update;
        this.shouldRemoved = shouldRemoved;
    }

    @Override
    public void update(Args args) {
        this.update.accept(this);
        if (this.shouldRemoved.apply(this))
            this.setRemoved(true);
    }
}
