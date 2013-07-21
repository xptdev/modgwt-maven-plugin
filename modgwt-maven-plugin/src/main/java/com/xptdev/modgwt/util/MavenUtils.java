package com.xptdev.modgwt.util;
/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;

import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * The Class MavenUtils.
 */
public class MavenUtils {

    /**
     * Find child pom.
     *
     * @param   projectName  the project name
     * @param   currentDir   the current dir
     *
     * @return  the path
     *
     * @throws  IOException             Signals that an I/O exception has occurred.
     * @throws  XmlPullParserException  the xml pull parser exception
     */
    public static Path findChildPom(final String projectName, final Path currentDir) throws IOException,
        XmlPullParserException {

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

            public boolean accept(final Path file) throws IOException {
                String fileName = file.getFileName().toString();

                // skip the target folder
                return Files.isDirectory(file) && !fileName.startsWith(".") && !"target".equals(fileName);
            }
        };

        DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir, filter);

        for (Path childDir : stream) {

            // If we find the right pom file return it
            Path pomFile = childDir.resolve("pom.xml");

            if (Files.exists(pomFile)) {
                Model childModel = parseMavenModel(pomFile);
                if (projectName.equals(childModel.getArtifactId())) {
                    return pomFile;
                }
            }

            Path foundPath = findChildPom(projectName, childDir);

            if (foundPath != null) {
                return foundPath;
            }
        }

        return null;

    }

    /**
     * Parses the maven model.
     *
     * @param   pomFile  the pom file
     *
     * @return  the model
     *
     * @throws  IOException             Signals that an I/O exception has occurred.
     * @throws  XmlPullParserException  the xml pull parser exception
     */
    public static Model parseMavenModel(final Path pomFile) throws IOException, XmlPullParserException {

        Model model = null;

        Reader reader = Files.newBufferedReader(pomFile, Charset.defaultCharset());
        MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
        model = xpp3Reader.read(reader);

        return model;
    }
}
