/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

import org.springframework.boot.loader.archive.Archive;

/**
 * Base class for executable archive {@link Launcher}s.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.0.0
 */
public abstract class ExecutableArchiveLauncher extends Launcher {

	// 一个Archive代表的是一个jar包或一个文件夹，Spring Boot可以从这个Archive启动
	private final Archive archive;

	public ExecutableArchiveLauncher() {
		try {
			// createArchive返回JarFileArchive对象，该对象的jarFile指向fat jar，url为fat jar的绝对路径
			this.archive = createArchive();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected ExecutableArchiveLauncher(Archive archive) {
		this.archive = archive;
	}

	protected final Archive getArchive() {
		return this.archive;
	}

	@Override
	protected String getMainClass() throws Exception {
		Manifest manifest = this.archive.getManifest();
		String mainClass = null;
		if (manifest != null) {
			mainClass = manifest.getMainAttributes().getValue("Start-Class");
		}
		if (mainClass == null) {
			throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
		}
		return mainClass;
	}

	@Override
	protected List<Archive> getClassPathArchives() throws Exception {
		/*
		 this.archive为fat jar的JarFileArchive对象，getNestedArchives方法使用传入的this::isNestedArchive对fat jar下的内容进行过滤遍历
		 this::isNestedArchive在JarLauncher的实现是只留下BOOT-INF/classes/和BOOT-INF/lib/文件夹，所以这里返回的archives实际上是BOOT-INF/classes/
		 和BOOT-INF/lib/文件夹下对应的JarFileArchive，每个BOOT-INF/lib/文件夹下的jar对应一个JarFileArchive对象，而BOOT-INF/classes/文件夹直接作为
		 一个JarFileArchive对象，对fat jar文件内容的遍历过程涉及到了jar文件结构，这里就不分析了，最后archives的内容可能是这样的：

		 0 = {JarFileArchive@1391} "jar:file:/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar!/BOOT-INF/classes!/"
		 1 = {JarFileArchive@1392} "jar:file:/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar!/BOOT-INF/lib/spring-boot-starter-2.3.0.BUILD-SNAPSHOT.jar!/"
		 2 = {JarFileArchive@1393} "jar:file:/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar!/BOOT-INF/lib/spring-boot-2.3.0.BUILD-SNAPSHOT.jar!/"
		 3 = {JarFileArchive@1394} "jar:file:/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar!/BOOT-INF/lib/spring-context-5.2.2.BUILD-SNAPSHOT.jar!/"
		 4 = {JarFileArchive@1395} "jar:file:/Users/dhf/IdeaProjects/spring-boot/spring-boot-project/demo-project/target/demo-project-2.3.0.BUILD-SNAPSHOT.jar!/BOOT-INF/lib/spring-boot-autoconfigure-2.3.0.BUILD-SNAPSHOT.jar!/"
		 ...
		 */

		List<Archive> archives = new ArrayList<>(this.archive.getNestedArchives(this::isNestedArchive));
		// 空方法
		postProcessClassPathArchives(archives);
		return archives;
	}

	/**
	 * Determine if the specified {@link JarEntry} is a nested item that should be added
	 * to the classpath. The method is called once for each entry.
	 * @param entry the jar entry
	 * @return {@code true} if the entry is a nested item (jar or folder)
	 */
	protected abstract boolean isNestedArchive(Archive.Entry entry);

	/**
	 * Called to post-process archive entries before they are used. Implementations can
	 * add and remove entries.
	 * @param archives the archives
	 * @throws Exception if the post processing fails
	 */
	protected void postProcessClassPathArchives(List<Archive> archives) throws Exception {
	}

}
