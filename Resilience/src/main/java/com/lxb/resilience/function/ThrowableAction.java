package com.lxb.resilience.function;


import static com.lxb.resilience.utils.ExceptionUtils.wrapThrowable;

public interface ThrowableAction {
    void execute() throws Throwable;


    static void execute(ThrowableAction action) throws RuntimeException {
        execute(action, RuntimeException.class);
    }

    /**
     * Executes {@link ThrowableAction}
     *
     * @param action {@link ThrowableAction}
     * @throws T wrap {@link Throwable} to the specified {@link Throwable} type
     */
    static <T extends Throwable> void execute(ThrowableAction action, Class<T> throwableType) throws T {
        try {
            action.execute();
        } catch (Throwable e) {
            throw wrapThrowable(e, throwableType);
        }
    }


}
