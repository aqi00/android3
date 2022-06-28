cmake_minimum_required(VERSION 3.6) # 指定CMake的最低要求版本号

set(target common) # 设置环境变量的名称（target）及其取值（common）
project(${target}) # 指定项目的名称

file(GLOB srcs *.cpp *.c) # 把所有cpp文件和c文件都放入名称为srcs的集合中
file(GLOB hdrs *.hpp *.h) # 把所有hpp文件和h文件都放入名称为hdrs的集合中

#add_library(${target} STATIC ${srcs} ${hdrs}) # 生成静态库（库文件的扩展名为a）
add_library(${target} SHARED ${srcs} ${hdrs}) # 生成动态库（共享库）（库文件的扩展名为so）
target_link_libraries(${target} log) # 指定要链接哪些库。log表示日志库
