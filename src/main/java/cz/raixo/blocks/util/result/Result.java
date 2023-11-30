package cz.raixo.blocks.util.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<R, E> {

    public static <R, E> Result<R, E> success(R result) {
        return new Result<>(result, null);
    }

    public static <R, E> Result<R, E> error(E error) {
        return new Result<>(null, error);
    }

    private final R result;
    private final E error;

    public boolean isSuccessful() {
        return error == null;
    }

    public void ifSuccessfulOrElse(Consumer<R> success, Consumer<E> error) {
        if (isSuccessful()) {
            success.accept(result);
        } else error.accept(this.error);
    }

}
