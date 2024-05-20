<$name ${formatAttr()}${selfClose ? '/' : ''}>${hasChildren() ? renderChildren() : ''}${hasChildren() || !selfClose ? "</$name>" : ''}
