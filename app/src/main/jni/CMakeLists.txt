cmake_minimum_required(VERSION 3.4.1)

# config third library lib dir
set(third_lib_DIR ${CMAKE_SOURCE_DIR}/../../../../third-lib)

add_library(lib_substrate SHARED IMPORTED)
set_target_properties(lib_substrate PROPERTIES IMPORTED_LOCATION
    ${third_lib_DIR}/substrate/armeabi/libsubstrate-dvm.so)

add_library(test-hook SHARED
            test-hook.cpp)

# Include libraries needed for hello-jni lib
target_link_libraries(test-hook log android lib_substrate)
