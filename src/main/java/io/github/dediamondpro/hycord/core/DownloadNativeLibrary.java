package io.github.dediamondpro.hycord.core;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadNativeLibrary {
    public static File downloadDiscordLibrary() throws IOException
    {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux
        String name = "discord_game_sdk";
        String suffix;
        if(System.getProperty("os.name").toLowerCase().contains("windows"))
        {
            suffix = ".dll";
        }
        else
        {
            suffix = ".so";
        }

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/x86_64/"+name+suffix;

        // Open the URL as a ZipInputStrea
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");

        URLConnection connection = downloadUrl.openConnection();
        //Need to convince cloudfare we are a real person.
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        ZipInputStream zin = new ZipInputStream(connection.getInputStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while((entry = zin.getNextEntry())!=null)
        {
            if(entry.getName().equals(zipPath))
            {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-"+name+System.nanoTime());
                if(!tempDir.mkdir())
                    throw new IOException("Cannot create temporary directory");
                tempDir.deleteOnExit();

                // Create a temporary file inside our directory (with a "normal" name)
                File temp = new File(tempDir, name+suffix);
                temp.deleteOnExit();

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                FMLLog.getLogger().log(Level.INFO,"dowloaded succesfully");
                return temp;
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return null;
    }

    public static void main(String[] args)
    {
        try
        {
            File discordLibrary = downloadDiscordLibrary();
            if(discordLibrary == null)
            {
               FMLLog.getLogger().log(Level.ERROR,"Error downloading Discord SDK.");
                return;
            }
            // Initialize the Core
            Core.init(discordLibrary);

            // Set parameters for the Core
            try(CreateParams params = new CreateParams())
            {
                params.setClientID(819625966627192864L);
                params.setFlags(CreateParams.getDefaultFlags());
                // Create the Core
                try(Core core = new Core(params))
                {
                    // Run callbacks forever
                    while(true)
                    {
                        core.runCallbacks();
                        try
                        {
                            // Sleep a bit to save CPU
                            Thread.sleep(16);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
           FMLLog.getLogger().log(Level.ERROR,"Error downloading Discord SDK.");
            return;
        }
    }
}
