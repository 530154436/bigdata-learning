package org.zcb.common.utils

import java.io.File

object FileUtil {
    /**
     * 删除文件及子目录
     */
    def delete(file: File): Unit = {
        if (file.isDirectory) {
            val files = file.listFiles()
            for (f <- files) {
                delete(f)
            }
        }
        else if (file.isFile) {
            file.delete()
        }
    }

    /**
     * 删除指定目录的所有文件夹和键
     */
    def deleteAll(dir: File): Unit = {
        if (dir.exists()) {
            val files: Array[File] = dir.listFiles()
            for (file <- files) {
                FileUtil.delete(file)
            }
        }
        dir.delete()
    }
}
