package lu.itrust.business.ts.database.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletContext;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;

@Service
public class ServiceStorageImpl implements ServiceStorage {

    private Path root;

    @Value("${app.settings.data.folder}")
    private String classPathResourceDir;

    @Autowired
    private ServletContext servletContext;

    @Override
    public void delete(String filename) {
        try {
            final Path path = load(filename);
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException e) {
            TrickLogManager.persist(e);
        }
    }

    @PreDestroy
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(getRoot().toFile());
    }

    @Override
    public boolean exists(String filename) {
        return Files.exists(load(filename));
    }

    @Async
    @Override
    public void init() {
        try {
           
            if (Files.notExists(getRoot())) {
                Files.createDirectories(getRoot());
            }

            final String path = servletContext.getRealPath(classPathResourceDir);

            if (path == null) {
                copyResourceFromClassPath(classPathResourceDir, getRoot().toString());
            } else {
                FileSystemUtils.copyRecursively(Paths.get(path), getRoot());
            }
        } catch (IOException e) {
            throw new TrickException("error.resource.intialise.storage.directory",
                    "Storage directory cannot be initilised", e);
        }
    }

    @Override
    public Path load(String filename) {
        return getRoot().resolve(filename);
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(getRoot(), 1)
                        .filter(path -> !path.equals(getRoot()))
                        .map(path -> getRoot().relativize(path));
        } catch (IOException e) {
            throw new TrickException("error.read.storage.file", "Storage directory cannot be load", e);
        }
    }

    @Override
    public File loadAsFile(String filename) {
        final File file = load(filename).toFile();
        if (file.exists()) return file;
        throw new TrickException("error.resource.not.found", "Resource cannot be found!");
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            final Resource resource = new UrlResource(load(filename).toUri());
            if (resource.exists() || resource.isReadable()) return resource;
            throw new TrickException("error.resource.not.found", "Resource cannot be found!");
        } catch (MalformedURLException e) {
            throw new TrickException("error.resource.bad.path", "Resource cannot be found!", e);
        }
    }

    @Value("${app.setting.storage.upload.folder:ot-data}")
    public void setStorage(String storage) {
        this.root = Paths.get(storage);
        File file = this.root.toFile();
        if (!(file.exists() || file.canWrite())) {
            try {
                Files.createDirectories(getRoot());
            } catch (SecurityException | IOException e) {
                this.root = Paths.get(System.getProperty("java.io.tmpdir")).resolve("ot-data");
                try { Files.createDirectories(this.root); } catch (IOException ignore) {}
            }
        }
    }

    @Override
    public void store(byte[] bytes, String filename) {
        try {
            final Path path = load(filename);
            Files.createDirectories(path.getParent());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new TrickException("error.store.file.failed", "An unknown error occurred while storing file", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        store(file, file.getOriginalFilename());
    }

    @Override
    public void store(Resource file) {
        try {
            copyResourceToFilePath(file, load(file.getFilename()).toString());
        } catch (IOException e) {
            throw new TrickException("error.store.file.failed", "An unknown error occurred while storing file", e);
        }
    }

    private void copyResourceFromClassPath(String resourceFolder, String destinationFolder) {
        try {
            final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final Resource[] resources = resolver.getResources(resourceFolder + "**");
            final String baseJarPath = resolver.getResource(resourceFolder)
                                               .getURI()
                                               .getRawSchemeSpecificPart();
            final Path destinationPath = Paths.get(destinationFolder);

            for (Resource resource : resources) {
                final String relativePath = URLDecoder.decode(
                        resource.getURI().getRawSchemeSpecificPart().replace(baseJarPath, ""),
                        StandardCharsets.UTF_8.name());

                if (relativePath.isEmpty()) continue;

                final Path dest = destinationPath.resolve(relativePath);
                if (relativePath.endsWith("/") || relativePath.endsWith("\\")) {
                    Files.createDirectories(dest);
                } else {
                    copyResourceToFilePath(resource, dest.toString());
                }
            }
        } catch (IOException e) {
            TrickLogManager.persist(e);
        }
    }

    
    private void copyResourceToFilePath(Resource resource, String filePath) throws IOException {
        final Path target = Paths.get(filePath);
        Files.createDirectories(target.getParent());

        if (!Files.exists(target)) {
            try (InputStream in = resource.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Override
    public void store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty())
                throw new TrickException("error.store.file.empty", "Empty file cannot be stored");
            final Path dest = load(filename);
            Files.createDirectories(dest.getParent());
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new TrickException("error.store.file.failed", "An unknown error occurred while storing file", e);
        }
    }

    @Override
    public Path copy(String source, String dest) {
        try {
            final Path s = load(source);
            final Path d = load(dest);
            Files.createDirectories(d.getParent());
            return Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new TrickException("error.copy.file.failed", "An unknown error occurred while copying file", e);
        }
    }

    @Override
    public Path getRoot() {
        return root;
    }

    @Override
    public File createTmpFile() {
        return load(ServiceStorage.RandoomFilename()).toFile();
    }

    @Override
    public File createTmpFileOf(String filename) {
        final String extension = FilenameUtils.getExtension(filename);
        final File file = load(ServiceStorage.RandoomFilename(extension)).toFile();
        copy(filename, file.getAbsolutePath());
        return file;
    }
}
