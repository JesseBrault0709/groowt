package groowt.view.web.runtime;

import groowt.view.web.transpile.DefaultSourceMapSerializer;
import groowt.view.web.transpile.SourceMap.SourceMapEntry;
import groowt.view.web.transpile.SourceMapDeserializer;
import groowt.view.web.util.SourcePosition;

import java.util.ArrayList;
import java.util.List;

public final class DefaultSourceMapDeserializer implements SourceMapDeserializer {

    @Override
    public List<SourceMapEntry> deserialize(String serializedSourceMap) {
        final List<SourceMapEntry> result = new ArrayList<>();
        int line = -1;
        SourcePosition from = null;
        StringBuilder numberBuilder = new StringBuilder();
        for (int i = 0; i < serializedSourceMap.length(); i++) {
            final char c = serializedSourceMap.charAt(i);
            if (c == DefaultSourceMapSerializer.LINE_COL_SEP) {
                line = Integer.parseInt(numberBuilder.toString());
                numberBuilder = new StringBuilder();
            } else if (c == DefaultSourceMapSerializer.POS_SEP) {
                if (line == -1) {
                    throw new IllegalStateException();
                }
                final int col = Integer.parseInt(numberBuilder.toString());
                from = new SourcePosition(line, col);
                numberBuilder = new StringBuilder();
            } else if (c == DefaultSourceMapSerializer.ENTRY_SEP) {
                if (from == null) {
                    throw new IllegalStateException();
                }
                final int col = Integer.parseInt(numberBuilder.toString());
                result.add(new SourceMapEntry(from, new SourcePosition(line, col)));
                line = -1;
                from = null;
                numberBuilder = new StringBuilder();
            } else {
                numberBuilder.append(c);
            }
        }
        return result;
    }

}
