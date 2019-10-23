cmake_minimum_required(VERSION 3.0)

if (NOT FIRMWARE_DIR)
    set(FIRMWARE_DIR /usr/local/src/particle/firmware)
endif ()

if (NOT CROSS_COMPILER_PREFIX)
    set(CROSS_COMPILER_PREFIX /usr/local/gcc-arm)
endif ()

set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)

set(CMAKE_C_COMPILER "${CROSS_COMPILER_PREFIX}/bin/arm-none-eabi-gcc")
set(CMAKE_CXX_COMPILER "${CROSS_COMPILER_PREFIX}/bin/arm-none-eabi-g++")
set(CMAKE_ASM_COMPILER "${CROSS_COMPILER_PREFIX}/bin/arm-none-eabi-as")
set(CMAKE_AR "${CROSS_COMPILER_PREFIX}/bin/arm-none-eabi-ar" CACHE FILEPATH "Archiver")
set(CMAKE_RANLIB "${CROSS_COMPILER_PREFIX}/bin/arm-none-eabi-ranlib")

project(tracker)
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_VERBOSE_MAKEFILE OFF)

if (NOT EXISTS ${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
    message(FATAL_ERROR "The file conanbuildinfo.cmake doesn't exist, you have to run conan install first")
endif ()

include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
conan_basic_setup(NO_OUTPUT_DIRS)
set(CMAKE_MODULE_PATH ${CMAKE_SOURCE_DIR}/cmake ${CONAN_CMAKE_MODULE_PATH} ${CMAKE_MODULE_PATH})

find_package(Particle)

add_particle_remote_app(firmware)
