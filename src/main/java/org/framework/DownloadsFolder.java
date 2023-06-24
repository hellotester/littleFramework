package org.framework;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.framework.util.FileUtil.deleteFolderIfEmpty;


public class DownloadsFolder {
    protected final File folder;

    protected DownloadsFolder(File folder) {
        this.folder = Objects.nonNull(folder) ? folder.getAbsoluteFile() : null;
    }

    
    @Nonnull
    public File toFile() {
        return folder;
    }

    
    @Nonnull
    public List<File> files() {
        File[] files = folder.listFiles();
        return files == null ? emptyList() : asList(files);
    }

    public void cleanupBeforeDownload() {
        if (Objects.nonNull(folder)) {
            deleteFolderIfEmpty(folder);
        }
    }

    
    @Nonnull
    public File file(String fileName) {
        return new File(folder, fileName).getAbsoluteFile();
    }

    @Override
    public String toString() {
        return folder.getPath();
    }
}
