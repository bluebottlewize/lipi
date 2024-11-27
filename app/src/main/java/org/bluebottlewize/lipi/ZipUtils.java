package org.bluebottlewize.lipi;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils
{

    // Method to zip a folder and save to external storage
    public static void zipFolderAndSaveToExternalStorage(Context context, String folderName, Uri externalDirectory)
    {
        // Get the internal storage directory
        File folderToZip = context.getDir(folderName, Context.MODE_PRIVATE);

        // Define the output zip file path in external storage
        DocumentFile externalFolder = DocumentFile.fromTreeUri(context, externalDirectory);
        DocumentFile outputFile = externalFolder.createFile("application/zip", folderName + ".zip");
        // File zipFile = new File(Environment.getExternalStorageDirectory(), folderName + ".zip");

        Path p = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            p = Paths.get(outputFile.getUri().getPath());

            try
            {

                try (ZipOutputStream zs = new ZipOutputStream(context.getContentResolver().openOutputStream(outputFile.getUri())))
                {
                    Path pp = null;
                    pp = Paths.get(folderToZip.toURI());
                    Path finalPp = pp;
                    Files.walk(pp)
                            .filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(finalPp.relativize(path).toString());
                                try
                                {
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                }
                                catch (Exception e)
                                {
                                    System.err.println(e);
                                }
                            });
                    Toast.makeText(context, "Export Success", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

//        try {
//            OutputStream os = context.getContentResolver().openOutputStream(outputFile.getUri());
//            ZipOutputStream zos = new ZipOutputStream(os);
//            zipDirectory(folderToZip, folderToZip.getName(), zos);
//
//
//            System.out.println("Folder zipped successfully: " + outputFile.getName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // Method to zip the contents of a directory
    private static void zipDirectory(File folder, String zipEntryName, ZipOutputStream zos) throws IOException
    {
        File[] files = folder.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    zipDirectory(file, zipEntryName + "/" + file.getName(), zos);
                }
                else
                {
                    try (FileInputStream fis = new FileInputStream(file))
                    {
                        ZipEntry zipEntry = new ZipEntry(zipEntryName + "/" + file.getName());
                        zos.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) >= 0)
                        {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
    }
}
