package groowt.view.component.web.transpile;

import groowt.view.component.web.util.SourcePosition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class SourceMap {

    public record SourceMapEntry(SourcePosition from, SourcePosition to) {}

    private final Map<SourcePosition, SourcePosition> mapFromTo = new LinkedHashMap<>();

    public SourceMap() {}

    public SourceMap(String serialized, SourceMapDeserializer deserializer) {
        this.putAll(deserializer.deserialize(serialized));
    }

    public void put(SourcePosition from, SourcePosition to) {
        this.mapFromTo.put(from, to);
    }

    public void put(SourceMapEntry entry) {
        this.mapFromTo.put(entry.from, entry.to);
    }

    public void putAll(Map<SourcePosition, SourcePosition> map) {
        this.mapFromTo.putAll(map);
    }

    public void putAll(SourceMap sourceMap) {
        this.mapFromTo.putAll(sourceMap.mapFromTo);
    }

    public void putAll(List<SourceMapEntry> entries) {
        entries.forEach(this::put);
    }

    public SourcePosition getTo(SourcePosition from) {
        return this.mapFromTo.getOrDefault(from, SourcePosition.UNKNOWN);
    }

    public Stream<SourceMapEntry> stream() {
        return this.mapFromTo.entrySet().stream().map(entry -> new SourceMapEntry(entry.getKey(), entry.getValue()));
    }

    public List<SourceMapEntry> getAll() {
        return this.stream().toList();
    }

    public String serialize(SourceMapSerializer serializer) {
        return serializer.serialize(this);
    }

}
