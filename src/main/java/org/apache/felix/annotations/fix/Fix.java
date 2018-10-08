package org.apache.felix.annotations.fix;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class Fix {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fix.class);

    public static void main(String[] args) throws IOException {
        File dir = new File(args[0]);
        LOGGER.info("starting to browse {}", dir.getAbsolutePath());
        Files.walkFileTree(dir.toPath(), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.endsWith(".java")) {
                    LOGGER.info("about to parse {}", file);
                    CompilationUnit cu = JavaParser.parse(file.toFile());
                    cu.findAll(AnnotationDeclaration.class).stream()
                            .forEach(a -> {
                                LOGGER.info("parsing {}", a.getNameAsString());
                            });
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                LOGGER.error("unable to visit {}", file, exc);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
