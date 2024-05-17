# TODO and general notes

## TODO
- [ ] Think about syntax for scriptlets. What semantic differences o we want between `${}`, `<%= %>`, and `<% %>`.
- [ ] Can the DollarScriptlet instead not include the dollar, more like React?
- [ ] Think about namespaces. This could ease having to write imports, because the namespace would automatically be
  mapped to a star import. Syntax could be something like `<turbo::Frame someKey='someVal' />`.
- [ ] Think about how GString are currently done. Do we need to even parse them as such, between element/component tags?
  Could we instead just have the `writer` variable take individual values? This would simplify implementation logic
  for differentiating between closures/scriplets that take an `out` (left-shiftable) param and ones that don't take any.
  - This could also make differentiating lazy/eager closures/scriptlets easier.
- [ ] Check that the lexer/parser can handle `-` dashes in component names/types, so that we can support `data-`, etc.
- [X] Think more about how to compile templates and components alongside each other. Perhaps we just need to bite
  the bullet and use a custom Groovy `CompilationUnit` or `CompilerConfiguration` or whatnot to identify `.wvc` files.
  - Update 5/17/24: This should be solved with the `DelegatingWvcParserPlugin` which just examines the file extension.
- [ ] Get rid of automatically generated script `main` and `run` methods; replace with a custom `main` method which
  forwards the args to a helper class and then runs the template with the variables similar to the current `runTemplate`
  helper tool.
- [ ] Create a custom tool for compiling which simply forwards to the Groovy `FileSystemCompiler` but with the correct
  compiler configuration and parser plugin factory.
- [ ] Create smoke screen test cases for the compiler.
- [ ] Separate the api, runtime, and compiler elements. The api/runtime can depend on the compiler. If users really want
  to meddle with the compiler for some reason, they can depend on it directly.
- [ ] Get rid of sketching source set.
- [ ] Fix inner class bug.

## Syntax ideas
- Perhaps we could have a lambda- or closure-like factory at the beginning of a component for creating it. This could
  bypass the resolve mechanism and just instantiate the component directly via constructor (with some protocol).
  Something like: `<Component::new ...>` or `<Component.&new ...>`. Could also have `<{ new Component() } />`.
