# TODO

## 0.3.0
- [ ] Explore slightly different syntax for web view components to allow better InteliJ and Groovy integration. 
For example:
```
@package mysite
@import mysite.Component

<html>
  <Component componentAttrWithParams={ key, value -> <Echo>$key: $value</Echo>} />
</html>
```

## 0.2.0
- [ ] Separate out the following into separate, non-Groowt projects with their own repositories and the com.jessebrault
  namespace:
  - antlr gradle plugin
  - all util:
    - di
    - extensible
    - fp
- [ ] Remove gradle plugins and whatnot until we actually build the whole framework

## 0.1.3
- [ ] refactor tools/gradle start scripts to use dist instead of custom bin script
  - [ ] have custom bin/* scripts which point to dist(s) for convenience
- [ ] di bug: @Singleton toSelf() causes stack overflow

## 0.1.2
- [x] `Outlet` component for rendering children like so:
```
<Outlet children={children} />
```
- [x] `Render` component
- [x] `data-` attributes need to function correctly (really any attribute with hyphen).

## 0.1.1
- [x] `Switch` and `Case` components
- [x] Fix bug with multiline nested component attributes.
- [x] `Each` with `Map`
- [x] `WhenNotEmpty` with `Map`

## 0.1.0
- [x] figure out how to make the GroovyClassLoader consistent between the groovy compiler configuration and the wvc
  compiler configuration.
- [x] figure out better closure transpilation for wvc.
- [x] in di, figure out how to not error if there are no injected args and @Inject is not present.
