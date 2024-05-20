package groowt.view.component.web.transpile.resolve;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;

import java.util.regex.Pattern;

public final class ResolveUtil {

    private static final Pattern packageSplitter = Pattern.compile("^(?<package>(?>\\p{Ll}[^.]*\\.)*)(?<top>\\p{Lu}[^.]*)(?<members>(?>\\.\\p{Lu}[^.]*)*)$");

    public static ClassNode getClassNode(Class<?> clazz) {
        return ClassHelper.makeCached(clazz);
    }

    public static String convertCanonicalNameToBinaryName(String canonicalName) {
        final var matcher = packageSplitter.matcher(canonicalName);
        if (matcher.matches()) {
            return new StringBuilder()
                    .append(matcher.group("package"))
                    .append(matcher.group("top"))
                    .append(matcher.group("members").replaceAll("\\.", "\\$"))
                    .toString();
        } else {
            throw new IllegalArgumentException("Cannot split apart " + canonicalName);
        }
    }

    private ResolveUtil() {}

}
