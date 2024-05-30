# TODO

- [ ] Separate out the following into separate, non-Groowt projects with their own repositories and the com.jessebrault
  namespace:
  - antlr gradle plugin
  - all util:
    - di
    - extensible
    - fp
- [ ] di bug: @Singleton toSelf() causes stack overflow
- [ ] figure out how to make the GroovyClassLoader consistent between the groovy compiler configuration and the wvc
  compiler configuration.
- [ ] figure out better closure transpilation for wvc.
- [ ] in di, figure out how to not error if there are no injected args and @Inject is not present.
