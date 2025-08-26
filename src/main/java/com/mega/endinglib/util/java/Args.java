package com.mega.endinglib.util.java;

import org.spongepowered.asm.mixin.injection.invoke.arg.ArgumentIndexOutOfBoundsException;

public class Args {

    /**
     * Argument values
     */
    protected final Object[] values;

    /**
     * Ctor.
     *
     * @param values argument values
     */
    public Args(Object... values) {
        this.values = values;
    }

    /**
     * Return the argument list size.
     *
     * @return number of arguments available
     */
    public int size() {
        return this.values.length;
    }

    /**
     * Retrieve the argument value at the specified index
     *
     * @param index argument index to retrieve
     * @param <T> the argument type
     * @return the argument value
     * @throws ArrayIndexOutOfBoundsException if a value outside the range of
     *      available arguments is accessed
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T)this.values[index];
    }

    /**
     * Set (modify) the specified argument value. Internal verification is
     * performed upon supplied values and the following requirements are
     * enforced:
     *
     * <ul>
     *   <li>Reference types must be assignable to the object type, or can be
     *      <tt>null</tt>.
     *   <li>Primitive types must match the target types exactly and <b>cannot
     *      </b> be <tt>null</tt>.
     * </ul>
     *
     * @param index Argument index to set
     * @param value Argument value
     * @param <T> Argument type
     * @throws ArgumentIndexOutOfBoundsException if the specified argument index
     *      is outside the range of available arguments
     */
    public <T> void set(int index, T value) {
        this.values[index] = value;
    }
}
