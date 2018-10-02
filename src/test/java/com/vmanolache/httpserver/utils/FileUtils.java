package com.vmanolache.httpserver.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class FileUtils {

    public static boolean writeToFileWithLock(File file, byte[] body) {
        ByteBuffer buffer;

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel();
             FileLock fileLock = fileChannel.tryLock()) {

            if (fileLock == null) {
                return false;
            } else {
                randomAccessFile.setLength(0);
                buffer = ByteBuffer.wrap(body);
                fileChannel.write(buffer);
            }

        } catch (OverlappingFileLockException | IOException e) {
            return false;
        }
        return true;
    }

}
