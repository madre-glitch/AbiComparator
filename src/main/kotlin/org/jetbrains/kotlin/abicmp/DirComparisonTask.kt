package org.jetbrains.kotlin.abicmp

import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class DirComparisonTask(
        private val dir1: File,
        private val dir2: File,
        private val reportDir: File,
        private val id1: String,
        private val id2: String,
        private val executor: ExecutorService
) : Runnable {
    private val lastNameIndex = HashMap<String, Int>()
    private val tasks = ArrayList<Future<*>>()

    override fun run() {
        println("Comparing directories: $dir1, $dir2")
        walkRecursively(dir1, dir2)
        tasks.forEach { it.get() }
        println("Done")
    }

    private fun walkRecursively(subdir1: File, subdir2: File) {
        val files1 = subdir1.listFiles() ?: return
        for (file1 in files1) {
            val file2 = File(subdir2, file1.name.replace(id1, id2))
            if (file1.canRead() && file2.exists() && file2.canRead()) {
                if (file1.isDirectory) {
                    if (file2.isDirectory) {
                        println("Comparing subdirectories: $file1, $file2")
                        walkRecursively(file1, file2)
                    }
                } else if (file1.name.endsWith(".jar")) {
                    println("Comparing jars: $file1, $file2")
                    val index0 = lastNameIndex.getOrElse(file1.name) { 0 }
                    val index = index0 + 1
                    lastNameIndex[file1.name] = index
                    val reportFile = File(reportDir, "${file1.name}-REPORT-$index.html")
                    tasks.add(executor.submit(JarComparisonTask(file1, file2, reportFile)))
                }
            }
        }
    }
}