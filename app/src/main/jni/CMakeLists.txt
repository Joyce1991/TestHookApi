cmake_minimum_required(VERSION 3.4.1)

add_library(test-hook SHARED
            test-hook.cpp)

# Include libraries needed for hello-jni lib
target_link_libraries(test-hook log android)