package com.stytch.mobile;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import java.util.function.Consumer;

/**
 * Bridges Java's {@link Consumer} into the Kotlin {@link Function1} expected by the
 * consumer-headless-extensions callback overloads.
 *
 * <p>Usage:
 * <pre>
 *   SmsOtpClientCallbacksKt.loginOrCreate(client, request,
 *       StytchCallbacks.onSuccess(response -> handleResponse(response)),
 *       StytchCallbacks.onFailure(error   -> handleError(error)));
 * </pre>
 *
 * <p>Note: the callbacks arrive on a background thread ({@code Dispatchers.Default}).
 * Wrap any UI updates in {@code SwingUtilities.invokeLater()}.
 */
public final class StytchCallbacks {

    private StytchCallbacks() {}

    public static <T> Function1<T, Unit> onSuccess(Consumer<T> consumer) {
        return value -> {
            consumer.accept(value);
            return Unit.INSTANCE;
        };
    }

    public static Function1<Throwable, Unit> onFailure(Consumer<Throwable> consumer) {
        return error -> {
            consumer.accept(error);
            return Unit.INSTANCE;
        };
    }
}
