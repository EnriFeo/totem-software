package org.enrico.totemsoftware.utils;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TikaUtils {
	private static final Tika TIKA = new Tika();

	public static String detect(File f) {
		try {
			return TIKA.detect(f);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}
}
