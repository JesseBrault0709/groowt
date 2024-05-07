package groowt.view.web.transpile.resolve;

import org.jetbrains.annotations.NotNull;

public sealed class ClassIdentifier permits ClassIdentifierWithFqn {

    private final String alias;

    public ClassIdentifier(@NotNull String alias) {
        this.alias = alias;
    }

    /**
     * @return the alias (the name without the package name)
     */
    public String getAlias() {
        return this.alias;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ClassIdentifier other) {
            return this.alias.equals(other.alias);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return this.alias.hashCode();
    }

    @Override
    public String toString() {
        return "ClassIdentifier(" + this.alias + ")";
    }

}
