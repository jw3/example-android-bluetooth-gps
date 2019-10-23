#!/usr/bin/env bash

if [[ -z "$1" ]]; then echo "usage: $0 <platform>"; exit 1; fi

cmake_args=""
platform_arg="$1"

readonly cross_compiler_root=${CROSS_COMPILER_ROOT:-/usr/local/gcc-arm}
readonly compiler_major_version=$("${cross_compiler_root}/bin/arm-none-eabi-gcc" -dumpspecs | grep *version -A1 | tail -n1 | cut -d. -f1)

if [[ "$platform_arg" == "asset-tracker" ]]; then
  platform_arg="electron"
  cmake_args="-DASSET_TRACKER=1 $cmake_args"
fi

cmake_args="-DPLATFORM=$platform_arg $cmake_args"
readonly builddir="build-$platform_arg"

if [[ "$2" != "quick" ]]; then
  rm -rf "$builddir"
  mkdir "$builddir"
fi

if [[ ! -d "$builddir" ]]; then mkdir "$builddir"; fi

cd "$builddir"
conan install .. -s compiler.version="$compiler_major_version"
cmake .. ${cmake_args}
make -j1
