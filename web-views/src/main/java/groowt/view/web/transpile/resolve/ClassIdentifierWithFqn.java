package groowt.view.web.transpile.resolve;

import org.jetbrains.annotations.NotNull;

public final class ClassIdentifierWithFqn extends ClassIdentifier {

    private final String fqn;

    public ClassIdentifierWithFqn(@NotNull String alias, @NotNull String fqn) {
        super(alias);
        this.fqn = fqn;
    }

    public String getFqn() {
        return this.fqn;
    }

    @Override
    public String toString() {
        return "ClassIdentifierWithFqn(" + this.getAlias() + ", " + this.fqn + ")";
    }

}
