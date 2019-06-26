/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceStorage;
import lu.itrust.business.TS.exception.TrickException;

/**
 * @author eomar
 *
 */
@Service
public class ServiceStorageImpl implements ServiceStorage {

	@Value("${app.settings.data.folder}")
	private String classPathResourceDir;

	@Autowired
	private ServletContext servletContext;

	private Path storage;

	@Override
	public void delete(String filename) {
		try {
			final Path path = load(filename);
			if (Files.exists(path))
				Files.delete(path);
		} catch (IOException e) {
			TrickLogManager.Persist(e);
		}
	}

	@PreDestroy
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(storage.toFile());
	}

	@Override
	public boolean exists(String filename) {
		return Files.exists(load(filename));
	}

	@Async
	@Override
	public void init() {
		try {
			if (!Files.notExists(storage))
				Files.createDirectories(storage);
			final String path = servletContext.getRealPath(classPathResourceDir);
			if (path == null)
				copyResourceFromClassPath(classPathResourceDir, storage.toString());
			else
				FileSystemUtils.copyRecursively(Paths.get(path), storage);
		} catch (IOException e) {
			throw new TrickException("error.resource.intialise.storage.directory", "Storage directory cannot be initilised", e);
		}
	}

	@Override
	public Path load(String filename) {
		return storage.resolve(filename);
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.storage, 1).filter(path -> !path.equals(this.storage)).map(path -> this.storage.relativize(path));
		} catch (IOException e) {
			throw new TrickException("error.read.storage.file", "Storage directory cannot be load", e);
		}
	}

	@Override
	public File loadAsFile(String filename) {
		final Path path = load(filename);
		if (path != null) {
			final File file = path.toFile();
			if (file.exists())
				return file;
		}
		throw new TrickException("error.resource.not.found", "Resource cannot be found!");
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			final Path path = load(filename);
			final Resource resource = new UrlResource(path.toUri());
			if (resource.exists() || resource.isReadable())
				return resource;
			throw new TrickException("error.resource.not.found", "Resource cannot be found!");
		} catch (MalformedURLException e) {
			throw new TrickException("error.resource.bad.path", "Resource cannot be found!", e);
		}
	}

	@Value("${app.setting.storage.upload.folder:ts-upload}")
	public void setStorage(String storage) {
		this.storage = Paths.get(storage);
	}

	@Override
	public void store(byte[] bytes, String filename) {
		try {
			final Path path = storage.resolve(filename);
			Files.write(path, bytes, StandardOpenOption.CREATE);
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
			copyResourceToFilePath(file, storage.resolve(file.getFilename()).toString());
		} catch (IOException e) {
			throw new TrickException("error.store.file.failed", "An unknown error occurred while storing file", e);
		}
	}

	private void copyResourceFromClassPath(String resourceFolder, String destinationFolder) {
		try {
			final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			final Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceFolder + "/**");
			final String baseJarPath = new DefaultResourceLoader().getResource(resourceFolder).getURI().getRawSchemeSpecificPart();
			for (Resource resource : resources) {
				String relativePath = resource.getURI().getRawSchemeSpecificPart().replace(baseJarPath, "");
				if (relativePath.isEmpty())
					continue;
				if (relativePath.endsWith("/") || relativePath.endsWith("\\")) {
					final File dirFile = new File(destinationFolder + relativePath);
					if (!dirFile.exists())
						dirFile.mkdir();
				} else
					copyResourceToFilePath(resource, destinationFolder + relativePath);
			}
		} catch (IOException e) {
			TrickLogManager.Persist(e);
		}
	}

	private void copyResourceToFilePath(Resource resource, String filePath) throws IOException {
		final InputStream inputStream = resource.getInputStream();
		final File file = new File(filePath);
		if (!file.exists())
			FileUtils.copyInputStreamToFile(inputStream, file);
	}

	@Override
	public void store(MultipartFile file, String filename) {
		try {
			if (file.isEmpty())
				throw new TrickException("error.store.file.empty", "Empty file cannot be stored");
			Files.copy(file.getInputStream(), this.storage.resolve(filename));
		} catch (IOException e) {
			throw new TrickException("error.store.file.failed", "An unknown error occurred while storing file", e);
		}

	}

}
