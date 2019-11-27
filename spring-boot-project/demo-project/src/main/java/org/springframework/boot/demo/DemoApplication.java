package org.springframework.boot.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class DemoApplication {
//  public static void main(String[] args) {
//    SpringApplication.run(DemoApplication.class, args);
//  }

	private static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
	private static final String BOOT_INF_LIB = "BOOT-INF/lib/";

	public static void main(String[] args) {
		File file = new File("/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar");
		try {
			final JarFileArchive jarFileArchive = new JarFileArchive(file);
			final List<Archive> archives = jarFileArchive.getNestedArchives((entry) -> {
				if (entry.isDirectory()) {
					return entry.getName().equals(BOOT_INF_CLASSES);
				}
				return entry.getName().startsWith(BOOT_INF_LIB);
			});

			System.out.println(archives);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}