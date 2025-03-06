#!/usr/bin/env python
# -*- coding:utf-8 -*-
# @author: zhengchubin
# @time: 2025/1/19 17:49
# @function:
from zipfile import ZipFile


def load_file(file: str) -> str:
    """
    加载提示词
    """
    print("加载提示词: %s" % file)
    file = str(file)
    if file.__contains__("zip"):
        zip_file, sub_path = file.split("zip")[0], file.split("zip")[1]
        sub_path = sub_path.lstrip("\\").lstrip("/")
        zip_file = ZipFile(zip_file + "zip")
        file = zip_file.open(sub_path, mode='r')
    else:
        file = open(file, mode='r', encoding='utf-8')

    lines = []
    for line in file:
        if isinstance(line, bytes):
            line = line.decode(encoding='utf-8')
        lines.append(line)
    return "".join(lines)


# print(load_file("io_util.py"))
