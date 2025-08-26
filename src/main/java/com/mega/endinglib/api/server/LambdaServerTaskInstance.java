package com.mega.endinglib.api.server;

import com.mega.endinglib.util.java.Args;
import com.mega.endinglib.util.java.funtion.Exe;

import java.util.function.Function;

public class LambdaServerTaskInstance extends ServerTaskInstance {
    private final Exe<LambdaServerTaskInstance> update;
    private final Function<LambdaServerTaskInstance, Boolean> shouldRemoved;
    public LambdaServerTaskInstance(Args initArgs, Exe<LambdaServerTaskInstance> update, Function<LambdaServerTaskInstance, Boolean> shouldRemoved ) {
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
