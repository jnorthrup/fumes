#!/bin/bash
set -e
set -x

# This script is used to build the gztool binary for the web.
# It is run from the root of the repository.

# Build the gztool binary.
pushd ../gztool

#use zig compiler to build the binary
zig cc -I /usr/include/  -target wasm32-freestanding gztool.c -l{c,z,m} -shared -o gztool.wasm
#https://github.com/ziglang/zig/issues/11045

# Copy the gztool binary to the web directory.
popd
mkdir -p target/web


# Copy the gztool binary to the web directory.
cp ../gztool/gztool.wasm target/web/gztool.wasm

#todo: https://github.com/jvilk/BrowserFS integration

