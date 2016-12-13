cmake_minimum_required(VERSION 3.4.1)

# config third library lib dir
set(third_lib_DIR ${CMAKE_SOURCE_DIR}/../../../../third-lib)

###################################################################
add_library(lib_substrate SHARED IMPORTED)
set_target_properties(lib_substrate PROPERTIES IMPORTED_LOCATION
    ${third_lib_DIR}/substrate/armeabi/libsubstrate.so)
add_library(lib_substrate-dvm SHARED IMPORTED)
set_target_properties(lib_substrate-dvm PROPERTIES IMPORTED_LOCATION
    ${third_lib_DIR}/substrate/armeabi/libsubstrate-dvm.so)
##################################################################

add_library(test-hook.cy SHARED
            test-hook.cpp main.c)

# Include libraries needed for hello-jni lib
target_link_libraries(test-hook.cy log android lib_substrate)
target_include_directories(test-hook.cy PRIVATE
                           ${third_lib_DIR}/substrate/include)

# build application's shared lib
include_directories(${third_lib_DIR}/substrate/include)
