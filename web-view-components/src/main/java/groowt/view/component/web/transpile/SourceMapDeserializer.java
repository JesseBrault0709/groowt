package groowt.view.component.web.transpile;

import java.util.List;

@FunctionalInterface
public interface SourceMapDeserializer {
    List<SourceMap.SourceMapEntry> deserialize(String serializedSourceMap);
}
