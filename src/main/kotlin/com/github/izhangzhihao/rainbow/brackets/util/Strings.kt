package com.github.izhangzhihao.rainbow.brackets.util


fun fileExtension(fileName: String) = fileName.substring(fileName.lastIndexOf(".") + 1)

val memoizedFileExtension = { fileName: String -> fileExtension(fileName) }.memoize()