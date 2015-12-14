package yoshikihigo.clonegear.gui.util;


import java.io.File;
import java.lang.String;
import javax.swing.filechooser.FileFilter;


public class CLSFileFilter extends FileFilter {

    public boolean accept(File pathname) {

        if (pathname.isDirectory())
            return true;
        else if (pathname.isFile()) {

            String fileName = pathname.getName();
            if (fileName.toLowerCase().endsWith(".cls"))
                return true;
            else
                return false;

        } else
            return false;
    }

    public String getDescription() {
        return "*.cls";
    }
}
