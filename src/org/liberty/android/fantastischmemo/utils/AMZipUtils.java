package org.liberty.android.fantastischmemo.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.liberty.android.fantastischmemo.AMEnv;

import android.util.Log;

public class AMZipUtils {
    private static final int BUFFER_SIZE = 8192;
    private static final String TAG = "org.liberty.android.fantastischmemo.utils.AMZipUtils";

    // unzipped file is in [external]/anymemo/
    public static void unZipFile(File file, File outputPath) throws Exception {
        BufferedOutputStream dest = null;
        BufferedInputStream ins = null;
        ZipEntry entry;

        try {
            ZipFile zipfile = new ZipFile(file);
            Enumeration<?> e = zipfile.entries();
            while(e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                Log.d(TAG, "Extracting zip: " + entry);
                if(entry.isDirectory()){
                    FileUtils.forceMkdir(new File(outputPath.getAbsolutePath() + "/" + entry.getName()));
                } else {
                    ins = new BufferedInputStream
                            (zipfile.getInputStream(entry), BUFFER_SIZE);
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];

                    // Make sure the directory is there
                    File outputFile = new File(outputPath.getAbsolutePath() + "/" + entry.getName());
                    FileUtils.forceMkdir(new File(outputFile.getParent()));

                    FileOutputStream fos = new FileOutputStream(outputFile);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = ins.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                }
            }
        } catch (IOException e) {
            throw new Exception("Exception when extracting zip file: " + file, e);
        } finally {
            if (dest != null) {
                dest.close();
            }

            if (ins != null) {
                ins.close();
            }
        }

    }

    // Zip the db file, the image files in [external]/anymemo/images/[db_name]/
    // and audio files in [external]/anymemo/voice/[db_name]/
    public static File compressFile(File dbFile, File outZipFile) throws Exception {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outZipFile));
            zipFile(dbFile, zos);

            String imageRelativePath = "images/" + dbFile.getName() + "/";
            File imageDirectory = new File(AMEnv.DEFAULT_ROOT_PATH + imageRelativePath);
            if (imageDirectory.exists()) {
                zos.putNextEntry(new ZipEntry("images/"));
                zos.putNextEntry(new ZipEntry(imageRelativePath));
                zipDirectory(imageDirectory, imageRelativePath, zos);
            }

            String audioRelativePath = "voice/" + dbFile.getName() + "/";
            File audioDirectory = new File(AMEnv.DEFAULT_ROOT_PATH + audioRelativePath);
            if (audioDirectory.exists()) {
                zos.putNextEntry(new ZipEntry("voice/"));
                zos.putNextEntry(new ZipEntry(audioRelativePath));
                zipDirectory(audioDirectory, audioRelativePath, zos);
            }

        } catch(IOException e) {
            throw new Exception("Exception when compressing zip file: " + dbFile, e);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
        return null;
    }


    private static void zipFile(File file, ZipOutputStream zout) throws IOException {
        String fileName = file.getName();
        zout.putNextEntry(new ZipEntry(fileName));
        InputStream in = new FileInputStream(file);
        try {
            IOUtils.copy(in , zout);
        } finally {
            in.close();
        }
    }

    // directory: the directoyr to compress
    // prefixDir: The prefix in ziped file for compressed dir
    // outputFile, the output zip file
    public static void zipDirectory(File directory, String prefixDir, File outputFile) throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outputFile));
            zipDirectory(directory, prefixDir, zos);
        } finally {
            zos.close();
        }

    }

    // Helper method to compress from ZIP
    // Inspired from:
    // http://stackoverflow.com/questions/1399126/java-util-zip-recreating-directory-structure
    // directory: the directory to compress
    // prefixDir: The prefix in ziped file for compressed dir
    // zout: the zip output stream opened in the caller
    private static void zipDirectory(File directory, String prefixDir, ZipOutputStream zout) throws IOException {
        URI base = directory.toURI();
        LinkedList<File> queue = new LinkedList<File>();
        queue.addLast(directory);
        while (!queue.isEmpty()) {
            directory = queue.removeFirst();
            System.out.println("DIR: " + directory);
            for (File kid : directory.listFiles()) {
                String name = prefixDir + base.relativize(kid.toURI()).getPath();
                if (kid.isDirectory()) {
                    queue.addLast(kid);
                    name = name.endsWith("/") ? name : name + "/";
                    zout.putNextEntry(new ZipEntry(name));
                } else {
                    zout.putNextEntry(new ZipEntry(name));
                    InputStream in = new FileInputStream(kid);
                    try {
                        IOUtils.copy(in , zout);
                    } finally {
                        in.close();
                    }

                    zout.closeEntry();
                }
            }
        }
    }


}
