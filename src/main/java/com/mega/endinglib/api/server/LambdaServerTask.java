package com.mega.endinglib.api.server;

import com.mega.endinglib.util.java.Args;
import com.mega.endinglib.util.java.funtion.Exe;

import java.util.function.Function;

public class LambdaServerTask extends ServerTask {
    private final Exe<LambdaServerTask> update;
    private final Function<LambdaServerTask, Boolean> shouldRemoved;
    public LambdaServerTask(Args initArgs, Exe<LambdaServerTask> update, Function<LambdaServerTask, Boolean> shouldRemoved ) {
        super(initArgs);
        this.update = update;
        this.shouldRemoved = shouldRemoved;
    }

    @Override
    public void update(Args args) {
        this.update.run(this);
        if (this.shouldRemoved.apply(this))
            this.setRemoved(true);
    }
}
