/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author eomar
 *
 */
public interface ServiceStorage {

	void init();

	void store(MultipartFile file);

	void store(MultipartFile file, String filename);

	void store(Resource file);

	void store(byte[] data, String filename);

	Path copy(String source, String dest);

	boolean exists(String filename);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	File createTmpFileOf(String filename);

	File createTmpFile();

	File loadAsFile(String filename);

	void delete(String filename);

	void deleteAll();

	Path getRoot();

	static String RandoomFilename() {
		return RandoomFilename(null);
	}

	static String RandoomFilename(String extension) {
		return "ts-" + UUID.randomUUID() + "-" + System.nanoTime() + (StringUtils.hasText(extension) ? extension.trim() : ".tmp");
	}

}
