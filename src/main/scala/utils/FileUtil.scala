package utils

import java.io.File

object FileUtil {
    /**
     * 删除文件及子目录
     */
    def delete(file : File): Unit = {
        if(file.isDirectory) {
            val files = file.listFiles()
            for(f <- files) {
                delete(f)
            }
        }
        else if(file.isFile) {
            file.delete()
        }
    }
}
