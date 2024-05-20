package groowt.view.web.transpile;

import groowt.view.web.util.SourcePosition;

public final class DefaultSourceMapSerializer implements SourceMapSerializer {

    public static final char LINE_COL_SEP = ',';
    public static final char POS_SEP = '.';
    public static final char ENTRY_SEP = ':';

    private void serialize(StringBuilder sb, SourcePosition sourcePosition) {
        final var line = sourcePosition.line();
        final var column = sourcePosition.column();
        sb.append(line).append(LINE_COL_SEP).append(column);
    }

    @Override
    public String serialize(SourceMap sourceMap) {
        final StringBuilder sb = new StringBuilder();
        final var iter = sourceMap.getAll().iterator();
        while (iter.hasNext()) {
            final var entry = iter.next();
            this.serialize(sb, entry.from());
            sb.append(POS_SEP);
            this.serialize(sb, entry.to());
            if (iter.hasNext()) {
                sb.append(ENTRY_SEP);
            }
        }
        return sb.toString();
    }

}
