package org.enrico.totemsoftware.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.enrico.totemsoftware.services.TreeWalker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TotemResourceController {

	private TreeWalker walker;

	@GetMapping("/resource")
	public void serveResource(@RequestParam String path, HttpServletResponse response) throws IOException {
		File file = walker.getRealPath(path, "").toFile();
		try (InputStream is = new FileInputStream(file)) {
			IOUtils.copy(is, response.getOutputStream());
		}
	}
}
