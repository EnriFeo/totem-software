package org.enrico.totemsoftware.services;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.enrico.totemsoftware.model.NodeModel;
import org.enrico.totemsoftware.model.NodeModel.NodeType;
import org.enrico.totemsoftware.utils.TikaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class TreeWalker {

	@Value("${base_location}")
	private String basePath;

	public List<NodeModel> resolvePath(String path) {
		return resolvePath(path, null);
	}

	public List<NodeModel> resolvePath(String path, String middlePath) {
		List<File> result;

		Path p = getRealPath(path, Optional.ofNullable(middlePath).orElse(""));

		File f = p.toFile();
		if (f.isDirectory()) {
			p = Paths.get(p.toString(), "/");
			result = Arrays.asList(p.toFile().listFiles());
		} else {
			result = Arrays.asList(f);
		}

		return result.stream().map(v -> makeNodeModel(v, middlePath)).toList();
	}

	private NodeModel makeNodeModel(File f, String middlePath) {
		boolean isDirectory = f.isDirectory();
		NodeModel.NodeModelBuilder result = NodeModel.builder()

				.name(f.getName())

				.link(makeNodeLink(f, ""));
		if (!isDirectory) {
			List<File> parentFolderContent = List.of(f.getParentFile().listFiles());
			int currentindex = parentFolderContent.indexOf(f);
			result

					.mediaType(TikaUtils.detect(f))

					.nodeType(NodeType.FILE)

					.previous(currentindex == 0 ? null
							: makeNodeLink(parentFolderContent.get(currentindex - 1), middlePath))

					.next(currentindex < parentFolderContent.size() - 1
							? makeNodeLink(parentFolderContent.get(currentindex + 1), middlePath)
							: null);

		} else {
			result.nodeType(NodeType.FOLDER);
		}
		return result.build();
	}

	private String makeNodeLink(File f, String middlePath) {
		if (!middlePath.isEmpty() && !middlePath.startsWith("/")) {
			middlePath = "/" + middlePath;
		}
		try {
			return Paths.get(middlePath, f.getCanonicalPath().replace(Paths.get(basePath).toString() + "\\", ""))
					.toString().replace("\\", "/");
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public Path getRealPath(@NonNull String path, @NonNull String middlePath) {
		if (path.contains("..")) {
			throw new IllegalStateException("unallowed pattern");
		}
		Path p = Paths.get(basePath, cleanPath(path.replace(middlePath, "")));
		if (p.startsWith(Paths.get(basePath))) {
			return p;
		} else {
			throw new IllegalStateException();
		}
	}

	private String cleanPath(String path) {
		return Optional.ofNullable(path).map(v -> {
			v = URLDecoder.decode(v, StandardCharsets.UTF_8);
			return v.equals("/") ? "./" : v;
		}).orElse("");
	}
}
