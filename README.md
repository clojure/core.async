# Andare

A fork of [core.async](https://github.com/clojure/core.async) ported for use with self-hosted ClojureScript. 

## Releases and Dependency Information

[![Clojars Project](https://img.shields.io/clojars/v/andare.svg)](https://clojars.org/andare)

[Deps](https://clojure.org/guides/deps_and_cli) dependency information:

```clj
 {andare {:mvn/version "0.10.0"}}
```  

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clj
 [andare "0.10.0"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>andare</groupId>
  <artifactId>andare</artifactId>
  <version>0.10.0</version>
</dependency>
```

### Lumo

[![NPM Version](http://img.shields.io/npm/v/andare.svg)](https://www.npmjs.org/package/andare)

```shell
npm install andare
```

## Usage

Andare preserves the namespaces present in `core.async`. Thus, bootstrap-compatible ClojureScript code that makes use of `core.async` can operate in self-hosted environments if you make the Andare artifact available for loading in lieu of the official `core.async` artifact.

## Compatibility

| Andare | `core.async` |
|:------:|:------------:|
| 0.10.0 | 0.4.490      |
| 0.9.0  | 0.4.474      |
| 0.8.0  | 0.3.465      |
| 0.7.0  | 0.3.443      |
| 0.6.0  | 0.3.442      |
| 0.5.0  | 0.3.426      |
| 0.4.0  | 0.2.395      |
| 0.3.0  | 0.2.391      |
| 0.2.0  | 0.2.374      |
| 0.1.0  | 0.2.374      |

## Testing

[![Build Status](https://travis-ci.org/mfikes/andare.svg?branch=master)](https://travis-ci.org/mfikes/andare)

### Self-Hosted ClojureScript
```
lein tach lumo
```

or

```
lein tach planck
```

### JVM ClojureScript

```
lein cljsbuild once adv
```

Then open `script/runtests.html`


### Clojure
```
lein test
```

## License

Revisions in this fork:
Copyright © 2016–2019 Mike Fikes and Contributors

Distributed under the Eclipse Public License, the same as Clojure.
