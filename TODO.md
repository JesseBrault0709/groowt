# TODO

## 0.2.0
- [ ] Separate out the following into separate, non-Groowt projects with their own repositories and the com.jessebrault
  namespace:
  - antlr gradle plugin
  - all util:
    - di
    - extensible
    - fp

## 0.1.2
- [ ] di bug: @Singleton toSelf() causes stack overflow

## 0.1.1
- [ ] `Switch` and `Case` components
- [ ] Fix bug with multiline nested component attributes.

## 0.1.0
- [x] figure out how to make the GroovyClassLoader consistent between the groovy compiler configuration and the wvc
  compiler configuration.
- [x] figure out better closure transpilation for wvc.
- [x] in di, figure out how to not error if there are no injected args and @Inject is not present.
