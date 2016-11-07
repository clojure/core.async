# Andare

A fork of [core.async](https://github.com/clojure/core.async) ported for use with bootstrapped ClojureScript. 

[![Clojars Project](https://img.shields.io/clojars/v/andare.svg)](https://clojars.org/andare)

## Usage

Andare preserves the namespaces present in `core.async`. Thus, bootstrap-compatible ClojureScript code that makes use of `core.async` can operate in self-hosted environments if you make the Andare artifact available for loading in lieu of the official `core.async` artifact.

## Compatibility

| Andare | `core.async` |
|:------:|:------------:|
| 0.4.0  | 0.2.395      |
| 0.3.0  | 0.2.391      |
| 0.2.0  | 0.2.374      |
| 0.1.0  | 0.2.374      |

## License

Revisions in this fork:
Copyright © 2016 Mike Fikes and Contributors

Distributed under the Eclipse Public License, the same as Clojure.